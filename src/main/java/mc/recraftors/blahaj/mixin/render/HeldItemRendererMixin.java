package mc.recraftors.blahaj.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import mc.recraftors.blahaj.Blahaj;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @ModifyExpressionValue(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isInvisible()Z"))
    private boolean dontRenderEmptyMainHandWhenCuddle(boolean original, @Local(argsOnly = true) AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return original || Blahaj.holdsOnlyCuddlyItem(abstractClientPlayerEntity);
    }
}
