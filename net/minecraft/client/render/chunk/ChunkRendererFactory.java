package net.minecraft.client.render.chunk;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.world.World;

public interface ChunkRendererFactory {
	ChunkRenderer create(World world, WorldRenderer worldRenderer);
}
