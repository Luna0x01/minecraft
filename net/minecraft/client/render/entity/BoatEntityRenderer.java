package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BoatEntityRenderer extends EntityRenderer<BoatEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/boat.png");
	protected EntityModel model = new BoatEntityModel();

	public BoatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize = 0.5F;
	}

	public void render(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)d, (float)e + 0.25F, (float)f);
		GlStateManager.rotate(180.0F - g, 0.0F, 1.0F, 0.0F);
		float i = (float)boatEntity.getBubbleWobbleTicks() - h;
		float j = boatEntity.getDamageWobbleStrength() - h;
		if (j < 0.0F) {
			j = 0.0F;
		}

		if (i > 0.0F) {
			GlStateManager.rotate(MathHelper.sin(i) * i * j / 10.0F * (float)boatEntity.getDamageWobbleSide(), 1.0F, 0.0F, 0.0F);
		}

		float k = 0.75F;
		GlStateManager.scale(k, k, k);
		GlStateManager.scale(1.0F / k, 1.0F / k, 1.0F / k);
		this.bindTexture(boatEntity);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		this.model.render(boatEntity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		super.render(boatEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(BoatEntity boatEntity) {
		return TEXTURE;
	}
}
