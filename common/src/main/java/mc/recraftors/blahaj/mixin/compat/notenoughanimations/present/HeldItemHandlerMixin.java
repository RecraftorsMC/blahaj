package mc.recraftors.blahaj.mixin.compat.notenoughanimations.present;

import dev.tr7zw.notenoughanimations.logic.HeldItemHandler;
import mc.recraftors.blahaj.Blahaj;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemHandler.class)
public abstract class HeldItemHandlerMixin {
    @Inject(
            method = "onRenderItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z",
                    shift = At.Shift.AFTER)
            ,
            cancellable = true)
    private void renderSleepingPlushyPredicate(LivingEntity entity, EntityModel<?> model, ItemStack itemStack, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info, CallbackInfo ci) {
        if (entity.isSleeping() && Blahaj.holdsOnlyCuddlyItem(entity)) {
            ci.cancel();
        }
    }
}
