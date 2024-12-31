package net.minecraft.client.render.world;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ChunkRenderFactory {
	BuiltChunk get(World world, WorldRenderer worldRenderer, BlockPos blockPos, int i);
}
