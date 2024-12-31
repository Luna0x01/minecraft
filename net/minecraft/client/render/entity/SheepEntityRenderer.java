package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

public class SheepEntityRenderer extends MobEntityRenderer<SheepEntity> {
	private static final Identifier SHEEP_TEX = new Identifier("textures/entity/sheep/sheep.png");

	public SheepEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
		this.addFeature(new SheepWoolFeatureRenderer(this));
	}

	protected Identifier getTexture(SheepEntity sheepEntity) {
		return SHEEP_TEX;
	}
}
