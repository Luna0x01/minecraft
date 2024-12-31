package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
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
		} else {
			this.head = new ModelPart(this);
			this.head.setPivot(0.0F, 0.0F + g, 0.0F);
			this.head.setTextureOffset(0, 32).addCuboid(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
			this.head.setTextureOffset(24, 32).addCuboid(-1.0F, -3.0F, -6.0F, 2, 4, 2, f);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
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
