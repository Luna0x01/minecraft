package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class LargeFireSmokeParticle extends FireSmokeParticle {
	protected LargeFireSmokeParticle(
		ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider
	) {
		super(world, x, y, z, velocityX, velocityY, velocityZ, 2.5F, spriteProvider);
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new LargeFireSmokeParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
