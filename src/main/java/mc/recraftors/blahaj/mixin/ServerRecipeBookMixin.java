package mc.recraftors.blahaj.mixin;

import mc.recraftors.blahaj.Blahaj;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerRecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {
    @ModifyVariable(method = "unlockRecipes", argsOnly = true, at = @At("HEAD"))
    private Collection<RecipeEntry<?>> glitchNoUnlock(Collection<RecipeEntry<?>> collection) {
        collection = new ArrayList<>(collection);
        collection.removeIf(r -> Blahaj.isHidden(r.id()));
        return collection;
    }
}
