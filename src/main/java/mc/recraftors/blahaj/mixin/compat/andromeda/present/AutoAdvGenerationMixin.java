package mc.recraftors.blahaj.mixin.compat.andromeda.present;

import com.llamalad7.mixinextras.sugar.Local;
import mc.recraftors.blahaj.Blahaj;
import me.melontini.andromeda.modules.misc.recipe_advancements_generation.Helper;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = Helper.class, remap = false)
public class AutoAdvGenerationMixin {
    @Redirect(
            method = "lambda$generateRecipeAdvancements$4",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;contains(Ljava/lang/Object;)Z",
                    ordinal = 1
            )
    )
    private static boolean blacklistGlitchRedirector(List<?> instance, Object o, @Local Recipe<?> recipe) {
        if (recipe.getId().equals(Blahaj.$k_O$8)) return true;
        return instance.contains(o);
    }
}
