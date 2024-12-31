package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FireSmokeParticle extends Particle {
	float prevScale;

	private FireSmokeParticle(World world, double d, double e, double f, double g, double h, double i) {
		this(world, d, e, f, g, h, i, 1.0F);
	}

	protected FireSmokeParticle(World world, double d, double e, double f, double g, double h, double i, float j) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g;
		this.velocityY += h;
		this.velocityZ += i;
		this.red = this.green = this.blue = (float)(Math.random() * 0.3F);
		this.scale *= 0.75F;
		this.scale *= j;
		this.prevScale = this.scale;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)((float)this.maxAge * j);
		this.noClip = false;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge * 32.0F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		this.scale = this.prevScale * f;
		super.draw(builder, entity, tickDelta, g, h, i, j, k);
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.remove();
		}

		this.setMiscTexture(7 - this.age * 8 / this.maxAge);
		this.velocityY += 0.004;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		if (this.y == this.prevY) {
			this.velocityX *= 1.1;
			this.velocityZ *= 1.1;
		}

		this.velocityX *= 0.96F;
		this.velocityY *= 0.96F;
		this.velocityZ *= 0.96F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new FireSmokeParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
