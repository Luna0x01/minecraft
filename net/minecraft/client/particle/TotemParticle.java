package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.world.World;

public class TotemParticle extends class_2864 {
	public TotemParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 176, 8, -0.05F);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.scale *= 0.75F;
		this.maxAge = 60 + this.field_13438.nextInt(12);
		if (this.field_13438.nextInt(4) == 0) {
			this.setColor(0.6F + this.field_13438.nextFloat() * 0.2F, 0.6F + this.field_13438.nextFloat() * 0.3F, this.field_13438.nextFloat() * 0.2F);
		} else {
			this.setColor(0.1F + this.field_13438.nextFloat() * 0.2F, 0.4F + this.field_13438.nextFloat() * 0.3F, this.field_13438.nextFloat() * 0.2F);
		}

		this.method_13844(0.6F);
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new TotemParticle(world, d, e, f, g, h, i);
		}
	}
}
