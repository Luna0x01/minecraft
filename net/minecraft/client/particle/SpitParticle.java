package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.world.World;

public class SpitParticle extends ExplosionSmokeParticle {
	protected SpitParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.gravityStrength = 0.5F;
	}

	@Override
	public void method_12241() {
		super.method_12241();
		this.velocityY = this.velocityY - (0.004 + 0.04 * (double)this.gravityStrength);
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new SpitParticle(world, d, e, f, g, h, i);
		}
	}
}
