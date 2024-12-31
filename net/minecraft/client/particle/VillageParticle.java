package net.minecraft.client.particle;

import net.minecraft.world.World;

public class VillageParticle extends Particle {
	protected VillageParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		float j = this.field_13438.nextFloat() * 0.1F + 0.2F;
		this.red = j;
		this.green = j;
		this.blue = j;
		this.setMiscTexture(0);
		this.method_12244(0.02F, 0.02F);
		this.scale = this.scale * (this.field_13438.nextFloat() * 0.6F + 0.5F);
		this.velocityX *= 0.02F;
		this.velocityY *= 0.02F;
		this.velocityZ *= 0.02F;
		this.maxAge = (int)(20.0 / (Math.random() * 0.8 + 0.2));
	}

	@Override
	public void method_12242(double d, double e, double f) {
		this.method_12246(this.method_12254().offset(d, e, f));
		this.method_12252();
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.99;
		this.velocityY *= 0.99;
		this.velocityZ *= 0.99;
		if (this.maxAge-- <= 0) {
			this.method_12251();
		}
	}

	public static class HappyVillagerFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			Particle particle = new VillageParticle(world, x, y, z, velocityX, velocityY, velocityZ);
			particle.setMiscTexture(82);
			particle.setColor(1.0F, 1.0F, 1.0F);
			return particle;
		}
	}

	public static class TownAuraFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new VillageParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
