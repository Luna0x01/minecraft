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

public class SweepAttackParticle extends Particle {
	private static final Identifier TEXTURE = new Identifier("textures/entity/sweep.png");
	private static final VertexFormat field_13410 = new VertexFormat()
		.addElement(VertexFormats.POSITION_ELEMENT)
		.addElement(VertexFormats.TEXTURE_FLOAT_ELEMENT)
		.addElement(VertexFormats.COLOR_ELEMENT)
		.addElement(VertexFormats.TEXTURE_SHORT_ELEMENT)
		.addElement(VertexFormats.NORMAL_ELEMENT)
		.addElement(VertexFormats.PADDING_ELEMENT);
	private int field_13411;
	private final int field_13412;
	private final TextureManager field_13413;
	private final float field_13414;

	protected SweepAttackParticle(TextureManager textureManager, World world, double d, double e, double f, double g, double h, double i) {
		super(world, d, e, f, 0.0, 0.0, 0.0);
		this.field_13413 = textureManager;
		this.field_13412 = 4;
		float j = this.field_13438.nextFloat() * 0.6F + 0.4F;
		this.red = j;
		this.green = j;
		this.blue = j;
		this.field_13414 = 1.0F - (float)g * 0.5F;
	}

	@Override
	public void draw(BufferBuilder builder, Entity entity, float tickDelta, float g, float h, float i, float j, float k) {
		int l = (int)(((float)this.field_13411 + tickDelta) * 3.0F / (float)this.field_13412);
		if (l <= 7) {
			this.field_13413.bindTexture(TEXTURE);
			float f = (float)(l % 4) / 4.0F;
			float m = f + 0.24975F;
			float n = (float)(l / 2) / 2.0F;
			float o = n + 0.4995F;
			float p = 1.0F * this.field_13414;
			float q = (float)(this.field_13425 + (this.field_13428 - this.field_13425) * (double)tickDelta - field_1722);
			float r = (float)(this.field_13426 + (this.field_13429 - this.field_13426) * (double)tickDelta - field_1723);
			float s = (float)(this.field_13427 + (this.field_13430 - this.field_13427) * (double)tickDelta - field_1724);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			DiffuseLighting.disable();
			builder.begin(7, field_13410);
			builder.vertex((double)(q - g * p - j * p), (double)(r - h * p * 0.5F), (double)(s - i * p - k * p))
				.texture((double)m, (double)o)
				.color(this.red, this.green, this.blue, 1.0F)
				.texture2(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			builder.vertex((double)(q - g * p + j * p), (double)(r + h * p * 0.5F), (double)(s - i * p + k * p))
				.texture((double)m, (double)n)
				.color(this.red, this.green, this.blue, 1.0F)
				.texture2(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			builder.vertex((double)(q + g * p + j * p), (double)(r + h * p * 0.5F), (double)(s + i * p + k * p))
				.texture((double)f, (double)n)
				.color(this.red, this.green, this.blue, 1.0F)
				.texture2(0, 240)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			builder.vertex((double)(q + g * p - j * p), (double)(r - h * p * 0.5F), (double)(s + i * p - k * p))
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
		this.field_13411++;
		if (this.field_13411 == this.field_13412) {
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
			return new SweepAttackParticle(MinecraftClient.getInstance().getTextureManager(), world, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
}
