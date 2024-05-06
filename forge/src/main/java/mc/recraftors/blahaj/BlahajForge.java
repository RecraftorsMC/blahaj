package mc.recraftors.blahaj;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Blahaj.MOD_ID)
public class BlahajForge {
    public BlahajForge() {
        Blahaj.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            BlahajForgeClient.init();
        }
    }
}