package net.minecraft.client.render.world;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.BuiltChunk;

public class ListedChunkRenderManager extends AbstractChunkRenderManager {
	@Override
	public void render(RenderLayer layer) {
		if (this.field_10667) {
			for (BuiltChunk builtChunk : this.helpers) {
				ChunkRenderHelperImpl chunkRenderHelperImpl = (ChunkRenderHelperImpl)builtChunk;
				GlStateManager.pushMatrix();
				this.method_9770(builtChunk);
				GlStateManager.callList(chunkRenderHelperImpl.method_10153(layer, chunkRenderHelperImpl.method_10170()));
				GlStateManager.popMatrix();
			}

			GlStateManager.clearColor();
			this.helpers.clear();
		}
	}
}
