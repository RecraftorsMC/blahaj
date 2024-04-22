package mc.recraftors.blahaj.mixin;

import mc.recraftors.blahaj.Blahaj;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemGroups.class)
public abstract class ItemGroupsMixin {
    @Inject(method = "lambda$bootstrap$18", at = @At("RETURN"))
    private static void toolsGroupItemInject(ItemGroup.DisplayContext arg, ItemGroup.Entries entries, CallbackInfo ci) {
        Blahaj.getItems().forEach(entries::add);
    }
}
