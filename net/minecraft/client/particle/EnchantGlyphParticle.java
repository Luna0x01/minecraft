package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class EnchantGlyphParticle extends SpriteBillboardParticle {
	private final double startX;
	private final double startY;
	private final double startZ;

	EnchantGlyphParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
		super(clientWorld, d, e, f);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.startX = d;
		this.startY = e;
		this.startZ = f;
		this.prevPosX = d + g;
		this.prevPosY = e + h;
		this.prevPosZ = f + i;
		this.x = this.prevPosX;
		this.y = this.prevPosY;
		this.z = this.prevPosZ;
		this.scale = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
		float j = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = 0.9F * j;
		this.colorGreen = 0.9F * j;
		this.colorBlue = j;
		this.collidesWithWorld = false;
		this.maxAge = (int)(Math.random() * 10.0) + 30;
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void move(double dx, double dy, double dz) {
		this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
		this.repositionFromBoundingBox();
	}

	@Override
	public int getBrightness(float tint) {
		int i = super.getBrightness(tint);
		float f = (float)this.age / (float)this.maxAge;
		f *= f;
		f *= f;
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		k += (int)(f * 15.0F * 16.0F);
		if (k > 240) {
			k = 240;
		}

		return j | k << 16;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			float f = (float)this.age / (float)this.maxAge;
			f = 1.0F - f;
			float g = 1.0F - f;
			g *= g;
			g *= g;
			this.x = this.startX + this.velocityX * (double)f;
			this.y = this.startY + this.velocityY * (double)f - (double)(g * 1.2F);
			this.z = this.startZ + this.velocityZ * (double)f;
		}
	}

	public static class EnchantFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public EnchantFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			EnchantGlyphParticle enchantGlyphParticle = new EnchantGlyphParticle(clientWorld, d, e, f, g, h, i);
			enchantGlyphParticle.setSprite(this.spriteProvider);
			return enchantGlyphParticle;
		}
	}

	public static class NautilusFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public NautilusFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			EnchantGlyphParticle enchantGlyphParticle = new EnchantGlyphParticle(clientWorld, d, e, f, g, h, i);
			enchantGlyphParticle.setSprite(this.spriteProvider);
			return enchantGlyphParticle;
		}
	}
}
