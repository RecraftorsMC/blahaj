package mc.recraftors.blahaj.mixin.compat.trinkets.present;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.item.CuddlyItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(value = Blahaj.class, remap = false)
@Environment(EnvType.CLIENT)
public class BlahajMixin {
    @Shadow
    public static Collection<CuddlyItem> getItems() {
        return null;
    }

    @Inject(method = "onInitialize", at = @At("TAIL"))
    private void onInitializeTrinketCompatInjector(CallbackInfo callbackInfo) {
        getItems().forEach(item -> TrinketRendererRegistry.registerRenderer(item, (TrinketRenderer) item));
    }
}
