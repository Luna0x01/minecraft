package net.minecraft.client.render.world;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.world.World;

public interface ChunkRenderFactory {
	BuiltChunk create(World world, WorldRenderer worldRenderer);
}
