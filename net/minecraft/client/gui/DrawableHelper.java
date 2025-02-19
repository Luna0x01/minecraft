package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BiConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public abstract class DrawableHelper {
	public static final Identifier OPTIONS_BACKGROUND_TEXTURE = new Identifier("textures/gui/options_background.png");
	public static final Identifier STATS_ICON_TEXTURE = new Identifier("textures/gui/container/stats_icons.png");
	public static final Identifier GUI_ICONS_TEXTURE = new Identifier("textures/gui/icons.png");
	private int zOffset;

	protected void drawHorizontalLine(MatrixStack matrices, int x1, int x2, int y, int color) {
		if (x2 < x1) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		fill(matrices, x1, y, x2 + 1, y + 1, color);
	}

	protected void drawVerticalLine(MatrixStack matrices, int x, int y1, int y2, int color) {
		if (y2 < y1) {
			int i = y1;
			y1 = y2;
			y2 = i;
		}

		fill(matrices, x, y1 + 1, x + 1, y2, color);
	}

	public static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
		fill(matrices.peek().getModel(), x1, y1, x2, y2, color);
	}

	private static void fill(Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
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
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(g, h, k, f).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	protected void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
		fillGradient(matrices, startX, startY, endX, endY, colorStart, colorEnd, this.zOffset);
	}

	protected static void fillGradient(MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd, int z) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		fillGradient(matrices.peek().getModel(), bufferBuilder, startX, startY, endX, endY, z, colorStart, colorEnd);
		tessellator.draw();
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}

	protected static void fillGradient(
		Matrix4f matrix, BufferBuilder bufferBuilder, int startX, int startY, int endX, int endY, int z, int colorStart, int colorEnd
	) {
		float f = (float)(colorStart >> 24 & 0xFF) / 255.0F;
		float g = (float)(colorStart >> 16 & 0xFF) / 255.0F;
		float h = (float)(colorStart >> 8 & 0xFF) / 255.0F;
		float i = (float)(colorStart & 0xFF) / 255.0F;
		float j = (float)(colorEnd >> 24 & 0xFF) / 255.0F;
		float k = (float)(colorEnd >> 16 & 0xFF) / 255.0F;
		float l = (float)(colorEnd >> 8 & 0xFF) / 255.0F;
		float m = (float)(colorEnd & 0xFF) / 255.0F;
		bufferBuilder.vertex(matrix, (float)endX, (float)startY, (float)z).color(g, h, i, f).next();
		bufferBuilder.vertex(matrix, (float)startX, (float)startY, (float)z).color(g, h, i, f).next();
		bufferBuilder.vertex(matrix, (float)startX, (float)endY, (float)z).color(k, l, m, j).next();
		bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float)z).color(k, l, m, j).next();
	}

	public static void drawCenteredText(MatrixStack matrices, TextRenderer textRenderer, String text, int centerX, int y, int color) {
		textRenderer.drawWithShadow(matrices, text, (float)(centerX - textRenderer.getWidth(text) / 2), (float)y, color);
	}

	public static void drawCenteredText(MatrixStack matrices, TextRenderer textRenderer, Text text, int centerX, int y, int color) {
		OrderedText orderedText = text.asOrderedText();
		textRenderer.drawWithShadow(matrices, orderedText, (float)(centerX - textRenderer.getWidth(orderedText) / 2), (float)y, color);
	}

	public static void drawCenteredTextWithShadow(MatrixStack matrices, TextRenderer textRenderer, OrderedText text, int centerX, int y, int color) {
		textRenderer.drawWithShadow(matrices, text, (float)(centerX - textRenderer.getWidth(text) / 2), (float)y, color);
	}

	public static void drawStringWithShadow(MatrixStack matrices, TextRenderer textRenderer, String text, int x, int y, int color) {
		textRenderer.drawWithShadow(matrices, text, (float)x, (float)y, color);
	}

	public static void drawWithShadow(MatrixStack matrices, TextRenderer textHandler, OrderedText text, int x, int y, int color) {
		textHandler.drawWithShadow(matrices, text, (float)x, (float)y, color);
	}

	public static void drawTextWithShadow(MatrixStack matrices, TextRenderer textRenderer, Text text, int x, int y, int color) {
		textRenderer.drawWithShadow(matrices, text, (float)x, (float)y, color);
	}

	public void drawWithOutline(int x, int y, BiConsumer<Integer, Integer> renderAction) {
		RenderSystem.blendFuncSeparate(
			GlStateManager.SrcFactor.ZERO,
			GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
			GlStateManager.SrcFactor.SRC_ALPHA,
			GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
		);
		renderAction.accept(x + 1, y);
		renderAction.accept(x - 1, y);
		renderAction.accept(x, y + 1);
		renderAction.accept(x, y - 1);
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		renderAction.accept(x, y);
	}

	public static void drawSprite(MatrixStack matrices, int x, int y, int z, int width, int height, Sprite sprite) {
		drawTexturedQuad(matrices.peek().getModel(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
	}

	public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
		drawTexture(matrices, x, y, this.zOffset, (float)u, (float)v, width, height, 256, 256);
	}

	public static void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureHeight, int textureWidth) {
		drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
	}

	public static void drawTexture(
		MatrixStack matrices, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
	) {
		drawTexture(matrices, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight);
	}

	public static void drawTexture(MatrixStack matrices, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
		drawTexture(matrices, x, y, width, height, u, v, width, height, textureWidth, textureHeight);
	}

	private static void drawTexture(
		MatrixStack matrices, int x0, int y0, int x1, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight
	) {
		drawTexturedQuad(
			matrices.peek().getModel(),
			x0,
			y0,
			x1,
			y1,
			z,
			(u + 0.0F) / (float)textureWidth,
			(u + (float)regionWidth) / (float)textureWidth,
			(v + 0.0F) / (float)textureHeight,
			(v + (float)regionHeight) / (float)textureHeight
		);
	}

	private static void drawTexturedQuad(Matrix4f matrices, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrices, (float)x0, (float)y1, (float)z).texture(u0, v1).next();
		bufferBuilder.vertex(matrices, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
		bufferBuilder.vertex(matrices, (float)x1, (float)y0, (float)z).texture(u1, v0).next();
		bufferBuilder.vertex(matrices, (float)x0, (float)y0, (float)z).texture(u0, v0).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
	}

	public int getZOffset() {
		return this.zOffset;
	}

	public void setZOffset(int zOffset) {
		this.zOffset = zOffset;
	}
}
