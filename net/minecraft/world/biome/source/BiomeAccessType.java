package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;

public interface BiomeAccessType {
	Biome getBiome(long l, int i, int j, int k, BiomeAccess.Storage storage);
}
