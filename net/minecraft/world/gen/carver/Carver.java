package net.minecraft.world.gen.carver;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class Carver {
	protected int complexity = 8;
	protected Random random = new Random();
	protected World world;

	public void method_4004(World world, int i, int j, ChunkBlockStateStorage chunkBlockStateStorage) {
		int k = this.complexity;
		this.world = world;
		this.random.setSeed(world.getSeed());
		long l = this.random.nextLong();
		long m = this.random.nextLong();

		for (int n = i - k; n <= i + k; n++) {
			for (int o = j - k; o <= j + k; o++) {
				long p = (long)n * l;
				long q = (long)o * m;
				this.random.setSeed(p ^ q ^ world.getSeed());
				this.carve(world, n, o, i, j, chunkBlockStateStorage);
			}
		}
	}

	public static void method_13769(long l, Random random, int i, int j) {
		random.setSeed(l);
		long m = random.nextLong();
		long n = random.nextLong();
		long o = (long)i * m;
		long p = (long)j * n;
		random.setSeed(o ^ p ^ l);
	}

	protected void carve(World world, int chunkX, int chunkZ, int mainChunkX, int mainChunkZ, ChunkBlockStateStorage chunkStorage) {
	}
}
