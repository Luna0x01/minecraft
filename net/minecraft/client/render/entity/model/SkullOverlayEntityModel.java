package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class SkullOverlayEntityModel extends SkullEntityModel {
	private final ModelPart field_10555 = new ModelPart(this, 32, 0);

	public SkullOverlayEntityModel() {
		super(0, 0, 64, 64);
		this.field_10555.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F);
		this.field_10555.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		super.render(entity, handSwing, handSwingAmount, tickDelta, age, headPitch, scale);
		this.field_10555.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_10555.posY = this.field_5131.posY;
		this.field_10555.posX = this.field_5131.posX;
	}
}
