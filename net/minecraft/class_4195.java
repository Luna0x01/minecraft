package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

public class class_4195 extends EntityModel {
	private final ModelPart field_20587;
	private final ModelPart field_20588;
	private final ModelPart field_20589;

	public class_4195() {
		this.textureHeight = 64;
		this.textureWidth = 64;
		this.field_20588 = new ModelPart(this);
		this.field_20587 = new ModelPart(this);
		this.field_20589 = new ModelPart(this);
		this.field_20588.setTextureOffset(0, 0).addCuboid(-8.0F, -16.0F, -8.0F, 16, 12, 16);
		this.field_20588.setPivot(0.0F, 24.0F, 0.0F);
		this.field_20587.setTextureOffset(0, 28).addCuboid(-8.0F, -8.0F, -8.0F, 16, 8, 16);
		this.field_20587.setPivot(0.0F, 24.0F, 0.0F);
		this.field_20589.setTextureOffset(0, 52).addCuboid(-3.0F, 0.0F, -3.0F, 6, 6, 6);
		this.field_20589.setPivot(0.0F, 12.0F, 0.0F);
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

		this.field_20588.setPivot(0.0F, 16.0F + MathHelper.sin(g) * 8.0F + i, 0.0F);
		if (shulkerEntity.method_13220(f) > 0.3F) {
			this.field_20588.posY = h * h * h * h * (float) Math.PI * 0.125F;
		} else {
			this.field_20588.posY = 0.0F;
		}

		this.field_20589.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_20589.posY = age * (float) (Math.PI / 180.0);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.field_20587.render(scale);
		this.field_20588.render(scale);
	}

	public ModelPart method_18934() {
		return this.field_20587;
	}

	public ModelPart method_18935() {
		return this.field_20588;
	}

	public ModelPart method_18936() {
		return this.field_20589;
	}
}
