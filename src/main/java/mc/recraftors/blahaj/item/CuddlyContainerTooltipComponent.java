package mc.recraftors.blahaj.item;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class CuddlyContainerTooltipComponent implements TooltipComponent {
    private final ItemStack stack;
    public CuddlyContainerTooltipComponent(CuddlyContainerTooltipData data) {
        this.stack = data.getStoredStack();
    }

    public boolean isHolding() {
        return this.stack != null;
    }

    @Override
    public int getHeight() {
        if (!isHolding()) return 0;
        return 26; // 20 + 2 + 4
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        if (this.isHolding()) return 20; // 18 + 2
        return 0;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int i, int j, MatrixStack matrixStack, ItemRenderer itemRenderer, int k) {
        this.drawSlot(i+1, j+1, textRenderer, matrixStack, itemRenderer, k);
        this.drawOutline(i, j, matrixStack, k);
    }

    // adapted from BundleItem
    protected void drawSlot(int i, int j, TextRenderer textRenderer, MatrixStack matrixStack, ItemRenderer itemRenderer, int l) {
        if (!this.isHolding()) return;
        ItemStack itemStack = this.stack;
        this.draw(matrixStack, i, j, l, Sprite.SLOT);
        itemRenderer.renderInGuiWithOverrides(itemStack, i + 1, j + 1, 0);
        itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, i + 1, j + 1);
        HandledScreen.drawSlotHighlight(matrixStack, i + 1, j + 1, l);
    }

    // adapted from BundleItem
    protected void drawOutline(int i, int j, MatrixStack matrixStack, int m) {
        this.draw(matrixStack, i, j, m, Sprite.BORDER_CORNER_TOP);
        this.draw(matrixStack, i + 19, j, m, Sprite.BORDER_CORNER_TOP);
        this.draw(matrixStack, i + 1, j, m, Sprite.BORDER_HORIZONTAL_TOP);
        this.draw(matrixStack, i + 1, j + 20, m, Sprite.BORDER_HORIZONTAL_BOTTOM);
        this.draw(matrixStack, i, j + + 1, m, Sprite.BORDER_VERTICAL);
        this.draw(matrixStack, i + 19, j + 1, m, Sprite.BORDER_VERTICAL);
        this.draw(matrixStack, i, j + 20, m, Sprite.BORDER_CORNER_BOTTOM);
        this.draw(matrixStack, i + 19, j + 20, m, Sprite.BORDER_CORNER_BOTTOM);
    }

    // adapted from BundleItem
    protected void draw(MatrixStack matrixStack, int i, int j, int k, Sprite sprite) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BundleTooltipComponent.TEXTURE);
        DrawableHelper.drawTexture(matrixStack, i, j, k, sprite.u, sprite.v, sprite.width, sprite.height, 128, 128);
    }

    // adapted from BundleItem
    @Environment(value= EnvType.CLIENT)
    public enum Sprite {
        SLOT(0, 0, 18, 20),
        BLOCKED_SLOT(0, 40, 18, 20),
        BORDER_VERTICAL(0, 18, 1, 20),
        BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
        BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
        BORDER_CORNER_TOP(0, 20, 1, 1),
        BORDER_CORNER_BOTTOM(0, 60, 1, 1);

        public final int u;
        public final int v;
        public final int width;
        public final int height;

        Sprite(int j, int k, int l, int m) {
            this.u = j;
            this.v = k;
            this.width = l;
            this.height = m;
        }
    }
}
