package mc.recraftors.blahaj;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ItemContainerCuddlyItem extends CuddlyItem {
    public static final String STORED_ITEM_KEY = "Item";

    public ItemContainerCuddlyItem(Settings settings, String subtitle) {
        super(settings, subtitle);
    }

    @Override
    public boolean onStackClicked(ItemStack itemStack, Slot slot, ClickType clickType, PlayerEntity playerEntity) {
        if (clickType != ClickType.RIGHT) return false;
        ItemStack target = slot.getStack();
        NbtCompound nbt = itemStack.getSubNbt(STORED_ITEM_KEY);
        if (target.isEmpty()) {
            if (nbt == null) return super.onStackClicked(itemStack, slot, clickType, playerEntity);
            ItemStack stored = getStoredStack(itemStack);
            if (stored == ItemStack.EMPTY || !slot.canInsert(stored)) {
                return super.onStackClicked(itemStack, slot, clickType, playerEntity);
            }
            ItemStack e = slot.insertStack(stored);
            storeItemStack(itemStack, e, e.getCount());
            this.playRemoveOneSound(playerEntity);
            return true;
        }
        if (nbt == null) {
            if (!canHold(target)) return super.onStackClicked(itemStack, slot, clickType, playerEntity);
            storeItemStack(itemStack, target, target.getCount());
            playInsertSound(playerEntity);
            return true;
        } else {
            ItemStack stored = ItemStack.fromNbt(nbt);
            if (canMergeItems(stored, target)) {
                mergeStored(itemStack, playerEntity, target, stored);
                return true;
            }
        }
        return super.onStackClicked(itemStack, slot, clickType, playerEntity);
    }

    @Override
    public boolean onClicked(ItemStack itemStack, ItemStack target, Slot slot, ClickType type, PlayerEntity player, StackReference reference) {
        if (type != ClickType.RIGHT ||! slot.canTakePartial(player)) {
            return super.onClicked(itemStack, target, slot, type, player, reference);
        }
        NbtCompound nbt = itemStack.getSubNbt(STORED_ITEM_KEY);
        if (target.isEmpty()) {
            ItemStack stored = getStoredStack(itemStack);
            if (nbt == null) return super.onClicked(itemStack, target, slot, type, player, reference);
            if (reference.set(stored)) {
                storeItemStack(itemStack, null, 0);
                playRemoveOneSound(player);
                return true;
            }
            return super.onClicked(itemStack, target, slot, type, player, reference);
        }
        if (nbt == null) {
            if (!canHold(target)) return super.onClicked(itemStack, target, slot, type, player, reference);
            int amount = Math.min(target.getCount(), target.getItem().getMaxCount());
            storeItemStack(itemStack, target, amount);
            playInsertSound(player);
            return true;
        }
        ItemStack stored = ItemStack.fromNbt(nbt);
        if (!canMergeItems(stored, target)) {
            return super.onClicked(itemStack, target, slot, type, player, reference);
        }
        mergeStored(itemStack, player, target, stored);
        return true;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack itemStack) {
        ItemStack stored = getStoredStack(itemStack);
        if (stored == ItemStack.EMPTY) {
            return Optional.empty();
        }
        return Optional.of(new CuddlyContainerTooltipData(stored));
    }

    @Override
    public boolean canBeNested() {
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack itemStack) {
        return super.hasGlint(((ItemStackProvider) itemStack).blahaj$getStack());
    }

    public ItemStack getContainedStack(ItemStack stack) {
        return getStoredStack(stack);
    }

    public TagKey<Item> usableContainedItemTag() {
        return Blahaj.BLAVINGAD_USABLE_ITEMS;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Optional<ItemStack> extract(ItemStack stack) {
        stack = ((ItemStackProvider) stack).blahaj$getStack();
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            return Optional.empty();
        }
        nbt = stack.getSubNbt(STORED_ITEM_KEY);
        if (nbt == null) {
            return Optional.empty();
        }
        stack.removeSubNbt(STORED_ITEM_KEY);
        return Optional.ofNullable(ItemStack.fromNbt(nbt));
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setContent(ItemStack itemStack, ItemStack target) {
        storeItemStack(itemStack, target, target.getCount());
        return true;
    }

    protected void mergeStored(ItemStack itemStack, PlayerEntity playerEntity, ItemStack target, ItemStack stored) {
        int acc = target.getCount();
        int in = stored.getCount();
        int t = acc + in;
        int m = stored.getMaxCount();
        if (t >= m) {
            storeItemStack(itemStack, stored, m);
            target.setCount(acc - (m - in));
        } else {
            storeItemStack(itemStack, stored, t);
            target.setCount(0);
        }
        playInsertSound(playerEntity);
    }

    public static ItemStack getStoredStack(ItemStack stack) {
        stack = ((ItemStackProvider) stack).blahaj$getStack();
        NbtCompound nbt = stack.getSubNbt(STORED_ITEM_KEY);
        if (nbt == null) {
            return ItemStack.EMPTY;
        }
        return ItemStack.fromNbt(nbt);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canHold(ItemStack stack) {
        stack = ((ItemStackProvider) stack).blahaj$getStack();
        return !stack.isEmpty() && stack.getItem().canBeNested() && !stack.isIn(Blahaj.NON_CONTAINABLE_ITEMS);
    }

    public static void storeItemStack(ItemStack container, ItemStack target, int q) {
        container = ((ItemStackProvider) container).blahaj$getStack();
        NbtCompound nbt = container.getOrCreateNbt();
        if (target == null || target.isEmpty() || q <= 0) {
            nbt.remove(STORED_ITEM_KEY);
        } else {
            ItemStack e = target.copy();
            e.setCount(q);
            nbt.put(STORED_ITEM_KEY, e.writeNbt(new NbtCompound()));
            target.setCount(target.getCount()-q);
        }
    }

    public static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
        if (!itemStack.isOf(itemStack2.getItem())) {
            return false;
        } else if (itemStack.getDamage() != itemStack2.getDamage()) {
            return false;
        } else if (itemStack.getCount() >= itemStack.getMaxCount()) {
            return false;
        } else {
            return ItemStack.areNbtEqual(itemStack, itemStack2);
        }
    }

    protected void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    protected void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    public static class CuddlyContainerTooltipData implements TooltipData {
        private final ItemStack storedStack;

        public CuddlyContainerTooltipData(ItemStack stack) {
            if (stack == null || stack.isEmpty()) {
                this.storedStack = null;
            } else {
                this.storedStack = stack;
            }
        }

        public ItemStack getStoredStack() {
            return storedStack;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class CuddlyContainerTooltipComponent implements TooltipComponent {
        private final ItemStack stack;
        public CuddlyContainerTooltipComponent(CuddlyContainerTooltipData data) {
            this.stack = data.getStoredStack();
        }

        public boolean isHolding() {
            return this.stack != null;
        }

        @Override
        public int getHeight() {
            if (!isHolding()) return 0;
            return 26; // 20 + 2 + 4
        }

        @Override
        public int getWidth(TextRenderer textRenderer) {
            if (this.isHolding()) return 20; // 18 + 2
            return 0;
        }

        @Override
        public void drawItems(TextRenderer textRenderer, int i, int j, MatrixStack matrixStack, ItemRenderer itemRenderer, int k) {
            this.drawSlot(i+1, j+1, textRenderer, matrixStack, itemRenderer, k);
            this.drawOutline(i, j, matrixStack, k);
        }

        // adapted from BundleItem
        protected void drawSlot(int i, int j, TextRenderer textRenderer, MatrixStack matrixStack, ItemRenderer itemRenderer, int l) {
            if (!this.isHolding()) return;
            ItemStack itemStack = this.stack;
            this.draw(matrixStack, i, j, l, Sprite.SLOT);
            itemRenderer.renderInGuiWithOverrides(itemStack, i + 1, j + 1, 0);
            itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, i + 1, j + 1);
            HandledScreen.drawSlotHighlight(matrixStack, i + 1, j + 1, l);
        }

        // adapted from BundleItem
        protected void drawOutline(int i, int j, MatrixStack matrixStack, int m) {
            this.draw(matrixStack, i, j, m, Sprite.BORDER_CORNER_TOP);
            this.draw(matrixStack, i + 19, j, m, Sprite.BORDER_CORNER_TOP);
            this.draw(matrixStack, i + 1, j, m, Sprite.BORDER_HORIZONTAL_TOP);
            this.draw(matrixStack, i + 1, j + 20, m, Sprite.BORDER_HORIZONTAL_BOTTOM);
            this.draw(matrixStack, i, j + + 1, m, Sprite.BORDER_VERTICAL);
            this.draw(matrixStack, i + 19, j + 1, m, Sprite.BORDER_VERTICAL);
            this.draw(matrixStack, i, j + 20, m, Sprite.BORDER_CORNER_BOTTOM);
            this.draw(matrixStack, i + 19, j + 20, m, Sprite.BORDER_CORNER_BOTTOM);
        }

        // adapted from BundleItem
        protected void draw(MatrixStack matrixStack, int i, int j, int k, Sprite sprite) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, BundleTooltipComponent.TEXTURE);
            DrawableHelper.drawTexture(matrixStack, i, j, k, sprite.u, sprite.v, sprite.width, sprite.height, 128, 128);
        }

        // adapted from BundleItem
        @Environment(value= EnvType.CLIENT)
        public enum Sprite {
            SLOT(0, 0, 18, 20),
            BLOCKED_SLOT(0, 40, 18, 20),
            BORDER_VERTICAL(0, 18, 1, 20),
            BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
            BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
            BORDER_CORNER_TOP(0, 20, 1, 1),
            BORDER_CORNER_BOTTOM(0, 60, 1, 1);

            public final int u;
            public final int v;
            public final int width;
            public final int height;

            Sprite(int j, int k, int l, int m) {
                this.u = j;
                this.v = k;
                this.width = l;
                this.height = m;
            }
        }
    }

    public static final class ContainedItemStack extends ItemStack implements ItemStackProvider {
        private final ItemStack current;
        private final ItemStack parent;
        private final ItemContainerCuddlyItem containerItem;

        public ContainedItemStack(ItemStack container, ItemStack content) {
            super(content.getItem(), content.getCount());
            if (!(container.getItem() instanceof ItemContainerCuddlyItem cuddly)) {
                throw new UnsupportedOperationException("Cannot contain from a non-container class");
            }
            cuddly = (ItemContainerCuddlyItem) Item.byRawId(Registry.ITEM.getRawId(cuddly));
            this.current = cuddly.getContainedStack(container);
            this.parent = container;
            this.containerItem = cuddly;
        }

        public void dirty() {
            if (this.getHolder() instanceof PlayerEntity player && player.isCreative()) return;
            this.containerItem.extract(this.parent);
            this.containerItem.setContent(this.parent, this.containerItem.getContainedStack(this.parent));
        }

        public boolean isContainerEmpty() {
            return this.containerItem.getContainedStack(this.parent).isEmpty();
        }

        public void tryInsertOrDrop(LivingEntity entity, ItemStack target) {
            if (target.isEmpty() || this == target) return;
            if (this.isContainerEmpty()) {
                this.containerItem.setContent(this.containerItem.getContainedStack(this.parent), target);
            } else {
                if (entity.isPlayer() &&
                        ((PlayerEntity) entity).isCreative() &&
                        target.isOf(this.containerItem.getContainedStack(this.parent).getItem())) {
                    return;
                }
                if (entity.getWorld().isClient()) {
                    entity.swingHand(Hand.MAIN_HAND);
                }
                double eyeY = entity.getEyeY();
                ItemEntity itemEntity = new ItemEntity(entity.getWorld(), entity.getX(), eyeY, entity.getZ(), target);
                itemEntity.setPickupDelay(40);
                Random random = entity.getRandom();
                float g = MathHelper.sin(entity.getPitch() * ((float)Math.PI / 180));
                float h = MathHelper.cos(entity.getPitch() * ((float)Math.PI / 180));
                float i = MathHelper.sin(entity.getYaw() * ((float)Math.PI / 180));
                float j = MathHelper.cos(entity.getYaw() * ((float)Math.PI / 180));
                float k = random.nextFloat() * ((float)Math.PI * 2);
                float l = 0.02f * random.nextFloat();
                float f = random.nextFloat();
                itemEntity.setVelocity((-i * h * 0.3f) + Math.cos(k) * l, -g * 0.3f + 0.1f + (random.nextFloat() - f) * 0.1f, (j * h * 0.3f) + Math.sin(k) * l);
                entity.getWorld().spawnEntity(itemEntity);
            }
        }

        @Override
        public ItemStack blahaj$getStack() {
            return this.current;
        }

        @Override
        public boolean isEmpty() {
            if (current == null) {
                return this.parent.isEmpty();
            }
            return this.current.isEmpty();
        }

        @Override
        public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
            ActionResult result = this.current.useOnBlock(itemUsageContext);
            this.dirty();
            return result;
        }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
            TypedActionResult<ItemStack> result = this.current.use(world, player, hand);
            this.dirty();
            return new TypedActionResult<>(result.getResult(), this);
        }

        @Override
        public ItemStack finishUsing(World world, LivingEntity livingEntity) {
            this.current.finishUsing(world, livingEntity);
            this.dirty();
            return this.parent;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbtCompound) {
            if (this.current == null) {
                return null;
            }
            NbtCompound result = this.current.writeNbt(nbtCompound);
            this.dirty();
            return result;
        }

        @Override
        public boolean isStackable() {
            return false;
        }

        @Override
        public boolean isDamaged() {
            return this.current.isDamaged();
        }

        @Override
        public int getDamage() {
            if (this.current == null) {
                return 0;
            }
            return this.current.getDamage();
        }

        @Override
        public void setDamage(int i) {
            if (this.current == null) {
                return;
            }
            this.current.setDamage(i);
            this.dirty();
        }

        @Override
        public boolean damage(int i, Random random, @Nullable ServerPlayerEntity player) {
            if (this.current == null) {
                return false;
            }
            boolean result = this.current.damage(i, random, player);
            this.dirty();
            return result;
        }

        @Override
        public <T extends LivingEntity> void damage(int i, T livingEntity, Consumer<T> consumer) {
            if (this.current == null) {
                return;
            }
            this.current.damage(i, livingEntity, consumer);
            this.dirty();
        }

        @Override
        public boolean onClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference reference) {
            boolean result = this.current.onClicked(stack, slot, clickType, player, reference);
            this.dirty();
            return result;
        }

        @Override
        public ActionResult useOnEntity(PlayerEntity player, LivingEntity livingEntity, Hand hand) {
            ActionResult result = this.current.useOnEntity(player, livingEntity, hand);
            this.dirty();
            return result;
        }

        @Override
        public ItemStack copy() {
            if (this.current == null) {
                return null;
            }
            return this.current.copy();
        }
        @Override
        public void inventoryTick(World world, Entity entity, int i, boolean bl) {
            this.current.inventoryTick(world, entity, i, bl);
        }

        @Override
        public void onStoppedUsing(World world, LivingEntity livingEntity, int i) {
            this.current.onStoppedUsing(world, livingEntity, i);
            this.dirty();
        }

        @Override
        public boolean hasNbt() {
            return this.current.hasNbt();
        }

        @Nullable
        @Override
        public NbtCompound getNbt() {
            //TODO: ContainedNbt
            return this.current.getNbt();
        }

        @Override
        public NbtCompound getOrCreateNbt() {
            NbtCompound result = this.current.getOrCreateNbt();
            this.dirty();
            return result;
        }

        @Override
        public NbtCompound getOrCreateSubNbt(String string) {
            NbtCompound result = this.current.getOrCreateSubNbt(string);
            this.dirty();
            return result;
        }

        @Nullable
        @Override
        public NbtCompound getSubNbt(String string) {
            return this.current.getSubNbt(string);
        }

        @Override
        public void removeSubNbt(String string) {
            this.current.removeSubNbt(string);
            this.dirty();
        }

        @Override
        public NbtList getEnchantments() {
            return this.current.getEnchantments();
        }

        @Override
        public void setNbt(@Nullable NbtCompound nbtCompound) {
            this.current.setNbt(nbtCompound);
            this.dirty();
        }

        @Override
        public Text getName() {
            return this.current.getName();
        }

        @Override
        public ItemStack setCustomName(@Nullable Text text) {
            this.current.setCustomName(text);
            this.dirty();
            return this.current;
        }

        @Override
        public void removeCustomName() {
            if (this.current.hasCustomName()) {
                this.current.removeCustomName();
                this.dirty();
            }
        }

        @Override
        public boolean hasCustomName() {
            return this.current.hasCustomName();
        }

        @Override
        public List<Text> getTooltip(@Nullable PlayerEntity playerEntity, TooltipContext context) {
            return this.current.getTooltip(playerEntity, context);
        }

        @Override
        public void addHideFlag(TooltipSection tooltipSection) {
            this.current.addHideFlag(tooltipSection);
            this.dirty();
        }

        @Override
        public void addEnchantment(Enchantment enchantment, int i) {
            this.current.addEnchantment(enchantment, i);
            this.dirty();
        }

        @Override
        public boolean hasEnchantments() {
            return this.current.hasEnchantments();
        }

        @Override
        public boolean isInFrame() {
            return this.parent.isInFrame();
        }

        @Override
        public void setHolder(@Nullable Entity entity) {
            this.parent.setHolder(entity);
        }

        @Nullable
        @Override
        public ItemFrameEntity getFrame() {
            return this.parent.getFrame();
        }

        @Nullable
        @Override
        public Entity getHolder() {
            return this.parent.getHolder();
        }

        @Override
        public int getRepairCost() {
            return this.current.getRepairCost();
        }

        @Override
        public void setRepairCost(int i) {
            if (i != this.current.getRepairCost()) {
                this.current.setRepairCost(i);
                this.dirty();
            }
        }

        @Override
        public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
            return this.current.getAttributeModifiers(equipmentSlot);
        }

        @Override
        public void addAttributeModifier(EntityAttribute attribute, EntityAttributeModifier modifier, @Nullable EquipmentSlot slot) {
            this.current.addAttributeModifier(attribute, modifier, slot);
            this.dirty();
        }

        @Override
        public boolean canPlaceOn(Registry<Block> registry, CachedBlockPosition position) {
            return this.current.canPlaceOn(registry, position);
        }

        @Override
        public boolean canDestroy(Registry<Block> registry, CachedBlockPosition position) {
            return this.current.canDestroy(registry, position);
        }

        @Override
        public int getBobbingAnimationTime() {
            return this.current.getBobbingAnimationTime();
        }

        @Override
        public void setBobbingAnimationTime(int i) {
            this.current.setBobbingAnimationTime(i);
            this.dirty();
        }

        @Override
        public int getCount() {
            return this.current.getCount();
        }

        @Override
        public void setCount(int i) {
            this.current.setCount(i);
            this.dirty();
        }

        @Override
        public void usageTick(World world, LivingEntity livingEntity, int i) {
            this.current.usageTick(world, livingEntity, i);
        }
    }
}
