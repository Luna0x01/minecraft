package net.minecraft.client.particle;

import net.minecraft.world.World;

public class WaterSplashParticle extends RainSplashParticle {
	protected WaterSplashParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f);
		this.gravityStrength = 0.04F;
		this.method_5133();
		if (h == 0.0 && (g != 0.0 || i != 0.0)) {
			this.velocityX = g;
			this.velocityY = h + 0.1;
			this.velocityZ = i;
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new WaterSplashParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
