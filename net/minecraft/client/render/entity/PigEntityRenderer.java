package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.PigSaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

public class PigEntityRenderer extends MobEntityRenderer<PigEntity> {
	private static final Identifier PIG_TEX = new Identifier("textures/entity/pig/pig.png");

	public PigEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
		this.addFeature(new PigSaddleFeatureRenderer(this));
	}

	protected Identifier getTexture(PigEntity pigEntity) {
		return PIG_TEX;
	}
}
