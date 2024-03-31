<<<<<<<< HEAD:src/main/java/mc/recraftors/blahaj/mixin/render/TooltipComponentMixin.java
package mc.recraftors.blahaj.mixin.render;
========
package mc.recraftors.blahaj.mixin;
>>>>>>>> dc3ce3d (Class refactor):src/main/java/mc/recraftors/blahaj/mixin/TooltipComponentMixin.java

import mc.recraftors.blahaj.ItemContainerCuddlyItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {
    @Inject(method = "of(Lnet/minecraft/client/item/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At("HEAD"), cancellable = true)
    private static void ofComponentHeadInjector(TooltipData tooltipData, CallbackInfoReturnable<TooltipComponent> cir) {
        if (tooltipData instanceof ItemContainerCuddlyItem.CuddlyContainerTooltipData data) {
            cir.setReturnValue(new ItemContainerCuddlyItem.CuddlyContainerTooltipComponent(data));
        }
    }
}
