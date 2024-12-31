package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class PortalParticle extends Particle {
	private final float prevScale;
	private final double startX;
	private final double startY;
	private final double startZ;

	protected PortalParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.field_13428 = d;
		this.field_13429 = e;
		this.field_13430 = f;
		this.startX = this.field_13428;
		this.startY = this.field_13429;
		this.startZ = this.field_13430;
		float j = this.field_13438.nextFloat() * 0.6F + 0.4F;
		this.scale = this.field_13438.nextFloat() * 0.2F + 0.5F;
		this.prevScale = this.scale;
		this.red = j * 0.9F;
		this.green = j * 0.3F;
		this.blue = j;
		this.maxAge = (int)(Math.random() * 10.0) + 40;
		this.setMiscTexture((int)(Math.random() * 8.0));
	}

	@Override
	public void method_12242(double d, double e, double f) {
		this.method_12246(this.method_12254().offset(d, e, f));
		this.method_12252();
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge;
		f = 1.0F - f;
		f *= f;
		f = 1.0F - f;
		this.scale = this.prevScale * f;
		super.draw(builder, entity, tickDelta, g, h, i, j, k);
	}

	@Override
	public int method_12243(float f) {
		int i = super.method_12243(f);
		float g = (float)this.age / (float)this.maxAge;
		g *= g;
		g *= g;
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		k += (int)(g * 15.0F * 16.0F);
		if (k > 240) {
			k = 240;
		}

		return j | k << 16;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		float f = (float)this.age / (float)this.maxAge;
		float var3 = -f + f * f * 2.0F;
		float var4 = 1.0F - var3;
		this.field_13428 = this.startX + this.velocityX * (double)var4;
		this.field_13429 = this.startY + this.velocityY * (double)var4 + (double)(1.0F - f);
		this.field_13430 = this.startZ + this.velocityZ * (double)var4;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		}
	}

	public static class NetherPortalFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new PortalParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
