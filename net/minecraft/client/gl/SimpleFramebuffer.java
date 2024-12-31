package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;

public class SimpleFramebuffer extends Framebuffer {
	public SimpleFramebuffer(int width, int height, boolean useDepth, boolean getError) {
		super(useDepth);
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		this.resize(width, height, getError);
	}
}
