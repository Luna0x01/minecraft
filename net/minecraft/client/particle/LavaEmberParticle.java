package net.minecraft.client.particle;

import net.minecraft.class_4342;
import net.minecraft.class_4343;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class LavaEmberParticle extends Particle {
	private final float prevScale;

	protected LavaEmberParticle(World world, double d, double e, double f) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.velocityX *= 0.8F;
		this.velocityY *= 0.8F;
		this.velocityZ *= 0.8F;
		this.velocityY = (double)(this.field_13438.nextFloat() * 0.4F + 0.05F);
		this.red = 1.0F;
		this.green = 1.0F;
		this.blue = 1.0F;
		this.scale = this.scale * (this.field_13438.nextFloat() * 2.0F + 0.2F);
		this.prevScale = this.scale;
		this.maxAge = (int)(16.0 / (Math.random() * 0.8 + 0.2));
		this.setMiscTexture(49);
	}

	@Override
	public int method_12243(float f) {
		int i = super.method_12243(f);
		int j = 240;
		int k = i >> 16 & 0xFF;
		return 240 | k << 16;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.age + tickDelta) / (float)this.maxAge;
		this.scale = this.prevScale * (1.0F - f * f);
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

		float f = (float)this.age / (float)this.maxAge;
		if (this.field_13438.nextFloat() > f) {
			this.field_13424.method_16343(class_4342.field_21363, this.field_13428, this.field_13429, this.field_13430, this.velocityX, this.velocityY, this.velocityZ);
		}

		this.velocityY -= 0.03;
		this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.999F;
		this.velocityY *= 0.999F;
		this.velocityZ *= 0.999F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new LavaEmberParticle(world, d, e, f);
		}
	}
}
