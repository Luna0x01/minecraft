package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.math.MathHelper;

public class OcelotEntityModel extends EntityModel {
	ModelPart field_1496;
	ModelPart field_1497;
	ModelPart field_1498;
	ModelPart field_1499;
	ModelPart field_1500;
	ModelPart field_1501;
	ModelPart field_1502;
	ModelPart field_1503;
	int field_1504 = 1;

	public OcelotEntityModel() {
		this.putTexture("head.main", 0, 0);
		this.putTexture("head.nose", 0, 24);
		this.putTexture("head.ear1", 0, 10);
		this.putTexture("head.ear2", 6, 10);
		this.field_1502 = new ModelPart(this, "head");
		this.field_1502.addCuboid("main", -2.5F, -2.0F, -3.0F, 5, 4, 5);
		this.field_1502.addCuboid("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2);
		this.field_1502.addCuboid("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2);
		this.field_1502.addCuboid("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2);
		this.field_1502.setPivot(0.0F, 15.0F, -9.0F);
		this.field_1503 = new ModelPart(this, 20, 0);
		this.field_1503.addCuboid(-2.0F, 3.0F, -8.0F, 4, 16, 6, 0.0F);
		this.field_1503.setPivot(0.0F, 12.0F, -10.0F);
		this.field_1500 = new ModelPart(this, 0, 15);
		this.field_1500.addCuboid(-0.5F, 0.0F, 0.0F, 1, 8, 1);
		this.field_1500.posX = 0.9F;
		this.field_1500.setPivot(0.0F, 15.0F, 8.0F);
		this.field_1501 = new ModelPart(this, 4, 15);
		this.field_1501.addCuboid(-0.5F, 0.0F, 0.0F, 1, 8, 1);
		this.field_1501.setPivot(0.0F, 20.0F, 14.0F);
		this.field_1496 = new ModelPart(this, 8, 13);
		this.field_1496.addCuboid(-1.0F, 0.0F, 1.0F, 2, 6, 2);
		this.field_1496.setPivot(1.1F, 18.0F, 5.0F);
		this.field_1497 = new ModelPart(this, 8, 13);
		this.field_1497.addCuboid(-1.0F, 0.0F, 1.0F, 2, 6, 2);
		this.field_1497.setPivot(-1.1F, 18.0F, 5.0F);
		this.field_1498 = new ModelPart(this, 40, 0);
		this.field_1498.addCuboid(-1.0F, 0.0F, 0.0F, 2, 10, 2);
		this.field_1498.setPivot(1.2F, 13.8F, -5.0F);
		this.field_1499 = new ModelPart(this, 40, 0);
		this.field_1499.addCuboid(-1.0F, 0.0F, 0.0F, 2, 10, 2);
		this.field_1499.setPivot(-1.2F, 13.8F, -5.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		if (this.child) {
			float f = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.5F / f, 1.5F / f, 1.5F / f);
			GlStateManager.translate(0.0F, 10.0F * scale, 4.0F * scale);
			this.field_1502.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F / f, 1.0F / f, 1.0F / f);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.field_1503.render(scale);
			this.field_1496.render(scale);
			this.field_1497.render(scale);
			this.field_1498.render(scale);
			this.field_1499.render(scale);
			this.field_1500.render(scale);
			this.field_1501.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.field_1502.render(scale);
			this.field_1503.render(scale);
			this.field_1500.render(scale);
			this.field_1501.render(scale);
			this.field_1496.render(scale);
			this.field_1497.render(scale);
			this.field_1498.render(scale);
			this.field_1499.render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.field_1502.posX = headPitch / (180.0F / (float)Math.PI);
		this.field_1502.posY = age / (180.0F / (float)Math.PI);
		if (this.field_1504 != 3) {
			this.field_1503.posX = (float) (Math.PI / 2);
			if (this.field_1504 == 2) {
				this.field_1496.posX = MathHelper.cos(handSwing * 0.6662F) * 1.0F * handSwingAmount;
				this.field_1497.posX = MathHelper.cos(handSwing * 0.6662F + 0.3F) * 1.0F * handSwingAmount;
				this.field_1498.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI + 0.3F) * 1.0F * handSwingAmount;
				this.field_1499.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.0F * handSwingAmount;
				this.field_1501.posX = 1.7278761F + (float) (Math.PI / 10) * MathHelper.cos(handSwing) * handSwingAmount;
			} else {
				this.field_1496.posX = MathHelper.cos(handSwing * 0.6662F) * 1.0F * handSwingAmount;
				this.field_1497.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.0F * handSwingAmount;
				this.field_1498.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.0F * handSwingAmount;
				this.field_1499.posX = MathHelper.cos(handSwing * 0.6662F) * 1.0F * handSwingAmount;
				if (this.field_1504 == 1) {
					this.field_1501.posX = 1.7278761F + (float) (Math.PI / 4) * MathHelper.cos(handSwing) * handSwingAmount;
				} else {
					this.field_1501.posX = 1.7278761F + 0.47123894F * MathHelper.cos(handSwing) * handSwingAmount;
				}
			}
		}
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		OcelotEntity ocelotEntity = (OcelotEntity)entity;
		this.field_1503.pivotY = 12.0F;
		this.field_1503.pivotZ = -10.0F;
		this.field_1502.pivotY = 15.0F;
		this.field_1502.pivotZ = -9.0F;
		this.field_1500.pivotY = 15.0F;
		this.field_1500.pivotZ = 8.0F;
		this.field_1501.pivotY = 20.0F;
		this.field_1501.pivotZ = 14.0F;
		this.field_1498.pivotY = this.field_1499.pivotY = 13.8F;
		this.field_1498.pivotZ = this.field_1499.pivotZ = -5.0F;
		this.field_1496.pivotY = this.field_1497.pivotY = 18.0F;
		this.field_1496.pivotZ = this.field_1497.pivotZ = 5.0F;
		this.field_1500.posX = 0.9F;
		if (ocelotEntity.isSneaking()) {
			this.field_1503.pivotY++;
			this.field_1502.pivotY += 2.0F;
			this.field_1500.pivotY++;
			this.field_1501.pivotY += -4.0F;
			this.field_1501.pivotZ += 2.0F;
			this.field_1500.posX = (float) (Math.PI / 2);
			this.field_1501.posX = (float) (Math.PI / 2);
			this.field_1504 = 0;
		} else if (ocelotEntity.isSprinting()) {
			this.field_1501.pivotY = this.field_1500.pivotY;
			this.field_1501.pivotZ += 2.0F;
			this.field_1500.posX = (float) (Math.PI / 2);
			this.field_1501.posX = (float) (Math.PI / 2);
			this.field_1504 = 2;
		} else if (ocelotEntity.isSitting()) {
			this.field_1503.posX = (float) (Math.PI / 4);
			this.field_1503.pivotY += -4.0F;
			this.field_1503.pivotZ += 5.0F;
			this.field_1502.pivotY += -3.3F;
			this.field_1502.pivotZ++;
			this.field_1500.pivotY += 8.0F;
			this.field_1500.pivotZ += -2.0F;
			this.field_1501.pivotY += 2.0F;
			this.field_1501.pivotZ += -0.8F;
			this.field_1500.posX = 1.7278761F;
			this.field_1501.posX = 2.670354F;
			this.field_1498.posX = this.field_1499.posX = (float) (-Math.PI / 20);
			this.field_1498.pivotY = this.field_1499.pivotY = 15.8F;
			this.field_1498.pivotZ = this.field_1499.pivotZ = -7.0F;
			this.field_1496.posX = this.field_1497.posX = (float) (-Math.PI / 2);
			this.field_1496.pivotY = this.field_1497.pivotY = 21.0F;
			this.field_1496.pivotZ = this.field_1497.pivotZ = 1.0F;
			this.field_1504 = 3;
		} else {
			this.field_1504 = 1;
		}
	}
}
