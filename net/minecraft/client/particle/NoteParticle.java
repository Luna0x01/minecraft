package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class NoteParticle extends Particle {
	float prevScale;

	protected NoteParticle(World world, double d, double e, double f, double g, double h, double i) {
		this(world, d, e, f, g, h, i, 2.0F);
	}

	protected NoteParticle(World world, double d, double e, double f, double g, double h, double i, float j) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.01F;
		this.velocityY *= 0.01F;
		this.velocityZ *= 0.01F;
		this.velocityY += 0.2;
		this.red = MathHelper.sin(((float)g + 0.0F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.green = MathHelper.sin(((float)g + 0.33333334F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.blue = MathHelper.sin(((float)g + 0.6666667F) * (float) (Math.PI * 2)) * 0.65F + 0.35F;
		this.scale *= 0.75F;
		this.scale *= j;
		this.prevScale = this.scale;
		this.maxAge = 6;
		this.setMiscTexture(64);
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

		this.velocityX *= 0.66F;
		this.velocityY *= 0.66F;
		this.velocityZ *= 0.66F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new NoteParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
