package net.minecraft.client.particle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.world.World;

public class SweepAttackParticle extends SpriteBillboardParticle {
	private final SpriteProvider spriteProvider;

	private SweepAttackParticle(World world, double d, double e, double f, double g, SpriteProvider spriteProvider) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.spriteProvider = spriteProvider;
		this.maxAge = 4;
		float h = this.random.nextFloat() * 0.6F + 0.4F;
		this.colorRed = h;
		this.colorGreen = h;
		this.colorBlue = h;
		this.scale = 1.0F - (float)g * 0.5F;
		this.setSpriteForAge(spriteProvider);
	}

	@Override
	public int getColorMultiplier(float f) {
		return 15728880;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(this.spriteProvider);
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_LIT;
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, World world, double d, double e, double f, double g, double h, double i) {
			return new SweepAttackParticle(world, d, e, f, g, this.spriteProvider);
		}
	}
}
