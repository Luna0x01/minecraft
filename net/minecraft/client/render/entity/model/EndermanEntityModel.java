package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.LivingEntity;

public class EndermanEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	public boolean carryingBlock;
	public boolean angry;

	public EndermanEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData() {
		float f = -14.0F;
		ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, -14.0F);
		ModelPartData modelPartData = modelData.getRoot();
		ModelTransform modelTransform = ModelTransform.pivot(0.0F, -13.0F, 0.0F);
		modelPartData.addChild("hat", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(-0.5F)), modelTransform);
		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), modelTransform);
		modelPartData.addChild("body", ModelPartBuilder.create().uv(32, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), ModelTransform.pivot(0.0F, -14.0F, 0.0F));
		modelPartData.addChild(
			"right_arm", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), ModelTransform.pivot(-5.0F, -12.0F, 0.0F)
		);
		modelPartData.addChild(
			"left_arm", ModelPartBuilder.create().uv(56, 0).mirrored().cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F), ModelTransform.pivot(5.0F, -12.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_leg", ModelPartBuilder.create().uv(56, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), ModelTransform.pivot(-2.0F, -5.0F, 0.0F)
		);
		modelPartData.addChild(
			"left_leg", ModelPartBuilder.create().uv(56, 0).mirrored().cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F), ModelTransform.pivot(2.0F, -5.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Override
	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		super.setAngles(livingEntity, f, g, h, i, j);
		this.head.visible = true;
		int k = -14;
		this.body.pitch = 0.0F;
		this.body.pivotY = -14.0F;
		this.body.pivotZ = -0.0F;
		this.rightLeg.pitch -= 0.0F;
		this.leftLeg.pitch -= 0.0F;
		this.rightArm.pitch = (float)((double)this.rightArm.pitch * 0.5);
		this.leftArm.pitch = (float)((double)this.leftArm.pitch * 0.5);
		this.rightLeg.pitch = (float)((double)this.rightLeg.pitch * 0.5);
		this.leftLeg.pitch = (float)((double)this.leftLeg.pitch * 0.5);
		float l = 0.4F;
		if (this.rightArm.pitch > 0.4F) {
			this.rightArm.pitch = 0.4F;
		}

		if (this.leftArm.pitch > 0.4F) {
			this.leftArm.pitch = 0.4F;
		}

		if (this.rightArm.pitch < -0.4F) {
			this.rightArm.pitch = -0.4F;
		}

		if (this.leftArm.pitch < -0.4F) {
			this.leftArm.pitch = -0.4F;
		}

		if (this.rightLeg.pitch > 0.4F) {
			this.rightLeg.pitch = 0.4F;
		}

		if (this.leftLeg.pitch > 0.4F) {
			this.leftLeg.pitch = 0.4F;
		}

		if (this.rightLeg.pitch < -0.4F) {
			this.rightLeg.pitch = -0.4F;
		}

		if (this.leftLeg.pitch < -0.4F) {
			this.leftLeg.pitch = -0.4F;
		}

		if (this.carryingBlock) {
			this.rightArm.pitch = -0.5F;
			this.leftArm.pitch = -0.5F;
			this.rightArm.roll = 0.05F;
			this.leftArm.roll = -0.05F;
		}

		this.rightLeg.pivotZ = 0.0F;
		this.leftLeg.pivotZ = 0.0F;
		this.rightLeg.pivotY = -5.0F;
		this.leftLeg.pivotY = -5.0F;
		this.head.pivotZ = -0.0F;
		this.head.pivotY = -13.0F;
		this.hat.pivotX = this.head.pivotX;
		this.hat.pivotY = this.head.pivotY;
		this.hat.pivotZ = this.head.pivotZ;
		this.hat.pitch = this.head.pitch;
		this.hat.yaw = this.head.yaw;
		this.hat.roll = this.head.roll;
		if (this.angry) {
			float m = 1.0F;
			this.head.pivotY -= 5.0F;
		}

		int n = -14;
		this.rightArm.setPivot(-5.0F, -12.0F, 0.0F);
		this.leftArm.setPivot(5.0F, -12.0F, 0.0F);
	}
}
