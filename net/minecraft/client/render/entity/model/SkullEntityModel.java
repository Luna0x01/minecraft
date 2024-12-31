package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class SkullEntityModel extends EntityModel {
	public ModelPart field_5131;

	public SkullEntityModel() {
		this(0, 35, 64, 64);
	}

	public SkullEntityModel(int i, int j, int k, int l) {
		this.textureWidth = k;
		this.textureHeight = l;
		this.field_5131 = new ModelPart(this, i, j);
		this.field_5131.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.field_5131.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_5131.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_5131.posY = age * (float) (Math.PI / 180.0);
		this.field_5131.posX = headPitch * (float) (Math.PI / 180.0);
	}
}
