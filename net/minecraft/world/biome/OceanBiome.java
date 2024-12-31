package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class OceanBiome extends Biome {
	public OceanBiome(Biome.Settings settings) {
		super(settings);
		this.passiveEntries.clear();
	}

	@Override
	public Biome.Temperature getBiomeTemperature() {
		return Biome.Temperature.OCEAN;
	}

	@Override
	public void method_6420(World world, Random random, ChunkBlockStateStorage chunkStorage, int i, int j, double d) {
		super.method_6420(world, random, chunkStorage, i, j, d);
	}
}
