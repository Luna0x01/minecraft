package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;

public enum VoronoiBiomeAccessType implements BiomeAccessType {
	INSTANCE;

	@Override
	public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
		int i = x - 2;
		int j = y - 2;
		int k = z - 2;
		int l = i >> 2;
		int m = j >> 2;
		int n = k >> 2;
		double d = (double)(i & 3) / 4.0;
		double e = (double)(j & 3) / 4.0;
		double f = (double)(k & 3) / 4.0;
		double[] ds = new double[8];

		for (int o = 0; o < 8; o++) {
			boolean bl = (o & 4) == 0;
			boolean bl2 = (o & 2) == 0;
			boolean bl3 = (o & 1) == 0;
			int p = bl ? l : l + 1;
			int q = bl2 ? m : m + 1;
			int r = bl3 ? n : n + 1;
			double g = bl ? d : d - 1.0;
			double h = bl2 ? e : e - 1.0;
			double s = bl3 ? f : f - 1.0;
			ds[o] = calcSquaredDistance(seed, p, q, r, g, h, s);
		}

		int t = 0;
		double u = ds[0];

		for (int v = 1; v < 8; v++) {
			if (u > ds[v]) {
				t = v;
				u = ds[v];
			}
		}

		int w = (t & 4) == 0 ? l : l + 1;
		int aa = (t & 2) == 0 ? m : m + 1;
		int ab = (t & 1) == 0 ? n : n + 1;
		return storage.getBiomeForNoiseGen(w, aa, ab);
	}

	private static double calcSquaredDistance(long seed, int x, int y, int z, double xFraction, double yFraction, double zFraction) {
		long l = SeedMixer.mixSeed(seed, (long)x);
		l = SeedMixer.mixSeed(l, (long)y);
		l = SeedMixer.mixSeed(l, (long)z);
		l = SeedMixer.mixSeed(l, (long)x);
		l = SeedMixer.mixSeed(l, (long)y);
		l = SeedMixer.mixSeed(l, (long)z);
		double d = distribute(l);
		l = SeedMixer.mixSeed(l, seed);
		double e = distribute(l);
		l = SeedMixer.mixSeed(l, seed);
		double f = distribute(l);
		return square(zFraction + f) + square(yFraction + e) + square(xFraction + d);
	}

	private static double distribute(long seed) {
		double d = (double)((int)Math.floorMod(seed >> 24, 1024L)) / 1024.0;
		return (d - 0.5) * 0.9;
	}

	private static double square(double d) {
		return d * d;
	}
}
