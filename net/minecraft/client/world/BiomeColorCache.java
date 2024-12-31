package net.minecraft.client.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class BiomeColorCache {
	private final ThreadLocal<BiomeColorCache.Last> last = ThreadLocal.withInitial(() -> new BiomeColorCache.Last());
	private final Long2ObjectLinkedOpenHashMap<int[]> colors = new Long2ObjectLinkedOpenHashMap(256, 0.25F);
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public int getBiomeColor(BlockPos blockPos, IntSupplier intSupplier) {
		int i = blockPos.getX() >> 4;
		int j = blockPos.getZ() >> 4;
		BiomeColorCache.Last last = (BiomeColorCache.Last)this.last.get();
		if (last.x != i || last.z != j) {
			last.x = i;
			last.z = j;
			last.colors = this.getColorArray(i, j);
		}

		int k = blockPos.getX() & 15;
		int l = blockPos.getZ() & 15;
		int m = l << 4 | k;
		int n = last.colors[m];
		if (n != -1) {
			return n;
		} else {
			int o = intSupplier.getAsInt();
			last.colors[m] = o;
			return o;
		}
	}

	public void reset(int i, int j) {
		try {
			this.lock.writeLock().lock();

			for (int k = -1; k <= 1; k++) {
				for (int l = -1; l <= 1; l++) {
					long m = ChunkPos.toLong(i + k, j + l);
					this.colors.remove(m);
				}
			}
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	public void reset() {
		try {
			this.lock.writeLock().lock();
			this.colors.clear();
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	private int[] getColorArray(int i, int j) {
		long l = ChunkPos.toLong(i, j);
		this.lock.readLock().lock();

		int[] is;
		try {
			is = (int[])this.colors.get(l);
		} finally {
			this.lock.readLock().unlock();
		}

		if (is != null) {
			return is;
		} else {
			int[] ks = new int[256];
			Arrays.fill(ks, -1);

			try {
				this.lock.writeLock().lock();
				if (this.colors.size() >= 256) {
					this.colors.removeFirst();
				}

				this.colors.put(l, ks);
			} finally {
				this.lock.writeLock().unlock();
			}

			return ks;
		}
	}

	static class Last {
		public int x = Integer.MIN_VALUE;
		public int z = Integer.MIN_VALUE;
		public int[] colors;

		private Last() {
		}
	}
}
