package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class EvokerFangsEntityModel extends EntityModel {
	private final ModelPart field_15256 = new ModelPart(this, 0, 0);
	private final ModelPart field_15257;
	private final ModelPart field_15258;

	public EvokerFangsEntityModel() {
		this.field_15256.setPivot(-5.0F, 22.0F, -5.0F);
		this.field_15256.addCuboid(0.0F, 0.0F, 0.0F, 10, 12, 10);
		this.field_15257 = new ModelPart(this, 40, 0);
		this.field_15257.setPivot(1.5F, 22.0F, -4.0F);
		this.field_15257.addCuboid(0.0F, 0.0F, 0.0F, 4, 14, 8);
		this.field_15258 = new ModelPart(this, 40, 0);
		this.field_15258.setPivot(-1.5F, 22.0F, 4.0F);
		this.field_15258.addCuboid(0.0F, 0.0F, 0.0F, 4, 14, 8);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		float f = handSwing * 2.0F;
		if (f > 1.0F) {
			f = 1.0F;
		}

		f = 1.0F - f * f * f;
		this.field_15257.posZ = (float) Math.PI - f * 0.35F * (float) Math.PI;
		this.field_15258.posZ = (float) Math.PI + f * 0.35F * (float) Math.PI;
		this.field_15258.posY = (float) Math.PI;
		float g = (handSwing + MathHelper.sin(handSwing * 2.7F)) * 0.6F * 12.0F;
		this.field_15257.pivotY = 24.0F - g;
		this.field_15258.pivotY = this.field_15257.pivotY;
		this.field_15256.pivotY = this.field_15257.pivotY;
		this.field_15256.render(scale);
		this.field_15257.render(scale);
		this.field_15258.render(scale);
	}
}
