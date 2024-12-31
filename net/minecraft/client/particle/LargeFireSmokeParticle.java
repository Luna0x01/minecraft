package net.minecraft.client.particle;

import net.minecraft.world.World;

public class LargeFireSmokeParticle extends FireSmokeParticle {
	protected LargeFireSmokeParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i, 2.5F);
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new LargeFireSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
