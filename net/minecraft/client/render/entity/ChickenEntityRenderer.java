package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ChickenEntityRenderer extends MobEntityRenderer<ChickenEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/chicken.png");

	public ChickenEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
	}

	protected Identifier getTexture(ChickenEntity chickenEntity) {
		return TEXTURE;
	}

	protected float method_5783(ChickenEntity chickenEntity, float f) {
		float g = chickenEntity.prevFlapProgress + (chickenEntity.flapProgress - chickenEntity.prevFlapProgress) * f;
		float h = chickenEntity.prevMaxWingDeviation + (chickenEntity.maxWingDeviation - chickenEntity.prevMaxWingDeviation) * f;
		return (MathHelper.sin(g) + 1.0F) * h;
	}
}
