package net.minecraft;

import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.client.render.entity.model.BiPedModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class class_4185 extends AbstractZombieModel {
	public class_4185(float f, float g, int i, int j) {
		super(f, g, i, j);
		this.rightArm = new ModelPart(this, 32, 48);
		this.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.rightArm.setPivot(-5.0F, 2.0F + g, 0.0F);
		this.rightLeg = new ModelPart(this, 16, 48);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.rightLeg.setPivot(-1.9F, 12.0F + g, 0.0F);
	}

	public class_4185(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		this.field_13385 = BiPedModel.class_2850.EMPTY;
		this.field_13384 = BiPedModel.class_2850.EMPTY;
		ItemStack itemStack = entity.getStackInHand(Hand.MAIN_HAND);
		if (itemStack.getItem() == Items.TRIDENT && ((DrownedEntity)entity).method_13247()) {
			if (entity.getDurability() == HandOption.RIGHT) {
				this.field_13385 = BiPedModel.class_2850.THROW_SPEAR;
			} else {
				this.field_13384 = BiPedModel.class_2850.THROW_SPEAR;
			}
		}

		super.animateModel(entity, limbAngle, limbDistance, tickDelta);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		if (this.field_13384 == BiPedModel.class_2850.THROW_SPEAR) {
			this.leftArm.posX = this.leftArm.posX * 0.5F - (float) Math.PI;
			this.leftArm.posY = 0.0F;
		}

		if (this.field_13385 == BiPedModel.class_2850.THROW_SPEAR) {
			this.rightArm.posX = this.rightArm.posX * 0.5F - (float) Math.PI;
			this.rightArm.posY = 0.0F;
		}

		if (this.field_20533 > 0.0F) {
			this.rightArm.posX = this.method_18915(this.rightArm.posX, (float) (-Math.PI * 4.0 / 5.0), this.field_20533)
				+ this.field_20533 * 0.35F * MathHelper.sin(0.1F * tickDelta);
			this.leftArm.posX = this.method_18915(this.leftArm.posX, (float) (-Math.PI * 4.0 / 5.0), this.field_20533)
				- this.field_20533 * 0.35F * MathHelper.sin(0.1F * tickDelta);
			this.rightArm.posZ = this.method_18915(this.rightArm.posZ, -0.15F, this.field_20533);
			this.leftArm.posZ = this.method_18915(this.leftArm.posZ, 0.15F, this.field_20533);
			this.leftLeg.posX = this.leftLeg.posX - this.field_20533 * 0.55F * MathHelper.sin(0.1F * tickDelta);
			this.rightLeg.posX = this.rightLeg.posX + this.field_20533 * 0.55F * MathHelper.sin(0.1F * tickDelta);
			this.head.posX = 0.0F;
		}
	}
}
