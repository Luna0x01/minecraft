package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Arrays;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BlazeEntityModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart[] rods;
	private final ModelPart head = new ModelPart(this, 0, 0);
	private final ImmutableList<ModelPart> parts;

	public BlazeEntityModel() {
		this.head.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		this.rods = new ModelPart[12];

		for (int i = 0; i < this.rods.length; i++) {
			this.rods[i] = new ModelPart(this, 0, 16);
			this.rods[i].addCuboid(0.0F, 0.0F, 0.0F, 2.0F, 8.0F, 2.0F);
		}

		Builder<ModelPart> builder = ImmutableList.builder();
		builder.add(this.head);
		builder.addAll(Arrays.asList(this.rods));
		this.parts = builder.build();
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return this.parts;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		float f = animationProgress * (float) Math.PI * -0.1F;

		for (int i = 0; i < 4; i++) {
			this.rods[i].pivotY = -2.0F + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25F);
			this.rods[i].pivotX = MathHelper.cos(f) * 9.0F;
			this.rods[i].pivotZ = MathHelper.sin(f) * 9.0F;
			f++;
		}

		f = (float) (Math.PI / 4) + animationProgress * (float) Math.PI * 0.03F;

		for (int j = 4; j < 8; j++) {
			this.rods[j].pivotY = 2.0F + MathHelper.cos(((float)(j * 2) + animationProgress) * 0.25F);
			this.rods[j].pivotX = MathHelper.cos(f) * 7.0F;
			this.rods[j].pivotZ = MathHelper.sin(f) * 7.0F;
			f++;
		}

		f = 0.47123894F + animationProgress * (float) Math.PI * -0.05F;

		for (int k = 8; k < 12; k++) {
			this.rods[k].pivotY = 11.0F + MathHelper.cos(((float)k * 1.5F + animationProgress) * 0.5F);
			this.rods[k].pivotX = MathHelper.cos(f) * 5.0F;
			this.rods[k].pivotZ = MathHelper.sin(f) * 5.0F;
			f++;
		}

		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
