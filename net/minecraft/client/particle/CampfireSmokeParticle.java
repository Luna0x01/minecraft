package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class CampfireSmokeParticle extends SpriteBillboardParticle {
	private CampfireSmokeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, boolean signalFire) {
		super(world, x, y, z);
		this.scale(3.0F);
		this.setBoundingBoxSpacing(0.25F, 0.25F);
		if (signalFire) {
			this.maxAge = this.random.nextInt(50) + 280;
		} else {
			this.maxAge = this.random.nextInt(50) + 80;
		}

		this.gravityStrength = 3.0E-6F;
		this.velocityX = velocityX;
		this.velocityY = velocityY + (double)(this.random.nextFloat() / 500.0F);
		this.velocityZ = velocityZ;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ < this.maxAge && !(this.colorAlpha <= 0.0F)) {
			this.velocityX = this.velocityX + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
			this.velocityZ = this.velocityZ + (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
			this.velocityY = this.velocityY - (double)this.gravityStrength;
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			if (this.age >= this.maxAge - 60 && this.colorAlpha > 0.01F) {
				this.colorAlpha -= 0.015F;
			}
		} else {
			this.markDead();
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static class CosySmokeFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public CosySmokeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			CampfireSmokeParticle campfireSmokeParticle = new CampfireSmokeParticle(clientWorld, d, e, f, g, h, i, false);
			campfireSmokeParticle.setColorAlpha(0.9F);
			campfireSmokeParticle.setSprite(this.spriteProvider);
			return campfireSmokeParticle;
		}
	}

	public static class SignalSmokeFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public SignalSmokeFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			CampfireSmokeParticle campfireSmokeParticle = new CampfireSmokeParticle(clientWorld, d, e, f, g, h, i, true);
			campfireSmokeParticle.setColorAlpha(0.95F);
			campfireSmokeParticle.setSprite(this.spriteProvider);
			return campfireSmokeParticle;
		}
	}
}
