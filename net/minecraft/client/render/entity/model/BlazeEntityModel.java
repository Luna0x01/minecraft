package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BlazeEntityModel extends EntityModel {
	private ModelPart[] rods = new ModelPart[12];
	private ModelPart head;

	public BlazeEntityModel() {
		for (int i = 0; i < this.rods.length; i++) {
			this.rods[i] = new ModelPart(this, 0, 16);
			this.rods[i].addCuboid(0.0F, 0.0F, 0.0F, 2, 8, 2);
		}

		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.head.render(scale);

		for (int i = 0; i < this.rods.length; i++) {
			this.rods[i].render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = tickDelta * (float) Math.PI * -0.1F;

		for (int i = 0; i < 4; i++) {
			this.rods[i].pivotY = -2.0F + MathHelper.cos(((float)(i * 2) + tickDelta) * 0.25F);
			this.rods[i].pivotX = MathHelper.cos(f) * 9.0F;
			this.rods[i].pivotZ = MathHelper.sin(f) * 9.0F;
			f++;
		}

		f = (float) (Math.PI / 4) + tickDelta * (float) Math.PI * 0.03F;

		for (int j = 4; j < 8; j++) {
			this.rods[j].pivotY = 2.0F + MathHelper.cos(((float)(j * 2) + tickDelta) * 0.25F);
			this.rods[j].pivotX = MathHelper.cos(f) * 7.0F;
			this.rods[j].pivotZ = MathHelper.sin(f) * 7.0F;
			f++;
		}

		f = 0.47123894F + tickDelta * (float) Math.PI * -0.05F;

		for (int k = 8; k < 12; k++) {
			this.rods[k].pivotY = 11.0F + MathHelper.cos(((float)k * 1.5F + tickDelta) * 0.5F);
			this.rods[k].pivotX = MathHelper.cos(f) * 5.0F;
			this.rods[k].pivotZ = MathHelper.sin(f) * 5.0F;
			f++;
		}

		this.head.posY = age / (180.0F / (float)Math.PI);
		this.head.posX = headPitch / (180.0F / (float)Math.PI);
	}
}
