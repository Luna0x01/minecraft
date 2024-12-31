package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class ElytraModel extends EntityModel {
	private final ModelPart field_13382;
	private final ModelPart field_13383 = new ModelPart(this, 22, 0);

	public ElytraModel() {
		this.field_13383.addCuboid(-10.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
		this.field_13382 = new ModelPart(this, 22, 0);
		this.field_13382.mirror = true;
		this.field_13382.addCuboid(0.0F, 0.0F, 0.0F, 10, 20, 2, 1.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableCull();
		if (entity instanceof LivingEntity && ((LivingEntity)entity).isBaby()) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 1.5F, -0.1F);
			this.field_13383.render(scale);
			this.field_13382.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.field_13383.render(scale);
			this.field_13382.render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		float f = (float) (Math.PI / 12);
		float g = (float) (-Math.PI / 12);
		float h = 0.0F;
		float i = 0.0F;
		if (entity instanceof LivingEntity && ((LivingEntity)entity).method_13055()) {
			float j = 1.0F;
			if (entity.velocityY < 0.0) {
				Vec3d vec3d = new Vec3d(entity.velocityX, entity.velocityY, entity.velocityZ).normalize();
				j = 1.0F - (float)Math.pow(-vec3d.y, 1.5);
			}

			f = j * (float) (Math.PI / 9) + (1.0F - j) * f;
			g = j * (float) (-Math.PI / 2) + (1.0F - j) * g;
		} else if (entity.isSneaking()) {
			f = (float) (Math.PI * 2.0 / 9.0);
			g = (float) (-Math.PI / 4);
			h = 3.0F;
			i = 0.08726646F;
		}

		this.field_13383.pivotX = 5.0F;
		this.field_13383.pivotY = h;
		if (entity instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)entity;
			abstractClientPlayerEntity.field_13449 = (float)((double)abstractClientPlayerEntity.field_13449 + (double)(f - abstractClientPlayerEntity.field_13449) * 0.1);
			abstractClientPlayerEntity.field_13450 = (float)((double)abstractClientPlayerEntity.field_13450 + (double)(i - abstractClientPlayerEntity.field_13450) * 0.1);
			abstractClientPlayerEntity.field_13451 = (float)((double)abstractClientPlayerEntity.field_13451 + (double)(g - abstractClientPlayerEntity.field_13451) * 0.1);
			this.field_13383.posX = abstractClientPlayerEntity.field_13449;
			this.field_13383.posY = abstractClientPlayerEntity.field_13450;
			this.field_13383.posZ = abstractClientPlayerEntity.field_13451;
		} else {
			this.field_13383.posX = f;
			this.field_13383.posZ = g;
			this.field_13383.posY = i;
		}

		this.field_13382.pivotX = -this.field_13383.pivotX;
		this.field_13382.posY = -this.field_13383.posY;
		this.field_13382.pivotY = this.field_13383.pivotY;
		this.field_13382.posX = this.field_13383.posX;
		this.field_13382.posZ = -this.field_13383.posZ;
	}
}
