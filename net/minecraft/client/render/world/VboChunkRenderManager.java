package net.minecraft.client.render.world;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.world.BuiltChunk;
import org.lwjgl.opengl.GL11;

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
		GL11.glVertexPointer(3, 5126, 28, 0L);
		GL11.glColorPointer(4, 5121, 28, 12L);
		GL11.glTexCoordPointer(2, 5126, 28, 16L);
		GLX.gl13ClientActiveTexture(GLX.lightmapTextureUnit);
		GL11.glTexCoordPointer(2, 5122, 28, 24L);
		GLX.gl13ClientActiveTexture(GLX.textureUnit);
	}
}
