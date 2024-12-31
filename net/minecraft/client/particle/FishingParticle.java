package net.minecraft.client.particle;

import net.minecraft.world.World;

public class FishingParticle extends Particle {
	protected FishingParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.3F;
		this.velocityY = Math.random() * 0.2F + 0.1F;
		this.velocityZ *= 0.3F;
		this.red = 1.0F;
		this.green = 1.0F;
		this.blue = 1.0F;
		this.setMiscTexture(19);
		this.setBounds(0.01F, 0.01F);
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.gravityStrength = 0.0F;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.velocityY = this.velocityY - (double)this.gravityStrength;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		int i = 60 - this.maxAge;
		float f = (float)i * 0.001F;
		this.setBounds(f, f);
		this.setMiscTexture(19 + i % 4);
		if (this.maxAge-- <= 0) {
			this.remove();
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new FishingParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
