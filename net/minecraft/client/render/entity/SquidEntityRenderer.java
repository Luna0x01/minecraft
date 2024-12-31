package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;

public class SquidEntityRenderer extends MobEntityRenderer<SquidEntity> {
	private static final Identifier SQUID_TEX = new Identifier("textures/entity/squid.png");

	public SquidEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SquidEntityModel(), 0.7F);
	}

	protected Identifier getTexture(SquidEntity squidEntity) {
		return SQUID_TEX;
	}

	protected void method_5777(SquidEntity squidEntity, float f, float g, float h) {
		float i = squidEntity.prevTiltAngle + (squidEntity.tiltAngle - squidEntity.prevTiltAngle) * h;
		float j = squidEntity.prevRollAngle + (squidEntity.rollAngle - squidEntity.prevRollAngle) * h;
		GlStateManager.translate(0.0F, 0.5F, 0.0F);
		GlStateManager.rotate(180.0F - g, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(i, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(j, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, -1.2F, 0.0F);
	}

	protected float method_5783(SquidEntity squidEntity, float f) {
		return squidEntity.prevTentacleAngle + (squidEntity.tentacleAngle - squidEntity.prevTentacleAngle) * f;
	}
}
