package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;

public class VanillaLayeredBiomeSource extends BiomeSource {
	private final BiomeLayerSampler biomeSampler;
	private static final Set<Biome> BIOMES = ImmutableSet.of(
		Biomes.field_9423,
		Biomes.field_9451,
		Biomes.field_9424,
		Biomes.field_9472,
		Biomes.field_9409,
		Biomes.field_9420,
		new Biome[]{
			Biomes.field_9471,
			Biomes.field_9438,
			Biomes.field_9435,
			Biomes.field_9463,
			Biomes.field_9452,
			Biomes.field_9444,
			Biomes.field_9462,
			Biomes.field_9407,
			Biomes.field_9434,
			Biomes.field_9466,
			Biomes.field_9459,
			Biomes.field_9428,
			Biomes.field_9464,
			Biomes.field_9417,
			Biomes.field_9432,
			Biomes.field_9474,
			Biomes.field_9446,
			Biomes.field_9419,
			Biomes.field_9478,
			Biomes.field_9412,
			Biomes.field_9421,
			Biomes.field_9475,
			Biomes.field_9454,
			Biomes.field_9425,
			Biomes.field_9477,
			Biomes.field_9429,
			Biomes.field_9460,
			Biomes.field_9449,
			Biomes.field_9430,
			Biomes.field_9415,
			Biomes.field_9410,
			Biomes.field_9433,
			Biomes.field_9408,
			Biomes.field_9441,
			Biomes.field_9467,
			Biomes.field_9448,
			Biomes.field_9439,
			Biomes.field_9470,
			Biomes.field_9418,
			Biomes.field_9455,
			Biomes.field_9427,
			Biomes.field_9476,
			Biomes.field_9414,
			Biomes.field_9422,
			Biomes.field_9479,
			Biomes.field_9453,
			Biomes.field_9426,
			Biomes.field_9405,
			Biomes.field_9431,
			Biomes.field_9458,
			Biomes.field_9450,
			Biomes.field_9437,
			Biomes.field_9416,
			Biomes.field_9404,
			Biomes.field_9436,
			Biomes.field_9456,
			Biomes.field_9445,
			Biomes.field_9443,
			Biomes.field_9413,
			Biomes.field_9406
		}
	);

	public VanillaLayeredBiomeSource(VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig) {
		super(BIOMES);
		this.biomeSampler = BiomeLayers.build(
			vanillaLayeredBiomeSourceConfig.getSeed(), vanillaLayeredBiomeSourceConfig.getGeneratorType(), vanillaLayeredBiomeSourceConfig.getGeneratorSettings()
		);
	}

	@Override
	public Biome getBiomeForNoiseGen(int i, int j, int k) {
		return this.biomeSampler.sample(i, k);
	}
}
