package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

public class PillagerEntityRenderer extends IllagerEntityRenderer<PillagerEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/pillager.png");

	public PillagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.PILLAGER)), 0.5F);
		this.addFeature(new HeldItemFeatureRenderer<>(this));
	}

	public Identifier getTexture(PillagerEntity pillagerEntity) {
		return TEXTURE;
	}
}
