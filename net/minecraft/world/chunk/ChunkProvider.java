package net.minecraft.world.chunk;

import javax.annotation.Nullable;

public interface ChunkProvider {
	@Nullable
	Chunk getLoadedChunk(int x, int z);

	Chunk getOrGenerateChunks(int x, int z);

	boolean tickChunks();

	String getChunkProviderName();
}
