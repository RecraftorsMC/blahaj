package mc.recraftors.blahaj;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@FunctionalInterface
public interface HandItemStackProvider {
    ItemStack blahaj$stackInHandFailSafe(Hand hand);
}
