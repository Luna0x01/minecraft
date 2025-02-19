package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;

public enum VoronoiBiomeAccessType implements BiomeAccessType {
	INSTANCE;

	private static final int field_30979 = 2;
	private static final int field_30980 = 4;
	private static final int field_30981 = 3;

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
		int o = 0;
		double g = Double.POSITIVE_INFINITY;

		for (int p = 0; p < 8; p++) {
			boolean bl = (p & 4) == 0;
			boolean bl2 = (p & 2) == 0;
			boolean bl3 = (p & 1) == 0;
			int q = bl ? l : l + 1;
			int r = bl2 ? m : m + 1;
			int s = bl3 ? n : n + 1;
			double h = bl ? d : d - 1.0;
			double t = bl2 ? e : e - 1.0;
			double u = bl3 ? f : f - 1.0;
			double v = calcSquaredDistance(seed, q, r, s, h, t, u);
			if (g > v) {
				o = p;
				g = v;
			}
		}

		int w = (o & 4) == 0 ? l : l + 1;
		int aa = (o & 2) == 0 ? m : m + 1;
		int ab = (o & 1) == 0 ? n : n + 1;
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
		double d = (double)Math.floorMod(seed >> 24, 1024) / 1024.0;
		return (d - 0.5) * 0.9;
	}

	private static double square(double d) {
		return d * d;
	}
}
