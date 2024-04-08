package mc.recraftors.blahaj.item;

import com.google.common.collect.Multimap;
import mc.recraftors.blahaj.item.nbt.ContainedNbtCompound;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.item.TooltipContext;
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
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public final class ContainedItemStack extends ItemStack implements ItemStackProvider {
    private final ItemStack current;
    private final ItemStack parent;
    private final ItemContainerCuddlyItem containerItem;
    private ContainedNbtCompound nbt;

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
            float g = MathHelper.sin(entity.getPitch() * ((float) Math.PI / 180));
            float h = MathHelper.cos(entity.getPitch() * ((float) Math.PI / 180));
            float i = MathHelper.sin(entity.getYaw() * ((float) Math.PI / 180));
            float j = MathHelper.cos(entity.getYaw() * ((float) Math.PI / 180));
            float k = random.nextFloat() * ((float) Math.PI * 2);
            float l = 0.02f * random.nextFloat();
            float f = random.nextFloat();
            itemEntity.setVelocity((-i * h * 0.3f) + Math.cos(k) * l, -g * 0.3f + 0.1f + (random.nextFloat() - f) * 0.1f, (j * h * 0.3f) + Math.sin(k) * l);
            entity.getWorld().spawnEntity(itemEntity);
        }
    }

    void updateContainedNbt(NbtCompound compound) {
        this.current.setNbt(compound);
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
        return new TypedActionResult<>(result.getResult(), this.parent);
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
    @SuppressWarnings("unchecked")
    public NbtCompound getNbt() {
        if (this.nbt == null) {
            NbtCompound compound = this.current.getNbt();
            if (compound == null) {
                return null;
            }
            this.nbt = new ContainedNbtCompound(compound, e -> this.updateContainedNbt(compound));
        }
        return this.nbt;
    }

    @Override
    public void setSubNbt(String string, NbtElement nbtElement) {
        this.current.setSubNbt(string, nbtElement);
        this.dirty();
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
