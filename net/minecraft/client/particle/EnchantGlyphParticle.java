package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EnchantGlyphParticle extends Particle {
	private float field_1703;
	private double startX;
	private double startY;
	private double startZ;

	protected EnchantGlyphParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.startX = d;
		this.startY = e;
		this.startZ = f;
		this.x = this.prevX = d + g;
		this.y = this.prevY = e + h;
		this.z = this.prevZ = f + i;
		float j = this.random.nextFloat() * 0.6F + 0.4F;
		this.field_1703 = this.scale = this.random.nextFloat() * 0.5F + 0.2F;
		this.red = this.green = this.blue = 1.0F * j;
		this.green *= 0.9F;
		this.red *= 0.9F;
		this.maxAge = (int)(Math.random() * 10.0) + 30;
		this.noClip = true;
		this.setMiscTexture((int)(Math.random() * 26.0 + 1.0 + 224.0));
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
		h *= h;
		h *= h;
		return g * (1.0F - h) + h;
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		float f = (float)this.age / (float)this.maxAge;
		f = 1.0F - f;
		float g = 1.0F - f;
		g *= g;
		g *= g;
		this.x = this.startX + this.velocityX * (double)f;
		this.y = this.startY + this.velocityY * (double)f - (double)(g * 1.2F);
		this.z = this.startZ + this.velocityZ * (double)f;
		if (this.age++ >= this.maxAge) {
			this.remove();
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new EnchantGlyphParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
