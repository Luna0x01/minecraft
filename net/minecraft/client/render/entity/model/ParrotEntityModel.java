package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ParrotEntity;
import net.minecraft.util.math.MathHelper;

public class ParrotEntityModel extends EntityModel {
	private final ModelPart field_16094;
	private final ModelPart field_16095;
	private final ModelPart field_16096;
	private final ModelPart field_16097;
	private final ModelPart field_16098;
	private final ModelPart field_16099;
	private final ModelPart field_16100;
	private final ModelPart field_16101;
	private final ModelPart field_16102;
	private final ModelPart field_16103;
	private final ModelPart field_16104;
	private ParrotEntityModel.Pose pose = ParrotEntityModel.Pose.STANDING;

	public ParrotEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.field_16094 = new ModelPart(this, 2, 8);
		this.field_16094.addCuboid(-1.5F, 0.0F, -1.5F, 3, 6, 3);
		this.field_16094.setPivot(0.0F, 16.5F, -3.0F);
		this.field_16095 = new ModelPart(this, 22, 1);
		this.field_16095.addCuboid(-1.5F, -1.0F, -1.0F, 3, 4, 1);
		this.field_16095.setPivot(0.0F, 21.07F, 1.16F);
		this.field_16096 = new ModelPart(this, 19, 8);
		this.field_16096.addCuboid(-0.5F, 0.0F, -1.5F, 1, 5, 3);
		this.field_16096.setPivot(1.5F, 16.94F, -2.76F);
		this.field_16097 = new ModelPart(this, 19, 8);
		this.field_16097.addCuboid(-0.5F, 0.0F, -1.5F, 1, 5, 3);
		this.field_16097.setPivot(-1.5F, 16.94F, -2.76F);
		this.field_16098 = new ModelPart(this, 2, 2);
		this.field_16098.addCuboid(-1.0F, -1.5F, -1.0F, 2, 3, 2);
		this.field_16098.setPivot(0.0F, 15.69F, -2.76F);
		this.field_16099 = new ModelPart(this, 10, 0);
		this.field_16099.addCuboid(-1.0F, -0.5F, -2.0F, 2, 1, 4);
		this.field_16099.setPivot(0.0F, -2.0F, -1.0F);
		this.field_16098.add(this.field_16099);
		this.field_16100 = new ModelPart(this, 11, 7);
		this.field_16100.addCuboid(-0.5F, -1.0F, -0.5F, 1, 2, 1);
		this.field_16100.setPivot(0.0F, -0.5F, -1.5F);
		this.field_16098.add(this.field_16100);
		this.field_16101 = new ModelPart(this, 16, 7);
		this.field_16101.addCuboid(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		this.field_16101.setPivot(0.0F, -1.75F, -2.45F);
		this.field_16098.add(this.field_16101);
		this.field_16102 = new ModelPart(this, 2, 18);
		this.field_16102.addCuboid(0.0F, -4.0F, -2.0F, 0, 5, 4);
		this.field_16102.setPivot(0.0F, -2.15F, 0.15F);
		this.field_16098.add(this.field_16102);
		this.field_16103 = new ModelPart(this, 14, 18);
		this.field_16103.addCuboid(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		this.field_16103.setPivot(1.0F, 22.0F, -1.05F);
		this.field_16104 = new ModelPart(this, 14, 18);
		this.field_16104.addCuboid(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		this.field_16104.setPivot(-1.0F, 22.0F, -1.05F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.field_16094.render(scale);
		this.field_16096.render(scale);
		this.field_16097.render(scale);
		this.field_16095.render(scale);
		this.field_16098.render(scale);
		this.field_16103.render(scale);
		this.field_16104.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = tickDelta * 0.3F;
		this.field_16098.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_16098.posY = age * (float) (Math.PI / 180.0);
		this.field_16098.posZ = 0.0F;
		this.field_16098.pivotX = 0.0F;
		this.field_16094.pivotX = 0.0F;
		this.field_16095.pivotX = 0.0F;
		this.field_16097.pivotX = -1.5F;
		this.field_16096.pivotX = 1.5F;
		if (this.pose != ParrotEntityModel.Pose.SITTING) {
			if (this.pose == ParrotEntityModel.Pose.PARTY) {
				float g = MathHelper.cos((float)entity.ticksAlive);
				float h = MathHelper.sin((float)entity.ticksAlive);
				this.field_16098.pivotX = g;
				this.field_16098.pivotY = 15.69F + h;
				this.field_16098.posX = 0.0F;
				this.field_16098.posY = 0.0F;
				this.field_16098.posZ = MathHelper.sin((float)entity.ticksAlive) * 0.4F;
				this.field_16094.pivotX = g;
				this.field_16094.pivotY = 16.5F + h;
				this.field_16096.posZ = -0.0873F - tickDelta;
				this.field_16096.pivotX = 1.5F + g;
				this.field_16096.pivotY = 16.94F + h;
				this.field_16097.posZ = 0.0873F + tickDelta;
				this.field_16097.pivotX = -1.5F + g;
				this.field_16097.pivotY = 16.94F + h;
				this.field_16095.pivotX = g;
				this.field_16095.pivotY = 21.07F + h;
			} else {
				if (this.pose == ParrotEntityModel.Pose.STANDING) {
					this.field_16103.posX = this.field_16103.posX + MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
					this.field_16104.posX = this.field_16104.posX + MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
				}

				this.field_16098.pivotY = 15.69F + f;
				this.field_16095.posX = 1.015F + MathHelper.cos(handSwing * 0.6662F) * 0.3F * handSwingAmount;
				this.field_16095.pivotY = 21.07F + f;
				this.field_16094.pivotY = 16.5F + f;
				this.field_16096.posZ = -0.0873F - tickDelta;
				this.field_16096.pivotY = 16.94F + f;
				this.field_16097.posZ = 0.0873F + tickDelta;
				this.field_16097.pivotY = 16.94F + f;
				this.field_16103.pivotY = 22.0F + f;
				this.field_16104.pivotY = 22.0F + f;
			}
		}
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		this.field_16102.posX = -0.2214F;
		this.field_16094.posX = 0.4937F;
		this.field_16096.posX = -0.6981F;
		this.field_16096.posY = (float) -Math.PI;
		this.field_16097.posX = -0.6981F;
		this.field_16097.posY = (float) -Math.PI;
		this.field_16103.posX = -0.0299F;
		this.field_16104.posX = -0.0299F;
		this.field_16103.pivotY = 22.0F;
		this.field_16104.pivotY = 22.0F;
		if (entity instanceof ParrotEntity) {
			ParrotEntity parrotEntity = (ParrotEntity)entity;
			if (parrotEntity.method_14106()) {
				this.field_16103.posZ = (float) (-Math.PI / 9);
				this.field_16104.posZ = (float) (Math.PI / 9);
				this.pose = ParrotEntityModel.Pose.PARTY;
				return;
			}

			if (parrotEntity.isSitting()) {
				float f = 1.9F;
				this.field_16098.pivotY = 17.59F;
				this.field_16095.posX = 1.5388988F;
				this.field_16095.pivotY = 22.97F;
				this.field_16094.pivotY = 18.4F;
				this.field_16096.posZ = -0.0873F;
				this.field_16096.pivotY = 18.84F;
				this.field_16097.posZ = 0.0873F;
				this.field_16097.pivotY = 18.84F;
				this.field_16103.pivotY++;
				this.field_16104.pivotY++;
				this.field_16103.posX++;
				this.field_16104.posX++;
				this.pose = ParrotEntityModel.Pose.SITTING;
			} else if (parrotEntity.method_14101()) {
				this.field_16103.posX += (float) (Math.PI * 2.0 / 9.0);
				this.field_16104.posX += (float) (Math.PI * 2.0 / 9.0);
				this.pose = ParrotEntityModel.Pose.FLYING;
			} else {
				this.pose = ParrotEntityModel.Pose.STANDING;
			}

			this.field_16103.posZ = 0.0F;
			this.field_16104.posZ = 0.0F;
		} else {
			this.pose = ParrotEntityModel.Pose.ON_SHOULDER;
		}
	}

	static enum Pose {
		FLYING,
		STANDING,
		SITTING,
		PARTY,
		ON_SHOULDER;
	}
}
