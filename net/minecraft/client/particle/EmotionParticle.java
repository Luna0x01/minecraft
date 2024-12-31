package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EmotionParticle extends Particle {
	private final float prevScale;

	protected EmotionParticle(World world, double d, double e, double f, double g, double h, double i) {
		this(world, d, e, f, g, h, i, 2.0F);
	}

	protected EmotionParticle(World world, double d, double e, double f, double g, double h, double i, float j) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.01F;
		this.velocityY *= 0.01F;
		this.velocityZ *= 0.01F;
		this.velocityY += 0.1;
		this.scale *= 0.75F;
		this.scale *= j;
		this.prevScale = this.scale;
		this.maxAge = 16;
		this.setMiscTexture(80);
		this.field_14950 = false;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge * 32.0F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		this.scale = this.prevScale * f;
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
		if (this.field_13429 == this.field_13426) {
			this.velocityX *= 1.1;
			this.velocityZ *= 1.1;
		}

		this.velocityX *= 0.86F;
		this.velocityY *= 0.86F;
		this.velocityZ *= 0.86F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			Particle particle = new EmotionParticle(world, d, e + 0.5, f, g, h, i);
			particle.setMiscTexture(81);
			particle.setColor(1.0F, 1.0F, 1.0F);
			return particle;
		}
	}

	public static class HealthFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new EmotionParticle(world, d, e, f, g, h, i);
		}
	}
}
