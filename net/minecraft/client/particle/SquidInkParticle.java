package net.minecraft.client.particle;

import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;

public class SquidInkParticle extends AnimatedParticle {
	SquidInkParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, int j, SpriteProvider spriteProvider) {
		super(clientWorld, d, e, f, spriteProvider, 0.0F);
		this.field_28786 = 0.92F;
		this.scale = 0.5F;
		this.setColorAlpha(1.0F);
		this.setColor((float)BackgroundHelper.ColorMixer.getRed(j), (float)BackgroundHelper.ColorMixer.getGreen(j), (float)BackgroundHelper.ColorMixer.getBlue(j));
		this.maxAge = (int)((double)(this.scale * 12.0F) / (Math.random() * 0.8F + 0.2F));
		this.setSpriteForAge(spriteProvider);
		this.collidesWithWorld = false;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.dead) {
			this.setSpriteForAge(this.spriteProvider);
			if (this.age > this.maxAge / 2) {
				this.setColorAlpha(1.0F - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
			}

			if (this.world.getBlockState(new BlockPos(this.x, this.y, this.z)).isAir()) {
				this.velocityY -= 0.0074F;
			}
		}
	}

	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new SquidInkParticle(clientWorld, d, e, f, g, h, i, BackgroundHelper.ColorMixer.getArgb(255, 255, 255, 255), this.spriteProvider);
		}
	}

	public static class GlowSquidInkFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public GlowSquidInkFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new SquidInkParticle(clientWorld, d, e, f, g, h, i, BackgroundHelper.ColorMixer.getArgb(255, 204, 31, 102), this.spriteProvider);
		}
	}
}
