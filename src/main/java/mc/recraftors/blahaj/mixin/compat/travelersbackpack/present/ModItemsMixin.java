package mc.recraftors.blahaj.mixin.compat.travelersbackpack.present;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.compat.DataHolder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ModItems.class, remap = false)
public abstract class ModItemsMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private static void initTailInjector(CallbackInfo ci) {
        Map<String, Object> modMap = DataHolder.modMap(TravelersBackpack.MODID);
        TravelersBackpackItem blahajBackpack = new TravelersBackpackItem((Block) modMap.get("block"),
                new Identifier(Blahaj.MOD_ID, "textures/model/blahaj_backpack.png"));
        Registry.register(Registries.ITEM, new Identifier(Blahaj.MOD_ID, "blahaj_backpack"), (Item) blahajBackpack);
    }
}
