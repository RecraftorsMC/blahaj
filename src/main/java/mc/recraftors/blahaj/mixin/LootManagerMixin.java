package mc.recraftors.blahaj.mixin;

import com.google.gson.JsonObject;
import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.item.CuddlyItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(LootManager.class)
public abstract class LootManagerMixin {
    @Shadow private Map<Identifier, LootTable> tables;

    @Inject(method = "apply", at = @At("RETURN"))
    private void applyReturnInjector(Map<Identifier, JsonObject> jsonMap, ResourceManager resourceManager, Profiler profiler, CallbackInfo info) {
        Optional<CuddlyItem> klapparSupplier = Blahaj.itemSupplier(Blahaj.KLAPPAR_HAJ_ID);
        Optional<CuddlyItem> blahajSupplier = Blahaj.itemSupplier(Blahaj.BLAHAJ_ID);
        LootTable table;
        if (klapparSupplier.isPresent()) {
            CuddlyItem klappar = klapparSupplier.get();
            table = this.tables.get(LootTables.STRONGHOLD_CROSSING_CHEST);
            if (table != null) {
                blahaj$tableInsert(table, klappar, 5, 100);
            }
            table = this.tables.get(LootTables.STRONGHOLD_CORRIDOR_CHEST);
            if (table != null) {
                blahaj$tableInsert(table, klappar, 5, 100);
            }
            table = this.tables.get(LootTables.VILLAGE_PLAINS_CHEST);
            if (table != null) {
                blahaj$tableInsert(table, klappar, 1, 45);
            }
            table = this.tables.get(LootTables.VILLAGE_TAIGA_HOUSE_CHEST);
            if (table != null) {
                blahaj$tableInsert(table, klappar, 5, 60);
            }
        }
        if (blahajSupplier.isPresent()) {
            CuddlyItem blahaj = blahajSupplier.get();
            table = this.tables.get(LootTables.VILLAGE_SNOWY_HOUSE_CHEST);
            if (table != null) {
                blahaj$tableInsert(table, blahaj, 5, 60);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Unique
    private static void blahaj$tableInsert(LootTable table, Item item, int weight, int total) {
        int i = table.pools.length;
        LootPool[] pools = new LootPool[i+1];
        System.arraycopy(table.pools, 0, pools, 0, i);
        pools[i] = LootPool.builder().with(ItemEntry.builder(item).weight(weight)).with(ItemEntry.builder(Items.AIR).weight(total-weight)).build();
        ((Consumer<LootPool[]>) table).accept(pools);
    }
}
