package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class class_4193 extends EntityModel {
	private final ModelPart field_20573;
	private final ModelPart field_20574;
	private final ModelPart field_20575;
	private final ModelPart field_20576;
	private final ModelPart field_20577;
	private final ModelPart field_20578;

	public class_4193() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 23;
		this.field_20573 = new ModelPart(this, 0, 27);
		this.field_20573.addCuboid(-1.5F, -2.0F, -1.5F, 3, 2, 3);
		this.field_20573.setPivot(0.0F, 23.0F, 0.0F);
		this.field_20574 = new ModelPart(this, 24, 6);
		this.field_20574.addCuboid(-1.5F, 0.0F, -1.5F, 1, 1, 1);
		this.field_20574.setPivot(0.0F, 20.0F, 0.0F);
		this.field_20575 = new ModelPart(this, 28, 6);
		this.field_20575.addCuboid(0.5F, 0.0F, -1.5F, 1, 1, 1);
		this.field_20575.setPivot(0.0F, 20.0F, 0.0F);
		this.field_20578 = new ModelPart(this, -3, 0);
		this.field_20578.addCuboid(-1.5F, 0.0F, 0.0F, 3, 0, 3);
		this.field_20578.setPivot(0.0F, 22.0F, 1.5F);
		this.field_20576 = new ModelPart(this, 25, 0);
		this.field_20576.addCuboid(-1.0F, 0.0F, 0.0F, 1, 0, 2);
		this.field_20576.setPivot(-1.5F, 22.0F, -1.5F);
		this.field_20577 = new ModelPart(this, 25, 0);
		this.field_20577.addCuboid(0.0F, 0.0F, 0.0F, 1, 0, 2);
		this.field_20577.setPivot(1.5F, 22.0F, -1.5F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_20573.render(scale);
		this.field_20574.render(scale);
		this.field_20575.render(scale);
		this.field_20578.render(scale);
		this.field_20576.render(scale);
		this.field_20577.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.field_20576.posZ = -0.2F + 0.4F * MathHelper.sin(tickDelta * 0.2F);
		this.field_20577.posZ = 0.2F - 0.4F * MathHelper.sin(tickDelta * 0.2F);
	}
}
