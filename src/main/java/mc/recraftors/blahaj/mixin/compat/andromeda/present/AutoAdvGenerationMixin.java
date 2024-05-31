package mc.recraftors.blahaj.mixin.compat.andromeda.present;

import com.llamalad7.mixinextras.sugar.Local;
import mc.recraftors.blahaj.Blahaj;
import me.melontini.andromeda.modules.misc.recipe_advancements_generation.AdvancementGeneration;
import me.melontini.andromeda.modules.misc.recipe_advancements_generation.Main;
import net.minecraft.advancement.Advancement;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = Main.class, remap = false)
public class AutoAdvGenerationMixin {
    @Inject(
            method = "lambda$generateRecipeAdvancements$2",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    shift = At.Shift.AFTER
            )
    )
    private void undoPutDefaultGlitchRecipe(
            List<Recipe<?>> list, AdvancementGeneration.Config config, AtomicInteger count, Map<Identifier,
            Advancement.Builder> advancementBuilders, CallbackInfo ci, @Local Recipe<?> recipe) {
        if (Blahaj.isHidden(recipe.getId())) {
            advancementBuilders.remove(recipe.getId());
        }
    }
}
