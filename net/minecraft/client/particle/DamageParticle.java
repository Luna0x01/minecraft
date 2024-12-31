package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class DamageParticle extends Particle {
	float field_10610;

	protected DamageParticle(World world, double d, double e, double f, double g, double h, double i) {
		this(world, d, e, f, g, h, i, 1.0F);
	}

	protected DamageParticle(World world, double d, double e, double f, double g, double h, double i, float j) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g * 0.4;
		this.velocityY += h * 0.4;
		this.velocityZ += i * 0.4;
		float k = (float)(Math.random() * 0.3F + 0.6F);
		this.red = k;
		this.green = k;
		this.blue = k;
		this.scale *= 0.75F;
		this.scale *= j;
		this.field_10610 = this.scale;
		this.maxAge = (int)(6.0 / (Math.random() * 0.8 + 0.6));
		this.maxAge = (int)((float)this.maxAge * j);
		this.setMiscTexture(65);
		this.method_12241();
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge * 32.0F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		this.scale = this.field_10610 * f;
		super.draw(builder, entity, tickDelta, g, h, i, j, k);
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		}

		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.green = (float)((double)this.green * 0.96);
		this.blue = (float)((double)this.blue * 0.9);
		this.velocityX *= 0.7F;
		this.velocityY *= 0.7F;
		this.velocityZ *= 0.7F;
		this.velocityY -= 0.02F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class CritFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new DamageParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}

	public static class CritMagicFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			Particle particle = new DamageParticle(world, x, y, z, velocityX, velocityY, velocityZ);
			particle.setColor(particle.getRed() * 0.3F, particle.getGreen() * 0.8F, particle.getBlue());
			particle.method_5133();
			return particle;
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			Particle particle = new DamageParticle(world, x, y, z, velocityX, velocityY + 1.0, velocityZ, 1.0F);
			particle.method_12245(20);
			particle.setMiscTexture(67);
			return particle;
		}
	}
}
