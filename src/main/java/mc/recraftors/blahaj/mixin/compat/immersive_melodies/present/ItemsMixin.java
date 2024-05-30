package mc.recraftors.blahaj.mixin.compat.immersive_melodies.present;

import immersive_melodies.Items;
import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.compat.BlahajBassAnimator;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Items.class)
public interface ItemsMixin {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void blahajClinitInjector(CallbackInfo ci) {
        Items.register(Blahaj.MOD_ID, "blahaj_bass", new BlahajBassAnimator(), 200L, new Vec3f(0, .1f, .5f));
    }
}
