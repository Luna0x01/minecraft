package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

public class PigEntityRenderer extends MobEntityRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/pig/pig.png");

	public PigEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new PigEntityModel<>(context.getPart(EntityModelLayers.PIG)), 0.7F);
		this.addFeature(
			new SaddleFeatureRenderer<>(this, new PigEntityModel<>(context.getPart(EntityModelLayers.PIG_SADDLE)), new Identifier("textures/entity/pig/pig_saddle.png"))
		);
	}

	public Identifier getTexture(PigEntity pigEntity) {
		return TEXTURE;
	}
}
