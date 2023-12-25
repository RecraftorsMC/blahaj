package hibi.blahaj.mixin;

import hibi.blahaj.CuddlyItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandlerMixin {
    @Inject(
        method = "updateResult",
        at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;removeCustomName()V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setCustomName(Lnet/minecraft/text/Text;)Lnet/minecraft/item/ItemStack;")
        },
        locals = LocalCapture.CAPTURE_FAILEXCEPTION,
        expect = 2,
        require = 2
    )
    private void setNameInjection(CallbackInfo ci, ItemStack itemStack, int i, int j, int k,
                                  ItemStack itemStack2, ItemStack itemStack3) {
        if (itemStack2.getItem() instanceof CuddlyItem) {
            itemStack2.setSubNbt(CuddlyItem.OWNER_KEY, NbtString.of(player.getName().getString()));
        }
    }
}
