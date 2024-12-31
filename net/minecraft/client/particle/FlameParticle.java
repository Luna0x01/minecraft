package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FlameParticle extends Particle {
	private float prevScale;

	protected FlameParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = this.velocityX * 0.01F + g;
		this.velocityY = this.velocityY * 0.01F + h;
		this.velocityZ = this.velocityZ * 0.01F + i;
		this.x = this.x + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.y = this.y + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.z = this.z + (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
		this.prevScale = this.scale;
		this.red = this.green = this.blue = 1.0F;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
		this.noClip = true;
		this.setMiscTexture(48);
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge;
		this.scale = this.prevScale * (1.0F - f * f * 0.5F);
		super.draw(builder, entity, tickDelta, g, h, i, j, k);
	}

	@Override
	public int getLightmapCoordinates(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		int i = super.getLightmapCoordinates(f);
		int j = i & 0xFF;
		int k = i >> 16 & 0xFF;
		j += (int)(g * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public float getBrightnessAtEyes(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		float h = super.getBrightnessAtEyes(f);
		return h * g + (1.0F - g);
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.remove();
		}

		this.move(this.velocityX, this.velocityY, this.velocityZ);
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
			return new FlameParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
