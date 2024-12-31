package net.minecraft;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class class_4182 extends class_4180 {
	private final ModelPart field_20524 = new ModelPart(this, 26, 21);
	private final ModelPart field_20525;

	public class_4182() {
		this.field_20524.addCuboid(-4.0F, 0.0F, -2.0F, 8, 8, 3);
		this.field_20525 = new ModelPart(this, 26, 21);
		this.field_20525.addCuboid(-4.0F, 0.0F, -2.0F, 8, 8, 3);
		this.field_20524.posY = (float) (-Math.PI / 2);
		this.field_20525.posY = (float) (Math.PI / 2);
		this.field_20524.setPivot(6.0F, -8.0F, 0.0F);
		this.field_20525.setPivot(-6.0F, -8.0F, 0.0F);
		this.field_20511.add(this.field_20524);
		this.field_20511.add(this.field_20525);
	}

	@Override
	protected void method_18901(ModelPart modelPart) {
		ModelPart modelPart2 = new ModelPart(this, 0, 12);
		modelPart2.addCuboid(-1.0F, -7.0F, 0.0F, 2, 7, 1);
		modelPart2.setPivot(1.25F, -10.0F, 4.0F);
		ModelPart modelPart3 = new ModelPart(this, 0, 12);
		modelPart3.addCuboid(-1.0F, -7.0F, 0.0F, 2, 7, 1);
		modelPart3.setPivot(-1.25F, -10.0F, 4.0F);
		modelPart2.posX = (float) (Math.PI / 12);
		modelPart2.posZ = (float) (Math.PI / 12);
		modelPart3.posX = (float) (Math.PI / 12);
		modelPart3.posZ = (float) (-Math.PI / 12);
		modelPart.add(modelPart2);
		modelPart.add(modelPart3);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		if (((class_3135)entity).method_13963()) {
			this.field_20524.visible = true;
			this.field_20525.visible = true;
		} else {
			this.field_20524.visible = false;
			this.field_20525.visible = false;
		}

		super.render(entity, handSwing, handSwingAmount, tickDelta, age, headPitch, scale);
	}
}
