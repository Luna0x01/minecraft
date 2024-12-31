package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

public class DrawableHelper {
	public static final Identifier OPTIONS_BACKGROUND_TEXTURE = new Identifier("textures/gui/options_background.png");
	public static final Identifier STATS_ICON_TEXTURE = new Identifier("textures/gui/container/stats_icons.png");
	public static final Identifier GUI_ICONS_TEXTURE = new Identifier("textures/gui/icons.png");
	protected float zOffset;

	protected void drawHorizontalLine(int x1, int x2, int y, int color) {
		if (x2 < x1) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		fill(x1, y, x2 + 1, y + 1, color);
	}

	protected void drawVerticalLine(int x, int y1, int y2, int color) {
		if (y2 < y1) {
			int i = y1;
			y1 = y2;
			y2 = i;
		}

		fill(x, y1 + 1, x + 1, y2, color);
	}

	public static void fill(int x1, int y1, int x2, int y2, int color) {
		if (x1 < x2) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1 < y2) {
			int j = y1;
			y1 = y2;
			y2 = j;
		}

		float f = (float)(color >> 24 & 0xFF) / 255.0F;
		float g = (float)(color >> 16 & 0xFF) / 255.0F;
		float h = (float)(color >> 8 & 0xFF) / 255.0F;
		float k = (float)(color & 0xFF) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(g, h, k, f);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)x1, (double)y2, 0.0).next();
		bufferBuilder.vertex((double)x2, (double)y2, 0.0).next();
		bufferBuilder.vertex((double)x2, (double)y1, 0.0).next();
		bufferBuilder.vertex((double)x1, (double)y1, 0.0).next();
		tessellator.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}

	protected void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
		float f = (float)(color1 >> 24 & 0xFF) / 255.0F;
		float g = (float)(color1 >> 16 & 0xFF) / 255.0F;
		float h = (float)(color1 >> 8 & 0xFF) / 255.0F;
		float i = (float)(color1 & 0xFF) / 255.0F;
		float j = (float)(color2 >> 24 & 0xFF) / 255.0F;
		float k = (float)(color2 >> 16 & 0xFF) / 255.0F;
		float l = (float)(color2 >> 8 & 0xFF) / 255.0F;
		float m = (float)(color2 & 0xFF) / 255.0F;
		GlStateManager.disableTexture();
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((double)x2, (double)y1, (double)this.zOffset).color(g, h, i, f).next();
		bufferBuilder.vertex((double)x1, (double)y1, (double)this.zOffset).color(g, h, i, f).next();
		bufferBuilder.vertex((double)x1, (double)y2, (double)this.zOffset).color(k, l, m, j).next();
		bufferBuilder.vertex((double)x2, (double)y2, (double)this.zOffset).color(k, l, m, j).next();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.enableTexture();
	}

	public void drawCenteredString(TextRenderer textRenderer, String text, int centerX, int y, int color) {
		textRenderer.drawWithShadow(text, (float)(centerX - textRenderer.getStringWidth(text) / 2), (float)y, color);
	}

	public void drawWithShadow(TextRenderer textRenderer, String text, int x, int y, int color) {
		textRenderer.drawWithShadow(text, (float)x, (float)y, color);
	}

	public void drawTexture(int x, int y, int u, int v, int width, int height) {
		float f = 0.00390625F;
		float g = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex((double)(x + 0), (double)(y + height), (double)this.zOffset)
			.texture((double)((float)(u + 0) * f), (double)((float)(v + height) * g))
			.next();
		bufferBuilder.vertex((double)(x + width), (double)(y + height), (double)this.zOffset)
			.texture((double)((float)(u + width) * f), (double)((float)(v + height) * g))
			.next();
		bufferBuilder.vertex((double)(x + width), (double)(y + 0), (double)this.zOffset)
			.texture((double)((float)(u + width) * f), (double)((float)(v + 0) * g))
			.next();
		bufferBuilder.vertex((double)(x + 0), (double)(y + 0), (double)this.zOffset).texture((double)((float)(u + 0) * f), (double)((float)(v + 0) * g)).next();
		tessellator.draw();
	}

	public void drawTexture(float x, float y, int u, int v, int width, int height) {
		float f = 0.00390625F;
		float g = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex((double)(x + 0.0F), (double)(y + (float)height), (double)this.zOffset)
			.texture((double)((float)(u + 0) * f), (double)((float)(v + height) * g))
			.next();
		bufferBuilder.vertex((double)(x + (float)width), (double)(y + (float)height), (double)this.zOffset)
			.texture((double)((float)(u + width) * f), (double)((float)(v + height) * g))
			.next();
		bufferBuilder.vertex((double)(x + (float)width), (double)(y + 0.0F), (double)this.zOffset)
			.texture((double)((float)(u + width) * f), (double)((float)(v + 0) * g))
			.next();
		bufferBuilder.vertex((double)(x + 0.0F), (double)(y + 0.0F), (double)this.zOffset).texture((double)((float)(u + 0) * f), (double)((float)(v + 0) * g)).next();
		tessellator.draw();
	}

	public void drawSprite(int x, int y, Sprite sprite, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex((double)(x + 0), (double)(y + height), (double)this.zOffset).texture((double)sprite.getMinU(), (double)sprite.getMaxV()).next();
		bufferBuilder.vertex((double)(x + width), (double)(y + height), (double)this.zOffset).texture((double)sprite.getMaxU(), (double)sprite.getMaxV()).next();
		bufferBuilder.vertex((double)(x + width), (double)(y + 0), (double)this.zOffset).texture((double)sprite.getMaxU(), (double)sprite.getMinV()).next();
		bufferBuilder.vertex((double)(x + 0), (double)(y + 0), (double)this.zOffset).texture((double)sprite.getMinU(), (double)sprite.getMinV()).next();
		tessellator.draw();
	}

	public static void drawTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float g = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex((double)x, (double)(y + height), 0.0).texture((double)(u * f), (double)((v + (float)height) * g)).next();
		bufferBuilder.vertex((double)(x + width), (double)(y + height), 0.0).texture((double)((u + (float)width) * f), (double)((v + (float)height) * g)).next();
		bufferBuilder.vertex((double)(x + width), (double)y, 0.0).texture((double)((u + (float)width) * f), (double)(v * g)).next();
		bufferBuilder.vertex((double)x, (double)y, 0.0).texture((double)(u * f), (double)(v * g)).next();
		tessellator.draw();
	}

	public static void drawTexture(
		int x, int y, float u, float v, int regionWidth, int regionHeight, int width, int height, float textureWidth, float textureHeight
	) {
		float f = 1.0F / textureWidth;
		float g = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex((double)x, (double)(y + height), 0.0).texture((double)(u * f), (double)((v + (float)regionHeight) * g)).next();
		bufferBuilder.vertex((double)(x + width), (double)(y + height), 0.0)
			.texture((double)((u + (float)regionWidth) * f), (double)((v + (float)regionHeight) * g))
			.next();
		bufferBuilder.vertex((double)(x + width), (double)y, 0.0).texture((double)((u + (float)regionWidth) * f), (double)(v * g)).next();
		bufferBuilder.vertex((double)x, (double)y, 0.0).texture((double)(u * f), (double)(v * g)).next();
		tessellator.draw();
	}
}
