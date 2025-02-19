package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlProgramManager {
	private static final Logger LOGGER = LogManager.getLogger();

	public static void useProgram(int program) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		GlStateManager._glUseProgram(program);
	}

	public static void deleteProgram(GlShader shader) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		shader.getFragmentShader().release();
		shader.getVertexShader().release();
		GlStateManager.glDeleteProgram(shader.getProgramRef());
	}

	public static int createProgram() throws IOException {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		int i = GlStateManager.glCreateProgram();
		if (i <= 0) {
			throw new IOException("Could not create shader program (returned program ID " + i + ")");
		} else {
			return i;
		}
	}

	public static void linkProgram(GlShader shader) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThread);
		shader.attachReferencedShaders();
		GlStateManager.glLinkProgram(shader.getProgramRef());
		int i = GlStateManager.glGetProgrami(shader.getProgramRef(), 35714);
		if (i == 0) {
			LOGGER.warn(
				"Error encountered when linking program containing VS {} and FS {}. Log output:", shader.getVertexShader().getName(), shader.getFragmentShader().getName()
			);
			LOGGER.warn(GlStateManager.glGetProgramInfoLog(shader.getProgramRef(), 32768));
		}
	}
}
