package net.minecraft.client.particle;

import net.minecraft.class_4343;
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
		this.method_12244(0.01F, 0.01F);
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.gravityStrength = 0.0F;
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		this.velocityY = this.velocityY - (double)this.gravityStrength;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		int i = 60 - this.maxAge;
		float f = (float)i * 0.001F;
		this.method_12244(f, f);
		this.setMiscTexture(19 + i % 4);
		if (this.maxAge-- <= 0) {
			this.method_12251();
		}
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new FishingParticle(world, d, e, f, g, h, i);
		}
	}
}
