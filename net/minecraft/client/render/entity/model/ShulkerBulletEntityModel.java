package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class ShulkerBulletEntityModel extends EntityModel {
	public ModelPart field_13394;

	public ShulkerBulletEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.field_13394 = new ModelPart(this);
		this.field_13394.setTextureOffset(0, 0).addCuboid(-4.0F, -4.0F, -1.0F, 8, 8, 2, 0.0F);
		this.field_13394.setTextureOffset(0, 10).addCuboid(-1.0F, -4.0F, -4.0F, 2, 8, 8, 0.0F);
		this.field_13394.setTextureOffset(20, 0).addCuboid(-4.0F, -1.0F, -4.0F, 8, 2, 8, 0.0F);
		this.field_13394.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_13394.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_13394.posY = age * (float) (Math.PI / 180.0);
		this.field_13394.posX = headPitch * (float) (Math.PI / 180.0);
	}
}
