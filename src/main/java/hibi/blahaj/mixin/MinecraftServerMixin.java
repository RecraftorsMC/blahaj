package hibi.blahaj.mixin;

import hibi.blahaj.Blahaj;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "reloadResources", at = @At("TAIL"))
    private void reloadResourcesTailInjector(Collection<String> dataPacks, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        Blahaj.injectTrades();
    }
}
