package mc.recraftors.blahaj.mixin.render;

import mc.recraftors.blahaj.Blahaj;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin <T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow public abstract M getModel();

    @Inject(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V",
                    ordinal = 0
            )
    )
    private void renderBipedSleepingCuddlingArms(
            T livingEntity, float f, float g, MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        blahaj$setArmsPos(livingEntity);
    }

    @Inject(
            method = "setupTransforms",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getSleepingDirection()Lnet/minecraft/util/math/Direction;",
                    shift = At.Shift.BEFORE
            )
    )
    private void transformBipedSleepingCuddlingArms(T livingEntity, MatrixStack matrixStack, float f,
                                                    float g, float h, CallbackInfo ci) {
        blahaj$setArmsPos(livingEntity);
    }

    @Unique
    private void blahaj$setArmsPos(T entity) {
        if (!(this.getModel() instanceof BipedEntityModel<?> model) || !Blahaj.holdsOnlyCuddlyItem(entity)) {
            return;
        }
        Vec3f right = new Vec3f(-30f, -15f, 0);
        Vec3f left = new Vec3f(30f, -15f, 0);
        model.rightArm.rotate(right);
        model.leftArm.rotate(left);
    }
}
