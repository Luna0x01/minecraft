package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.world.gen.feature.FoliageFeature;

public class BirchForestBiome extends ForestBiome {
	public BirchForestBiome(Biome.Settings settings) {
		super(ForestBiome.Type.BIRCH, settings);
	}

	@Override
	public FoliageFeature method_3822(Random random) {
		return random.nextBoolean() ? ForestBiome.field_7234 : ForestBiome.field_7235;
	}
}
