package hibi.blahaj.mixin.compat.travelersbackpack.present;

import com.tiviacz.travelersbackpack.util.ResourceUtils;
import hibi.blahaj.Blahaj;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ResourceUtils.class, remap = false)
public abstract class ResourceUtilsMixin {
    @Shadow @Final public static List<Identifier> TEXTURE_IDENTIFIERS;

    @Inject(method = "createTextureLocations", at = @At("TAIL"))
    private static void createTextureLocationsTailInjector(CallbackInfo ci) {
        TEXTURE_IDENTIFIERS.add(new Identifier(Blahaj.MOD_ID, "textures/model/blahaj_backpack.png"));
    }
}
