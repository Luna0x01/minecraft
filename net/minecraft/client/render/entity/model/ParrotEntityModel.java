package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

public class ParrotEntityModel extends EntityModel<ParrotEntity> {
	private final Cuboid field_3458;
	private final Cuboid field_3460;
	private final Cuboid field_3459;
	private final Cuboid field_3455;
	private final Cuboid field_3452;
	private final Cuboid field_3461;
	private final Cuboid field_3451;
	private final Cuboid field_3453;
	private final Cuboid field_3456;
	private final Cuboid field_3450;
	private final Cuboid field_3457;

	public ParrotEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		this.field_3458 = new Cuboid(this, 2, 8);
		this.field_3458.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3);
		this.field_3458.setRotationPoint(0.0F, 16.5F, -3.0F);
		this.field_3460 = new Cuboid(this, 22, 1);
		this.field_3460.addBox(-1.5F, -1.0F, -1.0F, 3, 4, 1);
		this.field_3460.setRotationPoint(0.0F, 21.07F, 1.16F);
		this.field_3459 = new Cuboid(this, 19, 8);
		this.field_3459.addBox(-0.5F, 0.0F, -1.5F, 1, 5, 3);
		this.field_3459.setRotationPoint(1.5F, 16.94F, -2.76F);
		this.field_3455 = new Cuboid(this, 19, 8);
		this.field_3455.addBox(-0.5F, 0.0F, -1.5F, 1, 5, 3);
		this.field_3455.setRotationPoint(-1.5F, 16.94F, -2.76F);
		this.field_3452 = new Cuboid(this, 2, 2);
		this.field_3452.addBox(-1.0F, -1.5F, -1.0F, 2, 3, 2);
		this.field_3452.setRotationPoint(0.0F, 15.69F, -2.76F);
		this.field_3461 = new Cuboid(this, 10, 0);
		this.field_3461.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4);
		this.field_3461.setRotationPoint(0.0F, -2.0F, -1.0F);
		this.field_3452.addChild(this.field_3461);
		this.field_3451 = new Cuboid(this, 11, 7);
		this.field_3451.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1);
		this.field_3451.setRotationPoint(0.0F, -0.5F, -1.5F);
		this.field_3452.addChild(this.field_3451);
		this.field_3453 = new Cuboid(this, 16, 7);
		this.field_3453.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		this.field_3453.setRotationPoint(0.0F, -1.75F, -2.45F);
		this.field_3452.addChild(this.field_3453);
		this.field_3456 = new Cuboid(this, 2, 18);
		this.field_3456.addBox(0.0F, -4.0F, -2.0F, 0, 5, 4);
		this.field_3456.setRotationPoint(0.0F, -2.15F, 0.15F);
		this.field_3452.addChild(this.field_3456);
		this.field_3450 = new Cuboid(this, 14, 18);
		this.field_3450.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		this.field_3450.setRotationPoint(1.0F, 22.0F, -1.05F);
		this.field_3457 = new Cuboid(this, 14, 18);
		this.field_3457.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
		this.field_3457.setRotationPoint(-1.0F, 22.0F, -1.05F);
	}

	public void method_17109(ParrotEntity parrotEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17105(k);
	}

	public void method_17112(ParrotEntity parrotEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17111(method_17107(parrotEntity), parrotEntity.age, f, g, h, i, j);
	}

	public void method_17108(ParrotEntity parrotEntity, float f, float g, float h) {
		this.method_17110(method_17107(parrotEntity));
	}

	public void method_17106(float f, float g, float h, float i, float j, int k) {
		this.method_17110(ParrotEntityModel.Pose.field_3464);
		this.method_17111(ParrotEntityModel.Pose.field_3464, k, f, g, 0.0F, h, i);
		this.method_17105(j);
	}

	private void method_17105(float f) {
		this.field_3458.render(f);
		this.field_3459.render(f);
		this.field_3455.render(f);
		this.field_3460.render(f);
		this.field_3452.render(f);
		this.field_3450.render(f);
		this.field_3457.render(f);
	}

	private void method_17111(ParrotEntityModel.Pose pose, int i, float f, float g, float h, float j, float k) {
		this.field_3452.pitch = k * (float) (Math.PI / 180.0);
		this.field_3452.yaw = j * (float) (Math.PI / 180.0);
		this.field_3452.roll = 0.0F;
		this.field_3452.rotationPointX = 0.0F;
		this.field_3458.rotationPointX = 0.0F;
		this.field_3460.rotationPointX = 0.0F;
		this.field_3455.rotationPointX = -1.5F;
		this.field_3459.rotationPointX = 1.5F;
		switch (pose) {
			case field_3466:
				break;
			case field_3463:
				float l = MathHelper.cos((float)i);
				float m = MathHelper.sin((float)i);
				this.field_3452.rotationPointX = l;
				this.field_3452.rotationPointY = 15.69F + m;
				this.field_3452.pitch = 0.0F;
				this.field_3452.yaw = 0.0F;
				this.field_3452.roll = MathHelper.sin((float)i) * 0.4F;
				this.field_3458.rotationPointX = l;
				this.field_3458.rotationPointY = 16.5F + m;
				this.field_3459.roll = -0.0873F - h;
				this.field_3459.rotationPointX = 1.5F + l;
				this.field_3459.rotationPointY = 16.94F + m;
				this.field_3455.roll = 0.0873F + h;
				this.field_3455.rotationPointX = -1.5F + l;
				this.field_3455.rotationPointY = 16.94F + m;
				this.field_3460.rotationPointX = l;
				this.field_3460.rotationPointY = 21.07F + m;
				break;
			case field_3465:
				this.field_3450.pitch = this.field_3450.pitch + MathHelper.cos(f * 0.6662F) * 1.4F * g;
				this.field_3457.pitch = this.field_3457.pitch + MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			case field_3462:
			case field_3464:
			default:
				float n = h * 0.3F;
				this.field_3452.rotationPointY = 15.69F + n;
				this.field_3460.pitch = 1.015F + MathHelper.cos(f * 0.6662F) * 0.3F * g;
				this.field_3460.rotationPointY = 21.07F + n;
				this.field_3458.rotationPointY = 16.5F + n;
				this.field_3459.roll = -0.0873F - h;
				this.field_3459.rotationPointY = 16.94F + n;
				this.field_3455.roll = 0.0873F + h;
				this.field_3455.rotationPointY = 16.94F + n;
				this.field_3450.rotationPointY = 22.0F + n;
				this.field_3457.rotationPointY = 22.0F + n;
		}
	}

	private void method_17110(ParrotEntityModel.Pose pose) {
		this.field_3456.pitch = -0.2214F;
		this.field_3458.pitch = 0.4937F;
		this.field_3459.pitch = -0.6981F;
		this.field_3459.yaw = (float) -Math.PI;
		this.field_3455.pitch = -0.6981F;
		this.field_3455.yaw = (float) -Math.PI;
		this.field_3450.pitch = -0.0299F;
		this.field_3457.pitch = -0.0299F;
		this.field_3450.rotationPointY = 22.0F;
		this.field_3457.rotationPointY = 22.0F;
		this.field_3450.roll = 0.0F;
		this.field_3457.roll = 0.0F;
		switch (pose) {
			case field_3466:
				float f = 1.9F;
				this.field_3452.rotationPointY = 17.59F;
				this.field_3460.pitch = 1.5388988F;
				this.field_3460.rotationPointY = 22.97F;
				this.field_3458.rotationPointY = 18.4F;
				this.field_3459.roll = -0.0873F;
				this.field_3459.rotationPointY = 18.84F;
				this.field_3455.roll = 0.0873F;
				this.field_3455.rotationPointY = 18.84F;
				this.field_3450.rotationPointY++;
				this.field_3457.rotationPointY++;
				this.field_3450.pitch++;
				this.field_3457.pitch++;
				break;
			case field_3463:
				this.field_3450.roll = (float) (-Math.PI / 9);
				this.field_3457.roll = (float) (Math.PI / 9);
			case field_3465:
			case field_3464:
			default:
				break;
			case field_3462:
				this.field_3450.pitch += (float) (Math.PI * 2.0 / 9.0);
				this.field_3457.pitch += (float) (Math.PI * 2.0 / 9.0);
		}
	}

	private static ParrotEntityModel.Pose method_17107(ParrotEntity parrotEntity) {
		if (parrotEntity.getSongPlaying()) {
			return ParrotEntityModel.Pose.field_3463;
		} else if (parrotEntity.isSitting()) {
			return ParrotEntityModel.Pose.field_3466;
		} else {
			return parrotEntity.isInAir() ? ParrotEntityModel.Pose.field_3462 : ParrotEntityModel.Pose.field_3465;
		}
	}

	public static enum Pose {
		field_3462,
		field_3465,
		field_3466,
		field_3463,
		field_3464;
	}
}
