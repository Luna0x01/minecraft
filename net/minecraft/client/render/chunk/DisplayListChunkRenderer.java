package net.minecraft.client.render.chunk;

import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.world.World;

public class DisplayListChunkRenderer extends ChunkRenderer {
	private final int displayListsStartIndex = GlAllocationUtils.genLists(BlockRenderLayer.values().length);

	public DisplayListChunkRenderer(World world, WorldRenderer worldRenderer) {
		super(world, worldRenderer);
	}

	public int method_3639(BlockRenderLayer blockRenderLayer, ChunkRenderData chunkRenderData) {
		return !chunkRenderData.isEmpty(blockRenderLayer) ? this.displayListsStartIndex + blockRenderLayer.ordinal() : -1;
	}

	@Override
	public void delete() {
		super.delete();
		GlAllocationUtils.deleteLists(this.displayListsStartIndex, BlockRenderLayer.values().length);
	}
}
