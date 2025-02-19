package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DustParticleEffect;

public class RedDustParticle extends AbstractDustParticle<DustParticleEffect> {
	protected RedDustParticle(
		ClientWorld world,
		double x,
		double y,
		double z,
		double velocityX,
		double velocityY,
		double velocityZ,
		DustParticleEffect dustParticleEffect,
		SpriteProvider spriteProvider
	) {
		super(world, x, y, z, velocityX, velocityY, velocityZ, dustParticleEffect, spriteProvider);
	}

	public static class Factory implements ParticleFactory<DustParticleEffect> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DustParticleEffect dustParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new RedDustParticle(clientWorld, d, e, f, g, h, i, dustParticleEffect, this.spriteProvider);
		}
	}
}
