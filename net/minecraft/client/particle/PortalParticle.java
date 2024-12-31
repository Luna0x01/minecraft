package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class PortalParticle extends Particle {
	private float prevScale;
	private double startX;
	private double startY;
	private double startZ;

	protected PortalParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.startX = this.x = d;
		this.startY = this.y = e;
		this.startZ = this.z = f;
		float j = this.random.nextFloat() * 0.6F + 0.4F;
		this.prevScale = this.scale = this.random.nextFloat() * 0.2F + 0.5F;
		this.red = this.green = this.blue = 1.0F * j;
		this.green *= 0.3F;
		this.red *= 0.9F;
		this.maxAge = (int)(Math.random() * 10.0) + 40;
		this.noClip = true;
		this.setMiscTexture((int)(Math.random() * 8.0));
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
	public int getLightmapCoordinates(float f) {
		int i = super.getLightmapCoordinates(f);
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
	public float getBrightnessAtEyes(float f) {
		float g = super.getBrightnessAtEyes(f);
		float h = (float)this.age / (float)this.maxAge;
		h = h * h * h * h;
		return g * (1.0F - h) + h;
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		float f = (float)this.age / (float)this.maxAge;
		float var3 = -f + f * f * 2.0F;
		float var4 = 1.0F - var3;
		this.x = this.startX + this.velocityX * (double)var4;
		this.y = this.startY + this.velocityY * (double)var4 + (double)(1.0F - f);
		this.z = this.startZ + this.velocityZ * (double)var4;
		if (this.age++ >= this.maxAge) {
			this.remove();
		}
	}

	public static class NetherPortalFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new PortalParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
