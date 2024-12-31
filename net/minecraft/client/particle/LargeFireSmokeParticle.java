package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.world.World;

public class LargeFireSmokeParticle extends FireSmokeParticle {
	protected LargeFireSmokeParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i, 2.5F);
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new LargeFireSmokeParticle(world, d, e, f, g, h, i);
		}
	}
}
