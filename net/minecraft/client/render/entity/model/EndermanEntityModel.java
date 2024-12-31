package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class EndermanEntityModel extends BiPedModel {
	public boolean carryingBlock;
	public boolean angry;

	public EndermanEntityModel(float f) {
		super(0.0F, -14.0F, 64, 32);
		float g = -14.0F;
		this.hat = new ModelPart(this, 0, 16);
		this.hat.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, f - 0.5F);
		this.hat.setPivot(0.0F, -14.0F, 0.0F);
		this.body = new ModelPart(this, 32, 16);
		this.body.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.body.setPivot(0.0F, -14.0F, 0.0F);
		this.rightArm = new ModelPart(this, 56, 0);
		this.rightArm.addCuboid(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.rightArm.setPivot(-3.0F, -12.0F, 0.0F);
		this.leftArm = new ModelPart(this, 56, 0);
		this.leftArm.mirror = true;
		this.leftArm.addCuboid(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.leftArm.setPivot(5.0F, -12.0F, 0.0F);
		this.rightLeg = new ModelPart(this, 56, 0);
		this.rightLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.rightLeg.setPivot(-2.0F, -2.0F, 0.0F);
		this.leftLeg = new ModelPart(this, 56, 0);
		this.leftLeg.mirror = true;
		this.leftLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.leftLeg.setPivot(2.0F, -2.0F, 0.0F);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.head.visible = true;
		float f = -14.0F;
		this.body.posX = 0.0F;
		this.body.pivotY = -14.0F;
		this.body.pivotZ = -0.0F;
		this.rightLeg.posX -= 0.0F;
		this.leftLeg.posX -= 0.0F;
		this.rightArm.posX = (float)((double)this.rightArm.posX * 0.5);
		this.leftArm.posX = (float)((double)this.leftArm.posX * 0.5);
		this.rightLeg.posX = (float)((double)this.rightLeg.posX * 0.5);
		this.leftLeg.posX = (float)((double)this.leftLeg.posX * 0.5);
		float g = 0.4F;
		if (this.rightArm.posX > 0.4F) {
			this.rightArm.posX = 0.4F;
		}

		if (this.leftArm.posX > 0.4F) {
			this.leftArm.posX = 0.4F;
		}

		if (this.rightArm.posX < -0.4F) {
			this.rightArm.posX = -0.4F;
		}

		if (this.leftArm.posX < -0.4F) {
			this.leftArm.posX = -0.4F;
		}

		if (this.rightLeg.posX > 0.4F) {
			this.rightLeg.posX = 0.4F;
		}

		if (this.leftLeg.posX > 0.4F) {
			this.leftLeg.posX = 0.4F;
		}

		if (this.rightLeg.posX < -0.4F) {
			this.rightLeg.posX = -0.4F;
		}

		if (this.leftLeg.posX < -0.4F) {
			this.leftLeg.posX = -0.4F;
		}

		if (this.carryingBlock) {
			this.rightArm.posX = -0.5F;
			this.leftArm.posX = -0.5F;
			this.rightArm.posZ = 0.05F;
			this.leftArm.posZ = -0.05F;
		}

		this.rightArm.pivotZ = 0.0F;
		this.leftArm.pivotZ = 0.0F;
		this.rightLeg.pivotZ = 0.0F;
		this.leftLeg.pivotZ = 0.0F;
		this.rightLeg.pivotY = -5.0F;
		this.leftLeg.pivotY = -5.0F;
		this.head.pivotZ = -0.0F;
		this.head.pivotY = -13.0F;
		this.hat.pivotX = this.head.pivotX;
		this.hat.pivotY = this.head.pivotY;
		this.hat.pivotZ = this.head.pivotZ;
		this.hat.posX = this.head.posX;
		this.hat.posY = this.head.posY;
		this.hat.posZ = this.head.posZ;
		if (this.angry) {
			float h = 1.0F;
			this.head.pivotY -= 5.0F;
		}
	}
}
