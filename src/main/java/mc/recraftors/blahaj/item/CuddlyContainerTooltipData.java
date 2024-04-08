package mc.recraftors.blahaj.item;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

public class CuddlyContainerTooltipData implements TooltipData {
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
