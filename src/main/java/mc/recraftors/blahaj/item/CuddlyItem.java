package mc.recraftors.blahaj.item;

import mc.recraftors.blahaj.Blahaj;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        super.appendTooltip(stack, world, tooltip, context);
        if (this.subtitle != null) {
            tooltip.add(this.subtitle);
        }
        if (stack.hasNbt()) {
            Blahaj.getOwnerName(stack, world).ifPresent(name -> {
                if (name.isBlank()) return;
                if (stack.hasCustomName()) {
                    tooltip.add(Text.translatable(TOOL_KEY_RENAME, getName(), Text.of(name)));
                } else {
                    tooltip.add(Text.translatable(TOOL_KEY_OWNER, Text.of(name)));
                }
            });
        }
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        Blahaj.setOwner(stack, player);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return 0.25f;
    }
}
