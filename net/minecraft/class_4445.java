package net.minecraft;

import java.util.Map;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_4445 {
	private static final Logger field_21866 = LogManager.getLogger();

	protected ChunkBlockStateStorage[] method_21300(class_3786 arg, int i, int j, Map<ChunkPos, ChunkBlockStateStorage> map) {
		int k = arg.method_17053();
		ChunkBlockStateStorage[] chunkBlockStateStorages = new ChunkBlockStateStorage[(1 + 2 * k) * (1 + 2 * k)];
		int l = 0;

		for (int m = -k; m <= k; m++) {
			for (int n = -k; n <= k; n++) {
				ChunkBlockStateStorage chunkBlockStateStorage = (ChunkBlockStateStorage)map.get(new ChunkPos(i + n, j + m));
				chunkBlockStateStorage.method_17127(arg.method_17056());
				chunkBlockStateStorages[l++] = chunkBlockStateStorage;
			}
		}

		return chunkBlockStateStorages;
	}

	public ChunkBlockStateStorage method_21301(
		class_3786 arg, World world, ChunkGenerator<?> chunkGenerator, Map<ChunkPos, ChunkBlockStateStorage> map, int i, int j
	) {
		ChunkBlockStateStorage[] chunkBlockStateStorages = this.method_21300(arg, i, j, map);
		return this.method_21302(arg, world, chunkGenerator, chunkBlockStateStorages, i, j);
	}

	protected abstract ChunkBlockStateStorage method_21302(
		class_3786 arg, World world, ChunkGenerator<?> chunkGenerator, ChunkBlockStateStorage[] chunkBlockStateStorages, int i, int j
	);
}
