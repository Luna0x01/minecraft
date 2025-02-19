package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryLookupCodec;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.gen.ChunkRandom;

public class MultiNoiseBiomeSource extends BiomeSource {
	private static final MultiNoiseBiomeSource.NoiseParameters DEFAULT_NOISE_PARAMETERS = new MultiNoiseBiomeSource.NoiseParameters(-7, ImmutableList.of(1.0, 1.0));
	public static final MapCodec<MultiNoiseBiomeSource> CUSTOM_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.LONG.fieldOf("seed").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.seed),
					RecordCodecBuilder.create(
							instancex -> instancex.group(
										Biome.MixedNoisePoint.CODEC.fieldOf("parameters").forGetter(Pair::getFirst), Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(Pair::getSecond)
									)
									.apply(instancex, Pair::of)
						)
						.listOf()
						.fieldOf("biomes")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.biomePoints),
					MultiNoiseBiomeSource.NoiseParameters.CODEC
						.fieldOf("temperature_noise")
						.forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.temperatureNoiseParameters),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("humidity_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.humidityNoiseParameters),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("altitude_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.altitudeNoiseParameters),
					MultiNoiseBiomeSource.NoiseParameters.CODEC.fieldOf("weirdness_noise").forGetter(multiNoiseBiomeSource -> multiNoiseBiomeSource.weirdnessNoiseParameters)
				)
				.apply(instance, MultiNoiseBiomeSource::new)
	);
	public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(MultiNoiseBiomeSource.Instance.CODEC, CUSTOM_CODEC)
		.xmap(
			either -> (MultiNoiseBiomeSource)either.map(MultiNoiseBiomeSource.Instance::getBiomeSource, Function.identity()),
			multiNoiseBiomeSource -> (Either)multiNoiseBiomeSource.getInstance().map(Either::left).orElseGet(() -> Either.right(multiNoiseBiomeSource))
		)
		.codec();
	private final MultiNoiseBiomeSource.NoiseParameters temperatureNoiseParameters;
	private final MultiNoiseBiomeSource.NoiseParameters humidityNoiseParameters;
	private final MultiNoiseBiomeSource.NoiseParameters altitudeNoiseParameters;
	private final MultiNoiseBiomeSource.NoiseParameters weirdnessNoiseParameters;
	private final DoublePerlinNoiseSampler temperatureNoise;
	private final DoublePerlinNoiseSampler humidityNoise;
	private final DoublePerlinNoiseSampler altitudeNoise;
	private final DoublePerlinNoiseSampler weirdnessNoise;
	private final List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints;
	private final boolean threeDimensionalSampling;
	private final long seed;
	private final Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance;

	public MultiNoiseBiomeSource(long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints) {
		this(seed, biomePoints, Optional.empty());
	}

	MultiNoiseBiomeSource(
		long seed, List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints, Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance
	) {
		this(seed, biomePoints, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, DEFAULT_NOISE_PARAMETERS, instance);
	}

	private MultiNoiseBiomeSource(
		long seed,
		List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints,
		MultiNoiseBiomeSource.NoiseParameters temperatureNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters humidityNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters altitudeNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters weirdnessNoiseParameters
	) {
		this(seed, biomePoints, temperatureNoiseParameters, humidityNoiseParameters, altitudeNoiseParameters, weirdnessNoiseParameters, Optional.empty());
	}

	private MultiNoiseBiomeSource(
		long seed,
		List<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> biomePoints,
		MultiNoiseBiomeSource.NoiseParameters temperatureNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters humidityNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters altitudeNoiseParameters,
		MultiNoiseBiomeSource.NoiseParameters weirdnessNoiseParameters,
		Optional<Pair<Registry<Biome>, MultiNoiseBiomeSource.Preset>> instance
	) {
		super(biomePoints.stream().map(Pair::getSecond));
		this.seed = seed;
		this.instance = instance;
		this.temperatureNoiseParameters = temperatureNoiseParameters;
		this.humidityNoiseParameters = humidityNoiseParameters;
		this.altitudeNoiseParameters = altitudeNoiseParameters;
		this.weirdnessNoiseParameters = weirdnessNoiseParameters;
		this.temperatureNoise = DoublePerlinNoiseSampler.create(
			new ChunkRandom(seed), temperatureNoiseParameters.getFirstOctave(), temperatureNoiseParameters.getAmplitudes()
		);
		this.humidityNoise = DoublePerlinNoiseSampler.create(
			new ChunkRandom(seed + 1L), humidityNoiseParameters.getFirstOctave(), humidityNoiseParameters.getAmplitudes()
		);
		this.altitudeNoise = DoublePerlinNoiseSampler.create(
			new ChunkRandom(seed + 2L), altitudeNoiseParameters.getFirstOctave(), altitudeNoiseParameters.getAmplitudes()
		);
		this.weirdnessNoise = DoublePerlinNoiseSampler.create(
			new ChunkRandom(seed + 3L), weirdnessNoiseParameters.getFirstOctave(), weirdnessNoiseParameters.getAmplitudes()
		);
		this.biomePoints = biomePoints;
		this.threeDimensionalSampling = false;
	}

	public static MultiNoiseBiomeSource method_35242(Registry<Biome> registry, long l) {
		ImmutableList<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> immutableList = method_35241(registry);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.0, 0.0, 3.0, 3.0, 3.0, 3.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters2 = new MultiNoiseBiomeSource.NoiseParameters(-7, 1.0, 2.0, 4.0, 4.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters3 = new MultiNoiseBiomeSource.NoiseParameters(-9, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0);
		MultiNoiseBiomeSource.NoiseParameters noiseParameters4 = new MultiNoiseBiomeSource.NoiseParameters(-8, 1.2, 0.6, 0.0, 0.0, 1.0, 0.0);
		return new MultiNoiseBiomeSource(l, immutableList, noiseParameters, noiseParameters2, noiseParameters3, noiseParameters4, Optional.empty());
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new MultiNoiseBiomeSource(
			seed,
			this.biomePoints,
			this.temperatureNoiseParameters,
			this.humidityNoiseParameters,
			this.altitudeNoiseParameters,
			this.weirdnessNoiseParameters,
			this.instance
		);
	}

	private Optional<MultiNoiseBiomeSource.Instance> getInstance() {
		return this.instance
			.map(pair -> new MultiNoiseBiomeSource.Instance((MultiNoiseBiomeSource.Preset)pair.getSecond(), (Registry<Biome>)pair.getFirst(), this.seed));
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		int i = this.threeDimensionalSampling ? biomeY : 0;
		Biome.MixedNoisePoint mixedNoisePoint = new Biome.MixedNoisePoint(
			(float)this.temperatureNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.humidityNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.altitudeNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			(float)this.weirdnessNoise.sample((double)biomeX, (double)i, (double)biomeZ),
			0.0F
		);
		return (Biome)this.biomePoints
			.stream()
			.min(Comparator.comparing(pair -> ((Biome.MixedNoisePoint)pair.getFirst()).calculateDistanceTo(mixedNoisePoint)))
			.map(Pair::getSecond)
			.map(Supplier::get)
			.orElse(BuiltinBiomes.THE_VOID);
	}

	public static ImmutableList<Pair<Biome.MixedNoisePoint, Supplier<Biome>>> method_35241(Registry<Biome> registry) {
		return ImmutableList.of(Pair.of(new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), (Supplier)() -> registry.getOrThrow(BiomeKeys.PLAINS)));
	}

	public boolean matchesInstance(long seed) {
		return this.seed == seed && this.instance.isPresent() && Objects.equals(((Pair)this.instance.get()).getSecond(), MultiNoiseBiomeSource.Preset.NETHER);
	}

	static final class Instance {
		public static final MapCodec<MultiNoiseBiomeSource.Instance> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						Identifier.CODEC
							.flatXmap(
								id -> (DataResult)Optional.ofNullable((MultiNoiseBiomeSource.Preset)MultiNoiseBiomeSource.Preset.BY_IDENTIFIER.get(id))
										.map(DataResult::success)
										.orElseGet(() -> DataResult.error("Unknown preset: " + id)),
								preset -> DataResult.success(preset.id)
							)
							.fieldOf("preset")
							.stable()
							.forGetter(MultiNoiseBiomeSource.Instance::getPreset),
						RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(MultiNoiseBiomeSource.Instance::getBiomeRegistry),
						Codec.LONG.fieldOf("seed").stable().forGetter(MultiNoiseBiomeSource.Instance::getSeed)
					)
					.apply(instance, instance.stable(MultiNoiseBiomeSource.Instance::new))
		);
		private final MultiNoiseBiomeSource.Preset preset;
		private final Registry<Biome> biomeRegistry;
		private final long seed;

		Instance(MultiNoiseBiomeSource.Preset preset, Registry<Biome> biomeRegistry, long seed) {
			this.preset = preset;
			this.biomeRegistry = biomeRegistry;
			this.seed = seed;
		}

		public MultiNoiseBiomeSource.Preset getPreset() {
			return this.preset;
		}

		public Registry<Biome> getBiomeRegistry() {
			return this.biomeRegistry;
		}

		public long getSeed() {
			return this.seed;
		}

		public MultiNoiseBiomeSource getBiomeSource() {
			return this.preset.getBiomeSource(this.biomeRegistry, this.seed);
		}
	}

	static class NoiseParameters {
		private final int firstOctave;
		private final DoubleList amplitudes;
		public static final Codec<MultiNoiseBiomeSource.NoiseParameters> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("firstOctave").forGetter(MultiNoiseBiomeSource.NoiseParameters::getFirstOctave),
						Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(MultiNoiseBiomeSource.NoiseParameters::getAmplitudes)
					)
					.apply(instance, MultiNoiseBiomeSource.NoiseParameters::new)
		);

		public NoiseParameters(int firstOctave, List<Double> amplitudes) {
			this.firstOctave = firstOctave;
			this.amplitudes = new DoubleArrayList(amplitudes);
		}

		public NoiseParameters(int firstOctave, double... amplitudes) {
			this.firstOctave = firstOctave;
			this.amplitudes = new DoubleArrayList(amplitudes);
		}

		public int getFirstOctave() {
			return this.firstOctave;
		}

		public DoubleList getAmplitudes() {
			return this.amplitudes;
		}
	}

	public static class Preset {
		static final Map<Identifier, MultiNoiseBiomeSource.Preset> BY_IDENTIFIER = Maps.newHashMap();
		public static final MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(
			new Identifier("nether"),
			(preset, biomeRegistry, seed) -> new MultiNoiseBiomeSource(
					seed,
					ImmutableList.of(
						Pair.of(new Biome.MixedNoisePoint(0.0F, 0.0F, 0.0F, 0.0F, 0.0F), (Supplier)() -> biomeRegistry.getOrThrow(BiomeKeys.NETHER_WASTES)),
						Pair.of(new Biome.MixedNoisePoint(0.0F, -0.5F, 0.0F, 0.0F, 0.0F), (Supplier)() -> biomeRegistry.getOrThrow(BiomeKeys.SOUL_SAND_VALLEY)),
						Pair.of(new Biome.MixedNoisePoint(0.4F, 0.0F, 0.0F, 0.0F, 0.0F), (Supplier)() -> biomeRegistry.getOrThrow(BiomeKeys.CRIMSON_FOREST)),
						Pair.of(new Biome.MixedNoisePoint(0.0F, 0.5F, 0.0F, 0.0F, 0.375F), (Supplier)() -> biomeRegistry.getOrThrow(BiomeKeys.WARPED_FOREST)),
						Pair.of(new Biome.MixedNoisePoint(-0.5F, 0.0F, 0.0F, 0.0F, 0.175F), (Supplier)() -> biomeRegistry.getOrThrow(BiomeKeys.BASALT_DELTAS))
					),
					Optional.of(Pair.of(biomeRegistry, preset))
				)
		);
		final Identifier id;
		private final Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction;

		public Preset(Identifier id, Function3<MultiNoiseBiomeSource.Preset, Registry<Biome>, Long, MultiNoiseBiomeSource> biomeSourceFunction) {
			this.id = id;
			this.biomeSourceFunction = biomeSourceFunction;
			BY_IDENTIFIER.put(id, this);
		}

		public MultiNoiseBiomeSource getBiomeSource(Registry<Biome> biomeRegistry, long seed) {
			return (MultiNoiseBiomeSource)this.biomeSourceFunction.apply(this, biomeRegistry, seed);
		}
	}
}
