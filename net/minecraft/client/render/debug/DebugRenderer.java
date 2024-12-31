package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_4247;
import net.minecraft.class_4248;
import net.minecraft.class_4249;
import net.minecraft.class_4250;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.player.PlayerEntity;

public class DebugRenderer {
	public final class_2891 field_20889;
	public final DebugRenderer.DebugRenderable field_14969;
	public final DebugRenderer.DebugRenderable chunkBorders;
	public final DebugRenderer.DebugRenderable field_14971;
	public final DebugRenderer.DebugRenderable field_15286;
	public final DebugRenderer.DebugRenderable blockUpdates;
	public final class_4247 field_20890;
	public final class_4249 field_20891;
	public final DebugRenderer.DebugRenderable field_20892;
	public final DebugRenderer.DebugRenderable field_20893;
	public final DebugRenderer.DebugRenderable field_16140;
	private boolean renderChunkBorders;
	private boolean field_14973;
	private boolean field_14974;
	private boolean field_14975;
	private boolean field_15288;
	private boolean renderBlockUpdates;
	private boolean field_20894;
	private boolean field_20895;
	private boolean field_20896;
	private boolean field_20897;
	private boolean field_16141;

	public DebugRenderer(MinecraftClient minecraftClient) {
		this.field_20889 = new class_2891(minecraftClient);
		this.field_14969 = new class_2892(minecraftClient);
		this.chunkBorders = new ChunkBorderDebugRenderer(minecraftClient);
		this.field_14971 = new class_3026(minecraftClient);
		this.field_15286 = new class_3097(minecraftClient);
		this.blockUpdates = new BlockUpdateDebugRenderer(minecraftClient);
		this.field_20890 = new class_4247(minecraftClient);
		this.field_20891 = new class_4249(minecraftClient);
		this.field_20892 = new class_4248(minecraftClient);
		this.field_20893 = new class_4250(minecraftClient);
		this.field_16140 = new class_3299(minecraftClient);
	}

	public boolean isEnabled() {
		return this.renderChunkBorders
			|| this.field_14973
			|| this.field_14974
			|| this.field_14975
			|| this.field_15288
			|| this.renderBlockUpdates
			|| this.field_20896
			|| this.field_20897
			|| this.field_16141;
	}

	public boolean toggleChunkBorders() {
		this.renderChunkBorders = !this.renderChunkBorders;
		return this.renderChunkBorders;
	}

	public void render(float tickDelta, long limitTime) {
		if (this.field_14973) {
			this.field_20889.render(tickDelta, limitTime);
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

		if (this.field_20894) {
			this.field_20890.render(tickDelta, limitTime);
		}

		if (this.field_20895) {
			this.field_20891.render(tickDelta, limitTime);
		}

		if (this.field_20896) {
			this.field_20892.render(tickDelta, limitTime);
		}

		if (this.field_20897) {
			this.field_20893.render(tickDelta, limitTime);
		}

		if (this.field_16141) {
			this.field_16140.render(tickDelta, limitTime);
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
			textRenderer.method_18355(text, (float)(-textRenderer.getStringWidth(text) / 2), 0.0F, i);
			GlStateManager.enableLighting();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	public interface DebugRenderable {
		void render(float tickDelta, long limitTime);
	}
}
