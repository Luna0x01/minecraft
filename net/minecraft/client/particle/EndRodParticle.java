package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.world.World;

public class EndRodParticle extends class_2864 {
	public EndRodParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 176, 8, -5.0E-4F);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.scale *= 0.75F;
		this.maxAge = 60 + this.field_13438.nextInt(12);
		this.method_12259(15916745);
	}

	@Override
	public void method_12242(double d, double e, double f) {
		this.method_12246(this.method_12254().offset(d, e, f));
		this.method_12252();
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new EndRodParticle(world, d, e, f, g, h, i);
		}
	}
}
