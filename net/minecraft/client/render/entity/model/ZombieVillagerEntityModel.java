package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;

public class ZombieVillagerEntityModel extends BiPedModel {
	public ZombieVillagerEntityModel() {
		this(0.0F, 0.0F, false);
	}

	public ZombieVillagerEntityModel(float f, float g, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
		if (bl) {
			this.head = new ModelPart(this, 0, 0);
			this.head.addCuboid(-4.0F, -10.0F, -4.0F, 8, 8, 8, f);
			this.head.setPivot(0.0F, 0.0F + g, 0.0F);
			this.body = new ModelPart(this, 16, 16);
			this.body.setPivot(0.0F, 0.0F + g, 0.0F);
			this.body.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.1F);
			this.rightLeg = new ModelPart(this, 0, 16);
			this.rightLeg.setPivot(-2.0F, 12.0F + g, 0.0F);
			this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.1F);
			this.leftLeg = new ModelPart(this, 0, 16);
			this.leftLeg.mirror = true;
			this.leftLeg.setPivot(2.0F, 12.0F + g, 0.0F);
			this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.1F);
		} else {
			this.head = new ModelPart(this, 0, 0);
			this.head.setPivot(0.0F, g, 0.0F);
			this.head.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
			this.head.setTextureOffset(24, 0).addCuboid(-1.0F, -3.0F, -6.0F, 2, 4, 2, f);
			this.body = new ModelPart(this, 16, 20);
			this.body.setPivot(0.0F, 0.0F + g, 0.0F);
			this.body.addCuboid(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
			this.body.setTextureOffset(0, 38).addCuboid(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.05F);
			this.rightArm = new ModelPart(this, 44, 38);
			this.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.rightArm.setPivot(-5.0F, 2.0F + g, 0.0F);
			this.leftArm = new ModelPart(this, 44, 38);
			this.leftArm.mirror = true;
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.leftArm.setPivot(5.0F, 2.0F + g, 0.0F);
			this.rightLeg = new ModelPart(this, 0, 22);
			this.rightLeg.setPivot(-2.0F, 12.0F + g, 0.0F);
			this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
			this.leftLeg = new ModelPart(this, 0, 22);
			this.leftLeg.mirror = true;
			this.leftLeg.setPivot(2.0F, 12.0F + g, 0.0F);
			this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		ZombieEntity zombieEntity = (ZombieEntity)entity;
		float f = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
		float g = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
		this.rightArm.posZ = 0.0F;
		this.leftArm.posZ = 0.0F;
		this.rightArm.posY = -(0.1F - f * 0.6F);
		this.leftArm.posY = 0.1F - f * 0.6F;
		float h = (float) -Math.PI / (zombieEntity.method_13247() ? 1.5F : 2.25F);
		this.rightArm.posX = h;
		this.leftArm.posX = h;
		this.rightArm.posX += f * 1.2F - g * 0.4F;
		this.leftArm.posX += f * 1.2F - g * 0.4F;
		this.rightArm.posZ = this.rightArm.posZ + MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F;
		this.leftArm.posZ = this.leftArm.posZ - (MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F);
		this.rightArm.posX = this.rightArm.posX + MathHelper.sin(tickDelta * 0.067F) * 0.05F;
		this.leftArm.posX = this.leftArm.posX - MathHelper.sin(tickDelta * 0.067F) * 0.05F;
	}
}
