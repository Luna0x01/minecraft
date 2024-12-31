package net.minecraft.client.particle;

import net.minecraft.class_4338;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RedstoneParticle extends Particle {
	private final float prevScale;

	public RedstoneParticle(World world, double d, double e, double f, double g, double h, double i, class_4338 arg) {
		super(world, d, e, f, g, h, i);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		float j = (float)Math.random() * 0.4F + 0.6F;
		this.red = ((float)(Math.random() * 0.2F) + 0.8F) * arg.method_19969() * j;
		this.green = ((float)(Math.random() * 0.2F) + 0.8F) * arg.method_19970() * j;
		this.blue = ((float)(Math.random() * 0.2F) + 0.8F) * arg.method_19971() * j;
		this.scale *= 0.75F;
		this.scale = this.scale * arg.method_19972();
		this.prevScale = this.scale;
		this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
		this.maxAge = (int)((float)this.maxAge * arg.method_19972());
		this.maxAge = Math.max(this.maxAge, 1);
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
		if (this.field_13429 == this.field_13426) {
			this.velocityX *= 1.1;
			this.velocityZ *= 1.1;
		}

		this.velocityX *= 0.96F;
		this.velocityY *= 0.96F;
		this.velocityZ *= 0.96F;
		if (this.field_13434) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
		}
	}

	public static class Factory implements ParticleFactory<class_4338> {
		public Particle method_19020(class_4338 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new RedstoneParticle(world, d, e, f, g, h, i, arg);
		}
	}
}
