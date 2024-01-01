package hibi.blahaj.mixin.compat.trinkets.present;

import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketsApi;
import hibi.blahaj.compat.BooleanConsumer;
import hibi.blahaj.compat.TrinketPlushRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

/**
 * Hides unsolicited cape if a cuddly item is in a back slot.
 * Works with {@link PlayerEntityModelMixin}
 */
@Environment(EnvType.CLIENT)
@Mixin(CapeFeatureRenderer.class)
public abstract class CapeFeatureRendererMixin extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    CapeFeatureRendererMixin(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void blahaj$trinket$onRenderCapeUpdate(MatrixStack stack, VertexConsumerProvider provider, int i,
                                                   AbstractClientPlayerEntity player, float f, float g, float h,
                                                   float j, float k, float l, CallbackInfo ci) {
        SlotGroup group = TrinketsApi.getPlayerSlots(player).get("chest");
        if (group == null) {
            return;
        }
        SlotType type = group.getSlots().get("back");
        if (type == null) {
            return;
        }
        boolean b = TrinketPlushRenderer.hasCuddlyInSlot(player, "chest", "back", "cape");
        ((BooleanConsumer) this.getContextModel()).blahaj$consume(b);
        if (b) {
            ci.cancel();
        }
    }
}
