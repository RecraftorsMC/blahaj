package mc.recraftors.blahaj.mixin.compat.notenoughanimations.present;

import dev.tr7zw.notenoughanimations.access.PlayerData;
import dev.tr7zw.notenoughanimations.animations.vanilla.SleepAnimation;
import mc.recraftors.blahaj.Blahaj;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(SleepAnimation.class)
public abstract class SleepAnimationMixin {
    @Inject(method = "isValid", at = @At("HEAD"), cancellable = true)
    private void isValidSleepPlushieInjector(AbstractClientPlayerEntity entity, PlayerData data,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (Blahaj.holdsOnlyCuddlyItem(entity)) cir.setReturnValue(false);
    }
}
