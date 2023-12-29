package hibi.blahaj.mixin.compat.trinkets.present;

import dev.emi.trinkets.api.SlotReference;
import hibi.blahaj.CuddlyItem;
import hibi.blahaj.compat.TrinketPlushRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Environment(EnvType.CLIENT)
@Mixin(CuddlyItem.class)
public abstract class CuddlyItemMixin implements TrinketPlushRenderer {

    @Unique
    public void blahaj$trinkets$renderHead(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {
        Objects.requireNonNull(reference);
        String s = reference.inventory().getSlotType().getName();
        if (!"hat".equalsIgnoreCase(s) && !"crown".equalsIgnoreCase(s)) return;
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        float xFactor = (entity.isInSwimmingPose() || entity.isFallFlying()) ? -45 : headPitch;
        matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(model.head.roll));
        matrix.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(headYaw));
        matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(xFactor));
        if (!entity.isInSneakingPose()) {
            matrix.translate(0f, -.25f, 0f);
        }
        matrix.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
        matrix.scale(.75f, .75f, .75f);
        renderer.renderItem(entity, stack, ModelTransformation.Mode.HEAD, false, matrix, provider, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, 0);
    }

    @Unique
    public void blahaj$trinkets$renderChest(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {
        Objects.requireNonNull(reference);
        String s = reference.inventory().getSlotType().getName();
        switch (s) {
            case "back", "backpack", "cape" -> blahaj$trinkets$renderChest$back(stack, model, matrix, provider, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            case "shoulder", "necklace" -> blahaj$trinkets$renderChest$shoulder(stack, model, matrix, provider, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        }
    }

    @Unique
    private void blahaj$trinkets$renderChest$back(ItemStack stack, BipedEntityModel<? extends LivingEntity> model,
                                                  MatrixStack matrix, VertexConsumerProvider provider, int light,
                                                  LivingEntity entity, float limbAngle, float limbDistance,
                                                  float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        matrix.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
        matrix.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(90));
        matrix.scale(1.25f, 1.25f, 1.25f);
        // x (vertical, + = up), y (depth, + = back), z (sideways, + = right)
        matrix.translate(-.25f, .3f, .175f);
        renderer.renderItem(entity, stack, ModelTransformation.Mode.FIXED, false, matrix, provider, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, 0);
    }

    @Unique
    private void blahaj$trinkets$renderChest$shoulder(ItemStack stack, BipedEntityModel<? extends LivingEntity> model,
                                                  MatrixStack matrix, VertexConsumerProvider provider, int light,
                                                  LivingEntity entity, float limbAngle, float limbDistance,
                                                  float tickDelta, float animationProgress, float headYaw, float headPitch) {}

    @Unique
    public void blahaj$trinkets$renderLegs(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {}

    @Unique
    public void blahaj$trinkets$renderFeet(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {}

    @Unique
    public void blahak$trinkets$renderHand(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {}
}
