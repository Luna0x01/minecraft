package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

public class GameTestDebugRenderer implements DebugRenderer.Renderer {
	private final Map<BlockPos, GameTestDebugRenderer.Marker> markers = Maps.newHashMap();

	public void addMarker(BlockPos blockPos, int i, String string, int j) {
		this.markers.put(blockPos, new GameTestDebugRenderer.Marker(i, string, Util.getMeasuringTimeMs() + (long)j));
	}

	@Override
	public void clear() {
		this.markers.clear();
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f) {
		long l = Util.getMeasuringTimeMs();
		this.markers.entrySet().removeIf(entry -> l > ((GameTestDebugRenderer.Marker)entry.getValue()).removalTime);
		this.markers.forEach(this::method_23111);
	}

	private void method_23111(BlockPos blockPos, GameTestDebugRenderer.Marker marker) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(
			GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
		);
		RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		RenderSystem.disableTexture();
		DebugRenderer.drawBox(blockPos, 0.02F, marker.method_23112(), marker.method_23113(), marker.method_23114(), marker.method_23115());
		if (!marker.message.isEmpty()) {
			double d = (double)blockPos.getX() + 0.5;
			double e = (double)blockPos.getY() + 1.2;
			double f = (double)blockPos.getZ() + 0.5;
			DebugRenderer.drawString(marker.message, d, e, f, -1, 0.01F, true, 0.0F, true);
		}

		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
	}

	static class Marker {
		public int color;
		public String message;
		public long removalTime;

		public Marker(int i, String string, long l) {
			this.color = i;
			this.message = string;
			this.removalTime = l;
		}

		public float method_23112() {
			return (float)(this.color >> 16 & 0xFF) / 255.0F;
		}

		public float method_23113() {
			return (float)(this.color >> 8 & 0xFF) / 255.0F;
		}

		public float method_23114() {
			return (float)(this.color & 0xFF) / 255.0F;
		}

		public float method_23115() {
			return (float)(this.color >> 24 & 0xFF) / 255.0F;
		}
	}
}
