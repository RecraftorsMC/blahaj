package hibi.blahaj.mixin.compat.travelersbackpack.present;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import hibi.blahaj.compat.DataHolder;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Arrays;

@Mixin(ModBlockEntityTypes.class)
public abstract class ModBlockEntityTypesMixin {
    @ModifyArgs(method = "init", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder;create(Lnet/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder$Factory;[Lnet/minecraft/block/Block;)Lnet/fabricmc/fabric/api/object/builder/v1/block/entity/FabricBlockEntityTypeBuilder;"))
    private static void initEntityTypeBuilderCreateArgsModifier(Args args) {
        Block[] blocks = args.get(1);
        blocks = Arrays.copyOf(blocks, blocks.length+1);
        blocks[blocks.length-1] = (Block) DataHolder.modMap(TravelersBackpack.MODID).get("block");
        args.set(1, blocks);
    }
}
