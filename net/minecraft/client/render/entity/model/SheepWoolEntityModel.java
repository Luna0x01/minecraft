package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.SheepEntity;

public class SheepWoolEntityModel<T extends SheepEntity> extends QuadrupedEntityModel<T> {
	private float field_3541;

	public SheepWoolEntityModel() {
		super(12, 0.0F);
		this.head = new Cuboid(this, 0, 0);
		this.head.addBox(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);
		this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
		this.body = new Cuboid(this, 28, 8);
		this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
		float f = 0.5F;
		this.leg1 = new Cuboid(this, 0, 16);
		this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.leg1.setRotationPoint(-3.0F, 12.0F, 7.0F);
		this.leg2 = new Cuboid(this, 0, 16);
		this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.leg2.setRotationPoint(3.0F, 12.0F, 7.0F);
		this.leg3 = new Cuboid(this, 0, 16);
		this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.leg3.setRotationPoint(-3.0F, 12.0F, -5.0F);
		this.leg4 = new Cuboid(this, 0, 16);
		this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
		this.leg4.setRotationPoint(3.0F, 12.0F, -5.0F);
	}

	public void method_17118(T sheepEntity, float f, float g, float h) {
		super.animateModel(sheepEntity, f, g, h);
		this.head.rotationPointY = 6.0F + sheepEntity.method_6628(h) * 9.0F;
		this.field_3541 = sheepEntity.method_6641(h);
	}

	public void method_17119(T sheepEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(sheepEntity, f, g, h, i, j, k);
		this.head.pitch = this.field_3541;
	}
}
