package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class LargeExplosionParticle extends Particle {
	private static final Identifier EXPLOSION = new Identifier("textures/entity/explosion.png");
	private static final VertexFormat field_10611 = new VertexFormat()
		.addElement(VertexFormats.POSITION_ELEMENT)
		.addElement(VertexFormats.TEXTURE_FLOAT_ELEMENT)
		.addElement(VertexFormats.COLOR_ELEMENT)
		.addElement(VertexFormats.TEXTURE_SHORT_ELEMENT)
		.addElement(VertexFormats.NORMAL_ELEMENT)
		.addElement(VertexFormats.PADDING_ELEMENT);
	private int field_1712;
	private int field_1713;
	private TextureManager textureManager;
	private float field_1715;

	protected LargeExplosionParticle(TextureManager textureManager, World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.textureManager = textureManager;
		this.field_1713 = 6 + this.random.nextInt(4);
		this.red = this.green = this.blue = this.random.nextFloat() * 0.6F + 0.4F;
		this.field_1715 = 1.0F - (float)g * 0.5F;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		int l = (int)(((float)this.field_1712 + tickDelta) * 15.0F / (float)this.field_1713);
		if (l <= 15) {
			this.textureManager.bindTexture(EXPLOSION);
			float f = (float)(l % 4) / 4.0F;
			float m = f + 0.24975F;
			float n = (float)(l / 4) / 4.0F;
			float o = n + 0.24975F;
			float p = 2.0F * this.field_1715;
			float q = (float)(this.prevX + (this.x - this.prevX) * (double)tickDelta - field_1722);
			float r = (float)(this.prevY + (this.y - this.prevY) * (double)tickDelta - field_1723);
			float s = (float)(this.prevZ + (this.z - this.prevZ) * (double)tickDelta - field_1724);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			DiffuseLighting.disable();
			builder.begin(7, field_10611);
			builder.vertex((double)(q - g * p - j * p), (double)(r - h * p), (double)(s - i * p - k * p))
				.texture((double)m, (double)o)
				.color(this.red, this.green, this.blue, 1.0F)
				.texture2(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			builder.vertex((double)(q - g * p + j * p), (double)(r + h * p), (double)(s - i * p + k * p))
				.texture((double)m, (double)n)
				.color(this.red, this.green, this.blue, 1.0F)
				.texture2(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			builder.vertex((double)(q + g * p + j * p), (double)(r + h * p), (double)(s + i * p + k * p))
				.texture((double)f, (double)n)
				.color(this.red, this.green, this.blue, 1.0F)
				.texture2(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			builder.vertex((double)(q + g * p - j * p), (double)(r - h * p), (double)(s + i * p - k * p))
				.texture((double)f, (double)o)
				.color(this.red, this.green, this.blue, 1.0F)
				.texture2(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			Tessellator.getInstance().draw();
			GlStateManager.enableLighting();
		}
	}

	@Override
	public int getLightmapCoordinates(float f) {
		return 61680;
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.field_1712++;
		if (this.field_1712 == this.field_1713) {
			this.remove();
		}
	}

	@Override
	public int getLayer() {
		return 3;
	}

	public static class Factory implements ParticleFactory {
		@Override
		public Particle createParticle(int id, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... arr) {
			return new LargeExplosionParticle(MinecraftClient.getInstance().getTextureManager(), world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
