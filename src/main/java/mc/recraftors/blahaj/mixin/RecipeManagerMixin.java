package mc.recraftors.blahaj.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mc.recraftors.blahaj.Blahaj;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {

    @Unique private static JsonObject randomBlahajGlitchRecipe() {
        JsonObject main = new JsonObject();
        Identifier type = Registry.RECIPE_TYPE.getId(RecipeType.CRAFTING);
        if (type == null) {
            return null;
        }
        main.addProperty("type", type +"_shaped");
        JsonArray pattern = new JsonArray();
        pattern.add(" A ");
        pattern.add("B#C");
        pattern.add("DE ");
        main.add("pattern", pattern);
        Map<String, Item> map = new HashMap<>();
        for (int i = 'A'; i < 'F'; i++) {
            String key = Character.toString(i);
            boolean b = true;
            while (b) {
                Item item = Blahaj.randomItem(ItemTags.WOOL);
                if (item == null) {
                    return null;
                }
                b = map.containsValue(item);
                if (b) continue;
                map.put(key, item);
            }
        }
        JsonObject key = new JsonObject();
        map.forEach((k, i) -> {
            JsonObject o = new JsonObject();
            Identifier id = Registry.ITEM.getId(i);
            Blahaj.LOGGER.warn("## {} -> {}", k, id);
            o.addProperty("item", id.toString());
            key.add(k, o);
        });
        JsonObject k = new JsonObject();
        k.addProperty("item", Blahaj.BLAHAJ_ID.toString());
        key.add("#", k);
        main.add("key", key);
        JsonObject result = new JsonObject();
        result.addProperty("item", Blahaj.$k_O$8.toString());
        main.add("result", result);
        return main;
    }

    @Inject(
            method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At("HEAD")
    )
    private void postRecipeParseAddSpecial(Map<Identifier, JsonElement> map, ResourceManager resourceManager,
                                           Profiler profiler, CallbackInfo ci) {
        if (!map.containsKey(Blahaj.$k_O$8)) {
            JsonObject object = randomBlahajGlitchRecipe();
            if (object == null) {
                return;
            }
            map.put(Blahaj.$k_O$8, object);
        }
    }
}
