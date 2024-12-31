package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FlameParticle extends Particle {
	private float prevScale;

	protected FlameParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = this.velocityX * 0.01F + g;
		this.velocityY = this.velocityY * 0.01F + h;
		this.velocityZ = this.velocityZ * 0.01F + i;
		this.field_13428 = this.field_13428 + (double)((this.field_13438.nextFloat() - this.field_13438.nextFloat()) * 0.05F);
		this.field_13429 = this.field_13429 + (double)((this.field_13438.nextFloat() - this.field_13438.nextFloat()) * 0.05F);
		this.field_13430 = this.field_13430 + (double)((this.field_13438.nextFloat() - this.field_13438.nextFloat()) * 0.05F);
		this.prevScale = this.scale;
		this.red = this.green = this.blue = 1.0F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
		this.setMiscTexture(48);
	}

	@Override
	public void method_12242(double d, double e, double f) {
		this.method_12246(this.method_12254().offset(d, e, f));
		this.method_12252();
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge;
		this.scale = this.prevScale * (1.0F - f * f * 0.5F);
		super.draw(builder, entity, tickDelta, g, h, i, j, k);
	}

	@Override
	public int method_12243(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		int i = super.method_12243(f);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(g * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		}

		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.96F;
		this.velocityY *= 0.96F;
		this.velocityZ *= 0.96F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new FlameParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
