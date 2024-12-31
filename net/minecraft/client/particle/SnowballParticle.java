package net.minecraft.client.particle;

import net.minecraft.class_4339;
import net.minecraft.class_4343;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class SnowballParticle extends Particle {
	protected SnowballParticle(World world, double d, double e, double f, double g, double h, double i, ItemStack itemStack) {
		this(world, d, e, f, itemStack);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g;
		this.velocityY += h;
		this.velocityZ += i;
	}

	protected SnowballParticle(World world, double d, double e, double f, ItemStack itemStack) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.setTexture(MinecraftClient.getInstance().getHeldItemRenderer().method_19372().method_19154(itemStack));
		this.red = 1.0F;
		this.green = 1.0F;
		this.blue = 1.0F;
		this.gravityStrength = 1.0F;
		this.scale /= 2.0F;
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = ((float)this.field_5935 + this.field_1725 / 4.0F) / 16.0F;
		float l = f + 0.015609375F;
		float m = ((float)this.field_5936 + this.field_1726 / 4.0F) / 16.0F;
		float n = m + 0.015609375F;
		float o = 0.1F * this.scale;
		if (this.sprite != null) {
			f = this.sprite.getFrameU((double)(this.field_1725 / 4.0F * 16.0F));
			l = this.sprite.getFrameU((double)((this.field_1725 + 1.0F) / 4.0F * 16.0F));
			m = this.sprite.getFrameV((double)(this.field_1726 / 4.0F * 16.0F));
			n = this.sprite.getFrameV((double)((this.field_1726 + 1.0F) / 4.0F * 16.0F));
		}

		float p = (float)(this.field_13425 + (this.field_13428 - this.field_13425) * (double)tickDelta - field_1722);
		float q = (float)(this.field_13426 + (this.field_13429 - this.field_13426) * (double)tickDelta - field_1723);
		float r = (float)(this.field_13427 + (this.field_13430 - this.field_13427) * (double)tickDelta - field_1724);
		int s = this.method_12243(tickDelta);
		int t = s >> 16 & 65535;
		int u = s & 65535;
		builder.vertex((double)(p - g * o - j * o), (double)(q - h * o), (double)(r - i * o - k * o))
			.texture((double)f, (double)n)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p - g * o + j * o), (double)(q + h * o), (double)(r - i * o + k * o))
			.texture((double)f, (double)m)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p + g * o + j * o), (double)(q + h * o), (double)(r + i * o + k * o))
			.texture((double)l, (double)m)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p + g * o - j * o), (double)(q - h * o), (double)(r + i * o - k * o))
			.texture((double)l, (double)n)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
	}

	public static class Factory implements ParticleFactory<class_4339> {
		public Particle method_19020(class_4339 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new SnowballParticle(world, d, e, f, g, h, i, arg.method_19975());
		}
	}

	public static class SlimeFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new SnowballParticle(world, d, e, f, new ItemStack(Items.SLIME_BALL));
		}
	}

	public static class SnowballFactory implements ParticleFactory<class_4343> {
		public Particle method_19020(class_4343 arg, World world, double d, double e, double f, double g, double h, double i) {
			return new SnowballParticle(world, d, e, f, new ItemStack(Items.SNOWBALL));
		}
	}
}
