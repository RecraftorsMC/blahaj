package hibi.blahaj;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

public class Blahaj implements ModInitializer {
    public static final Identifier BLAHAJ_ID;
    public static final Identifier KLAPPAR_HAJ_ID;
    public static final Identifier BREAD_ID;
    public static final String MOD_ID;
    private static final String TOOLTIP_PRE;

    static {
        MOD_ID = "blahaj";
        TOOLTIP_PRE = String.format("item.%s.%%s.tooltip",MOD_ID);
        BLAHAJ_ID = new Identifier(MOD_ID, "blue_shark");
        KLAPPAR_HAJ_ID = new Identifier(MOD_ID, "gray_shark");
        BREAD_ID = new Identifier(MOD_ID, "bread");
    }

    @Override
    public void onInitialize() {
        Item.Settings settings = new Item.Settings().maxCount(1).rarity(Rarity.RARE);

        Item grayShark = new CuddlyItem(settings, String.format(TOOLTIP_PRE, KLAPPAR_HAJ_ID));
        Item blueShark = new CuddlyItem(settings, String.format(TOOLTIP_PRE, BLAHAJ_ID));
        Item bread = new CuddlyItem(settings, null);

        Registry.register(Registries.ITEM, KLAPPAR_HAJ_ID, grayShark);
        Registry.register(Registries.ITEM, BLAHAJ_ID, blueShark);
        Registry.register(Registries.ITEM, BREAD_ID, bread);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(c -> {
            c.add(blueShark);
            c.add(grayShark);
            c.add(bread);
        });

        LootTableEvents.MODIFY.register((resourceManager, lootManager, identifier, builder, lootTableSource) -> {
            if (!lootTableSource.isBuiltin()) {
                return;
            }
            if (identifier.equals(LootTables.STRONGHOLD_CROSSING_CHEST) ||
                    identifier.equals(LootTables.STRONGHOLD_CORRIDOR_CHEST)) {
                LootPool.Builder pb = LootPool.builder()
                        .with(ItemEntry.builder(grayShark).weight(5))
                        .with(ItemEntry.builder(Items.AIR).weight(100));
                builder.pool(pb);
            } else if(identifier.equals(LootTables.VILLAGE_PLAINS_CHEST)) {
                LootPool.Builder pb = LootPool.builder()
                        .with(ItemEntry.builder(grayShark))
                        .with(ItemEntry.builder(Items.AIR).weight(43));
                builder.pool(pb);
            } else if(identifier.equals(LootTables.VILLAGE_TAIGA_HOUSE_CHEST) ||
                    identifier.equals(LootTables.VILLAGE_SNOWY_HOUSE_CHEST)) {
                LootPool.Builder pb = LootPool.builder()
                        .with(ItemEntry.builder(grayShark).weight(5))
                        .with(ItemEntry.builder(Items.AIR).weight(54));
                builder.pool(pb);
            }
        });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.SHEPHERD, 5,
                factories -> factories.add((entity, random) -> new TradeOffer(
                        new ItemStack(Items.EMERALD, 15), new ItemStack(grayShark),
                        2, 30, 0.1f
        )));
    }
}
