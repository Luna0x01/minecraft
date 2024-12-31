package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;

public abstract class IllagerEntityRenderer<T extends IllagerEntity> extends MobEntityRenderer<T, IllagerEntityModel<T>> {
	protected IllagerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, IllagerEntityModel<T> illagerEntityModel, float f) {
		super(entityRenderDispatcher, illagerEntityModel, f);
		this.addFeature(new HeadFeatureRenderer<>(this));
	}

	protected void scale(T illagerEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
	}
}
