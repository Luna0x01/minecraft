package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class class_4206 extends Particle {
	protected class_4206(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.red = 1.0F;
		this.green = 1.0F;
		this.blue = 1.0F;
		this.setMiscTexture(256);
		this.maxAge = 4;
		this.gravityStrength = 0.008F;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		this.velocityY = this.velocityY - (double)this.gravityStrength;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		} else {
			int i = this.age * 5 / this.maxAge;
			if (i <= 4) {
				this.setMiscTexture(256 + i);
			}
		}
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = (float)this.field_5935 / 32.0F;
		float l = f + 0.0624375F;
		float m = (float)this.field_5936 / 32.0F;
		float n = m + 0.0624375F;
		float o = 0.1F * this.scale;
		if (this.sprite != null) {
			f = this.sprite.getMinU();
			l = this.sprite.getMaxU();
			m = this.sprite.getMinV();
			n = this.sprite.getMaxV();
		}

		float p = (float)(this.field_13425 + (this.field_13428 - this.field_13425) * (double)tickDelta - field_1722);
		float q = (float)(this.field_13426 + (this.field_13429 - this.field_13426) * (double)tickDelta - field_1723);
		float r = (float)(this.field_13427 + (this.field_13430 - this.field_13427) * (double)tickDelta - field_1724);
		int s = this.method_12243(tickDelta);
		int t = s >> 16 & 65535;
		int u = s & 65535;
		Vec3d[] vec3ds = new Vec3d[]{
			new Vec3d((double)(-g * o - j * o), (double)(-h * o), (double)(-i * o - k * o)),
			new Vec3d((double)(-g * o + j * o), (double)(h * o), (double)(-i * o + k * o)),
			new Vec3d((double)(g * o + j * o), (double)(h * o), (double)(i * o + k * o)),
			new Vec3d((double)(g * o - j * o), (double)(-h * o), (double)(i * o - k * o))
		};
		builder.vertex((double)p + vec3ds[0].x, (double)q + vec3ds[0].y, (double)r + vec3ds[0].z)
			.texture((double)l, (double)n)
			.color(this.red, this.green, this.blue, this.field_13421)
			.texture2(t, u)
			.next();
		builder.vertex((double)p + vec3ds[1].x, (double)q + vec3ds[1].y, (double)r + vec3ds[1].z)
			.texture((double)l, (double)m)
			.color(this.red, this.green, this.blue, this.field_13421)
			.texture2(t, u)
			.next();
		builder.vertex((double)p + vec3ds[2].x, (double)q + vec3ds[2].y, (double)r + vec3ds[2].z)
			.texture((double)f, (double)m)
			.color(this.red, this.green, this.blue, this.field_13421)
			.texture2(t, u)
			.next();
		builder.vertex((double)p + vec3ds[3].x, (double)q + vec3ds[3].y, (double)r + vec3ds[3].z)
			.texture((double)f, (double)n)
			.color(this.red, this.green, this.blue, this.field_13421)
			.texture2(t, u)
			.next();
	}

	@Override
	public void setMiscTexture(int i) {
		if (this.getLayer() != 0) {
			throw new RuntimeException("Invalid call to Particle.setMiscTex");
		} else {
			this.field_5935 = 2 * i % 16;
			this.field_5936 = i / 16;
		}
	}

	public static class class_4207 implements ParticleFactory<class_4343> {
		@Nullable
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new class_4206(world, d, e, f, g, h, i);
		}
	}
}
