package mc.recraftors.blahaj.mixin.compat.travelersbackpack.and.trinkets;

import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackFeature;
import mc.recraftors.blahaj.compat.TrinketPlushRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(TravelersBackpackFeature.class)
public class TravelersBackpackFeatureMixin {
    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lcom/tiviacz/travelersbackpack/component/ComponentUtils;isWearingBackpack(Lnet/minecraft/entity/player/PlayerEntity;)Z", shift = At.Shift.AFTER),
            cancellable = true)
    private void onRenderHeadInjector(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                                      AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta,
                                      float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (TrinketPlushRenderer.hasCuddlyInSlot(entity, "chest", "back", "cape")) ci.cancel();
    }
}
