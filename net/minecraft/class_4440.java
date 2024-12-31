package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4440 extends Long2ObjectOpenHashMap<Chunk> {
	private static final Logger field_21836 = LogManager.getLogger();

	public class_4440(int i) {
		super(i);
	}

	public Chunk put(long l, Chunk chunk) {
		Chunk chunk2 = (Chunk)super.put(l, chunk);
		ChunkPos chunkPos = new ChunkPos(l);

		for (int i = chunkPos.x - 1; i <= chunkPos.x + 1; i++) {
			for (int j = chunkPos.z - 1; j <= chunkPos.z + 1; j++) {
				if (i != chunkPos.x || j != chunkPos.z) {
					long m = ChunkPos.getIdFromCoords(i, j);
					Chunk chunk3 = (Chunk)this.get(m);
					if (chunk3 != null) {
						chunk.method_17067();
						chunk3.method_17067();
					}
				}
			}
		}

		return chunk2;
	}

	public Chunk put(Long long_, Chunk chunk) {
		return this.put(long_.longValue(), chunk);
	}

	public Chunk remove(long l) {
		Chunk chunk = (Chunk)super.remove(l);
		ChunkPos chunkPos = new ChunkPos(l);

		for (int i = chunkPos.x - 1; i <= chunkPos.x + 1; i++) {
			for (int j = chunkPos.z - 1; j <= chunkPos.z + 1; j++) {
				if (i != chunkPos.x || j != chunkPos.z) {
					Chunk chunk2 = (Chunk)this.get(ChunkPos.getIdFromCoords(i, j));
					if (chunk2 != null) {
						chunk2.method_17068();
					}
				}
			}
		}

		return chunk;
	}

	public Chunk remove(Object object) {
		return this.remove(((Long)object).longValue());
	}

	public void putAll(Map<? extends Long, ? extends Chunk> map) {
		throw new RuntimeException("Not yet implemented");
	}

	public boolean remove(Object object, Object object2) {
		throw new RuntimeException("Not yet implemented");
	}
}
