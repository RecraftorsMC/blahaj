package mc.recraftors.blahaj.mixin.render;

import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.item.CuddlyItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin <T extends LivingEntity> {
    @Shadow @Final public ModelPart rightArm;

    @Shadow @Final public ModelPart leftArm;

    @Inject(
        method = {"positionRightArm", "positionLeftArm"},
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/model/CrossbowPosing;hold(Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;Z)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void positionArmsInjector(LivingEntity entity, CallbackInfo ci) {
        boolean b1 = entity.getMainHandStack().getItem() instanceof CuddlyItem && entity.getOffHandStack().isEmpty();
        boolean b2 = entity.getMainHandStack().isEmpty() && entity.getOffHandStack().getItem() instanceof CuddlyItem;
        if (b1 || b2) {
            this.rightArm.pitch = -0.95F;
            this.rightArm.yaw = (float) (-Math.PI / 8);
            this.leftArm.pitch = -0.90F;
            this.leftArm.yaw = (float) (Math.PI / 8);
            ci.cancel();
        }
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("RETURN"))
    private void sleepCuddleAngleInjector(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (!livingEntity.isSleeping()) return;
        blahaj$setCuddleArmsPos(livingEntity);
    }

    @Unique
    private void blahaj$setCuddleArmsPos(T entity) {
        if (!Blahaj.holdsOnlyCuddlyItem(entity)) {
            return;
        }
        Vector3f right = new Vector3f(-.1f, -.02f, 0);
        Vector3f left = new Vector3f(-.1f, -.08f, 0);
        this.rightArm.rotate(right);
        this.leftArm.rotate(left);
    }
}
