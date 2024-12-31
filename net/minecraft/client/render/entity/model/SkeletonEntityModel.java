package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SkeletonEntity;

public class SkeletonEntityModel extends AbstractZombieModel {
	public SkeletonEntityModel() {
		this(0.0F, false);
	}

	public SkeletonEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, 32);
		if (!bl) {
			this.rightArm = new ModelPart(this, 40, 16);
			this.rightArm.addCuboid(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
			this.leftArm = new ModelPart(this, 40, 16);
			this.leftArm.mirror = true;
			this.leftArm.addCuboid(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
			this.rightLeg = new ModelPart(this, 0, 16);
			this.rightLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
			this.leftLeg = new ModelPart(this, 0, 16);
			this.leftLeg.mirror = true;
			this.leftLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
		}
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		this.aiming = ((SkeletonEntity)entity).getType() == 1;
		super.animateModel(entity, limbAngle, limbDistance, tickDelta);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
	}
}
