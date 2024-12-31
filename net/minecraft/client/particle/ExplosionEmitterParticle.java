package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ExplosionEmitterParticle extends Particle {
	private int age_;
	private int maxAge_ = 8;

	protected ExplosionEmitterParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
	}

	@Override
	public void tick() {
		for (int i = 0; i < 6; i++) {
			double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			this.world.addParticle(ParticleType.LARGE_EXPLOSION, d, e, f, (double)((float)this.age_ / (float)this.maxAge_), 0.0, 0.0);
		}

		this.age_++;
		if (this.age_ == this.maxAge_) {
			this.remove();
		}
	}

	@Override
	public int getLayer() {
		return 1;
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new ExplosionEmitterParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
