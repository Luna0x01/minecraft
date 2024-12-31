package net.minecraft.client.particle;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BarrierParticle extends Particle {
	protected BarrierParticle(World world, double d, double e, double f, Item item) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.setTexture(MinecraftClient.getInstance().getItemRenderer().getModels().getSprite(item));
		this.red = this.green = this.blue = 1.0F;
		this.velocityX = this.velocityY = this.velocityZ = 0.0;
		this.gravityStrength = 0.0F;
		this.maxAge = 80;
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		float f = this.sprite.getMinU();
		float l = this.sprite.getMaxU();
		float m = this.sprite.getMinV();
		float n = this.sprite.getMaxV();
		float o = 0.5F;
		float p = (float)(this.prevX + (this.x - this.prevX) * (double)tickDelta - field_1722);
		float q = (float)(this.prevY + (this.y - this.prevY) * (double)tickDelta - field_1723);
		float r = (float)(this.prevZ + (this.z - this.prevZ) * (double)tickDelta - field_1724);
		int s = this.getLightmapCoordinates(tickDelta);
		int t = s >> 16 & 65535;
		int u = s & 65535;
		builder.vertex((double)(p - g * 0.5F - j * 0.5F), (double)(q - h * 0.5F), (double)(r - i * 0.5F - k * 0.5F))
			.texture((double)l, (double)n)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p - g * 0.5F + j * 0.5F), (double)(q + h * 0.5F), (double)(r - i * 0.5F + k * 0.5F))
			.texture((double)l, (double)m)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p + g * 0.5F + j * 0.5F), (double)(q + h * 0.5F), (double)(r + i * 0.5F + k * 0.5F))
			.texture((double)f, (double)m)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
		builder.vertex((double)(p + g * 0.5F - j * 0.5F), (double)(q - h * 0.5F), (double)(r + i * 0.5F - k * 0.5F))
			.texture((double)f, (double)n)
			.color(this.red, this.green, this.blue, 1.0F)
			.texture2(t, u)
			.next();
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new BarrierParticle(world, x, y, z, Item.fromBlock(Blocks.BARRIER));
		}
	}
}
