package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class LeashEntityModel extends EntityModel {
	private final ModelPart field_6395;

	public LeashEntityModel() {
		this(0, 0, 32, 32);
	}

	public LeashEntityModel(int i, int j, int k, int l) {
		this.textureWidth = k;
		this.textureHeight = l;
		this.field_6395 = new ModelPart(this, i, j);
		this.field_6395.addCuboid(-3.0F, -6.0F, -3.0F, 6, 8, 6, 0.0F);
		this.field_6395.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_6395.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_6395.posY = age * (float) (Math.PI / 180.0);
		this.field_6395.posX = headPitch * (float) (Math.PI / 180.0);
	}
}
