package net.minecraft.client.render.world;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.world.World;

public class ListChunkRenderFactoryImpl implements ChunkRenderFactory {
	@Override
	public BuiltChunk method_10175(World world, WorldRenderer worldRenderer, int i) {
		return new ChunkRenderHelperImpl(world, worldRenderer, i);
	}
}
