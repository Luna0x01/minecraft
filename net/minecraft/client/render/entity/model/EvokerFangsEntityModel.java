package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class EvokerFangsEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart field_3374 = new ModelPart(this, 0, 0);
	private final ModelPart field_3376;
	private final ModelPart field_3375;

	public EvokerFangsEntityModel() {
		this.field_3374.setPivot(-5.0F, 22.0F, -5.0F);
		this.field_3374.addCuboid(0.0F, 0.0F, 0.0F, 10.0F, 12.0F, 10.0F);
		this.field_3376 = new ModelPart(this, 40, 0);
		this.field_3376.setPivot(1.5F, 22.0F, -4.0F);
		this.field_3376.addCuboid(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
		this.field_3375 = new ModelPart(this, 40, 0);
		this.field_3375.setPivot(-1.5F, 22.0F, 4.0F);
		this.field_3375.addCuboid(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j) {
		float k = f * 2.0F;
		if (k > 1.0F) {
			k = 1.0F;
		}

		k = 1.0F - k * k * k;
		this.field_3376.roll = (float) Math.PI - k * 0.35F * (float) Math.PI;
		this.field_3375.roll = (float) Math.PI + k * 0.35F * (float) Math.PI;
		this.field_3375.yaw = (float) Math.PI;
		float l = (f + MathHelper.sin(f * 2.7F)) * 0.6F * 12.0F;
		this.field_3376.pivotY = 24.0F - l;
		this.field_3375.pivotY = this.field_3376.pivotY;
		this.field_3374.pivotY = this.field_3376.pivotY;
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.of(this.field_3374, this.field_3376, this.field_3375);
	}
}
