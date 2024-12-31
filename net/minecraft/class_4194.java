package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class class_4194 extends EntityModel {
	private final ModelPart field_20579;
	private final ModelPart field_20580;
	private final ModelPart field_20581;
	private final ModelPart field_20582;
	private final ModelPart field_20583;
	private final ModelPart field_20584;
	private final ModelPart field_20585;
	private final ModelPart field_20586;

	public class_4194() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 20;
		this.field_20579 = new ModelPart(this, 0, 0);
		this.field_20579.addCuboid(-1.5F, -2.5F, 0.0F, 3, 5, 8);
		this.field_20579.setPivot(0.0F, 20.0F, 0.0F);
		this.field_20580 = new ModelPart(this, 0, 13);
		this.field_20580.addCuboid(-1.5F, -2.5F, 0.0F, 3, 5, 8);
		this.field_20580.setPivot(0.0F, 20.0F, 8.0F);
		this.field_20581 = new ModelPart(this, 22, 0);
		this.field_20581.addCuboid(-1.0F, -2.0F, -3.0F, 2, 4, 3);
		this.field_20581.setPivot(0.0F, 20.0F, 0.0F);
		this.field_20584 = new ModelPart(this, 20, 10);
		this.field_20584.addCuboid(0.0F, -2.5F, 0.0F, 0, 5, 6);
		this.field_20584.setPivot(0.0F, 0.0F, 8.0F);
		this.field_20580.add(this.field_20584);
		this.field_20582 = new ModelPart(this, 2, 1);
		this.field_20582.addCuboid(0.0F, 0.0F, 0.0F, 0, 2, 3);
		this.field_20582.setPivot(0.0F, -4.5F, 5.0F);
		this.field_20579.add(this.field_20582);
		this.field_20583 = new ModelPart(this, 0, 2);
		this.field_20583.addCuboid(0.0F, 0.0F, 0.0F, 0, 2, 4);
		this.field_20583.setPivot(0.0F, -4.5F, -1.0F);
		this.field_20580.add(this.field_20583);
		this.field_20585 = new ModelPart(this, -4, 0);
		this.field_20585.addCuboid(-2.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_20585.setPivot(-1.5F, 21.5F, 0.0F);
		this.field_20585.posZ = (float) (-Math.PI / 4);
		this.field_20586 = new ModelPart(this, 0, 0);
		this.field_20586.addCuboid(0.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_20586.setPivot(1.5F, 21.5F, 0.0F);
		this.field_20586.posZ = (float) (Math.PI / 4);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_20579.render(scale);
		this.field_20580.render(scale);
		this.field_20581.render(scale);
		this.field_20585.render(scale);
		this.field_20586.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = 1.0F;
		float g = 1.0F;
		if (!entity.isTouchingWater()) {
			f = 1.3F;
			g = 1.7F;
		}

		this.field_20580.posY = -f * 0.25F * MathHelper.sin(g * 0.6F * tickDelta);
	}
}
