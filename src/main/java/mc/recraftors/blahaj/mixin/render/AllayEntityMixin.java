package mc.recraftors.blahaj.mixin.render;

import mc.recraftors.blahaj.CuddlyItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AllayEntity.class)
public abstract class AllayEntityMixin {

    @Inject(
        method = "interactMob",
        at = @At("HEAD"),
        cancellable = true
    )
    private void interactWithPlushInjector(PlayerEntity player, Hand hand,
                                           CallbackInfoReturnable<ActionResult> cir) {
        if (player.getStackInHand(hand).getItem() instanceof CuddlyItem) {
            cir.setReturnValue(ActionResult.PASS);
            cir.cancel();
        }
    }
}
