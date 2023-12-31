package hibi.blahaj.mixin;

import hibi.blahaj.ItemContainerCuddlyItem;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Shadow public abstract void setHealth(float f);

    @Shadow public abstract boolean clearStatusEffects();

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance statusEffectInstance);

    @Inject(method = "tryUseTotem", at = @At("TAIL"), cancellable = true)
    private void tryUseTotemTryUseContainedTotemInjector(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) return;
        ItemStack stack = null;
        ItemStack stack2 = null;
        for (Hand hand : Hand.values()) {
            ItemStack handStack = getStackInHand(hand);
            if (!(handStack.getItem() instanceof ItemContainerCuddlyItem cuddly)) continue;
            ItemStack contained = cuddly.getContainedStack(handStack);
            if (contained.isOf(Items.TOTEM_OF_UNDYING)) {
                stack = handStack;
                stack2 = contained;
                cuddly.extract(handStack);
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
