package mc.recraftors.blahaj.compat;

import immersive_melodies.client.MelodyProgress;
import immersive_melodies.client.animation.animators.Animator;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

public class BlahajBassAnimator implements Animator {
    @Override
    public void setAngles(ModelPart reach, ModelPart hold, BipedEntityModel<?> model, LivingEntity entity, MelodyProgress progress, float v) {
        reach.pitch = -1.2F - progress.getCurrent();
        reach.roll = -0.2F;
        reach.yaw = progress.getCurrentPitch() - 0.5F;
        hold.pitch = -0.75F;
        hold.yaw = 0.0F;
        hold.roll = hold.roll * 0.25F - 0.2F;
    }
}
