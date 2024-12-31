package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class class_4183 extends EntityModel {
	private final ModelPart field_20526;
	private final ModelPart field_20527;
	private final ModelPart field_20528;
	private final ModelPart field_20529;
	private final ModelPart field_20530;
	private final ModelPart field_20531;
	private final ModelPart field_20532;

	public class_4183() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.field_20526 = new ModelPart(this, 0, 0);
		this.field_20526.addCuboid(-1.0F, -2.0F, 0.0F, 2, 4, 7);
		this.field_20526.setPivot(0.0F, 22.0F, 0.0F);
		this.field_20528 = new ModelPart(this, 11, 0);
		this.field_20528.addCuboid(-1.0F, -2.0F, -3.0F, 2, 4, 3);
		this.field_20528.setPivot(0.0F, 22.0F, 0.0F);
		this.field_20529 = new ModelPart(this, 0, 0);
		this.field_20529.addCuboid(-1.0F, -2.0F, -1.0F, 2, 3, 1);
		this.field_20529.setPivot(0.0F, 22.0F, -3.0F);
		this.field_20530 = new ModelPart(this, 22, 1);
		this.field_20530.addCuboid(-2.0F, 0.0F, -1.0F, 2, 0, 2);
		this.field_20530.setPivot(-1.0F, 23.0F, 0.0F);
		this.field_20530.posZ = (float) (-Math.PI / 4);
		this.field_20531 = new ModelPart(this, 22, 4);
		this.field_20531.addCuboid(0.0F, 0.0F, -1.0F, 2, 0, 2);
		this.field_20531.setPivot(1.0F, 23.0F, 0.0F);
		this.field_20531.posZ = (float) (Math.PI / 4);
		this.field_20532 = new ModelPart(this, 22, 3);
		this.field_20532.addCuboid(0.0F, -2.0F, 0.0F, 0, 4, 4);
		this.field_20532.setPivot(0.0F, 22.0F, 7.0F);
		this.field_20527 = new ModelPart(this, 20, -6);
		this.field_20527.addCuboid(0.0F, -1.0F, -1.0F, 0, 1, 6);
		this.field_20527.setPivot(0.0F, 20.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_20526.render(scale);
		this.field_20528.render(scale);
		this.field_20529.render(scale);
		this.field_20530.render(scale);
		this.field_20531.render(scale);
		this.field_20532.render(scale);
		this.field_20527.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
		}

		this.field_20532.posY = -f * 0.45F * MathHelper.sin(0.6F * tickDelta);
	}
}
