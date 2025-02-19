package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

public class SheepEntityRenderer extends MobEntityRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/sheep/sheep.png");

	public SheepEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SheepEntityModel<>(context.getPart(EntityModelLayers.SHEEP)), 0.7F);
		this.addFeature(new SheepWoolFeatureRenderer(this, context.getModelLoader()));
	}

	public Identifier getTexture(SheepEntity sheepEntity) {
		return TEXTURE;
	}
}
