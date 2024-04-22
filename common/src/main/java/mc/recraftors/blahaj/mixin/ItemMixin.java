package mc.recraftors.blahaj.mixin;

import mc.recraftors.blahaj.item.ItemStackProvider;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Item.class)
public abstract class ItemMixin {
    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/TypedActionResult;pass(Ljava/lang/Object;)Lnet/minecraft/util/TypedActionResult;"))
    private <T> T containedItemStackFailsafeInjector(T initial) {
        return (T) ((ItemStackProvider) initial).blahaj$getStack();
    }
}
