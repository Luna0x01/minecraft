package net.minecraft.world.gen.chunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.BlockSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.DefaultBlockSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class ChunkGenerator {
	public static final Codec<ChunkGenerator> CODEC = Registry.CHUNK_GENERATOR.dispatchStable(ChunkGenerator::getCodec, Function.identity());
	protected final BiomeSource populationSource;
	protected final BiomeSource biomeSource;
	private final StructuresConfig structuresConfig;
	private final long worldSeed;
	private final List<ChunkPos> strongholds = Lists.newArrayList();
	private final BlockSource blockSource;

	public ChunkGenerator(BiomeSource biomeSource, StructuresConfig structuresConfig) {
		this(biomeSource, biomeSource, structuresConfig, 0L);
	}

	public ChunkGenerator(BiomeSource populationSource, BiomeSource biomeSource, StructuresConfig structuresConfig, long worldSeed) {
		this.populationSource = populationSource;
		this.biomeSource = biomeSource;
		this.structuresConfig = structuresConfig;
		this.worldSeed = worldSeed;
		this.blockSource = new DefaultBlockSource(Blocks.STONE.getDefaultState());
	}

	private void generateStrongholdPositions() {
		if (this.strongholds.isEmpty()) {
			StrongholdConfig strongholdConfig = this.structuresConfig.getStronghold();
			if (strongholdConfig != null && strongholdConfig.getCount() != 0) {
				List<Biome> list = Lists.newArrayList();

				for (Biome biome : this.populationSource.getBiomes()) {
					if (biome.getGenerationSettings().hasStructureFeature(StructureFeature.STRONGHOLD)) {
						list.add(biome);
					}
				}

				int i = strongholdConfig.getDistance();
				int j = strongholdConfig.getCount();
				int k = strongholdConfig.getSpread();
				Random random = new Random();
				random.setSeed(this.worldSeed);
				double d = random.nextDouble() * Math.PI * 2.0;
				int l = 0;
				int m = 0;

				for (int n = 0; n < j; n++) {
					double e = (double)(4 * i + i * m * 6) + (random.nextDouble() - 0.5) * (double)i * 2.5;
					int o = (int)Math.round(Math.cos(d) * e);
					int p = (int)Math.round(Math.sin(d) * e);
					BlockPos blockPos = this.populationSource
						.locateBiome(ChunkSectionPos.getOffsetPos(o, 8), 0, ChunkSectionPos.getOffsetPos(p, 8), 112, list::contains, random);
					if (blockPos != null) {
						o = ChunkSectionPos.getSectionCoord(blockPos.getX());
						p = ChunkSectionPos.getSectionCoord(blockPos.getZ());
					}

					this.strongholds.add(new ChunkPos(o, p));
					d += (Math.PI * 2) / (double)k;
					if (++l == k) {
						m++;
						l = 0;
						k += 2 * k / (m + 1);
						k = Math.min(k, j - n);
						d += random.nextDouble() * Math.PI * 2.0;
					}
				}
			}
		}
	}

	protected abstract Codec<? extends ChunkGenerator> getCodec();

	public abstract ChunkGenerator withSeed(long seed);

	public void populateBiomes(Registry<Biome> biomeRegistry, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		((ProtoChunk)chunk).setBiomes(new BiomeArray(biomeRegistry, chunk, chunkPos, this.biomeSource));
	}

	public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {
		BiomeAccess biomeAccess = access.withSource(this.populationSource);
		ChunkRandom chunkRandom = new ChunkRandom();
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		CarverContext carverContext = new CarverContext(this, chunk);
		AquiferSampler aquiferSampler = this.createAquiferSampler(chunk);
		BitSet bitSet = ((ProtoChunk)chunk).getOrCreateCarvingMask(carver);

		for (int j = -8; j <= 8; j++) {
			for (int k = -8; k <= 8; k++) {
				ChunkPos chunkPos2 = new ChunkPos(chunkPos.x + j, chunkPos.z + k);
				GenerationSettings generationSettings = this.populationSource
					.getBiomeForNoiseGen(BiomeCoords.fromBlock(chunkPos2.getStartX()), 0, BiomeCoords.fromBlock(chunkPos2.getStartZ()))
					.getGenerationSettings();
				List<Supplier<ConfiguredCarver<?>>> list = generationSettings.getCarversForStep(carver);
				ListIterator<Supplier<ConfiguredCarver<?>>> listIterator = list.listIterator();

				while (listIterator.hasNext()) {
					int l = listIterator.nextIndex();
					ConfiguredCarver<?> configuredCarver = (ConfiguredCarver<?>)((Supplier)listIterator.next()).get();
					chunkRandom.setCarverSeed(seed + (long)l, chunkPos2.x, chunkPos2.z);
					if (configuredCarver.shouldCarve(chunkRandom)) {
						configuredCarver.carve(carverContext, chunk, biomeAccess::getBiome, chunkRandom, aquiferSampler, chunkPos2, bitSet);
					}
				}
			}
		}
	}

	protected AquiferSampler createAquiferSampler(Chunk chunk) {
		return AquiferSampler.seaLevel(this.getSeaLevel(), Blocks.WATER.getDefaultState());
	}

	@Nullable
	public BlockPos locateStructure(ServerWorld world, StructureFeature<?> feature, BlockPos center, int radius, boolean skipExistingChunks) {
		if (!this.populationSource.hasStructureFeature(feature)) {
			return null;
		} else if (feature == StructureFeature.STRONGHOLD) {
			this.generateStrongholdPositions();
			BlockPos blockPos = null;
			double d = Double.MAX_VALUE;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (ChunkPos chunkPos : this.strongholds) {
				mutable.set(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), 32, ChunkSectionPos.getOffsetPos(chunkPos.z, 8));
				double e = mutable.getSquaredDistance(center);
				if (blockPos == null) {
					blockPos = new BlockPos(mutable);
					d = e;
				} else if (e < d) {
					blockPos = new BlockPos(mutable);
					d = e;
				}
			}

			return blockPos;
		} else {
			StructureConfig structureConfig = this.structuresConfig.getForType(feature);
			return structureConfig == null
				? null
				: feature.locateStructure(world, world.getStructureAccessor(), center, radius, skipExistingChunks, world.getSeed(), structureConfig);
		}
	}

	public void generateFeatures(ChunkRegion region, StructureAccessor accessor) {
		ChunkPos chunkPos = region.getCenterPos();
		int i = chunkPos.getStartX();
		int j = chunkPos.getStartZ();
		BlockPos blockPos = new BlockPos(i, region.getBottomY(), j);
		Biome biome = this.populationSource.getBiomeForNoiseGen(chunkPos);
		ChunkRandom chunkRandom = new ChunkRandom();
		long l = chunkRandom.setPopulationSeed(region.getSeed(), i, j);

		try {
			biome.generateFeatureStep(accessor, this, region, l, chunkRandom, blockPos);
		} catch (Exception var13) {
			CrashReport crashReport = CrashReport.create(var13, "Biome decoration");
			crashReport.addElement("Generation").add("CenterX", chunkPos.x).add("CenterZ", chunkPos.z).add("Seed", l).add("Biome", biome);
			throw new CrashException(crashReport);
		}
	}

	public abstract void buildSurface(ChunkRegion region, Chunk chunk);

	public void populateEntities(ChunkRegion region) {
	}

	public StructuresConfig getStructuresConfig() {
		return this.structuresConfig;
	}

	public int getSpawnHeight(HeightLimitView world) {
		return 64;
	}

	public BiomeSource getBiomeSource() {
		return this.biomeSource;
	}

	public int getWorldHeight() {
		return 256;
	}

	public Pool<SpawnSettings.SpawnEntry> getEntitySpawnList(Biome biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
		return biome.getSpawnSettings().getSpawnEntries(group);
	}

	public void setStructureStarts(
		DynamicRegistryManager registryManager, StructureAccessor accessor, Chunk chunk, StructureManager structureManager, long worldSeed
	) {
		Biome biome = this.populationSource.getBiomeForNoiseGen(chunk.getPos());
		this.setStructureStart(ConfiguredStructureFeatures.STRONGHOLD, registryManager, accessor, chunk, structureManager, worldSeed, biome);

		for (Supplier<ConfiguredStructureFeature<?, ?>> supplier : biome.getGenerationSettings().getStructureFeatures()) {
			this.setStructureStart((ConfiguredStructureFeature<?, ?>)supplier.get(), registryManager, accessor, chunk, structureManager, worldSeed, biome);
		}
	}

	private void setStructureStart(
		ConfiguredStructureFeature<?, ?> feature,
		DynamicRegistryManager registryManager,
		StructureAccessor accessor,
		Chunk chunk,
		StructureManager structureManager,
		long worldSeed,
		Biome biome
	) {
		ChunkPos chunkPos = chunk.getPos();
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
		StructureStart<?> structureStart = accessor.getStructureStart(chunkSectionPos, feature.feature, chunk);
		int i = structureStart != null ? structureStart.getReferences() : 0;
		StructureConfig structureConfig = this.structuresConfig.getForType(feature.feature);
		if (structureConfig != null) {
			StructureStart<?> structureStart2 = feature.tryPlaceStart(
				registryManager, this, this.populationSource, structureManager, worldSeed, chunkPos, biome, i, structureConfig, chunk
			);
			accessor.setStructureStart(chunkSectionPos, feature.feature, structureStart2, chunk);
		}
	}

	public void addStructureReferences(StructureWorldAccess world, StructureAccessor accessor, Chunk chunk) {
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		int l = chunkPos.getStartX();
		int m = chunkPos.getStartZ();
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);

		for (int n = j - 8; n <= j + 8; n++) {
			for (int o = k - 8; o <= k + 8; o++) {
				long p = ChunkPos.toLong(n, o);

				for (StructureStart<?> structureStart : world.getChunk(n, o).getStructureStarts().values()) {
					try {
						if (structureStart.hasChildren() && structureStart.setBoundingBoxFromChildren().intersectsXZ(l, m, l + 15, m + 15)) {
							accessor.addStructureReference(chunkSectionPos, structureStart.getFeature(), p, chunk);
							DebugInfoSender.sendStructureStart(world, structureStart);
						}
					} catch (Exception var20) {
						CrashReport crashReport = CrashReport.create(var20, "Generating structure reference");
						CrashReportSection crashReportSection = crashReport.addElement("Structure");
						crashReportSection.add("Id", (CrashCallable<String>)(() -> Registry.STRUCTURE_FEATURE.getId(structureStart.getFeature()).toString()));
						crashReportSection.add("Name", (CrashCallable<String>)(() -> structureStart.getFeature().getName()));
						crashReportSection.add("Class", (CrashCallable<String>)(() -> structureStart.getFeature().getClass().getCanonicalName()));
						throw new CrashException(crashReport);
					}
				}
			}
		}
	}

	public abstract CompletableFuture<Chunk> populateNoise(Executor executor, StructureAccessor accessor, Chunk chunk);

	public int getSeaLevel() {
		return 63;
	}

	public int getMinimumY() {
		return 0;
	}

	public abstract int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world);

	public abstract VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world);

	public int getHeightOnGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		return this.getHeight(x, z, heightmap, world);
	}

	public int getHeightInGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world) {
		return this.getHeight(x, z, heightmap, world) - 1;
	}

	public boolean isStrongholdStartingChunk(ChunkPos pos) {
		this.generateStrongholdPositions();
		return this.strongholds.contains(pos);
	}

	public BlockSource getBlockSource() {
		return this.blockSource;
	}

	static {
		Registry.register(Registry.CHUNK_GENERATOR, "noise", NoiseChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, "flat", FlatChunkGenerator.CODEC);
		Registry.register(Registry.CHUNK_GENERATOR, "debug", DebugChunkGenerator.CODEC);
	}
}
