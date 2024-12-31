package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.world.World;

public class WaterSplashParticle extends RainSplashParticle {
	protected WaterSplashParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f);
		this.gravityStrength = 0.04F;
		this.setMiscTexture(20 + this.field_13438.nextInt(3));
		if (h == 0.0 && (g != 0.0 || i != 0.0)) {
			this.velocityX = g;
			this.velocityY = 0.1;
			this.velocityZ = i;
		}
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new WaterSplashParticle(world, d, e, f, g, h, i);
		}
	}
}
