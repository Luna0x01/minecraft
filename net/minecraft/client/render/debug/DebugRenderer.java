package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.player.PlayerEntity;

public class DebugRenderer {
	public final DebugRenderer.DebugRenderable field_14968;
	public final DebugRenderer.DebugRenderable field_14969;
	public final DebugRenderer.DebugRenderable chunkBorders;
	public final DebugRenderer.DebugRenderable field_14971;
	public final DebugRenderer.DebugRenderable field_15286;
	public final DebugRenderer.DebugRenderable blockUpdates;
	private boolean renderChunkBorders;
	private boolean field_14973;
	private boolean field_14974;
	private boolean field_14975;
	private boolean field_15288;
	private boolean renderBlockUpdates;

	public DebugRenderer(MinecraftClient minecraftClient) {
		this.field_14968 = new class_2891(minecraftClient);
		this.field_14969 = new class_2892(minecraftClient);
		this.chunkBorders = new ChunkBorderDebugRenderer(minecraftClient);
		this.field_14971 = new class_3026(minecraftClient);
		this.field_15286 = new class_3097(minecraftClient);
		this.blockUpdates = new BlockUpdateDebugRenderer(minecraftClient);
	}

	public boolean isEnabled() {
		return this.renderChunkBorders || this.field_14973 || this.field_14974 || this.field_14975 || this.field_15288 || this.renderBlockUpdates;
	}

	public boolean toggleChunkBorders() {
		this.renderChunkBorders = !this.renderChunkBorders;
		return this.renderChunkBorders;
	}

	public void render(float tickDelta, long limitTime) {
		if (this.field_14973) {
			this.field_14968.render(tickDelta, limitTime);
		}

		if (this.renderChunkBorders && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
			this.chunkBorders.render(tickDelta, limitTime);
		}

		if (this.field_14974) {
			this.field_14969.render(tickDelta, limitTime);
		}

		if (this.field_14975) {
			this.field_14971.render(tickDelta, limitTime);
		}

		if (this.field_15288) {
			this.field_15286.render(tickDelta, limitTime);
		}

		if (this.renderBlockUpdates) {
			this.blockUpdates.render(tickDelta, limitTime);
		}
	}

	public static void method_13856(String text, int i, int j, int k, float f, int l) {
		method_13450(text, (double)i + 0.5, (double)j + 0.5, (double)k + 0.5, f, l);
	}

	public static void method_13450(String text, double d, double e, double f, float g, int i) {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		if (minecraftClient.player != null && minecraftClient.getEntityRenderManager() != null && minecraftClient.getEntityRenderManager().options != null) {
			TextRenderer textRenderer = minecraftClient.textRenderer;
			PlayerEntity playerEntity = minecraftClient.player;
			double h = playerEntity.prevTickX + (playerEntity.x - playerEntity.prevTickX) * (double)g;
			double j = playerEntity.prevTickY + (playerEntity.y - playerEntity.prevTickY) * (double)g;
			double k = playerEntity.prevTickZ + (playerEntity.z - playerEntity.prevTickZ) * (double)g;
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)(d - h), (float)(e - j) + 0.07F, (float)(f - k));
			GlStateManager.method_12272(0.0F, 1.0F, 0.0F);
			GlStateManager.scale(0.02F, -0.02F, 0.02F);
			EntityRenderDispatcher entityRenderDispatcher = minecraftClient.getEntityRenderManager();
			GlStateManager.rotate(-entityRenderDispatcher.yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float)(entityRenderDispatcher.options.perspective == 2 ? 1 : -1) * entityRenderDispatcher.pitch, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableLighting();
			GlStateManager.enableTexture();
			GlStateManager.enableDepthTest();
			GlStateManager.depthMask(true);
			GlStateManager.scale(-1.0F, 1.0F, 1.0F);
			textRenderer.draw(text, -textRenderer.getStringWidth(text) / 2, 0, i);
			GlStateManager.enableLighting();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	public interface DebugRenderable {
		void render(float tickDelta, long limitTime);
	}
}
