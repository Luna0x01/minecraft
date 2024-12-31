package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class class_4198 extends EntityModel {
	private final ModelPart field_20594;
	private final ModelPart field_20595;
	private final ModelPart field_20596;
	private final ModelPart field_20597;
	private final ModelPart field_20598;

	public class_4198() {
		this(0.0F);
	}

	public class_4198(float f) {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.field_20594 = new ModelPart(this, 0, 0);
		this.field_20594.addCuboid(-1.0F, -1.5F, -3.0F, 2, 3, 6, f);
		this.field_20594.setPivot(0.0F, 22.0F, 0.0F);
		this.field_20595 = new ModelPart(this, 22, -6);
		this.field_20595.addCuboid(0.0F, -1.5F, 0.0F, 0, 3, 6, f);
		this.field_20595.setPivot(0.0F, 22.0F, 3.0F);
		this.field_20596 = new ModelPart(this, 2, 16);
		this.field_20596.addCuboid(-2.0F, -1.0F, 0.0F, 2, 2, 0, f);
		this.field_20596.setPivot(-1.0F, 22.5F, 0.0F);
		this.field_20596.posY = (float) (Math.PI / 4);
		this.field_20597 = new ModelPart(this, 2, 12);
		this.field_20597.addCuboid(0.0F, -1.0F, 0.0F, 2, 2, 0, f);
		this.field_20597.setPivot(1.0F, 22.5F, 0.0F);
		this.field_20597.posY = (float) (-Math.PI / 4);
		this.field_20598 = new ModelPart(this, 10, -5);
		this.field_20598.addCuboid(0.0F, -3.0F, 0.0F, 0, 3, 6, f);
		this.field_20598.setPivot(0.0F, 20.5F, -3.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_20594.render(scale);
		this.field_20595.render(scale);
		this.field_20596.render(scale);
		this.field_20597.render(scale);
		this.field_20598.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
		}

		this.field_20595.posY = -f * 0.45F * MathHelper.sin(0.6F * tickDelta);
	}
}
