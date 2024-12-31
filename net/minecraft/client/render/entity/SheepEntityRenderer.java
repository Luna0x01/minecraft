package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

public class SheepEntityRenderer extends MobEntityRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/sheep/sheep.png");

	public SheepEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SheepEntityModel<>(), 0.7F);
		this.addFeature(new SheepWoolFeatureRenderer(this));
	}

	public Identifier getTexture(SheepEntity sheepEntity) {
		return SKIN;
	}
}
