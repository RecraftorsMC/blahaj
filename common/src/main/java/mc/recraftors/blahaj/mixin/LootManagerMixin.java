package mc.recraftors.blahaj.mixin;

import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.item.CuddlyItem;
import net.minecraft.loot.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(LootManager.class)
public abstract class LootManagerMixin {
    @Shadow private Map<LootDataKey<?>, ?> keyToValue;

    @Inject(method = "validate(Ljava/util/Map;)V", at = @At("RETURN"))
    private void applyReturnInjector(Map<LootDataType<?>, Map<Identifier, ?>> map, CallbackInfo ci) {
        Optional<CuddlyItem> klapparSupplier = Blahaj.itemSupplier(Blahaj.KLAPPAR_HAJ_ID);
        Optional<CuddlyItem> blahajSupplier = Blahaj.itemSupplier(Blahaj.BLAHAJ_ID);
        this.keyToValue.forEach((key, o) -> {
            if (!(o instanceof LootTable table)) return;
            if (key.id().equals(LootTables.STRONGHOLD_CROSSING_CHEST) || key.id().equals(LootTables.STRONGHOLD_CORRIDOR_CHEST)) {
                klapparSupplier.ifPresent(klappar -> Blahaj.injectTable(table, klappar, 5, 100));
            } else if (key.id().equals(LootTables.VILLAGE_PLAINS_CHEST)) {
                klapparSupplier.ifPresent(klappar -> Blahaj.injectTable(table, klappar, 1, 45));
            } else if (key.id().equals(LootTables.VILLAGE_TAIGA_HOUSE_CHEST)) {
                klapparSupplier.ifPresent(klappar -> Blahaj.injectTable(table, klappar, 5, 60));
            } else if (key.id().equals(LootTables.VILLAGE_SNOWY_HOUSE_CHEST)) {
                blahajSupplier.ifPresent(blahaj -> Blahaj.injectTable(table, blahaj, 5, 60));
            }
        });
    }
}
