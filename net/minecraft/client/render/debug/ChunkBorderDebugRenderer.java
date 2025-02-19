package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkPos;

public class ChunkBorderDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public ChunkBorderDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		double d = (double)this.client.world.getBottomY() - cameraY;
		double e = (double)this.client.world.getTopY() - cameraY;
		RenderSystem.disableTexture();
		RenderSystem.disableBlend();
		ChunkPos chunkPos = entity.getChunkPos();
		double f = (double)chunkPos.getStartX() - cameraX;
		double g = (double)chunkPos.getStartZ() - cameraZ;
		RenderSystem.lineWidth(1.0F);
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

		for (int i = -16; i <= 32; i += 16) {
			for (int j = -16; j <= 32; j += 16) {
				bufferBuilder.vertex(f + (double)i, d, g + (double)j).color(1.0F, 0.0F, 0.0F, 0.0F).next();
				bufferBuilder.vertex(f + (double)i, d, g + (double)j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(f + (double)i, e, g + (double)j).color(1.0F, 0.0F, 0.0F, 0.5F).next();
				bufferBuilder.vertex(f + (double)i, e, g + (double)j).color(1.0F, 0.0F, 0.0F, 0.0F).next();
			}
		}

		for (int k = 2; k < 16; k += 2) {
			bufferBuilder.vertex(f + (double)k, d, g).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + (double)k, d, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + (double)k, e, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + (double)k, e, g).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + (double)k, d, g + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + (double)k, d, g + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + (double)k, e, g + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + (double)k, e, g + 16.0).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int l = 2; l < 16; l += 2) {
			bufferBuilder.vertex(f, d, g + (double)l).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f, d, g + (double)l).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, e, g + (double)l).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, e, g + (double)l).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + 16.0, d, g + (double)l).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f + 16.0, d, g + (double)l).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, e, g + (double)l).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, e, g + (double)l).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		for (int m = this.client.world.getBottomY(); m <= this.client.world.getTopY(); m += 2) {
			double h = (double)m - cameraY;
			bufferBuilder.vertex(f, h, g).color(1.0F, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(f, h, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, h, g + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, h, g + 16.0).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, h, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, h, g).color(1.0F, 1.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(f, h, g).color(1.0F, 1.0F, 0.0F, 0.0F).next();
		}

		tessellator.draw();
		RenderSystem.lineWidth(2.0F);
		bufferBuilder.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

		for (int n = 0; n <= 16; n += 16) {
			for (int o = 0; o <= 16; o += 16) {
				bufferBuilder.vertex(f + (double)n, d, g + (double)o).color(0.25F, 0.25F, 1.0F, 0.0F).next();
				bufferBuilder.vertex(f + (double)n, d, g + (double)o).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(f + (double)n, e, g + (double)o).color(0.25F, 0.25F, 1.0F, 1.0F).next();
				bufferBuilder.vertex(f + (double)n, e, g + (double)o).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			}
		}

		for (int p = this.client.world.getBottomY(); p <= this.client.world.getTopY(); p += 16) {
			double q = (double)p - cameraY;
			bufferBuilder.vertex(f, q, g).color(0.25F, 0.25F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(f, q, g).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f, q, g + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, q, g + 16.0).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f + 16.0, q, g).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f, q, g).color(0.25F, 0.25F, 1.0F, 1.0F).next();
			bufferBuilder.vertex(f, q, g).color(0.25F, 0.25F, 1.0F, 0.0F).next();
		}

		tessellator.draw();
		RenderSystem.lineWidth(1.0F);
		RenderSystem.enableBlend();
		RenderSystem.enableTexture();
	}
}
