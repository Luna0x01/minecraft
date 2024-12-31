package net.minecraft.world.gen.carver;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.chunk.ChunkProvider;

public class Carver {
	protected int complexity = 8;
	protected Random random = new Random();
	protected World world;

	public void carveRegion(ChunkProvider chunkProvider, World world, int x, int z, ChunkBlockStateStorage chunkStorage) {
		int i = this.complexity;
		this.world = world;
		this.random.setSeed(world.getSeed());
		long l = this.random.nextLong();
		long m = this.random.nextLong();

		for (int j = x - i; j <= x + i; j++) {
			for (int k = z - i; k <= z + i; k++) {
				long n = (long)j * l;
				long o = (long)k * m;
				this.random.setSeed(n ^ o ^ world.getSeed());
				this.carve(world, j, k, x, z, chunkStorage);
			}
		}
	}

	protected void carve(World world, int chunkX, int chunkZ, int mainChunkX, int mainChunkZ, ChunkBlockStateStorage chunkStorage) {
	}
}
