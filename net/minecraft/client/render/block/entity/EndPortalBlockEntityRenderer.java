package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.class_4239;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

public class EndPortalBlockEntityRenderer extends class_4239<EndPortalBlockEntity> {
	private static final Identifier SKY_TEXTURE = new Identifier("textures/environment/end_sky.png");
	private static final Identifier PORTAL_TEXTURE = new Identifier("textures/entity/end_portal.png");
	private static final Random RANDOM = new Random(31100L);
	private static final FloatBuffer field_15279 = GlAllocationUtils.allocateFloatBuffer(16);
	private static final FloatBuffer field_15280 = GlAllocationUtils.allocateFloatBuffer(16);
	private final FloatBuffer buffer = GlAllocationUtils.allocateFloatBuffer(16);

	public void method_1631(EndPortalBlockEntity endPortalBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.disableLighting();
		RANDOM.setSeed(31100L);
		GlStateManager.getFloat(2982, field_15279);
		GlStateManager.getFloat(2983, field_15280);
		double h = d * d + e * e + f * f;
		int j = this.method_13854(h);
		float k = this.method_13855();
		boolean bl = false;

		for (int l = 0; l < j; l++) {
			GlStateManager.pushMatrix();
			float m = 2.0F / (float)(18 - l);
			if (l == 0) {
				this.method_19327(SKY_TEXTURE);
				m = 0.15F;
				GlStateManager.enableBlend();
				GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
			}

			if (l >= 1) {
				this.method_19327(PORTAL_TEXTURE);
				bl = true;
				MinecraftClient.getInstance().field_3818.method_19079(true);
			}

			if (l == 1) {
				GlStateManager.enableBlend();
				GlStateManager.method_12287(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ONE);
			}

			GlStateManager.genTex(GlStateManager.TexCoord.S, 9216);
			GlStateManager.genTex(GlStateManager.TexCoord.T, 9216);
			GlStateManager.genTex(GlStateManager.TexCoord.R, 9216);
			GlStateManager.genTex(GlStateManager.TexCoord.S, 9474, this.createBuffer(1.0F, 0.0F, 0.0F, 0.0F));
			GlStateManager.genTex(GlStateManager.TexCoord.T, 9474, this.createBuffer(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.genTex(GlStateManager.TexCoord.R, 9474, this.createBuffer(0.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.method_12289(GlStateManager.TexCoord.S);
			GlStateManager.method_12289(GlStateManager.TexCoord.T);
			GlStateManager.method_12289(GlStateManager.TexCoord.R);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			GlStateManager.scale(0.5F, 0.5F, 1.0F);
			float n = (float)(l + 1);
			GlStateManager.translate(17.0F / n, (2.0F + n / 1.5F) * ((float)Util.method_20227() % 800000.0F / 800000.0F), 0.0F);
			GlStateManager.rotate((n * n * 4321.0F + n * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.scale(4.5F - n / 4.0F, 4.5F - n / 4.0F, 1.0F);
			GlStateManager.multiMatrix(field_15280);
			GlStateManager.multiMatrix(field_15279);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			float o = (RANDOM.nextFloat() * 0.5F + 0.1F) * m;
			float p = (RANDOM.nextFloat() * 0.5F + 0.4F) * m;
			float q = (RANDOM.nextFloat() * 0.5F + 0.5F) * m;
			if (endPortalBlockEntity.method_11689(Direction.SOUTH)) {
				bufferBuilder.vertex(d, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f + 1.0).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.method_11689(Direction.NORTH)) {
				bufferBuilder.vertex(d, e + 1.0, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e, f).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.method_11689(Direction.EAST)) {
				bufferBuilder.vertex(d + 1.0, e + 1.0, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.method_11689(Direction.WEST)) {
				bufferBuilder.vertex(d, e, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.method_11689(Direction.DOWN)) {
				bufferBuilder.vertex(d, e, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e, f + 1.0).color(o, p, q, 1.0F).next();
			}

			if (endPortalBlockEntity.method_11689(Direction.UP)) {
				bufferBuilder.vertex(d, e + (double)k, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + (double)k, f + 1.0).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + (double)k, f).color(o, p, q, 1.0F).next();
				bufferBuilder.vertex(d, e + (double)k, f).color(o, p, q, 1.0F).next();
			}

			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			this.method_19327(SKY_TEXTURE);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.S);
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.T);
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.R);
		GlStateManager.enableLighting();
		if (bl) {
			MinecraftClient.getInstance().field_3818.method_19079(false);
		}
	}

	protected int method_13854(double d) {
		int i;
		if (d > 36864.0) {
			i = 1;
		} else if (d > 25600.0) {
			i = 3;
		} else if (d > 16384.0) {
			i = 5;
		} else if (d > 9216.0) {
			i = 7;
		} else if (d > 4096.0) {
			i = 9;
		} else if (d > 1024.0) {
			i = 11;
		} else if (d > 576.0) {
			i = 13;
		} else if (d > 256.0) {
			i = 14;
		} else {
			i = 15;
		}

		return i;
	}

	protected float method_13855() {
		return 0.75F;
	}

	private FloatBuffer createBuffer(float f1, float f2, float f3, float f4) {
		this.buffer.clear();
		this.buffer.put(f1).put(f2).put(f3).put(f4);
		this.buffer.flip();
		return this.buffer;
	}
}
