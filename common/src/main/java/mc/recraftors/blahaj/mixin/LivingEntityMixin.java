package mc.recraftors.blahaj.mixin;

import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.item.ContainedItemStack;
import mc.recraftors.blahaj.item.HandItemStackProvider;
import mc.recraftors.blahaj.item.ItemContainerCuddlyItem;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements HandItemStackProvider {
    LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract void setHealth(float f);

    @Shadow public abstract boolean clearStatusEffects();

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance statusEffectInstance);

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot var1);

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Override
    public ItemStack blahaj$stackInHandFailSafe(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            return this.getEquippedStack(EquipmentSlot.MAINHAND);
        }
        if (hand == Hand.OFF_HAND) {
            return this.getEquippedStack(EquipmentSlot.OFFHAND);
        }
        throw new IllegalArgumentException("Invalid hand " + hand);
    }

    @Inject(method = "getStackInHand", at = @At("RETURN"), cancellable = true)
    private void onGetStackInHandReturnInjector(Hand hand, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.getWorld().getGameRules().getBoolean(Blahaj.ENABLE_CONTAINER_USE)) return;
        ItemStack stack = cir.getReturnValue();
        if (stack.getItem() instanceof ItemContainerCuddlyItem cuddly) {
            ItemStack content = cuddly.getContainedStack(stack);
            if (content.isIn(cuddly.usableContainedItemTag())) {
                cir.setReturnValue(new ContainedItemStack(stack, content));
            }
        }
    }

    @Inject(method = "setStackInHand", at = @At("HEAD"), cancellable = true)
    private void onSetStackInHandHeadInjector(Hand hand, ItemStack itemStack, CallbackInfo ci) {
        ItemStack stack = getStackInHand(hand);
        if (stack instanceof ContainedItemStack containedStack) {
            ci.cancel();
            blahaj$idkSetStackInHand(this, containedStack, itemStack);
        }
    }

    @Unique
    private static void blahaj$idkSetStackInHand(Entity entity, ContainedItemStack stack1, ItemStack stack2) {
        if (!(entity instanceof PlayerEntity player)) return;
        if (player.isCreative()) return;
        stack1.tryInsertOrDrop(player, stack2);
    }

    @Redirect(method = "swapHandStacks", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack handSwapStackGetRedirector(LivingEntity instance, EquipmentSlot equipmentSlot) {
        return switch (equipmentSlot) {
            case MAINHAND -> blahaj$stackInHandFailSafe(Hand.MAIN_HAND);
            case OFFHAND -> blahaj$stackInHandFailSafe(Hand.OFF_HAND);
            default -> ItemStack.EMPTY;
        };
    }

    @SuppressWarnings("ConstantValue")
    @Inject(method = "tryUseTotem", at = @At("TAIL"), cancellable = true)
    private void tryUseTotemTryUseContainedTotemInjector(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) return;
        ItemStack stack = null;
        ItemStack stack2 = null;
        for (Hand hand : Hand.values()) {
            ItemStack handStack = blahaj$stackInHandFailSafe(hand);
            if (!(handStack.getItem() instanceof ItemContainerCuddlyItem cuddly)) continue;
            ItemStack contained = cuddly.getContainedStack(handStack);
            if (contained.isOf(Items.TOTEM_OF_UNDYING)) {
                stack = handStack;
                stack2 = contained;
                if (!(((Object)this) instanceof PlayerEntity player && player.isCreative())) {
                    cuddly.extract(handStack);
                }
                break;
            }
        }
        if (stack == null) return;
        Object o = this;
        if (o instanceof ServerPlayerEntity player) {
            player.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
            Criteria.USED_TOTEM.trigger(player, stack2);
        }
        this.setHealth(1f);
        this.clearStatusEffects();
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 700, 0));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 0));
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 500, 0));
        this.getWorld().sendEntityStatus(this, (byte) 35);
        cir.setReturnValue(true);
    }
}
