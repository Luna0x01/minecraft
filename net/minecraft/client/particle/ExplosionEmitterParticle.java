package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;

public class ExplosionEmitterParticle extends NoRenderParticle {
	private int age_;
	private final int maxAge_ = 8;

	ExplosionEmitterParticle(ClientWorld clientWorld, double d, double e, double f) {
		super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void tick() {
		for (int i = 0; i < 6; i++) {
			double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
			this.world.addParticle(ParticleTypes.EXPLOSION, d, e, f, (double)((float)this.age_ / (float)this.maxAge_), 0.0, 0.0);
		}

		this.age_++;
		if (this.age_ == this.maxAge_) {
			this.markDead();
		}
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new ExplosionEmitterParticle(clientWorld, d, e, f);
		}
	}
}
