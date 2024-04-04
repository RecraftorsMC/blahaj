package mc.recraftors.blahaj.mixin.render;

import mc.recraftors.blahaj.Blahaj;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends
        LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    PlayerEntityRendererMixin(EntityRendererFactory.Context context,
                              PlayerEntityModel<AbstractClientPlayerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(
        method = "getArmPose",
        at = @At("TAIL"),
        cancellable = true
    )
    private static void getCuddlePoseInjector(AbstractClientPlayerEntity player, Hand hand,
                                              CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (Blahaj.holdsOnlyCuddlyItem(player)) {
            cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
            cir.cancel();
        }
    }

    @Inject(method = "getArmPose", at = @At("RETURN"), cancellable = true)
    private static void getCuddlySleepArmPose(AbstractClientPlayerEntity player, Hand hand,
                                              CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        if (Blahaj.holdsOnlyCuddlyItem(player)) cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
    }
}
