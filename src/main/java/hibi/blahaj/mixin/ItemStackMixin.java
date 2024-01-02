package hibi.blahaj.mixin;

import hibi.blahaj.ItemStackProvider;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackProvider {
    @Override
    public ItemStack blahaj$getStack() {
        return (ItemStack) ((Object) this);
    }
}
