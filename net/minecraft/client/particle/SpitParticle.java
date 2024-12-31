package net.minecraft.client.particle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

public class SpitParticle extends ExplosionSmokeParticle {
	private SpitParticle(World world, double d, double e, double f, double g, double h, double i, SpriteProvider spriteProvider) {
		super(world, d, e, f, g, h, i, spriteProvider);
		this.gravityStrength = 0.5F;
	}

	@Override
	public void tick() {
		super.tick();
		this.velocityY = this.velocityY - (0.004 + 0.04 * (double)this.gravityStrength);
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SpitParticle(world, d, e, f, g, h, i, this.spriteProvider);
		}
	}
}
