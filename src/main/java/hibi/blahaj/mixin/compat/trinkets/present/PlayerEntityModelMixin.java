package hibi.blahaj.mixin.compat.trinkets.present;

import hibi.blahaj.BooleanProvider;
import hibi.blahaj.compat.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

/**
 * Hides unsolicited cape if a cuddly item is in a back slot.
 * Works with {@link CapeFeatureRendererMixin}
 */
@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin <T extends LivingEntity> extends BipedEntityModel<T> implements BooleanConsumer, BooleanProvider {
    @Unique
    private boolean cloackLock = false;

    PlayerEntityModelMixin(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
        super(modelPart, function);
    }

    @Override
    public void blahaj$consume(boolean b) {
        this.cloackLock = b;
    }

    @Override
    public boolean blahaj$getBool() {
        return this.cloackLock;
    }

    @Inject(method = "renderCape", at = @At("HEAD"), cancellable = true)
    private void blahaj$trinket$cloackIsHiddenByPlush(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, CallbackInfo ci) {
        if (this.cloackLock) ci.cancel();
    }
}
