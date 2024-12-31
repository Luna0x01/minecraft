package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.Identifier;

public class class_4216 {
	private final Identifier[] field_20642 = new Identifier[6];

	public class_4216(Identifier identifier) {
		for (int i = 0; i < 6; i++) {
			this.field_20642[i] = new Identifier(identifier.getNamespace(), identifier.getPath() + '_' + i + ".png");
		}
	}

	public void method_19048(MinecraftClient minecraftClient, float f, float g) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.method_19121(
			Matrix4f.method_19642(85.0, (float)minecraftClient.field_19944.method_18317() / (float)minecraftClient.field_19944.method_18318(), 0.05F, 10.0F)
		);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.method_12288(
			GlStateManager.class_2870.SRC_ALPHA, GlStateManager.class_2866.ONE_MINUS_SRC_ALPHA, GlStateManager.class_2870.ONE, GlStateManager.class_2866.ZERO
		);
		int i = 2;

		for (int j = 0; j < 4; j++) {
			GlStateManager.pushMatrix();
			float h = ((float)(j % 2) / 2.0F - 0.5F) / 256.0F;
			float k = ((float)(j / 2) / 2.0F - 0.5F) / 256.0F;
			float l = 0.0F;
			GlStateManager.translate(h, k, 0.0F);
			GlStateManager.rotate(f, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(g, 0.0F, 1.0F, 0.0F);

			for (int m = 0; m < 6; m++) {
				minecraftClient.getTextureManager().bindTexture(this.field_20642[m]);
				bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
				int n = 255 / (j + 1);
				if (m == 0) {
					bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, 1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, -1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, n).next();
				}

				if (m == 1) {
					bufferBuilder.vertex(1.0, -1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, 1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, n).next();
				}

				if (m == 2) {
					bufferBuilder.vertex(1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, n).next();
				}

				if (m == 3) {
					bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, n).next();
				}

				if (m == 4) {
					bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(0.0, 0.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(0.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, -1.0, 1.0).texture(1.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, -1.0, -1.0).texture(1.0, 0.0).color(255, 255, 255, n).next();
				}

				if (m == 5) {
					bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(0.0, 0.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(0.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, 1.0, -1.0).texture(1.0, 1.0).color(255, 255, 255, n).next();
					bufferBuilder.vertex(1.0, 1.0, 1.0).texture(1.0, 0.0).color(255, 255, 255, n).next();
				}

				tessellator.draw();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		bufferBuilder.offset(0.0, 0.0, 0.0);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepthTest();
	}
}
