package net.minecraft.client.particle;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CloudParticle extends Particle {
	float prevScale;

	protected CloudParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		float j = 2.5F;
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g;
		this.velocityY += h;
		this.velocityZ += i;
		this.red = this.green = this.blue = 1.0F - (float)(Math.random() * 0.3F);
		this.scale *= 0.75F;
		this.scale *= j;
		this.prevScale = this.scale;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.3));
		this.maxAge = (int)((float)this.maxAge * j);
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

		this.setMiscTexture(7 - this.age * 8 / this.maxAge);
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.96F;
		this.velocityY *= 0.96F;
		this.velocityZ *= 0.96F;
		PlayerEntity playerEntity = this.field_13424.method_11478(this.field_13428, this.field_13429, this.field_13430, 2.0, false);
		if (playerEntity != null) {
			Box box = playerEntity.getBoundingBox();
			if (this.field_13429 > box.minY) {
				this.field_13429 = this.field_13429 + (box.minY - this.field_13429) * 0.2;
				this.velocityY = this.velocityY + (playerEntity.velocityY - this.velocityY) * 0.2;
				this.method_12247(this.field_13428, this.field_13429, this.field_13430);
			}
		}

		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new CloudParticle(world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
