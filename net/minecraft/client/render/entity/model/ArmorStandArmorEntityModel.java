package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class ArmorStandArmorEntityModel extends BiPedModel {
	public ArmorStandArmorEntityModel() {
		this(0.0F);
	}

	public ArmorStandArmorEntityModel(float f) {
		this(f, 64, 32);
	}

	protected ArmorStandArmorEntityModel(float f, int i, int j) {
		super(f, 0.0F, i, j);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		if (entity instanceof ArmorStandEntity) {
			ArmorStandEntity armorStandEntity = (ArmorStandEntity)entity;
			this.head.posX = (float) (Math.PI / 180.0) * armorStandEntity.getHeadAngle().getPitch();
			this.head.posY = (float) (Math.PI / 180.0) * armorStandEntity.getHeadAngle().getYaw();
			this.head.posZ = (float) (Math.PI / 180.0) * armorStandEntity.getHeadAngle().getRoll();
			this.head.setPivot(0.0F, 1.0F, 0.0F);
			this.body.posX = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getPitch();
			this.body.posY = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getYaw();
			this.body.posZ = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getRoll();
			this.leftArm.posX = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmAngle().getPitch();
			this.leftArm.posY = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmAngle().getYaw();
			this.leftArm.posZ = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmAngle().getRoll();
			this.rightArm.posX = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmAngle().getPitch();
			this.rightArm.posY = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmAngle().getYaw();
			this.rightArm.posZ = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmAngle().getRoll();
			this.leftLeg.posX = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegAngle().getPitch();
			this.leftLeg.posY = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegAngle().getYaw();
			this.leftLeg.posZ = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegAngle().getRoll();
			this.leftLeg.setPivot(1.9F, 11.0F, 0.0F);
			this.rightLeg.posX = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegAngle().getPitch();
			this.rightLeg.posY = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegAngle().getYaw();
			this.rightLeg.posZ = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegAngle().getRoll();
			this.rightLeg.setPivot(-1.9F, 11.0F, 0.0F);
			copyModelPart(this.head, this.hat);
		}
	}
}
