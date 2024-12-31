package net.minecraft.client.render.world;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.world.BuiltChunk;

public class VboChunkRenderManager extends AbstractChunkRenderManager {
	@Override
	public void render(RenderLayer layer) {
		if (this.field_10667) {
			for (BuiltChunk builtChunk : this.helpers) {
				VertexBuffer vertexBuffer = builtChunk.method_10165(layer.ordinal());
				GlStateManager.pushMatrix();
				this.method_9770(builtChunk);
				builtChunk.method_10169();
				vertexBuffer.bind();
				this.method_9929();
				vertexBuffer.draw(7);
				GlStateManager.popMatrix();
			}

			GLX.gl15BindBuffer(GLX.arrayBuffer, 0);
			GlStateManager.clearColor();
			this.helpers.clear();
		}
	}

	private void method_9929() {
		GlStateManager.method_12307(3, 5126, 28, 0);
		GlStateManager.method_12311(4, 5121, 28, 12);
		GlStateManager.method_12302(2, 5126, 28, 16);
		GLX.gl13ClientActiveTexture(GLX.lightmapTextureUnit);
		GlStateManager.method_12302(2, 5122, 28, 24);
		GLX.gl13ClientActiveTexture(GLX.textureUnit);
	}
}
