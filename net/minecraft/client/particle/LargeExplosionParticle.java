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
	private final int field_1713;
	private final TextureManager textureManager;
	private final float field_1715;

	protected LargeExplosionParticle(TextureManager textureManager, World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.textureManager = textureManager;
		this.field_1713 = 6 + this.field_13438.nextInt(4);
		float j = this.field_13438.nextFloat() * 0.6F + 0.4F;
		this.red = j;
		this.green = j;
		this.blue = j;
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
			float q = (float)(this.field_13425 + (this.field_13428 - this.field_13425) * (double)tickDelta - field_1722);
			float r = (float)(this.field_13426 + (this.field_13429 - this.field_13426) * (double)tickDelta - field_1723);
			float s = (float)(this.field_13427 + (this.field_13430 - this.field_13427) * (double)tickDelta - field_1724);
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
	public int method_12243(float f) {
		return 61680;
	}

	@Override
	public void method_12241() {
		this.field_13425 = this.field_13428;
		this.field_13426 = this.field_13429;
		this.field_13427 = this.field_13430;
		this.field_1712++;
		if (this.field_1712 == this.field_1713) {
			this.method_12251();
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
