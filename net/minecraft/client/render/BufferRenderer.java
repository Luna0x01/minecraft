package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryUtil;

public class BufferRenderer {
	public static void draw(BufferBuilder bufferBuilder) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> {
				Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pairx = bufferBuilder.popData();
				BufferBuilder.DrawArrayParameters drawArrayParametersx = (BufferBuilder.DrawArrayParameters)pairx.getFirst();
				draw((ByteBuffer)pairx.getSecond(), drawArrayParametersx.getMode(), drawArrayParametersx.getVertexFormat(), drawArrayParametersx.getCount());
			});
		} else {
			Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
			BufferBuilder.DrawArrayParameters drawArrayParameters = (BufferBuilder.DrawArrayParameters)pair.getFirst();
			draw((ByteBuffer)pair.getSecond(), drawArrayParameters.getMode(), drawArrayParameters.getVertexFormat(), drawArrayParameters.getCount());
		}
	}

	private static void draw(ByteBuffer byteBuffer, int i, VertexFormat vertexFormat, int j) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		byteBuffer.clear();
		if (j > 0) {
			vertexFormat.startDrawing(MemoryUtil.memAddress(byteBuffer));
			GlStateManager.drawArrays(i, 0, j);
			vertexFormat.endDrawing();
		}
	}
}
