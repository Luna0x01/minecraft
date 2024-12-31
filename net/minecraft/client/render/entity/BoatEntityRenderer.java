package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.class_2854;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BoatEntityRenderer extends EntityRenderer<BoatEntity> {
	private static final Identifier[] field_13628 = new Identifier[]{
		new Identifier("textures/entity/boat/oak.png"),
		new Identifier("textures/entity/boat/spruce.png"),
		new Identifier("textures/entity/boat/birch.png"),
		new Identifier("textures/entity/boat/jungle.png"),
		new Identifier("textures/entity/boat/acacia.png"),
		new Identifier("textures/entity/boat/dark_oak.png")
	};
	protected EntityModel model = new BoatEntityModel();

	public BoatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize = 0.5F;
	}

	public void render(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.method_12439(d, e, f);
		this.method_12440(boatEntity, g, h);
		this.bindTexture(boatEntity);
		if (this.field_13631) {
			GlStateManager.enableColorMaterial();
			GlStateManager.method_12309(this.method_12454(boatEntity));
		}

		this.model.render(boatEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (this.field_13631) {
			GlStateManager.method_12315();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.render(boatEntity, d, e, f, g, h);
	}

	public void method_12440(BoatEntity boatEntity, float f, float g) {
		GlStateManager.rotate(180.0F - f, 0.0F, 1.0F, 0.0F);
		float h = (float)boatEntity.getBubbleWobbleTicks() - g;
		float i = boatEntity.getDamageWobbleStrength() - g;
		if (i < 0.0F) {
			i = 0.0F;
		}

		if (h > 0.0F) {
			GlStateManager.rotate(MathHelper.sin(h) * h * i / 10.0F * (float)boatEntity.getDamageWobbleSide(), 1.0F, 0.0F, 0.0F);
		}

		float j = boatEntity.method_15960(g);
		if (!MathHelper.approximatelyEquals(j, 0.0F)) {
			GlStateManager.rotate(boatEntity.method_15960(g), 1.0F, 0.0F, 1.0F);
		}

		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
	}

	public void method_12439(double d, double e, double f) {
		GlStateManager.translate((float)d, (float)e + 0.375F, (float)f);
	}

	protected Identifier getTexture(BoatEntity boatEntity) {
		return field_13628[boatEntity.getBoatType().ordinal()];
	}

	@Override
	public boolean method_12450() {
		return true;
	}

	public void method_12453(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.method_12439(d, e, f);
		this.method_12440(boatEntity, g, h);
		this.bindTexture(boatEntity);
		((class_2854)this.model).method_12226(boatEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}
