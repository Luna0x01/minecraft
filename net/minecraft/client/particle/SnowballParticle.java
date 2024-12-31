package net.minecraft.client.particle;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class SnowballParticle extends Particle {
	protected SnowballParticle(World world, double d, double e, double f, Item item) {
		this(world, d, e, f, item, 0);
	}

	protected SnowballParticle(World world, double d, double e, double f, double g, double h, double i, Item item, int j) {
		this(world, d, e, f, item, j);
		this.velocityX *= 0.1F;
		this.velocityY *= 0.1F;
		this.velocityZ *= 0.1F;
		this.velocityX += g;
		this.velocityY += h;
		this.velocityZ += i;
	}

	protected SnowballParticle(World world, double d, double e, double f, Item item, int i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.setTexture(MinecraftClient.getInstance().getItemRenderer().getModels().getSprite(item, i));
		this.red = this.green = this.blue = 1.0F;
		this.gravityStrength = Blocks.SNOW.particleGravity;
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

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			int i = arr.length > 1 ? arr[1] : 0;
			return new SnowballParticle(world, x, y, z, velocityX, velocityY, velocityZ, Item.byRawId(arr[0]), i);
		}
	}

	public static class SlimeFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new SnowballParticle(world, x, y, z, Items.SLIME_BALL);
		}
	}

	public static class SnowballFactory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new SnowballParticle(world, x, y, z, Items.SNOWBALL);
		}
	}
}
