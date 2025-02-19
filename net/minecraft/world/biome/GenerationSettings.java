package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenerationSettings {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final GenerationSettings INSTANCE = new GenerationSettings(
		() -> ConfiguredSurfaceBuilders.NOPE, ImmutableMap.of(), ImmutableList.of(), ImmutableList.of()
	);
	public static final MapCodec<GenerationSettings> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					ConfiguredSurfaceBuilder.REGISTRY_CODEC
						.fieldOf("surface_builder")
						.flatXmap(Codecs.createPresentValueChecker(), Codecs.createPresentValueChecker())
						.forGetter(generationSettings -> generationSettings.surfaceBuilder),
					Codec.simpleMap(
							GenerationStep.Carver.CODEC,
							ConfiguredCarver.LIST_CODEC
								.promotePartial(Util.addPrefix("Carver: ", LOGGER::error))
								.flatXmap(Codecs.createPresentValuesChecker(), Codecs.createPresentValuesChecker()),
							StringIdentifiable.toKeyable(GenerationStep.Carver.values())
						)
						.fieldOf("carvers")
						.forGetter(generationSettings -> generationSettings.carvers),
					ConfiguredFeature.field_26756
						.promotePartial(Util.addPrefix("Feature: ", LOGGER::error))
						.flatXmap(Codecs.createPresentValuesChecker(), Codecs.createPresentValuesChecker())
						.listOf()
						.fieldOf("features")
						.forGetter(generationSettings -> generationSettings.features),
					ConfiguredStructureFeature.REGISTRY_ELEMENT_CODEC
						.promotePartial(Util.addPrefix("Structure start: ", LOGGER::error))
						.fieldOf("starts")
						.flatXmap(Codecs.createPresentValuesChecker(), Codecs.createPresentValuesChecker())
						.forGetter(generationSettings -> generationSettings.structureFeatures)
				)
				.apply(instance, GenerationSettings::new)
	);
	private final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder;
	private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers;
	private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features;
	private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures;
	private final List<ConfiguredFeature<?, ?>> flowerFeatures;

	GenerationSettings(
		Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder,
		Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers,
		List<List<Supplier<ConfiguredFeature<?, ?>>>> features,
		List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures
	) {
		this.surfaceBuilder = surfaceBuilder;
		this.carvers = carvers;
		this.features = features;
		this.structureFeatures = structureFeatures;
		this.flowerFeatures = (List<ConfiguredFeature<?, ?>>)features.stream()
			.flatMap(Collection::stream)
			.map(Supplier::get)
			.flatMap(ConfiguredFeature::getDecoratedFeatures)
			.filter(configuredFeature -> configuredFeature.feature == Feature.FLOWER)
			.collect(ImmutableList.toImmutableList());
	}

	public List<Supplier<ConfiguredCarver<?>>> getCarversForStep(GenerationStep.Carver carverStep) {
		return (List<Supplier<ConfiguredCarver<?>>>)this.carvers.getOrDefault(carverStep, ImmutableList.of());
	}

	public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
		return this.structureFeatures.stream().anyMatch(supplier -> ((ConfiguredStructureFeature)supplier.get()).feature == structureFeature);
	}

	public Collection<Supplier<ConfiguredStructureFeature<?, ?>>> getStructureFeatures() {
		return this.structureFeatures;
	}

	public ConfiguredStructureFeature<?, ?> method_30978(ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		return (ConfiguredStructureFeature<?, ?>)DataFixUtils.orElse(
			this.structureFeatures
				.stream()
				.map(Supplier::get)
				.filter(configuredStructureFeature2 -> configuredStructureFeature2.feature == configuredStructureFeature.feature)
				.findAny(),
			configuredStructureFeature
		);
	}

	public List<ConfiguredFeature<?, ?>> getFlowerFeatures() {
		return this.flowerFeatures;
	}

	public List<List<Supplier<ConfiguredFeature<?, ?>>>> getFeatures() {
		return this.features;
	}

	public Supplier<ConfiguredSurfaceBuilder<?>> getSurfaceBuilder() {
		return this.surfaceBuilder;
	}

	public SurfaceConfig getSurfaceConfig() {
		return ((ConfiguredSurfaceBuilder)this.surfaceBuilder.get()).getConfig();
	}

	public static class Builder {
		private Optional<Supplier<ConfiguredSurfaceBuilder<?>>> surfaceBuilder = Optional.empty();
		private final Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> carvers = Maps.newLinkedHashMap();
		private final List<List<Supplier<ConfiguredFeature<?, ?>>>> features = Lists.newArrayList();
		private final List<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures = Lists.newArrayList();

		public GenerationSettings.Builder surfaceBuilder(ConfiguredSurfaceBuilder<?> surfaceBuilder) {
			return this.surfaceBuilder(() -> surfaceBuilder);
		}

		public GenerationSettings.Builder surfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilderSupplier) {
			this.surfaceBuilder = Optional.of(surfaceBuilderSupplier);
			return this;
		}

		public GenerationSettings.Builder feature(GenerationStep.Feature featureStep, ConfiguredFeature<?, ?> feature) {
			return this.feature(featureStep.ordinal(), () -> feature);
		}

		public GenerationSettings.Builder feature(int stepIndex, Supplier<ConfiguredFeature<?, ?>> featureSupplier) {
			this.addFeatureStep(stepIndex);
			((List)this.features.get(stepIndex)).add(featureSupplier);
			return this;
		}

		public <C extends CarverConfig> GenerationSettings.Builder carver(GenerationStep.Carver carverStep, ConfiguredCarver<C> carver) {
			((List)this.carvers.computeIfAbsent(carverStep, carverx -> Lists.newArrayList())).add((Supplier)() -> carver);
			return this;
		}

		public GenerationSettings.Builder structureFeature(ConfiguredStructureFeature<?, ?> structureFeature) {
			this.structureFeatures.add((Supplier)() -> structureFeature);
			return this;
		}

		private void addFeatureStep(int stepIndex) {
			while (this.features.size() <= stepIndex) {
				this.features.add(Lists.newArrayList());
			}
		}

		public GenerationSettings build() {
			return new GenerationSettings(
				(Supplier<ConfiguredSurfaceBuilder<?>>)this.surfaceBuilder.orElseThrow(() -> new IllegalStateException("Missing surface builder")),
				(Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>>)this.carvers
					.entrySet()
					.stream()
					.collect(ImmutableMap.toImmutableMap(Entry::getKey, entry -> ImmutableList.copyOf((Collection)entry.getValue()))),
				(List<List<Supplier<ConfiguredFeature<?, ?>>>>)this.features.stream().map(ImmutableList::copyOf).collect(ImmutableList.toImmutableList()),
				ImmutableList.copyOf(this.structureFeatures)
			);
		}
	}
}
