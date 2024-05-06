package mc.recraftors.blahaj.item;

import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.PreLaunchUtils;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;

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
            if (stored == ItemStack.EMPTY || !slot.canInsert(stored)) {
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
            if (ItemStack.canCombine(stored, target)) {
                mergeStored(itemStack, playerEntity, target, stored);
                return true;
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
            playInsertSound(player);
            return true;
        }
        ItemStack stored = ItemStack.fromNbt(nbt);
        if (!ItemStack.canCombine(stored, target)) {
            return super.onClicked(itemStack, target, slot, type, player, reference);
        }
        mergeStored(itemStack, player, target, stored);
        return true;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack itemStack) {
        ItemStack stored = getStoredStack(itemStack);
        if (stored == ItemStack.EMPTY) {
            return Optional.empty();
        }
        return Optional.of(new CuddlyContainerTooltipData(stored));
    }

    @Override
    public boolean canBeNested() {
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack itemStack) {
        return super.hasGlint(((ItemStackProvider) itemStack).blahaj$getStack());
    }

    public ItemStack getContainedStack(ItemStack stack) {
        return getStoredStack(stack);
    }

    public TagKey<Item> usableContainedItemTag() {
        return Blahaj.BLAVINGAD_USABLE_ITEMS;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Optional<ItemStack> extract(ItemStack stack) {
        stack = ((ItemStackProvider) stack).blahaj$getStack();
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            return Optional.empty();
        }
        nbt = stack.getSubNbt(STORED_ITEM_KEY);
        if (nbt == null) {
            return Optional.empty();
        }
        stack.removeSubNbt(STORED_ITEM_KEY);
        return Optional.ofNullable(ItemStack.fromNbt(nbt));
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean setContent(ItemStack itemStack, ItemStack target) {
        storeItemStack(itemStack, target, target.getCount());
        return true;
    }

    protected void mergeStored(ItemStack itemStack, PlayerEntity playerEntity, ItemStack target, ItemStack stored) {
        int acc = target.getCount();
        int in = stored.getCount();
        int t = acc + in;
        int m = stored.getMaxCount();
        if (t >= m) {
            storeItemStack(itemStack, stored, m);
            target.setCount(acc - (m - in));
        } else {
            storeItemStack(itemStack, stored, t);
            target.setCount(0);
        }
        playInsertSound(playerEntity);
    }

    public static ItemStack getStoredStack(ItemStack stack) {
        stack = ((ItemStackProvider) stack).blahaj$getStack();
        NbtCompound nbt = stack.getSubNbt(STORED_ITEM_KEY);
        if (nbt == null) {
            return ItemStack.EMPTY;
        }
        return ItemStack.fromNbt(nbt);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canHold(ItemStack stack) {
        stack = ((ItemStackProvider) stack).blahaj$getStack();
        return !stack.isEmpty() && stack.getItem().canBeNested() && !stack.isIn(Blahaj.NON_CONTAINABLE_ITEMS);
    }

    public static void storeItemStack(ItemStack container, ItemStack target, int q) {
        container = ((ItemStackProvider) container).blahaj$getStack();
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

    protected void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    protected void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

}
