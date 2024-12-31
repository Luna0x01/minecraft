package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class LavaEmberParticle extends Particle {
	private float prevScale;

	protected LavaEmberParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.8F;
		this.velocityY *= 0.8F;
		this.velocityZ *= 0.8F;
		this.velocityY = (double)(this.random.nextFloat() * 0.4F + 0.05F);
		this.red = this.green = this.blue = 1.0F;
		this.scale = this.scale * (this.random.nextFloat() * 2.0F + 0.2F);
		this.prevScale = this.scale;
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		this.noClip = false;
		this.setMiscTexture(49);
	}

	@Override
	public int getLightmapCoordinates(float f) {
		float g = ((float)this.age + f) / (float)this.maxAge;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		int i = super.getLightmapCoordinates(f);
		int j = 240;
		int k = i >> 16 & 0xFF;
		return j | k << 16;
	}

	@Override
	public float getBrightnessAtEyes(float f) {
		return 1.0F;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge;
		this.scale = this.prevScale * (1.0F - f * f);
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

		float f = (float)this.age / (float)this.maxAge;
		if (this.random.nextFloat() > f) {
			this.world.addParticle(ParticleType.SMOKE, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
		}

		this.velocityY -= 0.03;
		this.move(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.999F;
		this.velocityY *= 0.999F;
		this.velocityZ *= 0.999F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new LavaEmberParticle(world, x, y, z);
		}
	}
}
