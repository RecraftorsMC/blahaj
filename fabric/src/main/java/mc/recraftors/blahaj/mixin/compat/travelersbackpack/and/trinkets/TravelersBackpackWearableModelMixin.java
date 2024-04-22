package mc.recraftors.blahaj.mixin.compat.travelersbackpack.and.trinkets;

import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import mc.recraftors.blahaj.compat.TrinketPlushRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(TravelersBackpackWearableModel.class)
public class TravelersBackpackWearableModelMixin {
    @Shadow @Final private LivingEntity livingEntity;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderHeadInjector(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                                      float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (TrinketPlushRenderer.hasCuddlyInSlot(this.livingEntity, "chest", "back", "cape")) ci.cancel();
    }
}
