package hibi.blahaj;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
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
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
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
            if (ItemStack.canCombine(stored, target)) {
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
        if (!ItemStack.canCombine(stored, target)) {
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
        public void drawItems(TextRenderer textRenderer, int i, int j, DrawContext drawContext) {
            this.drawSlot(i+1, j+1, drawContext, textRenderer);
            this.drawOutline(i, j, drawContext);
        }

        // adapted from BundleItem
        protected void drawSlot(int i, int j, DrawContext drawContext, TextRenderer textRenderer) {
            if (!this.isHolding()) return;
            ItemStack itemStack = this.stack;
            this.draw(drawContext, i, j, Sprite.SLOT);
            drawContext.drawItem(itemStack, i+1, j+1, 0);
            drawContext.drawItemInSlot(textRenderer, itemStack, i+1, j+1);
            HandledScreen.drawSlotHighlight(drawContext, i + 1, j + 1, 0);
        }

        // adapted from BundleItem
        protected void drawOutline(int i, int j, DrawContext drawContext) {
            this.draw(drawContext, i, j, Sprite.BORDER_CORNER_TOP);
            this.draw(drawContext, i + 19, j, Sprite.BORDER_CORNER_TOP);
            this.draw(drawContext, i + 1, j, Sprite.BORDER_HORIZONTAL_TOP);
            this.draw(drawContext, i + 1, j + 20, Sprite.BORDER_HORIZONTAL_BOTTOM);
            this.draw(drawContext, i, j + 1, Sprite.BORDER_VERTICAL);
            this.draw(drawContext, i + 19, j + 1, Sprite.BORDER_VERTICAL);
            this.draw(drawContext, i, j + 20, Sprite.BORDER_CORNER_BOTTOM);
            this.draw(drawContext, i + 19, j + 20, Sprite.BORDER_CORNER_BOTTOM);
        }

        // adapted from BundleItem
        protected void draw(DrawContext drawContext, int i, int j, Sprite sprite) {
            drawContext.drawTexture(BundleTooltipComponent.TEXTURE, i, j, 0, sprite.u, sprite.v, sprite.width, sprite.height, 128, 128);
        }

        // adapted from BundleItem
        @Environment(value= EnvType.CLIENT)
        public enum Sprite {
            SLOT(0, 0, 18, 20),
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

    public static final class ContainerItemStack extends ItemStack implements ItemStackProvider {
        private final ItemStack current;
        private final ItemContainerCuddlyItem containerItem;
        private boolean isContainedUsable;

        public ContainerItemStack(ItemStack container) {
            super(container.getItem(), container.getCount());
            if (!(container.getItem() instanceof ItemContainerCuddlyItem cuddly)) {
                throw new UnsupportedOperationException("Cannot contain from a non-container class");
            }
            this.current = container;
            this.containerItem = cuddly;
            this.isContainedUsable = cuddly.getContainedStack(container).isIn(cuddly.usableContainedItemTag());
        }

        public void dirty() {
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            this.isContainedUsable = stack.isIn(this.containerItem.usableContainedItemTag());
        }

        private void updateContained(ItemStack content) {
            if (!this.containerItem.getContainedStack(this.current).isOf(content.getItem())) return;
            this.containerItem.extract(this.current);
            this.containerItem.setContent(this.current, content);
        }

        public boolean isContainerEmpty() {
            return this.containerItem.getContainedStack(this.current).isEmpty();
        }

        public void tryInsertOrDrop(LivingEntity entity, ItemStack target) {
            if (target.isEmpty() || this == target) return;
            if (this.isContainerEmpty()) {
                this.containerItem.setContent(this.current, target);
            } else {
                if (entity.isPlayer() &&
                        ((PlayerEntity) entity).isCreative() &&
                        target.isOf(this.containerItem.getContainedStack(this.current).getItem())) {
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
            if (!this.isContainedUsable || this.isContainerEmpty()) return current.isEmpty();
            return this.containerItem.getContainedStack(this.current).isEmpty() && this.current.isEmpty();
        }

        @Override
        public Item getItem() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return containerItem;
            return this.containerItem.getContainedStack(this.current).getItem();
        }

        @Override
        public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.useOnBlock(itemUsageContext);
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            ActionResult result = stack.useOnBlock(itemUsageContext);
            this.updateContained(stack);
            return result;
        }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.use(world, playerEntity, hand);
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            TypedActionResult<ItemStack> result = stack.use(world, playerEntity, hand);
            if (stack != this) this.updateContained(stack);
            return result;
        }

        @Override
        public ItemStack finishUsing(World world, LivingEntity livingEntity) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.finishUsing(world, livingEntity);
            ItemStack stack = this.containerItem.getContainedStack(this.current).finishUsing(world, livingEntity);
            this.updateContained(stack);
            return stack;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbtCompound) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.writeNbt(nbtCompound);
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            NbtCompound result = stack.writeNbt(nbtCompound);
            this.updateContained(stack);
            this.dirty();
            return result;
        }

        @Override
        public int getMaxCount() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getMaxCount();
            return this.containerItem.getContainedStack(this.current).getMaxCount();
        }

        @Override
        public boolean isStackable() {
            return false;
        }

        @Override
        public boolean isDamageable() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.isDamageable();
            return this.containerItem.getContainedStack(this.current).isDamageable();
        }

        @Override
        public boolean isDamaged() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.isDamaged();
            return this.containerItem.getContainedStack(this.current).isDamaged();
        }

        @Override
        public int getDamage() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getDamage();
            return this.containerItem.getContainedStack(this.current).getDamage();
        }

        @Override
        public void setDamage(int i) {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.setDamage(i);
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.setDamage(i);
            this.updateContained(stack);
        }

        @Override
        public boolean damage(int i, Random random, @Nullable ServerPlayerEntity serverPlayerEntity) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.damage(i, random, serverPlayerEntity);
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            boolean result = stack.damage(i, random, serverPlayerEntity);
            this.updateContained(stack);
            this.dirty();
            return result;
        }

        @Override
        public <T extends LivingEntity> void damage(int i, T livingEntity, Consumer<T> consumer) {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.damage(i, livingEntity, consumer);
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.damage(i, livingEntity, consumer);
            this.updateContained(stack);
            this.dirty();
        }

        @Override
        public boolean onClicked(ItemStack itemStack, Slot slot, ClickType clickType, PlayerEntity playerEntity, StackReference stackReference) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.onClicked(itemStack, slot, clickType, playerEntity, stackReference);
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            boolean result = stack.onClicked(itemStack, slot, clickType, playerEntity, stackReference);
            this.updateContained(stack);
            this.dirty();
            return result;
        }

        @Override
        public ActionResult useOnEntity(PlayerEntity playerEntity, LivingEntity livingEntity, Hand hand) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.useOnEntity(playerEntity, livingEntity, hand);
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            ActionResult result = stack.useOnEntity(playerEntity, livingEntity, hand);
            this.updateContained(stack);
            this.dirty();
            return result;
        }

        @Override
        public ItemStack copy() {
            return this.current.copy();
        }

        @Override
        public ItemStack copyWithCount(int i) {
            return this.current.copyWithCount(i);
        }

        @Override
        public String getTranslationKey() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getTranslationKey();
            return this.containerItem.getContainedStack(this.current).getTranslationKey();
        }

        @Override
        public String toString() {
            return this.current.toString();
        }

        @Override
        public void inventoryTick(World world, Entity entity, int i, boolean bl) {
            this.current.inventoryTick(world, entity, i, bl);
        }

        @Override
        public int getMaxUseTime() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getMaxUseTime();
            return this.containerItem.getContainedStack(this.current).getMaxUseTime();
        }

        @Override
        public UseAction getUseAction() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getUseAction();
            return this.containerItem.getContainedStack(this.current).getUseAction();
        }

        @Override
        public void onStoppedUsing(World world, LivingEntity livingEntity, int i) {
            if (!this.isContainedUsable || this.isContainerEmpty()) this.current.onStoppedUsing(world, livingEntity, i);
            else {
                ItemStack stack = this.containerItem.getContainedStack(this.current);
                stack.onStoppedUsing(world, livingEntity, i);
                this.updateContained(stack);
            }
        }

        @Override
        public boolean isUsedOnRelease() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.isUsedOnRelease();
            return this.containerItem.getContainedStack(this.current).isUsedOnRelease();
        }

        @Override
        public boolean hasNbt() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.hasNbt();
            return this.containerItem.getContainedStack(this.current).hasNbt();
        }

        @Nullable
        @Override
        public NbtCompound getNbt() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getNbt();
            return this.containerItem.getContainedStack(this.current).getNbt();
        }

        @Override
        public NbtCompound getOrCreateNbt() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getOrCreateNbt();
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            NbtCompound result = stack.getOrCreateNbt();
            this.updateContained(stack);
            return result;
        }

        @Override
        public NbtCompound getOrCreateSubNbt(String string) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getOrCreateSubNbt(string);
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            NbtCompound result = stack.getOrCreateSubNbt(string);
            this.updateContained(stack);
            return result;
        }

        @Nullable
        @Override
        public NbtCompound getSubNbt(String string) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getSubNbt(string);
            return this.containerItem.getContainedStack(this.current).getSubNbt(string);
        }

        @Override
        public void removeSubNbt(String string) {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.removeSubNbt(string);
                this.dirty();
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.removeSubNbt(string);
            this.updateContained(stack);
        }

        @Override
        public NbtList getEnchantments() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getEnchantments();
            return this.containerItem.getContainedStack(this.current).getEnchantments();
        }

        @Override
        public void setNbt(@Nullable NbtCompound nbtCompound) {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.setNbt(nbtCompound);
                this.dirty();
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.setNbt(nbtCompound);
            this.updateContained(stack);
        }

        @Override
        public Text getName() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getName();
            return this.containerItem.getContainedStack(this.current).getName();
        }

        @Override
        public ItemStack setCustomName(@Nullable Text text) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.setCustomName(text);
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.setCustomName(text);
            this.updateContained(stack);
            return stack;
        }

        @Override
        public void removeCustomName() {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.removeCustomName();
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.removeCustomName();
            this.updateContained(stack);
        }

        @Override
        public boolean hasCustomName() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.hasCustomName();
            return this.containerItem.getContainedStack(this.current).hasCustomName();
        }

        @Override
        public List<Text> getTooltip(@Nullable PlayerEntity playerEntity, TooltipContext tooltipContext) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getTooltip(playerEntity, tooltipContext);
            return this.containerItem.getContainedStack(this.current).getTooltip(playerEntity, tooltipContext);
        }

        @Override
        public void addHideFlag(TooltipSection tooltipSection) {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.addHideFlag(tooltipSection);
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.addHideFlag(tooltipSection);
            this.updateContained(stack);
        }

        @Override
        public void addEnchantment(Enchantment enchantment, int i) {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.addEnchantment(enchantment, i);
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.addEnchantment(enchantment, i);
            this.updateContained(stack);
        }

        @Override
        public boolean hasEnchantments() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.hasEnchantments();
            return this.containerItem.getContainedStack(this.current).hasEnchantments();
        }

        @Override
        public boolean isInFrame() {
            return this.current.isInFrame();
        }

        @Override
        public void setHolder(@Nullable Entity entity) {
            this.current.setHolder(entity);
        }

        @Nullable
        @Override
        public ItemFrameEntity getFrame() {
            return this.current.getFrame();
        }

        @Nullable
        @Override
        public Entity getHolder() {
            return this.current.getHolder();
        }

        @Override
        public int getRepairCost() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getRepairCost();
            return this.containerItem.getContainedStack(this.current).getRepairCost();
        }

        @Override
        public void setRepairCost(int i) {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.setRepairCost(i);
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.setRepairCost(i);
            this.updateContained(stack);
        }

        @Override
        public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getAttributeModifiers(equipmentSlot);
            return this.containerItem.getContainedStack(this.current).getAttributeModifiers(equipmentSlot);
        }

        @Override
        public void addAttributeModifier(EntityAttribute entityAttribute, EntityAttributeModifier entityAttributeModifier, @Nullable EquipmentSlot equipmentSlot) {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.addAttributeModifier(entityAttribute, entityAttributeModifier, equipmentSlot);
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.addAttributeModifier(entityAttribute, entityAttributeModifier, equipmentSlot);
            this.updateContained(stack);
        }

        @Override
        public boolean canPlaceOn(Registry<Block> registry, CachedBlockPosition cachedBlockPosition) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.canPlaceOn(registry, cachedBlockPosition);
            return this.containerItem.getContainedStack(this.current).canPlaceOn(registry, cachedBlockPosition);
        }

        @Override
        public boolean canDestroy(Registry<Block> registry, CachedBlockPosition cachedBlockPosition) {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.canDestroy(registry, cachedBlockPosition);
            return this.containerItem.getContainedStack(this.current).canDestroy(registry, cachedBlockPosition);
        }

        @Override
        public int getBobbingAnimationTime() {
            return this.current.getBobbingAnimationTime();
        }

        @Override
        public void setBobbingAnimationTime(int i) {
            this.current.setBobbingAnimationTime(i);
        }

        @Override
        public int getCount() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getCount();
            return this.containerItem.getContainedStack(this.current).getCount();
        }

        @Override
        public void setCount(int i) {
            if (!this.isContainedUsable || this.isContainerEmpty()) {
                this.current.setCount(i);
                return;
            }
            ItemStack stack = this.containerItem.getContainedStack(this.current);
            stack.setCount(i);
            this.updateContained(stack);
        }

        @Override
        public void usageTick(World world, LivingEntity livingEntity, int i) {
            if (!this.isContainedUsable || this.isContainerEmpty()) this.current.usageTick(world, livingEntity, i);
            else this.containerItem.getContainedStack(this.current).usageTick(world, livingEntity, i);
        }

        @Override
        public boolean isFood() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.isFood();
            return this.containerItem.getContainedStack(this.current).isFood();
        }

        @Override
        public SoundEvent getDrinkSound() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getDrinkSound();
            return this.containerItem.getContainedStack(this.current).getDrinkSound();
        }

        @Override
        public SoundEvent getEatSound() {
            if (!this.isContainedUsable || this.isContainerEmpty()) return this.current.getEatSound();
            return this.containerItem.getContainedStack(this.current).getEatSound();
        }
    }
}
