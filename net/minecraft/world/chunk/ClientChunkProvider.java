package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientChunkProvider implements ChunkProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Chunk emptyChunk;
	private final Long2ObjectMap<Chunk> chunkMap = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<Chunk>(8192) {
		protected void rehash(int i) {
			if (i > this.key.length) {
				super.rehash(i);
			}
		}
	});
	private final World world;

	public ClientChunkProvider(World world) {
		this.emptyChunk = new EmptyChunk(world, 0, 0);
		this.world = world;
	}

	public void unloadChunk(int x, int z) {
		Chunk chunk = (Chunk)this.chunkMap.remove(ChunkPos.getIdFromCoords(x, z));
		if (chunk != null) {
			chunk.unloadFromWorld();
		}
	}

	@Nullable
	@Override
	public Chunk method_17044(int i, int j, boolean bl, boolean bl2) {
		Chunk chunk = (Chunk)this.chunkMap.get(ChunkPos.getIdFromCoords(i, j));
		return bl2 && chunk == null ? this.emptyChunk : chunk;
	}

	public Chunk method_18954(int i, int j, PacketByteBuf packetByteBuf, int k, boolean bl) {
		synchronized (this.chunkMap) {
			long l = ChunkPos.getIdFromCoords(i, j);
			Chunk chunk = (Chunk)this.chunkMap.computeIfAbsent(l, lx -> new Chunk(this.world, i, j, new Biome[256]));
			chunk.method_3895(packetByteBuf, k, bl);
			chunk.setChunkLoaded(true);
			return chunk;
		}
	}

	@Override
	public boolean method_17045(BooleanSupplier booleanSupplier) {
		long l = Util.method_20227();
		ObjectIterator var4 = this.chunkMap.values().iterator();

		while (var4.hasNext()) {
			Chunk chunk = (Chunk)var4.next();
			chunk.populateBlockEntities(Util.method_20227() - l > 5L);
		}

		if (Util.method_20227() - l > 100L) {
			LOGGER.info("Warning: Clientside chunk ticking took {} ms", Util.method_20227() - l);
		}

		return false;
	}

	@Override
	public String getChunkProviderName() {
		return "MultiplayerChunkCache: " + this.chunkMap.size() + ", " + this.chunkMap.size();
	}

	@Override
	public ChunkGenerator<?> method_17046() {
		return null;
	}
}
