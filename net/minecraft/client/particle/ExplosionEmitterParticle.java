package net.minecraft.client.particle;

import net.minecraft.class_4342;
import net.minecraft.class_4343;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ExplosionEmitterParticle extends Particle {
	private int age_;
	private final int maxAge_ = 8;

	protected ExplosionEmitterParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
	}

	@Override
	public void method_12241() {
		for (int i = 0; i < 6; i++) {
			double d = this.field_13428 + (this.field_13438.nextDouble() - this.field_13438.nextDouble()) * 4.0;
			double e = this.field_13429 + (this.field_13438.nextDouble() - this.field_13438.nextDouble()) * 4.0;
			double f = this.field_13430 + (this.field_13438.nextDouble() - this.field_13438.nextDouble()) * 4.0;
			this.field_13424.method_16343(class_4342.field_21395, d, e, f, (double)((float)this.age_ / (float)this.maxAge_), 0.0, 0.0);
		}

		this.age_++;
		if (this.age_ == this.maxAge_) {
			this.method_12251();
		}
	}

	@Override
	public int getLayer() {
		return 1;
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new ExplosionEmitterParticle(world, d, e, f, g, h, i);
		}
	}
}
