package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.class_2854;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

public class BoatEntityModel extends EntityModel implements class_2854 {
	public ModelPart[] boatParts = new ModelPart[5];
	public ModelPart[] field_13379 = new ModelPart[2];
	public ModelPart field_13380;
	private int field_13381 = GlAllocationUtils.genLists(1);

	public BoatEntityModel() {
		this.boatParts[0] = new ModelPart(this, 0, 0).setTextureSize(128, 64);
		this.boatParts[1] = new ModelPart(this, 0, 19).setTextureSize(128, 64);
		this.boatParts[2] = new ModelPart(this, 0, 27).setTextureSize(128, 64);
		this.boatParts[3] = new ModelPart(this, 0, 35).setTextureSize(128, 64);
		this.boatParts[4] = new ModelPart(this, 0, 43).setTextureSize(128, 64);
		int i = 32;
		int j = 6;
		int k = 20;
		int l = 4;
		int m = 28;
		this.boatParts[0].addCuboid((float)(-m / 2), (float)(-k / 2 + 1), -3.0F, m, k - 4, 3, 0.0F);
		this.boatParts[0].setPivot(0.0F, (float)(l - 1), 1.0F);
		this.boatParts[1].addCuboid((float)(-i / 2 + 3), (float)(-j - 1), -1.0F, k - 2, j, 2, 0.0F);
		this.boatParts[1].setPivot((float)(-i / 2 + 1), (float)l, 4.0F);
		this.boatParts[2].addCuboid((float)(-i / 2 + 8), (float)(-j - 1), -1.0F, k - 4, j, 2, 0.0F);
		this.boatParts[2].setPivot((float)(i / 2 - 1), (float)l, 0.0F);
		this.boatParts[3].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.boatParts[3].setPivot(0.0F, (float)l, (float)(-k / 2 + 1));
		this.boatParts[4].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.boatParts[4].setPivot(0.0F, (float)l, (float)(k / 2 - 1));
		this.boatParts[0].posX = (float) (Math.PI / 2);
		this.boatParts[1].posY = (float) (Math.PI * 3.0 / 2.0);
		this.boatParts[2].posY = (float) (Math.PI / 2);
		this.boatParts[3].posY = (float) Math.PI;
		this.field_13379[0] = this.method_12220(true);
		this.field_13379[0].setPivot(3.0F, -5.0F, 9.0F);
		this.field_13379[1] = this.method_12220(false);
		this.field_13379[1].setPivot(3.0F, -5.0F, -9.0F);
		this.field_13379[1].posY = (float) Math.PI;
		this.field_13379[0].posZ = this.field_13379[1].posZ = (float) (Math.PI / 16);
		this.field_13380 = new ModelPart(this, 0, 0).setTextureSize(128, 64);
		this.field_13380.addCuboid((float)(-m / 2), (float)(-k / 2 + 1), -3.0F, m, k - 4, 3, 0.0F);
		this.field_13380.setPivot(0.0F, (float)(l - 7), 1.0F);
		this.field_13380.posX = (float) (Math.PI / 2);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		BoatEntity boatEntity = (BoatEntity)entity;
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);

		for (int i = 0; i < 5; i++) {
			this.boatParts[i].render(scale);
		}

		this.method_12219(boatEntity, 0, scale, handSwing);
		this.method_12219(boatEntity, 1, scale, handSwing);
	}

	@Override
	public void method_12226(Entity entity, float f, float g, float h, float i, float j, float k) {
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.colorMask(false, false, false, false);
		this.field_13380.render(k);
		GlStateManager.colorMask(true, true, true, true);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
	}

	ModelPart method_12220(boolean bl) {
		ModelPart modelPart = new ModelPart(this, 62, bl ? 0 : 20).setTextureSize(128, 64);
		int i = 20;
		int j = 7;
		int k = 6;
		float f = -5.0F;
		modelPart.addCuboid(-1.0F, 0.0F, f, 2, 2, i - 2);
		modelPart.addCuboid(bl ? -1.001F : 0.001F, (float)(-k / 2), (float)(i - j) + f, 1, k, j);
		return modelPart;
	}

	void method_12219(BoatEntity boatEntity, int i, float f, float g) {
		float h = 40.0F;
		float j = boatEntity.interpolatePaddlePhase(i, g) * h;
		ModelPart modelPart = this.field_13379[i];
		modelPart.posX = (float)MathHelper.clampedLerp((float) (-Math.PI / 3), (float) (-Math.PI / 12), (double)((MathHelper.sin(-j) + 1.0F) / 2.0F));
		modelPart.posY = (float)MathHelper.clampedLerp((float) (-Math.PI / 4), (float) (Math.PI / 4), (double)((MathHelper.sin(-j + 1.0F) + 1.0F) / 2.0F));
		if (i == 1) {
			modelPart.posY = (float) Math.PI - modelPart.posY;
		}

		modelPart.render(f);
	}
}
