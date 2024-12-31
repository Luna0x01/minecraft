package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;

public enum HorizontalVoronoiBiomeAccessType implements BiomeAccessType {
	field_20646;

	@Override
	public Biome getBiome(long l, int i, int j, int k, BiomeAccess.Storage storage) {
		return VoronoiBiomeAccessType.field_20644.getBiome(l, i, 0, k, storage);
	}
}
