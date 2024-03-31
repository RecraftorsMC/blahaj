package mc.recraftors.blahaj.compat;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface TrinketPlushRenderer extends TrinketRenderer {
    @Override
    default void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel,
                               MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                               LivingEntity entity, float limbAngle, float limbDistance, float tickDelta,
                               float animationProgress, float headYaw, float headPitch) {
        if (! (contextModel instanceof BipedEntityModel<? extends LivingEntity> model)) return;
        //matrices.push();
        switch (slotReference.inventory().getSlotType().getGroup()) {
            case "head" -> blahaj$trinkets$renderHead(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            case "chest" -> blahaj$trinkets$renderChest(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            case "legs" -> blahaj$trinkets$renderLegs(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            case "feet" -> blahaj$trinkets$renderFeet(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            case "hand" -> blahak$trinkets$renderHand(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        }
        //matrices.pop();
    }

    default void blahaj$trinkets$renderHead(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {}

    default void blahaj$trinkets$renderChest(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                             MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                             float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                             float headYaw, float headPitch) {}

    default void blahaj$trinkets$renderLegs(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {}

    default void blahaj$trinkets$renderFeet(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {}

    default void blahak$trinkets$renderHand(ItemStack stack, SlotReference reference, BipedEntityModel<? extends LivingEntity> model,
                                            MatrixStack matrix, VertexConsumerProvider provider, int light, LivingEntity entity,
                                            float limbAngle, float limbDistance, float tickDelta, float animationProgress,
                                            float headYaw, float headPitch) {}
}
