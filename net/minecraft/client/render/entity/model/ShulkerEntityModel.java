package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

public class ShulkerEntityModel extends EntityModel {
	private final ModelPart field_13396;
	private final ModelPart field_13397;
	public ModelPart field_13395;

	public ShulkerEntityModel() {
		this.textureHeight = 64;
		this.textureWidth = 64;
		this.field_13397 = new ModelPart(this);
		this.field_13396 = new ModelPart(this);
		this.field_13395 = new ModelPart(this);
		this.field_13397.setTextureOffset(0, 0).addCuboid(-8.0F, -16.0F, -8.0F, 16, 12, 16);
		this.field_13397.setPivot(0.0F, 24.0F, 0.0F);
		this.field_13396.setTextureOffset(0, 28).addCuboid(-8.0F, -8.0F, -8.0F, 16, 8, 16);
		this.field_13396.setPivot(0.0F, 24.0F, 0.0F);
		this.field_13395.setTextureOffset(0, 52).addCuboid(-3.0F, 0.0F, -3.0F, 6, 6, 6);
		this.field_13395.setPivot(0.0F, 12.0F, 0.0F);
	}

	public int method_12225() {
		return 28;
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		ShulkerEntity shulkerEntity = (ShulkerEntity)entity;
		float f = tickDelta - (float)shulkerEntity.ticksAlive;
		float g = (0.5F + shulkerEntity.method_13220(f)) * (float) Math.PI;
		float h = -1.0F + MathHelper.sin(g);
		float i = 0.0F;
		if (g > (float) Math.PI) {
			i = MathHelper.sin(tickDelta * 0.1F) * 0.7F;
		}

		this.field_13397.setPivot(0.0F, 16.0F + MathHelper.sin(g) * 8.0F + i, 0.0F);
		if (shulkerEntity.method_13220(f) > 0.3F) {
			this.field_13397.posY = h * h * h * h * (float) Math.PI * 0.125F;
		} else {
			this.field_13397.posY = 0.0F;
		}

		this.field_13395.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_13395.posY = age * (float) (Math.PI / 180.0);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.field_13396.render(scale);
		this.field_13397.render(scale);
	}
}
