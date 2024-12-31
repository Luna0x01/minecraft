package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.QuadruPedEntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;

public class class_4200 extends QuadruPedEntityModel {
	private final ModelPart field_20605;

	public class_4200(float f) {
		super(12, f);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 3, 0);
		this.head.addCuboid(-3.0F, -1.0F, -3.0F, 6, 5, 6, 0.0F);
		this.head.setPivot(0.0F, 19.0F, -10.0F);
		this.torso = new ModelPart(this);
		this.torso.setTextureOffset(7, 37).addCuboid(-9.5F, 3.0F, -10.0F, 19, 20, 6, 0.0F);
		this.torso.setTextureOffset(31, 1).addCuboid(-5.5F, 3.0F, -13.0F, 11, 18, 3, 0.0F);
		this.torso.setPivot(0.0F, 11.0F, -10.0F);
		this.field_20605 = new ModelPart(this);
		this.field_20605.setTextureOffset(70, 33).addCuboid(-4.5F, 3.0F, -14.0F, 9, 18, 1, 0.0F);
		this.field_20605.setPivot(0.0F, 11.0F, -10.0F);
		int i = 1;
		this.backRightLeg = new ModelPart(this, 1, 23);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
		this.backRightLeg.setPivot(-3.5F, 22.0F, 11.0F);
		this.backLeftLeg = new ModelPart(this, 1, 12);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
		this.backLeftLeg.setPivot(3.5F, 22.0F, 11.0F);
		this.frontRightLeg = new ModelPart(this, 27, 30);
		this.frontRightLeg.addCuboid(-13.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
		this.frontRightLeg.setPivot(-5.0F, 21.0F, -4.0F);
		this.frontLeftLeg = new ModelPart(this, 27, 24);
		this.frontLeftLeg.addCuboid(0.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
		this.frontLeftLeg.setPivot(5.0F, 21.0F, -4.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		TurtleEntity turtleEntity = (TurtleEntity)entity;
		if (this.child) {
			float f = 6.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.16666667F, 0.16666667F, 0.16666667F);
			GlStateManager.translate(0.0F, 120.0F * scale, 0.0F);
			this.head.render(scale);
			this.torso.render(scale);
			this.backRightLeg.render(scale);
			this.backLeftLeg.render(scale);
			this.frontRightLeg.render(scale);
			this.frontLeftLeg.render(scale);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			if (turtleEntity.method_15812()) {
				GlStateManager.translate(0.0F, -0.08F, 0.0F);
			}

			this.head.render(scale);
			this.torso.render(scale);
			GlStateManager.pushMatrix();
			this.backRightLeg.render(scale);
			this.backLeftLeg.render(scale);
			GlStateManager.popMatrix();
			this.frontRightLeg.render(scale);
			this.frontLeftLeg.render(scale);
			if (turtleEntity.method_15812()) {
				this.field_20605.render(scale);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		TurtleEntity turtleEntity = (TurtleEntity)entity;
		this.backRightLeg.posX = MathHelper.cos(handSwing * 0.6662F * 0.6F) * 0.5F * handSwingAmount;
		this.backLeftLeg.posX = MathHelper.cos(handSwing * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * handSwingAmount;
		this.frontRightLeg.posZ = MathHelper.cos(handSwing * 0.6662F * 0.6F + (float) Math.PI) * 0.5F * handSwingAmount;
		this.frontLeftLeg.posZ = MathHelper.cos(handSwing * 0.6662F * 0.6F) * 0.5F * handSwingAmount;
		this.frontRightLeg.posX = 0.0F;
		this.frontLeftLeg.posX = 0.0F;
		this.frontRightLeg.posY = 0.0F;
		this.frontLeftLeg.posY = 0.0F;
		this.backRightLeg.posY = 0.0F;
		this.backLeftLeg.posY = 0.0F;
		this.field_20605.posX = (float) (Math.PI / 2);
		if (!turtleEntity.isTouchingWater() && turtleEntity.onGround) {
			float f = turtleEntity.method_15813() ? 4.0F : 1.0F;
			float g = turtleEntity.method_15813() ? 2.0F : 1.0F;
			float h = 5.0F;
			this.frontRightLeg.posY = MathHelper.cos(f * handSwing * 5.0F + (float) Math.PI) * 8.0F * handSwingAmount * g;
			this.frontRightLeg.posZ = 0.0F;
			this.frontLeftLeg.posY = MathHelper.cos(f * handSwing * 5.0F) * 8.0F * handSwingAmount * g;
			this.frontLeftLeg.posZ = 0.0F;
			this.backRightLeg.posY = MathHelper.cos(handSwing * 5.0F + (float) Math.PI) * 3.0F * handSwingAmount;
			this.backRightLeg.posX = 0.0F;
			this.backLeftLeg.posY = MathHelper.cos(handSwing * 5.0F) * 3.0F * handSwingAmount;
			this.backLeftLeg.posX = 0.0F;
		}
	}
}
