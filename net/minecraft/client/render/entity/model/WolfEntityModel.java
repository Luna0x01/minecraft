package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

public class WolfEntityModel extends EntityModel {
	public ModelPart field_1562;
	public ModelPart field_1563;
	public ModelPart field_1564;
	public ModelPart field_1565;
	public ModelPart field_1566;
	public ModelPart field_1567;
	ModelPart field_1568;
	ModelPart field_1569;

	public WolfEntityModel() {
		float f = 0.0F;
		float g = 13.5F;
		this.field_1562 = new ModelPart(this, 0, 0);
		this.field_1562.addCuboid(-2.0F, -3.0F, -2.0F, 6, 6, 4, f);
		this.field_1562.setPivot(-1.0F, g, -7.0F);
		this.field_1563 = new ModelPart(this, 18, 14);
		this.field_1563.addCuboid(-3.0F, -2.0F, -3.0F, 6, 9, 6, f);
		this.field_1563.setPivot(0.0F, 14.0F, 2.0F);
		this.field_1569 = new ModelPart(this, 21, 0);
		this.field_1569.addCuboid(-3.0F, -3.0F, -3.0F, 8, 6, 7, f);
		this.field_1569.setPivot(-1.0F, 14.0F, 2.0F);
		this.field_1564 = new ModelPart(this, 0, 18);
		this.field_1564.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, f);
		this.field_1564.setPivot(-2.5F, 16.0F, 7.0F);
		this.field_1565 = new ModelPart(this, 0, 18);
		this.field_1565.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, f);
		this.field_1565.setPivot(0.5F, 16.0F, 7.0F);
		this.field_1566 = new ModelPart(this, 0, 18);
		this.field_1566.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, f);
		this.field_1566.setPivot(-2.5F, 16.0F, -4.0F);
		this.field_1567 = new ModelPart(this, 0, 18);
		this.field_1567.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, f);
		this.field_1567.setPivot(0.5F, 16.0F, -4.0F);
		this.field_1568 = new ModelPart(this, 9, 18);
		this.field_1568.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, f);
		this.field_1568.setPivot(-1.0F, 12.0F, 8.0F);
		this.field_1562.setTextureOffset(16, 14).addCuboid(-2.0F, -5.0F, 0.0F, 2, 2, 1, f);
		this.field_1562.setTextureOffset(16, 14).addCuboid(2.0F, -5.0F, 0.0F, 2, 2, 1, f);
		this.field_1562.setTextureOffset(0, 10).addCuboid(-0.5F, 0.0F, -5.0F, 3, 3, 4, f);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		super.render(entity, handSwing, handSwingAmount, tickDelta, age, headPitch, scale);
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		if (this.child) {
			float f = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 5.0F * scale, 2.0F * scale);
			this.field_1562.rotateAndRender(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F / f, 1.0F / f, 1.0F / f);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.field_1563.render(scale);
			this.field_1564.render(scale);
			this.field_1565.render(scale);
			this.field_1566.render(scale);
			this.field_1567.render(scale);
			this.field_1568.rotateAndRender(scale);
			this.field_1569.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.field_1562.rotateAndRender(scale);
			this.field_1563.render(scale);
			this.field_1564.render(scale);
			this.field_1565.render(scale);
			this.field_1566.render(scale);
			this.field_1567.render(scale);
			this.field_1568.rotateAndRender(scale);
			this.field_1569.render(scale);
		}
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		WolfEntity wolfEntity = (WolfEntity)entity;
		if (wolfEntity.isAngry()) {
			this.field_1568.posY = 0.0F;
		} else {
			this.field_1568.posY = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		}

		if (wolfEntity.isSitting()) {
			this.field_1569.setPivot(-1.0F, 16.0F, -3.0F);
			this.field_1569.posX = (float) (Math.PI * 2.0 / 5.0);
			this.field_1569.posY = 0.0F;
			this.field_1563.setPivot(0.0F, 18.0F, 0.0F);
			this.field_1563.posX = (float) (Math.PI / 4);
			this.field_1568.setPivot(-1.0F, 21.0F, 6.0F);
			this.field_1564.setPivot(-2.5F, 22.0F, 2.0F);
			this.field_1564.posX = (float) (Math.PI * 3.0 / 2.0);
			this.field_1565.setPivot(0.5F, 22.0F, 2.0F);
			this.field_1565.posX = (float) (Math.PI * 3.0 / 2.0);
			this.field_1566.posX = 5.811947F;
			this.field_1566.setPivot(-2.49F, 17.0F, -4.0F);
			this.field_1567.posX = 5.811947F;
			this.field_1567.setPivot(0.51F, 17.0F, -4.0F);
		} else {
			this.field_1563.setPivot(0.0F, 14.0F, 2.0F);
			this.field_1563.posX = (float) (Math.PI / 2);
			this.field_1569.setPivot(-1.0F, 14.0F, -3.0F);
			this.field_1569.posX = this.field_1563.posX;
			this.field_1568.setPivot(-1.0F, 12.0F, 8.0F);
			this.field_1564.setPivot(-2.5F, 16.0F, 7.0F);
			this.field_1565.setPivot(0.5F, 16.0F, 7.0F);
			this.field_1566.setPivot(-2.5F, 16.0F, -4.0F);
			this.field_1567.setPivot(0.5F, 16.0F, -4.0F);
			this.field_1564.posX = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
			this.field_1565.posX = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
			this.field_1566.posX = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
			this.field_1567.posX = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		}

		this.field_1562.posZ = wolfEntity.method_2880(tickDelta) + wolfEntity.method_2876(tickDelta, 0.0F);
		this.field_1569.posZ = wolfEntity.method_2876(tickDelta, -0.08F);
		this.field_1563.posZ = wolfEntity.method_2876(tickDelta, -0.16F);
		this.field_1568.posZ = wolfEntity.method_2876(tickDelta, -0.2F);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_1562.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_1562.posY = age * (float) (Math.PI / 180.0);
		this.field_1568.posX = tickDelta;
	}
}
