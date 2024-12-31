package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class EndGatewayBlockEntityRenderer extends BlockEntityRenderer<EndGatewayBlockEntity> {
	private static final Identifier field_13600 = new Identifier("textures/environment/end_sky.png");
	private static final Identifier field_13601 = new Identifier("textures/entity/end_portal.png");
	private static final Identifier TEXTURE = new Identifier("textures/entity/end_gateway_beam.png");
	private static final Random field_13603 = new Random(31100L);
	private static final FloatBuffer field_13604 = GlAllocationUtils.allocateFloatBuffer(16);
	private static final FloatBuffer field_13605 = GlAllocationUtils.allocateFloatBuffer(16);
	FloatBuffer field_13599 = GlAllocationUtils.allocateFloatBuffer(16);

	public void render(EndGatewayBlockEntity endGatewayBlockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.disableFog();
		if (endGatewayBlockEntity.method_11692() || endGatewayBlockEntity.hasCooldown()) {
			GlStateManager.alphaFunc(516, 0.1F);
			this.bindTexture(TEXTURE);
			float h = endGatewayBlockEntity.method_11692() ? endGatewayBlockEntity.method_11694() : endGatewayBlockEntity.method_11695();
			double j = endGatewayBlockEntity.method_11692() ? 256.0 - e : 25.0;
			h = MathHelper.sin(h * (float) Math.PI);
			int k = MathHelper.floor((double)h * j);
			float[] fs = SheepEntity.getDyedColor(endGatewayBlockEntity.method_11692() ? DyeColor.MAGENTA : DyeColor.YELLOW);
			BeaconBlockEntityRenderer.method_12407(
				d, e, f, (double)g, (double)h, (double)endGatewayBlockEntity.getEntityWorld().getLastUpdateTime(), 0, k, fs, 0.15, 0.175
			);
			BeaconBlockEntityRenderer.method_12407(
				d, e, f, (double)g, (double)h, (double)endGatewayBlockEntity.getEntityWorld().getLastUpdateTime(), 0, -k, fs, 0.15, 0.175
			);
		}

		GlStateManager.disableLighting();
		field_13603.setSeed(31100L);
		GlStateManager.getFloat(2982, field_13604);
		GlStateManager.getFloat(2983, field_13605);
		double l = d * d + e * e + f * f;
		int m;
		if (l > 36864.0) {
			m = 2;
		} else if (l > 25600.0) {
			m = 4;
		} else if (l > 16384.0) {
			m = 6;
		} else if (l > 9216.0) {
			m = 8;
		} else if (l > 4096.0) {
			m = 10;
		} else if (l > 1024.0) {
			m = 12;
		} else if (l > 576.0) {
			m = 14;
		} else if (l > 256.0) {
			m = 15;
		} else {
			m = 16;
		}

		for (int v = 0; v < m; v++) {
			GlStateManager.pushMatrix();
			float w = 2.0F / (float)(18 - v);
			if (v == 0) {
				this.bindTexture(field_13600);
				w = 0.15F;
				GlStateManager.enableBlend();
				GlStateManager.method_12287(GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA);
			}

			if (v >= 1) {
				this.bindTexture(field_13601);
			}

			if (v == 1) {
				GlStateManager.enableBlend();
				GlStateManager.method_12287(GlStateManager.class_2870.ONE, GlStateManager.class_2866.ONE);
			}

			GlStateManager.genTex(GlStateManager.TexCoord.S, 9216);
			GlStateManager.genTex(GlStateManager.TexCoord.T, 9216);
			GlStateManager.genTex(GlStateManager.TexCoord.R, 9216);
			GlStateManager.genTex(GlStateManager.TexCoord.S, 9474, this.method_12414(1.0F, 0.0F, 0.0F, 0.0F));
			GlStateManager.genTex(GlStateManager.TexCoord.T, 9474, this.method_12414(0.0F, 1.0F, 0.0F, 0.0F));
			GlStateManager.genTex(GlStateManager.TexCoord.R, 9474, this.method_12414(0.0F, 0.0F, 1.0F, 0.0F));
			GlStateManager.method_12289(GlStateManager.TexCoord.S);
			GlStateManager.method_12289(GlStateManager.TexCoord.T);
			GlStateManager.method_12289(GlStateManager.TexCoord.R);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			GlStateManager.scale(0.5F, 0.5F, 1.0F);
			float x = (float)(v + 1);
			GlStateManager.translate(17.0F / x, (2.0F + x / 1.5F) * ((float)MinecraftClient.getTime() % 800000.0F / 800000.0F), 0.0F);
			GlStateManager.rotate((x * x * 4321.0F + x * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.scale(4.5F - x / 4.0F, 4.5F - x / 4.0F, 1.0F);
			GlStateManager.multiMatrix(field_13605);
			GlStateManager.multiMatrix(field_13604);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
			float y = (field_13603.nextFloat() * 0.5F + 0.1F) * w;
			float z = (field_13603.nextFloat() * 0.5F + 0.4F) * w;
			float aa = (field_13603.nextFloat() * 0.5F + 0.5F) * w;
			if (endGatewayBlockEntity.method_11689(Direction.SOUTH)) {
				bufferBuilder.vertex(d, e, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f + 1.0).color(y, z, aa, 1.0F).next();
			}

			if (endGatewayBlockEntity.method_11689(Direction.NORTH)) {
				bufferBuilder.vertex(d, e + 1.0, f).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d, e, f).color(y, z, aa, 1.0F).next();
			}

			if (endGatewayBlockEntity.method_11689(Direction.EAST)) {
				bufferBuilder.vertex(d + 1.0, e + 1.0, f).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f).color(y, z, aa, 1.0F).next();
			}

			if (endGatewayBlockEntity.method_11689(Direction.WEST)) {
				bufferBuilder.vertex(d, e, f).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d, e, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f).color(y, z, aa, 1.0F).next();
			}

			if (endGatewayBlockEntity.method_11689(Direction.DOWN)) {
				bufferBuilder.vertex(d, e, f).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d, e, f + 1.0).color(y, z, aa, 1.0F).next();
			}

			if (endGatewayBlockEntity.method_11689(Direction.UP)) {
				bufferBuilder.vertex(d, e + 1.0, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f + 1.0).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d + 1.0, e + 1.0, f).color(y, z, aa, 1.0F).next();
				bufferBuilder.vertex(d, e + 1.0, f).color(y, z, aa, 1.0F).next();
			}

			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
			this.bindTexture(field_13600);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.S);
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.T);
		GlStateManager.disableTexCoord(GlStateManager.TexCoord.R);
		GlStateManager.enableLighting();
		GlStateManager.enableFog();
	}

	private FloatBuffer method_12414(float f, float g, float h, float i) {
		this.field_13599.clear();
		this.field_13599.put(f).put(g).put(h).put(i);
		this.field_13599.flip();
		return this.field_13599;
	}

	public boolean method_12410(EndGatewayBlockEntity endGatewayBlockEntity) {
		return endGatewayBlockEntity.method_11692() || endGatewayBlockEntity.hasCooldown();
	}
}
