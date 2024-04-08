package mc.recraftors.blahaj.compat;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import mc.recraftors.blahaj.item.CuddlyItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Optional;

public interface TrinketPlushRenderer extends TrinketRenderer {
    @Override
    default void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel,
                               MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                               LivingEntity entity, float limbAngle, float limbDistance, float tickDelta,
                               float animationProgress, float headYaw, float headPitch) {
        if (! (contextModel instanceof BipedEntityModel<? extends LivingEntity> model)) return;
        switch (slotReference.inventory().getSlotType().getGroup()) {
            case "head" -> blahaj$trinkets$renderHead(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            case "chest" -> blahaj$trinkets$renderChest(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            case "legs" -> blahaj$trinkets$renderLegs(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            case "feet" -> blahaj$trinkets$renderFeet(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            case "hand" -> blahak$trinkets$renderHand(stack, slotReference, model, matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        }
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

    static boolean hasCuddlyInSlot(LivingEntity entity, String group, String... names) {
        Optional<TrinketComponent> optionalComponent = TrinketsApi.getTrinketComponent(entity);
        if (optionalComponent.isEmpty()) return false;
        TrinketComponent component = optionalComponent.get();
        Map<String, TrinketInventory> groupMap = component.getInventory().get(group);
        if (groupMap == null) return false;
        Optional<TrinketInventory> optionalInventory = Optional.empty();
        for (String name : names) {
            optionalInventory = Optional.ofNullable(groupMap.get(name));
            if (optionalInventory.isPresent()) {
                break;
            }
        }
        if (optionalInventory.isEmpty()) return false;
        TrinketInventory inventory = optionalInventory.get();
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).getItem() instanceof CuddlyItem) return true;
        }
        return false;
    }
}
