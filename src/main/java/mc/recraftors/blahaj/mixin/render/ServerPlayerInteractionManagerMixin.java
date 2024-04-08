package mc.recraftors.blahaj.mixin.render;

import mc.recraftors.blahaj.item.ItemContainerCuddlyItem;
import mc.recraftors.blahaj.item.ItemStackProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @ModifyVariable(
            method = "interactItem",
            name = "itemStack2",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;setStackInHand(Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private ItemStack onInteractItemSetItemInHand(ItemStack value) {
        if (((ItemStackProvider)value).blahaj$getStack().getItem() instanceof ItemContainerCuddlyItem cuddly) {
            return cuddly.getContainedStack(value);
        }
        return value;
    }
}
