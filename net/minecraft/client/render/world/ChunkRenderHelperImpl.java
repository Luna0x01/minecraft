package net.minecraft.client.render.world;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.client.world.ChunkAssemblyHelper;
import net.minecraft.world.World;

public class ChunkRenderHelperImpl extends BuiltChunk {
	private final int field_11068 = GlAllocationUtils.genLists(RenderLayer.values().length);

	public ChunkRenderHelperImpl(World world, WorldRenderer worldRenderer) {
		super(world, worldRenderer);
	}

	public int method_10153(RenderLayer renderLayer, ChunkAssemblyHelper chunkAssemblyHelper) {
		return !chunkAssemblyHelper.method_10149(renderLayer) ? this.field_11068 + renderLayer.ordinal() : -1;
	}

	@Override
	public void delete() {
		super.delete();
		GlAllocationUtils.deleteLists(this.field_11068, RenderLayer.values().length);
	}
}
