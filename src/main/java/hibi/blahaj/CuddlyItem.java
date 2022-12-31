package hibi.blahaj;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CuddlyItem extends Item {
    public static final String OWNER_KEY = "owner";
    public static final String TOOL_KEY_RENAME = "tooltip.blahaj.owner.rename";
    public static final String TOOL_KEY_OWNER = "tooltip.blahaj.owner.craft";
    private final Text subtitle;

    public CuddlyItem(Settings settings, String subtitle) {
        super(settings);
        this.subtitle = subtitle == null ? null : Text.translatable(subtitle).formatted(Formatting.GRAY);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (this.subtitle != null) {
            tooltip.add(this.subtitle);
        }
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            String owner = nbt.getString(OWNER_KEY);
            if(owner.isBlank() || owner.isEmpty()) {
                return;
            }
            if (stack.hasCustomName()) {
                tooltip.add(Text.translatable(TOOL_KEY_RENAME, getName(), Text.of(owner)).formatted(Formatting.GRAY));
            } else {
                tooltip.add(Text.translatable(TOOL_KEY_OWNER, Text.of(owner)).formatted(Formatting.GRAY));
            }
        }
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        if (player != null) {
            stack.setSubNbt(OWNER_KEY, NbtString.of(player.getName().getString()));
        }
        super.onCraft(stack, world, player);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return 0.25f;
    }
}
