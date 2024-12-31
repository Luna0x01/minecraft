package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class class_4190 extends EntityModel {
	private final ModelPart field_20541;
	private final ModelPart field_20542;
	private final ModelPart field_20543;
	private final ModelPart field_20544;
	private final ModelPart field_20545;
	private final ModelPart field_20546;
	private final ModelPart field_20547;
	private final ModelPart field_20548;

	public class_4190() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_20541 = new ModelPart(this, 0, 8);
		this.field_20541.addCuboid(-3.0F, -2.0F, -8.0F, 5, 3, 9);
		this.field_20547 = new ModelPart(this, 3, 20);
		this.field_20547.addCuboid(-2.0F, 0.0F, 0.0F, 3, 2, 6);
		this.field_20547.setPivot(0.0F, -2.0F, 1.0F);
		this.field_20541.add(this.field_20547);
		this.field_20548 = new ModelPart(this, 4, 29);
		this.field_20548.addCuboid(-1.0F, 0.0F, 0.0F, 1, 1, 6);
		this.field_20548.setPivot(0.0F, 0.5F, 6.0F);
		this.field_20547.add(this.field_20548);
		this.field_20542 = new ModelPart(this, 23, 12);
		this.field_20542.addCuboid(0.0F, 0.0F, 0.0F, 6, 2, 9);
		this.field_20542.setPivot(2.0F, -2.0F, -8.0F);
		this.field_20543 = new ModelPart(this, 16, 24);
		this.field_20543.addCuboid(0.0F, 0.0F, 0.0F, 13, 1, 9);
		this.field_20543.setPivot(6.0F, 0.0F, 0.0F);
		this.field_20542.add(this.field_20543);
		this.field_20544 = new ModelPart(this, 23, 12);
		this.field_20544.mirror = true;
		this.field_20544.addCuboid(-6.0F, 0.0F, 0.0F, 6, 2, 9);
		this.field_20544.setPivot(-3.0F, -2.0F, -8.0F);
		this.field_20545 = new ModelPart(this, 16, 24);
		this.field_20545.mirror = true;
		this.field_20545.addCuboid(-13.0F, 0.0F, 0.0F, 13, 1, 9);
		this.field_20545.setPivot(-6.0F, 0.0F, 0.0F);
		this.field_20544.add(this.field_20545);
		this.field_20542.posZ = 0.1F;
		this.field_20543.posZ = 0.1F;
		this.field_20544.posZ = -0.1F;
		this.field_20545.posZ = -0.1F;
		this.field_20541.posX = -0.1F;
		this.field_20546 = new ModelPart(this, 0, 0);
		this.field_20546.addCuboid(-4.0F, -2.0F, -5.0F, 7, 3, 5);
		this.field_20546.setPivot(0.0F, 1.0F, -7.0F);
		this.field_20546.posX = 0.2F;
		this.field_20541.add(this.field_20546);
		this.field_20541.add(this.field_20542);
		this.field_20541.add(this.field_20544);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.field_20541.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = ((float)(entity.getEntityId() * 3) + tickDelta) * 0.13F;
		float g = 16.0F;
		this.field_20542.posZ = (0.0F + MathHelper.cos(f) * 16.0F) * (float) (Math.PI / 180.0);
		this.field_20543.posZ = (0.0F + MathHelper.cos(f) * 16.0F) * (float) (Math.PI / 180.0);
		this.field_20544.posZ = -this.field_20542.posZ;
		this.field_20545.posZ = -this.field_20543.posZ;
		this.field_20547.posX = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
		this.field_20548.posX = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
	}
}
