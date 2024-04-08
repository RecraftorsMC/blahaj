package mc.recraftors.blahaj.mixin.compat.trinkets.present;

import dev.emi.trinkets.api.SlotReference;
import mc.recraftors.blahaj.item.CuddlyItem;
import mc.recraftors.blahaj.compat.TrinketPlushRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
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
        matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(model.head.roll));
        matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
        matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(xFactor));
        if (!entity.isInSneakingPose()) {
            matrix.translate(0f, -.25f, 0f);
        }
        matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        matrix.scale(.75f, .75f, .75f);
        renderer.renderItem(entity, stack, ModelTransformationMode.HEAD, false, matrix, provider, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, 0);
    }

    @Unique
    public void blahaj$trinkets$renderChest(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {
        Objects.requireNonNull(reference);
        String s = reference.inventory().getSlotType().getName();
        switch (s) {
            case "back", "backpack", "cape" -> blahaj$trinkets$renderChest$back(stack, matrix, provider, light, entity);
            case "shoulder", "necklace" -> blahaj$trinkets$renderChest$shoulder(stack, matrix, provider, light, entity);
        }
    }

    @Unique
    private void blahaj$trinkets$renderChest$back(ItemStack stack, MatrixStack matrix, VertexConsumerProvider provider,
                                                  int light, LivingEntity entity) {
        Objects.requireNonNull(matrix);
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrix.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90));
        matrix.scale(1.25f, 1.25f, 1.25f);
        // x (vertical, + = up), y (depth, + = back), z (sideways, + = right)
        matrix.translate(-0.25f, 0.3f, .175f);
        renderer.renderItem(entity, stack, ModelTransformationMode.FIXED, false, matrix, provider, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, 0);
    }

    @Unique
    private void blahaj$trinkets$renderChest$shoulder(ItemStack stack, MatrixStack matrix,
                                                      VertexConsumerProvider provider, int light, LivingEntity entity) {
        Objects.requireNonNull(matrix);
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        boolean b = false; // false = render on left shoulder
        if (entity instanceof PlayerEntity player) {
            NbtCompound left = player.getShoulderEntityLeft();
            NbtCompound right = player.getShoulderEntityRight();
            if (!left.isEmpty() && !right.isEmpty()) return;
            if (player.getMainArm() == Arm.LEFT) b = right.isEmpty();
            else b = !left.isEmpty();
        }
        matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
        float side = b ? .465f : -.275f;
        // x (depth, + = front), y (vertical, + = up), z (side, + = right)
        matrix.translate(0f, 0.115f, side);
        matrix.scale(0.5f, 0.5f, 0.5f);
        if (entity.isInSneakingPose()) {
            matrix.translate(0f, -0.25f, 0f);
            matrix.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(35));
        }
        renderer.renderItem(entity, stack, ModelTransformationMode.FIXED, false, matrix, provider, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, 0);
    }

    @Unique
    public void blahaj$trinkets$renderLegs(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {
        Objects.requireNonNull(reference);
        String s = reference.inventory().getSlotType().getName();
        if ("belt".equals(s)) {
            blahaj$trinkets$renderLegs$belt(stack, matrix, provider, light, entity);
        } else if ("thighs".equals(s)) {
            blahaj$trinkets$renderLegs$thighs(stack, matrix, provider, light, entity);
        }
    }

    @Unique
    private void blahaj$trinkets$renderLegs$belt(ItemStack stack, MatrixStack matrix, VertexConsumerProvider provider,
                                                 int light, LivingEntity entity) {
        Objects.requireNonNull(matrix);
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        boolean b = blahaj$trinkets$getMainHand(entity, false);
        float side = b ? .355f : -.355f;
        float r = b ? 90 : -90;
        matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
        // x (depth, + = front), y (vertical, + = up), z (side, + = right)
        matrix.translate(0f, -0.75f, side);
        matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(r));
        matrix.scale(0.5f, 0.5f, 0.5f);
        renderer.renderItem(entity, stack, ModelTransformationMode.FIXED, false, matrix, provider, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, 0);
    }

    @Unique
    private void blahaj$trinkets$renderLegs$thighs(ItemStack stack, MatrixStack matrix, VertexConsumerProvider provider,
                                                  int light, LivingEntity entity) {
        Objects.requireNonNull(matrix);
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        boolean b = blahaj$trinkets$getMainHand(entity, false);
        if (TrinketPlushRenderer.hasCuddlyInSlot(entity, "legs", "belt")) b = !b;
        float side = b ? -.355f : .355f;
        float r = b ? -90 : 90;
        matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
        matrix.translate(0f, -0.75f, side);
        matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees(r));
        matrix.scale(0.5f, 0.5f, 0.5f);
        renderer.renderItem(entity, stack, ModelTransformationMode.FIXED, false, matrix, provider, entity.getWorld(), light, OverlayTexture.DEFAULT_UV, 0);
    }

    @Unique
    public void blahaj$trinkets$renderFeet(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {
        Objects.requireNonNull(reference);
        String s = reference.inventory().getSlotType().getName();
        if (!"shoes".equalsIgnoreCase(s)) return;
        ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();
        //TODO feet models
    }

    @Unique
    public void blahak$trinkets$renderHand(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {}

    /**
     * Gets the main hand/arm as a boolean. False for right.
     * @param entity the entity to get the main hand/arm for
     * @param fallback the fallback if none could be acquired
     * @return {@code true} if left hand/arm, {@code false} if right hand/arm,
     *         {@code fallback} if none could be acquired.
     */
    @Unique
    private static boolean blahaj$trinkets$getMainHand(LivingEntity entity, boolean fallback) {
        if (entity instanceof PlayerEntity player) {
            return player.getMainArm() == Arm.LEFT;
        }
        return fallback;
    }
}
