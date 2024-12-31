package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.world.World;

public class ExplosionSmokeParticle extends Particle {
	protected ExplosionSmokeParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = g + (Math.random() * 2.0 - 1.0) * 0.05F;
		this.velocityY = h + (Math.random() * 2.0 - 1.0) * 0.05F;
		this.velocityZ = i + (Math.random() * 2.0 - 1.0) * 0.05F;
		float j = this.field_13438.nextFloat() * 0.3F + 0.7F;
		this.red = j;
		this.green = j;
		this.blue = j;
		this.scale = this.field_13438.nextFloat() * this.field_13438.nextFloat() * 6.0F + 1.0F;
		this.maxAge = (int)(16.0 / ((double)this.field_13438.nextFloat() * 0.8 + 0.2)) + 2;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		}

		this.setMiscTexture(7 - this.age * 8 / this.maxAge);
		this.velocityY += 0.004;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.9F;
		this.velocityY *= 0.9F;
		this.velocityZ *= 0.9F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new ExplosionSmokeParticle(world, d, e, f, g, h, i);
		}
	}
}
