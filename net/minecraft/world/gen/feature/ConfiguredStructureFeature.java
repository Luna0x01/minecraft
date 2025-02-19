package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;

public class ConfiguredStructureFeature<FC extends FeatureConfig, F extends StructureFeature<FC>> {
	public static final Codec<ConfiguredStructureFeature<?, ?>> CODEC = Registry.STRUCTURE_FEATURE
		.dispatch(configuredStructureFeature -> configuredStructureFeature.feature, StructureFeature::getCodec);
	public static final Codec<Supplier<ConfiguredStructureFeature<?, ?>>> REGISTRY_CODEC = RegistryElementCodec.of(
		Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, CODEC
	);
	public static final Codec<List<Supplier<ConfiguredStructureFeature<?, ?>>>> REGISTRY_ELEMENT_CODEC = RegistryElementCodec.method_31194(
		Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, CODEC
	);
	public final F feature;
	public final FC config;

	public ConfiguredStructureFeature(F feature, FC config) {
		this.feature = feature;
		this.config = config;
	}

	public StructureStart<?> tryPlaceStart(
		DynamicRegistryManager registryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		StructureManager structureManager,
		long worldSeed,
		ChunkPos chunkPos,
		Biome biome,
		int referenceCount,
		StructureConfig structureConfig,
		HeightLimitView heightLimitView
	) {
		return this.feature
			.tryPlaceStart(
				registryManager,
				chunkGenerator,
				biomeSource,
				structureManager,
				worldSeed,
				chunkPos,
				biome,
				referenceCount,
				new ChunkRandom(),
				structureConfig,
				this.config,
				heightLimitView
			);
	}
}
