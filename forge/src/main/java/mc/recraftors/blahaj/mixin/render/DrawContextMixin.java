package mc.recraftors.blahaj.mixin.render;

import mc.recraftors.blahaj.PreLaunchUtils;
import mc.recraftors.blahaj.item.CuddlyContainerTooltipComponent;
import mc.recraftors.blahaj.item.CuddlyContainerTooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.text.Text;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mixin(DrawContext.class)
public abstract class DrawContextMixin {
    @Inject(
            method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private void blahaj$containedItemTooltipDrawInjector(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, CallbackInfo ci, List<TooltipComponent> list) {
        if (PreLaunchUtils.isForge()) return;
        data.ifPresent(e -> {
            if (e instanceof CuddlyContainerTooltipData d) {
                list.add(1, new CuddlyContainerTooltipComponent(d));
            }
        });
    }
}
