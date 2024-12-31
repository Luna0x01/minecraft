package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.PigSaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

public class PigEntityRenderer extends MobEntityRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/pig/pig.png");

	public PigEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PigEntityModel<>(), 0.7F);
		this.addFeature(new PigSaddleFeatureRenderer(this));
	}

	protected Identifier method_4087(PigEntity pigEntity) {
		return SKIN;
	}
}
