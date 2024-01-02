package hibi.blahaj.mixin;

import hibi.blahaj.ItemContainerCuddlyItem;
import hibi.blahaj.ItemStackProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrowablePotionItem.class)
public abstract class ThrowablePotionItemMixin {

    @Redirect(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack getStackInHandContainerFailsafe(PlayerEntity instance, Hand hand) {
        ItemStack value = instance.getStackInHand(hand);
        if (((ItemStackProvider)value).blahaj$getStack().getItem() instanceof ItemContainerCuddlyItem cuddly) {
            value = cuddly.getContainedStack(value);
        }
        return value;
    }
}
