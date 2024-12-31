package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;

public class WitherEntityModel extends EntityModel {
	private final ModelPart[] field_5136;
	private final ModelPart[] field_5137;

	public WitherEntityModel(float f) {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_5136 = new ModelPart[3];
		this.field_5136[0] = new ModelPart(this, 0, 16);
		this.field_5136[0].addCuboid(-10.0F, 3.9F, -0.5F, 20, 3, 3, f);
		this.field_5136[1] = new ModelPart(this).setTextureSize(this.textureWidth, this.textureHeight);
		this.field_5136[1].setPivot(-2.0F, 6.9F, -0.5F);
		this.field_5136[1].setTextureOffset(0, 22).addCuboid(0.0F, 0.0F, 0.0F, 3, 10, 3, f);
		this.field_5136[1].setTextureOffset(24, 22).addCuboid(-4.0F, 1.5F, 0.5F, 11, 2, 2, f);
		this.field_5136[1].setTextureOffset(24, 22).addCuboid(-4.0F, 4.0F, 0.5F, 11, 2, 2, f);
		this.field_5136[1].setTextureOffset(24, 22).addCuboid(-4.0F, 6.5F, 0.5F, 11, 2, 2, f);
		this.field_5136[2] = new ModelPart(this, 12, 22);
		this.field_5136[2].addCuboid(0.0F, 0.0F, 0.0F, 3, 6, 3, f);
		this.field_5137 = new ModelPart[3];
		this.field_5137[0] = new ModelPart(this, 0, 0);
		this.field_5137[0].addCuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8, f);
		this.field_5137[1] = new ModelPart(this, 32, 0);
		this.field_5137[1].addCuboid(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
		this.field_5137[1].pivotX = -8.0F;
		this.field_5137[1].pivotY = 4.0F;
		this.field_5137[2] = new ModelPart(this, 32, 0);
		this.field_5137[2].addCuboid(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
		this.field_5137[2].pivotX = 10.0F;
		this.field_5137[2].pivotY = 4.0F;
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);

		for (ModelPart modelPart : this.field_5137) {
			modelPart.render(scale);
		}

		for (ModelPart modelPart2 : this.field_5136) {
			modelPart2.render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = MathHelper.cos(tickDelta * 0.1F);
		this.field_5136[1].posX = (0.065F + 0.05F * f) * (float) Math.PI;
		this.field_5136[2].setPivot(-2.0F, 6.9F + MathHelper.cos(this.field_5136[1].posX) * 10.0F, -0.5F + MathHelper.sin(this.field_5136[1].posX) * 10.0F);
		this.field_5136[2].posX = (0.265F + 0.1F * f) * (float) Math.PI;
		this.field_5137[0].posY = age * (float) (Math.PI / 180.0);
		this.field_5137[0].posX = headPitch * (float) (Math.PI / 180.0);
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		WitherEntity witherEntity = (WitherEntity)entity;

		for (int i = 1; i < 3; i++) {
			this.field_5137[i].posY = (witherEntity.getHeadYaw(i - 1) - entity.bodyYaw) * (float) (Math.PI / 180.0);
			this.field_5137[i].posX = witherEntity.getHeadPitch(i - 1) * (float) (Math.PI / 180.0);
		}
	}
}
