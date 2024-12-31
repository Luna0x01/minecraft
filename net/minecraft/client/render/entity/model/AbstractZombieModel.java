package net.minecraft.client.render.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;

public class AbstractZombieModel extends BiPedModel {
	public AbstractZombieModel() {
		this(0.0F, false);
	}

	protected AbstractZombieModel(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	public AbstractZombieModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		boolean bl = entity instanceof ZombieEntity && ((ZombieEntity)entity).method_13247();
		float f = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
		float g = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
		this.rightArm.posZ = 0.0F;
		this.leftArm.posZ = 0.0F;
		this.rightArm.posY = -(0.1F - f * 0.6F);
		this.leftArm.posY = 0.1F - f * 0.6F;
		float h = (float) -Math.PI / (bl ? 1.5F : 2.25F);
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
