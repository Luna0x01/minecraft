package net.minecraft.client.render.world;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.world.World;

public class VboChunkRenderFactoryImpl implements ChunkRenderFactory {
	@Override
	public BuiltChunk method_10175(World world, WorldRenderer worldRenderer, int i) {
		return new BuiltChunk(world, worldRenderer, i);
	}
}
