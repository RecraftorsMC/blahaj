package mc.recraftors.blahaj.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Inject(method = "getEquipment", at = @At("RETURN"))
    private void handItemsInjector(LivingEntity entity, CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        cir.getReturnValue().put(EquipmentSlot.MAINHAND, entity.getStackInHand(Hand.MAIN_HAND));
        cir.getReturnValue().put(EquipmentSlot.OFFHAND, entity.getStackInHand(Hand.OFF_HAND));
    }
}
