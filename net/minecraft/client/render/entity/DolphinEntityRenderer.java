package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

public class DolphinEntityRenderer extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/dolphin.png");

	public DolphinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new DolphinEntityModel<>(), 0.7F);
		this.addFeature(new DolphinHeldItemFeatureRenderer(this));
	}

	public Identifier getTexture(DolphinEntity dolphinEntity) {
		return TEXTURE;
	}
}
