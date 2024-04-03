package mc.recraftors.blahaj.mixin;

import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.CuddlyItem;
import mc.recraftors.blahaj.ItemContainerCuddlyItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mc.recraftors.blahaj.Blahaj.*;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @Shadow
    private static Item register(Identifier identifier, Item item) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clinitTailInjector(CallbackInfo ci) {
        Item.Settings settings = new Item.Settings().maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE);

        Blahaj.storeItem(KLAPPAR_HAJ_ID, (CuddlyItem) register(KLAPPAR_HAJ_ID, new CuddlyItem(settings, String.format(TOOLTIP_PRE, KLAPPAR_HAJ_ID.getPath()))));
        Blahaj.storeItem(BLAHAJ_ID, (CuddlyItem) register(BLAHAJ_ID, new CuddlyItem(settings, String.format(TOOLTIP_PRE, BLAHAJ_ID.getPath()))));
        Blahaj.storeItem(BEYOU_BLAHAJ_ID, (CuddlyItem) register(BEYOU_BLAHAJ_ID, new CuddlyItem(settings, String.format(TOOLTIP_PRE, BEYOU_BLAHAJ_ID.getPath()))));
        Blahaj.storeItem(BLAVINGAD_ID, (CuddlyItem) register(BLAVINGAD_ID, new ItemContainerCuddlyItem(settings, String.format(TOOLTIP_PRE, BLAVINGAD_ID.getPath()))));
        Blahaj.storeItem(BREAD_ID, (CuddlyItem) register(BREAD_ID,new CuddlyItem(settings, null)));
    }
}
