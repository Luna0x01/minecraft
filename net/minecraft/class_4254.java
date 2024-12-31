package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.math.MathHelper;

public class class_4254 extends EntityModel {
	private final ModelPart field_20915;
	private final ModelPart field_20916;
	private final ModelPart field_20917;
	private final ModelPart field_20918;

	public class_4254() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		float f = 18.0F;
		float g = -8.0F;
		this.field_20916 = new ModelPart(this, 22, 0);
		this.field_20916.addCuboid(-4.0F, -7.0F, 0.0F, 8, 7, 13);
		this.field_20916.setPivot(0.0F, 22.0F, -5.0F);
		ModelPart modelPart = new ModelPart(this, 51, 0);
		modelPart.addCuboid(-0.5F, 0.0F, 8.0F, 1, 4, 5);
		modelPart.posX = (float) (Math.PI / 3);
		this.field_20916.add(modelPart);
		ModelPart modelPart2 = new ModelPart(this, 48, 20);
		modelPart2.mirror = true;
		modelPart2.addCuboid(-0.5F, -4.0F, 0.0F, 1, 4, 7);
		modelPart2.setPivot(2.0F, -2.0F, 4.0F);
		modelPart2.posX = (float) (Math.PI / 3);
		modelPart2.posZ = (float) (Math.PI * 2.0 / 3.0);
		this.field_20916.add(modelPart2);
		ModelPart modelPart3 = new ModelPart(this, 48, 20);
		modelPart3.addCuboid(-0.5F, -4.0F, 0.0F, 1, 4, 7);
		modelPart3.setPivot(-2.0F, -2.0F, 4.0F);
		modelPart3.posX = (float) (Math.PI / 3);
		modelPart3.posZ = (float) (-Math.PI * 2.0 / 3.0);
		this.field_20916.add(modelPart3);
		this.field_20917 = new ModelPart(this, 0, 19);
		this.field_20917.addCuboid(-2.0F, -2.5F, 0.0F, 4, 5, 11);
		this.field_20917.setPivot(0.0F, -2.5F, 11.0F);
		this.field_20917.posX = -0.10471976F;
		this.field_20916.add(this.field_20917);
		this.field_20918 = new ModelPart(this, 19, 20);
		this.field_20918.addCuboid(-5.0F, -0.5F, 0.0F, 10, 1, 6);
		this.field_20918.setPivot(0.0F, 0.0F, 9.0F);
		this.field_20918.posX = 0.0F;
		this.field_20917.add(this.field_20918);
		this.field_20915 = new ModelPart(this, 0, 0);
		this.field_20915.addCuboid(-4.0F, -3.0F, -3.0F, 8, 7, 6);
		this.field_20915.setPivot(0.0F, -4.0F, -3.0F);
		ModelPart modelPart4 = new ModelPart(this, 0, 13);
		modelPart4.addCuboid(-1.0F, 2.0F, -7.0F, 2, 2, 4);
		this.field_20915.add(modelPart4);
		this.field_20916.add(this.field_20915);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.field_20916.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.field_20916.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_20916.posY = age * (float) (Math.PI / 180.0);
		if (entity instanceof DolphinEntity) {
			DolphinEntity dolphinEntity = (DolphinEntity)entity;
			if (dolphinEntity.velocityX != 0.0 || dolphinEntity.velocityZ != 0.0) {
				this.field_20916.posX = this.field_20916.posX + -0.05F + -0.05F * MathHelper.cos(tickDelta * 0.3F);
				this.field_20917.posX = -0.1F * MathHelper.cos(tickDelta * 0.3F);
				this.field_20918.posX = -0.2F * MathHelper.cos(tickDelta * 0.3F);
			}
		}
	}
}
