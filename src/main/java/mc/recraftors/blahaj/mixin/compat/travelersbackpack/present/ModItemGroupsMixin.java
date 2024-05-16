package mc.recraftors.blahaj.mixin.compat.travelersbackpack.present;

import com.tiviacz.travelersbackpack.init.ModItemGroups;
import mc.recraftors.blahaj.Blahaj;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModItemGroups.class)
public abstract class ModItemGroupsMixin {
    @Inject(method = "lambda$addItemGroup$0", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/itemgroup/v1/FabricItemGroupEntries;add(Lnet/minecraft/item/ItemConvertible;)V", ordinal = 51, shift = At.Shift.AFTER))
    private static void registerEntriesInjector(FabricItemGroupEntries entries, CallbackInfo ci) {
        entries.add(Registries.ITEM.get(new Identifier(Blahaj.MOD_ID, "blahaj_backpack")));
    }
}
