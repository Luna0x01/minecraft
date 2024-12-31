package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

public class ChunkBorderDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public ChunkBorderDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	@Override
	public void render(long l) {
		Camera camera = this.client.gameRenderer.getCamera();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double f = camera.getPos().z;
		double g = 0.0 - e;
		double h = 256.0 - e;
		GlStateManager.disableTexture();
		GlStateManager.disableBlend();
		double i = (double)(camera.getFocusedEntity().chunkX << 4) - d;
		double j = (double)(camera.getFocusedEntity().chunkZ << 4) - f;
		GlStateManager.lineWidth(1.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int k = -16; k <= 32; k += 16) {
			for (int m = -16; m <= 32; m += 16) {
				bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(1.0F, 0.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex(i + (double)k, g, j + (double)m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(i + (double)k, h, j + (double)m).color(1.0F, 0.0F, 0.0F, 0.0F).next();
			}
		}

		for (int n = 2; n < 16; n += 2) {
			bufferBuilder.vertex(i + (double)n, g, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + (double)n, g, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + (double)n, h, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + (double)n, h, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + (double)n, g, j + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + (double)n, g, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + (double)n, h, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + (double)n, h, j + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int o = 2; o < 16; o += 2) {
			bufferBuilder.vertex(i, g, j + (double)o).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i, g, j + (double)o).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, h, j + (double)o).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, h, j + (double)o).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + 16.0, g, j + (double)o).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i + 16.0, g, j + (double)o).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, h, j + (double)o).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, h, j + (double)o).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int p = 0; p <= 256; p += 2) {
			double q = (double)p - e;
			bufferBuilder.vertex(i, q, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(i, q, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, q, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, q, j + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, q, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, q, j).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(i, q, j).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		tessellator.draw();
		GlStateManager.lineWidth(2.0F);
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int r = 0; r <= 16; r += 16) {
			for (int s = 0; s <= 16; s += 16) {
				bufferBuilder.vertex(i + (double)r, g, j + (double)s).color(0.25F, 0.25F, 1.0F, 0.0F).next();
				bufferBuilder.vertex(i + (double)r, g, j + (double)s).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(i + (double)r, h, j + (double)s).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(i + (double)r, h, j + (double)s).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			}
		}

		for (int t = 0; t <= 256; t += 16) {
			double u = (double)t - e;
			bufferBuilder.vertex(i, u, j).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(i, u, j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i, u, j + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, u, j + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i + 16.0, u, j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i, u, j).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(i, u, j).color(0.25F, 0.25F, 1.0F, 0.0F).next();
		}

		tessellator.draw();
		GlStateManager.lineWidth(1.0F);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture();
	}
}
