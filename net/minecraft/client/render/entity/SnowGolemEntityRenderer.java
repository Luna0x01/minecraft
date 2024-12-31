package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.SnowGolemPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.Identifier;

public class SnowGolemEntityRenderer extends MobEntityRenderer<SnowGolemEntity> {
	private static final Identifier SNOW_GOLEM_TEX = new Identifier("textures/entity/snowman.png");

	public SnowGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SnowmanEntityModel(), 0.5F);
		this.addFeature(new SnowGolemPumpkinFeatureRenderer(this));
	}

	protected Identifier getTexture(SnowGolemEntity snowGolemEntity) {
		return SNOW_GOLEM_TEX;
	}

	public SnowmanEntityModel getModel() {
		return (SnowmanEntityModel)super.getModel();
	}
}
