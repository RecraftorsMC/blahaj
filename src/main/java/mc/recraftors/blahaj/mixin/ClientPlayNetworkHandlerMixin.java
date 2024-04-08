package mc.recraftors.blahaj.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import mc.recraftors.blahaj.item.HandItemStackProvider;
import mc.recraftors.blahaj.item.ItemStackProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Redirect(
            method = "getActiveTotemOfUndying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"
            )
    )
    private static ItemStack getPossilbyContainerStackInHend(PlayerEntity player, Hand hand) {
        return ((HandItemStackProvider) player).blahaj$stackInHandFailSafe(hand);
    }

    @ModifyExpressionValue(
            method = "getActiveTotemOfUndying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
            )
    )
    private static boolean getIsOfOrContainedIsOf(boolean original, @Local ItemStack itemStack) {
        return original || ! ((ItemStackProvider)itemStack).blahaj$getStack().isOf(Items.TOTEM_OF_UNDYING);
    }
}
