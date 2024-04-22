package mc.recraftors.blahaj.mixin.compat.travelersbackpack.present;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import mc.recraftors.blahaj.Blahaj;
import mc.recraftors.blahaj.compat.DataHolder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ModBlocks.class, remap = false)
public abstract class ModBlocksMixin {
    @Inject(method = "init", at = @At("TAIL"))
    private static void initInjector(CallbackInfo ci) {
        Map<String, Object> modMap = DataHolder.modMap(TravelersBackpack.MODID);
        modMap.put("block", Registry.register(Registries.BLOCK, new Identifier(Blahaj.MOD_ID, "blahaj_backpack"), new TravelersBackpackBlock(FabricBlockSettings.create().mapColor(MapColor.BLUE).sounds(BlockSoundGroup.WOOL))));
    }
}
