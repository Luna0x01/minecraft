package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class class_4199 extends EntityModel {
	private final ModelPart field_20599;
	private final ModelPart field_20600;
	private final ModelPart field_20601;
	private final ModelPart field_20602;
	private final ModelPart field_20603;
	private final ModelPart field_20604;

	public class_4199() {
		this(0.0F);
	}

	public class_4199(float f) {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 19;
		this.field_20599 = new ModelPart(this, 0, 20);
		this.field_20599.addCuboid(-1.0F, -3.0F, -3.0F, 2, 6, 6, f);
		this.field_20599.setPivot(0.0F, 19.0F, 0.0F);
		this.field_20600 = new ModelPart(this, 21, 16);
		this.field_20600.addCuboid(0.0F, -3.0F, 0.0F, 0, 6, 5, f);
		this.field_20600.setPivot(0.0F, 19.0F, 3.0F);
		this.field_20601 = new ModelPart(this, 2, 16);
		this.field_20601.addCuboid(-2.0F, 0.0F, 0.0F, 2, 2, 0, f);
		this.field_20601.setPivot(-1.0F, 20.0F, 0.0F);
		this.field_20601.posY = (float) (Math.PI / 4);
		this.field_20602 = new ModelPart(this, 2, 12);
		this.field_20602.addCuboid(0.0F, 0.0F, 0.0F, 2, 2, 0, f);
		this.field_20602.setPivot(1.0F, 20.0F, 0.0F);
		this.field_20602.posY = (float) (-Math.PI / 4);
		this.field_20603 = new ModelPart(this, 20, 11);
		this.field_20603.addCuboid(0.0F, -4.0F, 0.0F, 0, 4, 6, f);
		this.field_20603.setPivot(0.0F, 16.0F, -3.0F);
		this.field_20604 = new ModelPart(this, 20, 21);
		this.field_20604.addCuboid(0.0F, 0.0F, 0.0F, 0, 4, 6, f);
		this.field_20604.setPivot(0.0F, 22.0F, -3.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_20599.render(scale);
		this.field_20600.render(scale);
		this.field_20601.render(scale);
		this.field_20602.render(scale);
		this.field_20603.render(scale);
		this.field_20604.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.5F;
		}

		this.field_20600.posY = -f * 0.45F * MathHelper.sin(0.6F * tickDelta);
	}
}
