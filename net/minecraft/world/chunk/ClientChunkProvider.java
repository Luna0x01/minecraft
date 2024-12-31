package net.minecraft.world.chunk;

import com.google.common.base.Objects;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientChunkProvider implements ChunkProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Chunk emptyChunk;
	private final Long2ObjectMap<Chunk> chunkMap = new Long2ObjectOpenHashMap<Chunk>(8192) {
		protected void rehash(int i) {
			if (i > this.key.length) {
				super.rehash(i);
			}
		}
	};
	private final World world;

	public ClientChunkProvider(World world) {
		this.emptyChunk = new EmptyChunk(world, 0, 0);
		this.world = world;
	}

	public void unloadChunk(int x, int z) {
		Chunk chunk = this.getOrGenerateChunks(x, z);
		if (!chunk.isEmpty()) {
			chunk.unloadFromWorld();
		}

		this.chunkMap.remove(ChunkPos.getIdFromCoords(x, z));
	}

	@Nullable
	@Override
	public Chunk getLoadedChunk(int x, int z) {
		return (Chunk)this.chunkMap.get(ChunkPos.getIdFromCoords(x, z));
	}

	public Chunk getOrGenerateChunk(int x, int z) {
		Chunk chunk = new Chunk(this.world, x, z);
		this.chunkMap.put(ChunkPos.getIdFromCoords(x, z), chunk);
		chunk.setChunkLoaded(true);
		return chunk;
	}

	@Override
	public Chunk getOrGenerateChunks(int x, int z) {
		return (Chunk)Objects.firstNonNull(this.getLoadedChunk(x, z), this.emptyChunk);
	}

	@Override
	public boolean tickChunks() {
		long l = System.currentTimeMillis();

		for (Chunk chunk : this.chunkMap.values()) {
			chunk.populateBlockEntities(System.currentTimeMillis() - l > 5L);
		}

		if (System.currentTimeMillis() - l > 100L) {
			LOGGER.info("Warning: Clientside chunk ticking took {} ms", new Object[]{System.currentTimeMillis() - l});
		}

		return false;
	}

	@Override
	public String getChunkProviderName() {
		return "MultiplayerChunkCache: " + this.chunkMap.size() + ", " + this.chunkMap.size();
	}
}
