package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SnowmanEntityModel extends EntityModel {
	public ModelPart field_1530;
	public ModelPart field_1531;
	public ModelPart field_1532;
	public ModelPart field_1533;
	public ModelPart field_1534;

	public SnowmanEntityModel() {
		float f = 4.0F;
		float g = 0.0F;
		this.field_1532 = new ModelPart(this, 0, 0).setTextureSize(64, 64);
		this.field_1532.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, -0.5F);
		this.field_1532.setPivot(0.0F, 4.0F, 0.0F);
		this.field_1533 = new ModelPart(this, 32, 0).setTextureSize(64, 64);
		this.field_1533.addCuboid(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
		this.field_1533.setPivot(0.0F, 6.0F, 0.0F);
		this.field_1534 = new ModelPart(this, 32, 0).setTextureSize(64, 64);
		this.field_1534.addCuboid(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
		this.field_1534.setPivot(0.0F, 6.0F, 0.0F);
		this.field_1530 = new ModelPart(this, 0, 16).setTextureSize(64, 64);
		this.field_1530.addCuboid(-5.0F, -10.0F, -5.0F, 10, 10, 10, -0.5F);
		this.field_1530.setPivot(0.0F, 13.0F, 0.0F);
		this.field_1531 = new ModelPart(this, 0, 36).setTextureSize(64, 64);
		this.field_1531.addCuboid(-6.0F, -12.0F, -6.0F, 12, 12, 12, -0.5F);
		this.field_1531.setPivot(0.0F, 24.0F, 0.0F);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_1532.posY = age * (float) (Math.PI / 180.0);
		this.field_1532.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_1530.posY = age * (float) (Math.PI / 180.0) * 0.25F;
		float f = MathHelper.sin(this.field_1530.posY);
		float g = MathHelper.cos(this.field_1530.posY);
		this.field_1533.posZ = 1.0F;
		this.field_1534.posZ = -1.0F;
		this.field_1533.posY = 0.0F + this.field_1530.posY;
		this.field_1534.posY = (float) Math.PI + this.field_1530.posY;
		this.field_1533.pivotX = g * 5.0F;
		this.field_1533.pivotZ = -f * 5.0F;
		this.field_1534.pivotX = -g * 5.0F;
		this.field_1534.pivotZ = f * 5.0F;
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_1530.render(scale);
		this.field_1531.render(scale);
		this.field_1532.render(scale);
		this.field_1533.render(scale);
		this.field_1534.render(scale);
	}
}
