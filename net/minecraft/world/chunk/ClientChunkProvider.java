package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.collection.LongObjectStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientChunkProvider implements ChunkProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private Chunk emptyChunk;
	private LongObjectStorage<Chunk> chunkStorage = new LongObjectStorage<>();
	private List<Chunk> chunks = Lists.newArrayList();
	private World world;

	public ClientChunkProvider(World world) {
		this.emptyChunk = new EmptyChunk(world, 0, 0);
		this.world = world;
	}

	@Override
	public boolean chunkExists(int x, int z) {
		return true;
	}

	public void unloadChunk(int x, int z) {
		Chunk chunk = this.getChunk(x, z);
		if (!chunk.isEmpty()) {
			chunk.unloadFromWorld();
		}

		this.chunkStorage.remove(ChunkPos.getIdFromCoords(x, z));
		this.chunks.remove(chunk);
	}

	public Chunk getOrGenerateChunk(int x, int z) {
		Chunk chunk = new Chunk(this.world, x, z);
		this.chunkStorage.set(ChunkPos.getIdFromCoords(x, z), chunk);
		this.chunks.add(chunk);
		chunk.setChunkLoaded(true);
		return chunk;
	}

	@Override
	public Chunk getChunk(int x, int z) {
		Chunk chunk = this.chunkStorage.get(ChunkPos.getIdFromCoords(x, z));
		return chunk == null ? this.emptyChunk : chunk;
	}

	@Override
	public boolean saveChunks(boolean saveEntities, ProgressListener progressListener) {
		return true;
	}

	@Override
	public void flushChunks() {
	}

	@Override
	public boolean tickChunks() {
		long l = System.currentTimeMillis();

		for (Chunk chunk : this.chunks) {
			chunk.populateBlockEntities(System.currentTimeMillis() - l > 5L);
		}

		if (System.currentTimeMillis() - l > 100L) {
			LOGGER.info("Warning: Clientside chunk ticking took {} ms", new Object[]{System.currentTimeMillis() - l});
		}

		return false;
	}

	@Override
	public boolean isSavingEnabled() {
		return false;
	}

	@Override
	public void decorateChunk(ChunkProvider provider, int x, int z) {
	}

	@Override
	public boolean isChunkModified(ChunkProvider chunkProvider, Chunk chunk, int x, int z) {
		return false;
	}

	@Override
	public String getChunkProviderName() {
		return "MultiplayerChunkCache: " + this.chunkStorage.getUsedEntriesCount() + ", " + this.chunks.size();
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		return null;
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos) {
		return null;
	}

	@Override
	public int getLoadedChunksCount() {
		return this.chunks.size();
	}

	@Override
	public void handleInitialLoad(Chunk chunk, int x, int z) {
	}

	@Override
	public Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}
}
