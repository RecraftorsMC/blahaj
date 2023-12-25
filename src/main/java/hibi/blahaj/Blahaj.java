package hibi.blahaj;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import java.util.*;

public class Blahaj implements ModInitializer {
    public static final Identifier BLAHAJ_ID;
    public static final Identifier KLAPPAR_HAJ_ID;
    public static final Identifier BREAD_ID;

    public static final String MOD_ID;
    public static final String TOOLTIP_PRE;
    private static final Map<Identifier, CuddlyItem> ITEM_MAP;

    static {
        MOD_ID = "blahaj";
        TOOLTIP_PRE = String.format("item.%s.%%s.tooltip",MOD_ID);
        BLAHAJ_ID = new Identifier(MOD_ID, "blue_shark");
        KLAPPAR_HAJ_ID = new Identifier(MOD_ID, "gray_shark");
        BREAD_ID = new Identifier(MOD_ID, "bread");

        ITEM_MAP = new HashMap<>();
    }

    @Override
    public void onInitialize() {
        injectTrades();
    }

    public static void storeItem(Identifier id, CuddlyItem item) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(item);
        ITEM_MAP.put(id, item);
    }

    public static Collection<CuddlyItem> getItems() {
        return ITEM_MAP.values();
    }

    public static Optional<CuddlyItem> itemSupplier(Identifier id) {
        return Optional.ofNullable(ITEM_MAP.get(id));
    }

    public static void injectTrades() {
        Optional<CuddlyItem> klapparSupplier = itemSupplier(KLAPPAR_HAJ_ID);
        if (klapparSupplier.isEmpty()) return;
        TradeOffers.PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(VillagerProfession.SHEPHERD, key -> new Int2ObjectLinkedOpenHashMap<>()).compute(5, (i, v) -> {
            if (v == null) {
                v = new TradeOffers.Factory[1];
            } else {
                v = Arrays.copyOf(v, v.length+1);
            }
            v[v.length-1] = (entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 15), new ItemStack(klapparSupplier.get()), 2,30, 0.1f);
            return v;
        });
    }
}
