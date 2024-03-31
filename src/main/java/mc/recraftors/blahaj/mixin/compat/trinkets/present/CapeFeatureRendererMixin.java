package mc.recraftors.blahaj.mixin.compat.trinkets.present;

import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import mc.recraftors.blahaj.CuddlyItem;
import mc.recraftors.blahaj.compat.BooleanConsumer;
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
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            Map<String, TrinketInventory> chest = component.getInventory().get("chest");
            if (chest == null) return;
            Optional<TrinketInventory> back = Optional.ofNullable(chest.get("back"));
            if (back.isEmpty()) back = Optional.ofNullable(chest.get("cape"));
            back.ifPresent(inv -> {
                boolean b = false;
                for (int t = 0; t < inv.size(); t++) {
                    if (inv.getStack(t).getItem() instanceof CuddlyItem) {
                        b = true;
                        break;
                    }
                }
                ((BooleanConsumer) this.getContextModel()).blahaj$consume(b);
                if (b) {
                    ci.cancel();
                }
            });
        });
    }
}
