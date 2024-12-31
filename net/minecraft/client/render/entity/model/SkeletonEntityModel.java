package net.minecraft.client.render.entity.model;

import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class SkeletonEntityModel extends BiPedModel {
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
		this.field_13385 = BiPedModel.class_2850.EMPTY;
		this.field_13384 = BiPedModel.class_2850.EMPTY;
		ItemStack itemStack = entity.getStackInHand(Hand.MAIN_HAND);
		if (itemStack != null && itemStack.getItem() == Items.BOW && ((SkeletonEntity)entity).method_13239()) {
			if (entity.getDurability() == HandOption.RIGHT) {
				this.field_13385 = BiPedModel.class_2850.BOW_AND_ARROW;
			} else {
				this.field_13384 = BiPedModel.class_2850.BOW_AND_ARROW;
			}
		}

		super.animateModel(entity, limbAngle, limbDistance, tickDelta);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		ItemStack itemStack = ((LivingEntity)entity).getMainHandStack();
		SkeletonEntity skeletonEntity = (SkeletonEntity)entity;
		if (skeletonEntity.method_13239() && (itemStack == null || itemStack.getItem() != Items.BOW)) {
			float f = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
			float g = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
			this.rightArm.posZ = 0.0F;
			this.leftArm.posZ = 0.0F;
			this.rightArm.posY = -(0.1F - f * 0.6F);
			this.leftArm.posY = 0.1F - f * 0.6F;
			this.rightArm.posX = (float) (-Math.PI / 2);
			this.leftArm.posX = (float) (-Math.PI / 2);
			this.rightArm.posX -= f * 1.2F - g * 0.4F;
			this.leftArm.posX -= f * 1.2F - g * 0.4F;
			this.rightArm.posZ = this.rightArm.posZ + MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F;
			this.leftArm.posZ = this.leftArm.posZ - (MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F);
			this.rightArm.posX = this.rightArm.posX + MathHelper.sin(tickDelta * 0.067F) * 0.05F;
			this.leftArm.posX = this.leftArm.posX - MathHelper.sin(tickDelta * 0.067F) * 0.05F;
		}
	}

	@Override
	public void method_12221(float f, HandOption handOption) {
		float g = handOption == HandOption.RIGHT ? 1.0F : -1.0F;
		ModelPart modelPart = this.method_12223(handOption);
		modelPart.pivotX += g;
		modelPart.preRender(f);
		modelPart.pivotX -= g;
	}
}
