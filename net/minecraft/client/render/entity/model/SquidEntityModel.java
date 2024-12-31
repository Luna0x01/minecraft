package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class SquidEntityModel extends EntityModel {
	private final ModelPart field_1546;
	private final ModelPart[] field_1547 = new ModelPart[8];

	public SquidEntityModel() {
		int i = -16;
		this.field_1546 = new ModelPart(this, 0, 0);
		this.field_1546.addCuboid(-6.0F, -8.0F, -6.0F, 12, 16, 12);
		this.field_1546.pivotY += 8.0F;

		for (int j = 0; j < this.field_1547.length; j++) {
			this.field_1547[j] = new ModelPart(this, 48, 0);
			double d = (double)j * Math.PI * 2.0 / (double)this.field_1547.length;
			float f = (float)Math.cos(d) * 5.0F;
			float g = (float)Math.sin(d) * 5.0F;
			this.field_1547[j].addCuboid(-1.0F, 0.0F, -1.0F, 2, 18, 2);
			this.field_1547[j].pivotX = f;
			this.field_1547[j].pivotZ = g;
			this.field_1547[j].pivotY = 15.0F;
			d = (double)j * Math.PI * -2.0 / (double)this.field_1547.length + (Math.PI / 2);
			this.field_1547[j].posY = (float)d;
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		for (ModelPart modelPart : this.field_1547) {
			modelPart.posX = tickDelta;
		}
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_1546.render(scale);

		for (ModelPart modelPart : this.field_1547) {
			modelPart.render(scale);
		}
	}
}
