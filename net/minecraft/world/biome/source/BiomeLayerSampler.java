package net.minecraft.world.biome.source;

import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeLayerSampler {
	private static final Logger LOGGER = LogManager.getLogger();
	private final CachingLayerSampler sampler;

	public BiomeLayerSampler(LayerFactory<CachingLayerSampler> layerFactory) {
		this.sampler = layerFactory.make();
	}

	private Biome getBiome(int i) {
		Biome biome = Registry.field_11153.get(i);
		if (biome == null) {
			if (SharedConstants.isDevelopment) {
				throw (IllegalStateException)Util.throwOrPause(new IllegalStateException("Unknown biome id: " + i));
			} else {
				LOGGER.warn("Unknown biome id: ", i);
				return Biomes.DEFAULT;
			}
		} else {
			return biome;
		}
	}

	public Biome sample(int i, int j) {
		return this.getBiome(this.sampler.sample(i, j));
	}
}
