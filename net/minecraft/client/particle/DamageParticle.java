package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class DamageParticle extends SpriteBillboardParticle {
	DamageParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
		super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
		this.field_28786 = 0.7F;
		this.gravityStrength = 0.5F;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g * 0.4;
		this.velocityY += h * 0.4;
		this.velocityZ += i * 0.4;
		float j = (float)(Math.random() * 0.3F + 0.6F);
		this.colorRed = j;
		this.colorGreen = j;
		this.colorBlue = j;
		this.scale *= 0.75F;
		this.maxAge = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1);
		this.collidesWithWorld = false;
		this.tick();
	}

	@Override
	public float getSize(float tickDelta) {
		return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
	}

	@Override
	public void tick() {
		super.tick();
		this.colorGreen = (float)((double)this.colorGreen * 0.96);
		this.colorBlue = (float)((double)this.colorBlue * 0.9);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			DamageParticle damageParticle = new DamageParticle(clientWorld, d, e, f, g, h + 1.0, i);
			damageParticle.setMaxAge(20);
			damageParticle.setSprite(this.spriteProvider);
			return damageParticle;
		}
	}

	public static class EnchantedHitFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public EnchantedHitFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			DamageParticle damageParticle = new DamageParticle(clientWorld, d, e, f, g, h, i);
			damageParticle.colorRed *= 0.3F;
			damageParticle.colorGreen *= 0.8F;
			damageParticle.setSprite(this.spriteProvider);
			return damageParticle;
		}
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			DamageParticle damageParticle = new DamageParticle(clientWorld, d, e, f, g, h, i);
			damageParticle.setSprite(this.spriteProvider);
			return damageParticle;
		}
	}
}
