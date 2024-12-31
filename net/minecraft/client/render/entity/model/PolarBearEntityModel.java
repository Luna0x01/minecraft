package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PolarBearEntity;

public class PolarBearEntityModel extends QuadruPedEntityModel {
	public PolarBearEntityModel() {
		super(12, 0.0F);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
		this.head.setPivot(0.0F, 10.0F, -16.0F);
		this.head.setTextureOffset(0, 44).addCuboid(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
		this.head.setTextureOffset(26, 0).addCuboid(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		ModelPart modelPart = this.head.setTextureOffset(26, 0);
		modelPart.mirror = true;
		modelPart.addCuboid(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		this.torso = new ModelPart(this);
		this.torso.setTextureOffset(0, 19).addCuboid(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
		this.torso.setTextureOffset(39, 0).addCuboid(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
		this.torso.setPivot(-2.0F, 9.0F, 12.0F);
		int i = 10;
		this.backRightLeg = new ModelPart(this, 50, 22);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.backRightLeg.setPivot(-3.5F, 14.0F, 6.0F);
		this.backLeftLeg = new ModelPart(this, 50, 22);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.backLeftLeg.setPivot(3.5F, 14.0F, 6.0F);
		this.frontRightLeg = new ModelPart(this, 50, 40);
		this.frontRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.frontRightLeg.setPivot(-2.5F, 14.0F, -7.0F);
		this.frontLeftLeg = new ModelPart(this, 50, 40);
		this.frontLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.frontLeftLeg.setPivot(2.5F, 14.0F, -7.0F);
		this.backRightLeg.pivotX--;
		this.backLeftLeg.pivotX++;
		this.backRightLeg.pivotZ += 0.0F;
		this.backLeftLeg.pivotZ += 0.0F;
		this.frontRightLeg.pivotX--;
		this.frontLeftLeg.pivotX++;
		this.frontRightLeg.pivotZ--;
		this.frontLeftLeg.pivotZ--;
		this.field_1515 += 2.0F;
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		if (this.child) {
			float f = 2.0F;
			this.field_1514 = 16.0F;
			this.field_1515 = 4.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.6666667F, 0.6666667F, 0.6666667F);
			GlStateManager.translate(0.0F, this.field_1514 * scale, this.field_1515 * scale);
			this.head.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.torso.render(scale);
			this.backRightLeg.render(scale);
			this.backLeftLeg.render(scale);
			this.frontRightLeg.render(scale);
			this.frontLeftLeg.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.head.render(scale);
			this.torso.render(scale);
			this.backRightLeg.render(scale);
			this.backLeftLeg.render(scale);
			this.frontRightLeg.render(scale);
			this.frontLeftLeg.render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		float f = tickDelta - (float)entity.ticksAlive;
		float g = ((PolarBearEntity)entity).method_13508(f);
		g *= g;
		float h = 1.0F - g;
		this.torso.posX = (float) (Math.PI / 2) - g * (float) Math.PI * 0.35F;
		this.torso.pivotY = 9.0F * h + 11.0F * g;
		this.frontRightLeg.pivotY = 14.0F * h - 6.0F * g;
		this.frontRightLeg.pivotZ = -8.0F * h - 4.0F * g;
		this.frontRightLeg.posX -= g * (float) Math.PI * 0.45F;
		this.frontLeftLeg.pivotY = this.frontRightLeg.pivotY;
		this.frontLeftLeg.pivotZ = this.frontRightLeg.pivotZ;
		this.frontLeftLeg.posX -= g * (float) Math.PI * 0.45F;
		this.head.pivotY = 10.0F * h - 12.0F * g;
		this.head.pivotZ = -16.0F * h - 3.0F * g;
		this.head.posX += g * (float) Math.PI * 0.15F;
	}
}
