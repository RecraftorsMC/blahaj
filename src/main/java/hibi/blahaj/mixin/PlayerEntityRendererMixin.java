package hibi.blahaj.mixin;

import hibi.blahaj.CuddlyItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(
        method = "getArmPose",
        at = @At("TAIL"),
        cancellable = true
    )
    private static void getCuddlePoseInjector(AbstractClientPlayerEntity player, Hand hand,
                                              CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        boolean b1 = player.getMainHandStack().getItem() instanceof CuddlyItem && player.getOffHandStack().isEmpty();
        boolean b2 = player.getMainHandStack().isEmpty() && player.getOffHandStack().getItem() instanceof CuddlyItem;
        if (b1 || b2) {
            cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
            cir.cancel();
        }
    }
}
