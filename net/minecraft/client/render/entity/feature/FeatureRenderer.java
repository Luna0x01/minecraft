package net.minecraft.client.render.entity.feature;

import net.minecraft.entity.LivingEntity;

public interface FeatureRenderer<E extends LivingEntity> {
	void render(E entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headYaw, float headPitch, float scale);

	boolean combineTextures();
}
