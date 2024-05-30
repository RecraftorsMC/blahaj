package mc.recraftors.blahaj;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import mc.recraftors.blahaj.item.CuddlyItem;
import mc.recraftors.unruled_api.UnruledApi;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Blahaj implements ModInitializer {
    public static final Identifier BLAHAJ_ID;
    public static final Identifier KLAPPAR_HAJ_ID;
    public static final Identifier BEYOU_BLAHAJ_ID;
    public static final Identifier BLAVINGAD_ID;
    public static final Identifier ORCA_HAJ_ID;
    public static final Identifier BREAD_ID;
    public static final Identifier SEAL_ID;
    public static final Identifier CUDDLY_ITEM_TAG_ID;
    public static final Identifier NON_CONTAINABLE_ITEMS_TAG_ID;
    public static final Identifier BLAVINGAD_USABLE_ITEMS_ID;
    public static final TagKey<Item> CUDDLY_ITEMS;
    public static final TagKey<Item> NON_CONTAINABLE_ITEMS;
    public static final TagKey<Item> BLAVINGAD_USABLE_ITEMS;

    public static final GameRules.Key<GameRules.BooleanRule> ENABLE_CONTAINER_USE;

    public static final String MOD_ID;
    public static final String TOOLTIP_PRE;
    private static final Map<Identifier, CuddlyItem> ITEM_MAP;

    static {
        MOD_ID = "blahaj";
        TOOLTIP_PRE = String.format("item.%s.%%s.tooltip",MOD_ID);
        BLAHAJ_ID = new Identifier(MOD_ID, "blue_shark");
        KLAPPAR_HAJ_ID = new Identifier(MOD_ID, "gray_shark");
        BEYOU_BLAHAJ_ID = new Identifier(MOD_ID, "trans_shark");
        BLAVINGAD_ID = new Identifier(MOD_ID, "blue_whale");
        ORCA_HAJ_ID = new Identifier(MOD_ID, "killer_whale");
        BREAD_ID = new Identifier(MOD_ID, "bread");
        SEAL_ID = new Identifier(MOD_ID, "seal");
        CUDDLY_ITEM_TAG_ID = new Identifier(MOD_ID, "cuddly_items");
        NON_CONTAINABLE_ITEMS_TAG_ID = new Identifier(MOD_ID, "not_containable");
        BLAVINGAD_USABLE_ITEMS_ID = new Identifier(MOD_ID, "blavingad_usable");
        CUDDLY_ITEMS = TagKey.of(Registry.ITEM_KEY, CUDDLY_ITEM_TAG_ID);
        NON_CONTAINABLE_ITEMS = TagKey.of(Registry.ITEM_KEY, NON_CONTAINABLE_ITEMS_TAG_ID);
        BLAVINGAD_USABLE_ITEMS = TagKey.of(Registry.ITEM_KEY, BLAVINGAD_USABLE_ITEMS_ID);

        ENABLE_CONTAINER_USE = UnruledApi.registerBoolean("blahaj.contained.enable_use", GameRules.Category.PLAYER, false);

        ITEM_MAP = new HashMap<>();
    }

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

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
        Optional<CuddlyItem> blahajSupplier = itemSupplier(BLAHAJ_ID);
        Optional<CuddlyItem> klapparSupplier = itemSupplier(KLAPPAR_HAJ_ID);
        Optional<CuddlyItem> beyouSupplier = itemSupplier(BEYOU_BLAHAJ_ID);
        klapparSupplier.ifPresent(
                klappar -> TradeOffers.PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(VillagerProfession.SHEPHERD,
                        key -> new Int2ObjectLinkedOpenHashMap<>()).compute(5, (i, v) -> {
                            if (v == null) {
                                v = new TradeOffers.Factory[1];
                            } else {
                                v = Arrays.copyOf(v, v.length +1);
                            }
                            v[v.length-1] = (entity, random) -> new TradeOffer(new ItemStack(Items.EMERALD, 15), new ItemStack(klappar), 2, 30, 0.1f);
                            return v;
                        })
        );
        beyouSupplier.ifPresent(beyou -> {
            if (blahajSupplier.isEmpty()) return;
            TradeOffers.PROFESSION_TO_LEVELED_TRADE.computeIfAbsent(VillagerProfession.LEATHERWORKER, key -> new Int2ObjectLinkedOpenHashMap<>()).compute(4, (i, v) -> {
                if (v == null) {
                    v = new TradeOffers.Factory[1];
                } else {
                    v = Arrays.copyOf(v, v.length+1);
                }
                v[v.length-1] = (entity, random) -> new TradeOffer(new ItemStack(blahajSupplier.get(), 1), new ItemStack(Items.EMERALD, 20), new ItemStack(beyou), 2, 20, 0.1f);
                return v;
            });
        });
    }

    public static boolean holdsOnlyCuddlyItem(LivingEntity entity) {
        return (isCuddly(entity.getMainHandStack()) && entity.getOffHandStack().isEmpty() ||
                isCuddly(entity.getOffHandStack()) && entity.getMainHandStack().isEmpty());
    }

    public static boolean isCuddly(ItemStack stack) {
        return stack.isIn(CUDDLY_ITEMS) || stack.getItem() instanceof CuddlyItem;
    }
}
