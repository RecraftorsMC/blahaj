package mc.recraftors.blahaj.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CuddlyContainerTooltipComponent implements TooltipComponent {
    public static final Identifier SPRITE_TEXTURE = new Identifier("container/bundle/slot");

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
    public void drawItems(TextRenderer textRenderer, int i, int j, DrawContext drawContext) {
        this.drawSlot(i+1, j+1, drawContext, textRenderer);
        this.drawOutline(i, j, drawContext);
    }

    // adapted from BundleItem
    protected void drawSlot(int i, int j, DrawContext drawContext, TextRenderer textRenderer) {
        if (!this.isHolding()) return;
        ItemStack itemStack = this.stack;
        this.draw(drawContext, i, j, Sprite.SLOT);
        drawContext.drawItem(itemStack, i+1, j+1, 0);
        drawContext.drawItemInSlot(textRenderer, itemStack, i+1, j+1);
        HandledScreen.drawSlotHighlight(drawContext, i + 1, j + 1, 0);
    }

    // adapted from BundleItem
    protected void drawOutline(int i, int j, DrawContext drawContext) {
        this.draw(drawContext, i, j, Sprite.BORDER_CORNER_TOP);
        this.draw(drawContext, i + 19, j, Sprite.BORDER_CORNER_TOP);
        this.draw(drawContext, i + 1, j, Sprite.BORDER_HORIZONTAL_TOP);
        this.draw(drawContext, i + 1, j + 20, Sprite.BORDER_HORIZONTAL_BOTTOM);
        this.draw(drawContext, i, j + 1, Sprite.BORDER_VERTICAL);
        this.draw(drawContext, i + 19, j + 1, Sprite.BORDER_VERTICAL);
        this.draw(drawContext, i, j + 20, Sprite.BORDER_CORNER_BOTTOM);
        this.draw(drawContext, i + 19, j + 20, Sprite.BORDER_CORNER_BOTTOM);
    }

    // adapted from BundleItem
    protected void draw(DrawContext drawContext, int i, int j, Sprite sprite) {
        drawContext.drawTexture(SPRITE_TEXTURE, i, j, 0, sprite.u, sprite.v, sprite.width, sprite.height, 128, 128);
    }

    // adapted from BundleItem
    @Environment(value= EnvType.CLIENT)
    public enum Sprite {
        SLOT(0, 0, 18, 20),
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
