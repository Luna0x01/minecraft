package net.minecraft.client.particle;

import net.minecraft.class_4343;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class DragonBreathParticle extends Particle {
	private final float field_13417;
	private boolean field_13416;

	protected DragonBreathParticle(World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, g, h, i);
		this.velocityX = g;
		this.velocityY = h;
		this.velocityZ = i;
		this.red = MathHelper.nextFloat(this.field_13438, 0.7176471F, 0.8745098F);
		this.green = MathHelper.nextFloat(this.field_13438, 0.0F, 0.0F);
		this.blue = MathHelper.nextFloat(this.field_13438, 0.8235294F, 0.9764706F);
		this.scale *= 0.75F;
		this.field_13417 = this.scale;
		this.maxAge = (int)(20.0 / ((double)this.field_13438.nextFloat() * 0.8 + 0.2));
		this.field_13416 = false;
		this.field_14950 = false;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		if (this.age++ >= this.maxAge) {
			this.method_12251();
		} else {
			this.setMiscTexture(3 * this.age / this.maxAge + 5);
			if (this.field_13434) {
				this.velocityY = 0.0;
				this.field_13416 = true;
			}

			if (this.field_13416) {
				this.velocityY += 0.002;
			}

			this.method_12242(this.velocityX, this.velocityY, this.velocityZ);
			if (this.field_13429 == this.field_13426) {
				this.velocityX *= 1.1;
				this.velocityZ *= 1.1;
			}

			this.velocityX *= 0.96F;
			this.velocityZ *= 0.96F;
			if (this.field_13416) {
				this.velocityY *= 0.96F;
			}
		}
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		this.scale = this.field_13417 * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
		super.draw(builder, entity, tickDelta, g, h, i, j, k);
	}

	public static class Factory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new DragonBreathParticle(world, d, e, f, g, h, i);
		}
	}
}
