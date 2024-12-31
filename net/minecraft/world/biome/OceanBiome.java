package net.minecraft.world.biome;

public class OceanBiome extends Biome {
	public OceanBiome(Biome.Settings settings) {
		super(settings);
		this.passiveEntries.clear();
	}

	@Override
	public Biome.Temperature getBiomeTemperature() {
		return Biome.Temperature.OCEAN;
	}
}
