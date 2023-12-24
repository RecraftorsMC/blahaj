package hibi.blahaj;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;

import java.util.Optional;

public class ItemContainerCuddlyItem extends CuddlyItem {
    public static final String STORED_ITEM_KEY = "Item";

    public ItemContainerCuddlyItem(Settings settings, String subtitle) {
        super(settings, subtitle);
    }

    @Override
    public boolean onStackClicked(ItemStack itemStack, Slot slot, ClickType clickType, PlayerEntity playerEntity) {
        if (clickType != ClickType.RIGHT) return false;
        ItemStack target = slot.getStack();
        NbtCompound nbt = itemStack.getSubNbt(STORED_ITEM_KEY);
        if (target.isEmpty()) {
            if (nbt == null) return super.onStackClicked(itemStack, slot, clickType, playerEntity);
            ItemStack stored = getStoredStack(itemStack);
            if (stored == null || !slot.canInsert(stored)) {
                return super.onStackClicked(itemStack, slot, clickType, playerEntity);
            }
            ItemStack e = slot.insertStack(stored);
            storeItemStack(itemStack, e, e.getCount());
            this.playRemoveOneSound(playerEntity);
            return true;
        }
        if (nbt == null) {
            if (!canHold(target)) return super.onStackClicked(itemStack, slot, clickType, playerEntity);
            storeItemStack(itemStack, target, target.getCount());
            playInsertSound(playerEntity);
            return true;
        } else {
            ItemStack stored = ItemStack.fromNbt(nbt);
            int count = stored.getCount();
            if (canMergeItems(target, stored)) {
                int m = stored.getMaxCount();
                if (count < m) {
                    int v = target.getCount();
                    while (count + v > m) v -= m;
                    if (v < 0) return super.onStackClicked(itemStack, slot, clickType, playerEntity);
                    stored.setCount(count+v);
                    storeItemStack(itemStack, stored, count-v);
                    playInsertSound(playerEntity);
                    return true;
                } else return super.onStackClicked(itemStack, slot, clickType, playerEntity);
            }
        }
        return super.onStackClicked(itemStack, slot, clickType, playerEntity);
    }

    @Override
    public boolean onClicked(ItemStack itemStack, ItemStack target, Slot slot, ClickType type, PlayerEntity player, StackReference reference) {
        if (type != ClickType.RIGHT ||! slot.canTakePartial(player)) {
            return super.onClicked(itemStack, target, slot, type, player, reference);
        }
        NbtCompound nbt = itemStack.getSubNbt(STORED_ITEM_KEY);
        if (target.isEmpty()) {
            ItemStack stored = getStoredStack(itemStack);
            if (nbt == null) return super.onClicked(itemStack, target, slot, type, player, reference);
            if (reference.set(stored)) {
                storeItemStack(itemStack, null, 0);
                playRemoveOneSound(player);
                return true;
            }
            return super.onClicked(itemStack, target, slot, type, player, reference);
        }
        if (nbt == null) {
            if (!canHold(target)) return super.onClicked(itemStack, target, slot, type, player, reference);
            int amount = Math.min(target.getCount(), target.getItem().getMaxCount());
            storeItemStack(itemStack, target, amount);
            return true;
        }
        ItemStack stored = ItemStack.fromNbt(nbt);
        if (!stored.isEmpty() && slot.canInsert(stored)) {
            slot.insertStack(stored);
            storeItemStack(itemStack, null, 0);
            return true;
        }
        if (!canMergeItems(stored, target)) {
            return super.onClicked(itemStack, target, slot, type, player, reference);
        }
        int acc = target.getCount();
        int in = stored.getCount();
        int t = acc + in;
        int m = stored.getMaxCount();
        if (t > m) {
            storeItemStack(itemStack, stored, m);
            target.setCount(acc - (m - in));
        } else {
            storeItemStack(itemStack, stored, t);
            target.setCount(0);
        }
        return true;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack itemStack) {
        ItemStack stored = getStoredStack(itemStack);
        if (stored == null) {
            return Optional.empty();
        }
        return Optional.of(new CuddlyContainerTooltipData(stored));
    }

    protected static ItemStack getStoredStack(ItemStack stack) {
        NbtCompound nbt = stack.getSubNbt(STORED_ITEM_KEY);
        if (nbt == null) {
            return null;
        }
        return ItemStack.fromNbt(nbt);
    }

    protected boolean canHold(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem().canBeNested() && !stack.isIn(Blahaj.NON_CONTAINABLE_ITEMS);
    }

    protected static void storeItemStack(ItemStack container, ItemStack target, int q) {
        NbtCompound nbt = container.getOrCreateNbt();
        if (target == null || target.isEmpty() || q <= 0) {
            nbt.remove(STORED_ITEM_KEY);
        } else {
            ItemStack e = target.copy();
            e.setCount(q);
            nbt.put(STORED_ITEM_KEY, e.writeNbt(new NbtCompound()));
            target.setCount(target.getCount()-q);
        }
    }

    public static boolean canMergeItems(ItemStack itemStack, ItemStack itemStack2) {
        if (!itemStack.isOf(itemStack2.getItem())) {
            return false;
        } else if (itemStack.getDamage() != itemStack2.getDamage()) {
            return false;
        } else if (itemStack.getCount() > itemStack.getMaxCount()) {
            return false;
        } else {
            return ItemStack.areNbtEqual(itemStack, itemStack2);
        }
    }

    protected void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    protected void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    public static class CuddlyContainerTooltipData implements TooltipData {
        private final ItemStack storedStack;

        public CuddlyContainerTooltipData(ItemStack stack) {
            if (stack == null || stack.isEmpty()) {
                this.storedStack = null;
            } else {
                this.storedStack = stack;
            }
        }

        public ItemStack getStoredStack() {
            return storedStack;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class CuddlyContainerTooltipComponent implements TooltipComponent {
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
}
