package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public interface ChunkProvider {
	boolean chunkExists(int x, int z);

	Chunk getChunk(int x, int z);

	Chunk getChunk(BlockPos pos);

	void decorateChunk(ChunkProvider provider, int x, int z);

	boolean isChunkModified(ChunkProvider chunkProvider, Chunk chunk, int x, int z);

	boolean saveChunks(boolean saveEntities, ProgressListener progressListener);

	boolean tickChunks();

	boolean isSavingEnabled();

	String getChunkProviderName();

	List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos);

	BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos);

	int getLoadedChunksCount();

	void handleInitialLoad(Chunk chunk, int x, int z);

	void flushChunks();
}
