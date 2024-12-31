package net.minecraft.world.chunk;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.structure.MineshaftStructure;
import net.minecraft.structure.OceanMonumentStructure;
import net.minecraft.structure.StrongholdStructure;
import net.minecraft.structure.TempleStructure;
import net.minecraft.structure.VillageStructure;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.CustomizedWorldProperties;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CaveCarver;
import net.minecraft.world.gen.carver.RavineCarver;
import net.minecraft.world.gen.feature.DungeonFeature;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.level.LevelGeneratorType;

public class SurfaceChunkGenerator implements ChunkProvider {
	private Random random;
	private NoiseGenerator field_4832;
	private NoiseGenerator field_4833;
	private NoiseGenerator field_4834;
	private PerlinNoiseGenerator field_7514;
	public NoiseGenerator field_4821;
	public NoiseGenerator field_4822;
	public NoiseGenerator field_4823;
	private World world;
	private final boolean hasStructures;
	private LevelGeneratorType type;
	private final double[] field_7516;
	private final float[] field_7517;
	private CustomizedWorldProperties properties;
	private Block underwaterBlock = Blocks.WATER;
	private double[] field_4839 = new double[256];
	private Carver caveCarver = new CaveCarver();
	private StrongholdStructure strongholdGenerator = new StrongholdStructure();
	private VillageStructure village = new VillageStructure();
	private MineshaftStructure mineshaft = new MineshaftStructure();
	private TempleStructure witchHut = new TempleStructure();
	private Carver ravineCarver = new RavineCarver();
	private OceanMonumentStructure oceanMonument = new OceanMonumentStructure();
	private Biome[] biomes;
	double[] field_7511;
	double[] field_7512;
	double[] field_7513;
	double[] field_4828;

	public SurfaceChunkGenerator(World world, long l, boolean bl, String string) {
		this.world = world;
		this.hasStructures = bl;
		this.type = world.getLevelProperties().getGeneratorType();
		this.random = new Random(l);
		this.field_4832 = new NoiseGenerator(this.random, 16);
		this.field_4833 = new NoiseGenerator(this.random, 16);
		this.field_4834 = new NoiseGenerator(this.random, 8);
		this.field_7514 = new PerlinNoiseGenerator(this.random, 4);
		this.field_4821 = new NoiseGenerator(this.random, 10);
		this.field_4822 = new NoiseGenerator(this.random, 16);
		this.field_4823 = new NoiseGenerator(this.random, 8);
		this.field_7516 = new double[825];
		this.field_7517 = new float[25];

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				this.field_7517[i + 2 + (j + 2) * 5] = f;
			}
		}

		if (string != null) {
			this.properties = CustomizedWorldProperties.Builder.fromJson(string).build();
			this.underwaterBlock = this.properties.useLavaOceans ? Blocks.LAVA : Blocks.WATER;
			world.setSeaLevel(this.properties.seaLevel);
		}
	}

	public void method_9194(int i, int j, ChunkBlockStateStorage chunkBlockStateStorage) {
		this.biomes = this.world.getBiomeSource().method_3857(this.biomes, i * 4 - 2, j * 4 - 2, 10, 10);
		this.method_6544(i * 4, 0, j * 4);

		for (int k = 0; k < 4; k++) {
			int l = k * 5;
			int m = (k + 1) * 5;

			for (int n = 0; n < 4; n++) {
				int o = (l + n) * 33;
				int p = (l + n + 1) * 33;
				int q = (m + n) * 33;
				int r = (m + n + 1) * 33;

				for (int s = 0; s < 32; s++) {
					double d = 0.125;
					double e = this.field_7516[o + s];
					double f = this.field_7516[p + s];
					double g = this.field_7516[q + s];
					double h = this.field_7516[r + s];
					double t = (this.field_7516[o + s + 1] - e) * d;
					double u = (this.field_7516[p + s + 1] - f) * d;
					double v = (this.field_7516[q + s + 1] - g) * d;
					double w = (this.field_7516[r + s + 1] - h) * d;

					for (int x = 0; x < 8; x++) {
						double y = 0.25;
						double z = e;
						double aa = f;
						double ab = (g - e) * y;
						double ac = (h - f) * y;

						for (int ad = 0; ad < 4; ad++) {
							double ae = 0.25;
							double ag = (aa - z) * ae;
							double af = z - ag;

							for (int ah = 0; ah < 4; ah++) {
								if ((af += ag) > 0.0) {
									chunkBlockStateStorage.set(k * 4 + ad, s * 8 + x, n * 4 + ah, Blocks.STONE.getDefaultState());
								} else if (s * 8 + x < this.properties.seaLevel) {
									chunkBlockStateStorage.set(k * 4 + ad, s * 8 + x, n * 4 + ah, this.underwaterBlock.getDefaultState());
								}
							}

							z += ab;
							aa += ac;
						}

						e += t;
						f += u;
						g += v;
						h += w;
					}
				}
			}
		}
	}

	public void method_6546(int i, int j, ChunkBlockStateStorage chunkBlockStateStorage, Biome[] biomes) {
		double d = 0.03125;
		this.field_4839 = this.field_7514.method_6580(this.field_4839, (double)(i * 16), (double)(j * 16), 16, 16, d * 2.0, d * 2.0, 1.0);

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				Biome biome = biomes[l + k * 16];
				biome.method_6420(this.world, this.random, chunkBlockStateStorage, i * 16 + k, j * 16 + l, this.field_4839[l + k * 16]);
			}
		}
	}

	@Override
	public Chunk getChunk(int x, int z) {
		this.random.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage();
		this.method_9194(x, z, chunkBlockStateStorage);
		this.biomes = this.world.getBiomeSource().method_3861(this.biomes, x * 16, z * 16, 16, 16);
		this.method_6546(x, z, chunkBlockStateStorage, this.biomes);
		if (this.properties.useCaves) {
			this.caveCarver.carveRegion(this, this.world, x, z, chunkBlockStateStorage);
		}

		if (this.properties.useRavines) {
			this.ravineCarver.carveRegion(this, this.world, x, z, chunkBlockStateStorage);
		}

		if (this.properties.useMineshafts && this.hasStructures) {
			this.mineshaft.carveRegion(this, this.world, x, z, chunkBlockStateStorage);
		}

		if (this.properties.useVillages && this.hasStructures) {
			this.village.carveRegion(this, this.world, x, z, chunkBlockStateStorage);
		}

		if (this.properties.useStrongholds && this.hasStructures) {
			this.strongholdGenerator.carveRegion(this, this.world, x, z, chunkBlockStateStorage);
		}

		if (this.properties.useTemples && this.hasStructures) {
			this.witchHut.carveRegion(this, this.world, x, z, chunkBlockStateStorage);
		}

		if (this.properties.useMonuments && this.hasStructures) {
			this.oceanMonument.carveRegion(this, this.world, x, z, chunkBlockStateStorage);
		}

		Chunk chunk = new Chunk(this.world, chunkBlockStateStorage, x, z);
		byte[] bs = chunk.getBiomeArray();

		for (int i = 0; i < bs.length; i++) {
			bs[i] = (byte)this.biomes[i].id;
		}

		chunk.calculateSkyLight();
		return chunk;
	}

	private void method_6544(int i, int j, int k) {
		this.field_4828 = this.field_4822
			.method_121(
				this.field_4828,
				i,
				k,
				5,
				5,
				(double)this.properties.depthNoiseScaleX,
				(double)this.properties.depthNoiseScaleZ,
				(double)this.properties.depthNoiseScaleExponent
			);
		float f = this.properties.coordinateScale;
		float g = this.properties.heightScale;
		this.field_7511 = this.field_4834
			.method_122(
				this.field_7511,
				i,
				j,
				k,
				5,
				33,
				5,
				(double)(f / this.properties.mainNoiseScaleX),
				(double)(g / this.properties.mainNoiseScaleY),
				(double)(f / this.properties.mainNoiseScaleZ)
			);
		this.field_7512 = this.field_4832.method_122(this.field_7512, i, j, k, 5, 33, 5, (double)f, (double)g, (double)f);
		this.field_7513 = this.field_4833.method_122(this.field_7513, i, j, k, 5, 33, 5, (double)f, (double)g, (double)f);
		int var37 = false;
		int var36 = false;
		int l = 0;
		int m = 0;

		for (int n = 0; n < 5; n++) {
			for (int o = 0; o < 5; o++) {
				float h = 0.0F;
				float p = 0.0F;
				float q = 0.0F;
				int r = 2;
				Biome biome = this.biomes[n + 2 + (o + 2) * 10];

				for (int s = -r; s <= r; s++) {
					for (int t = -r; t <= r; t++) {
						Biome biome2 = this.biomes[n + s + 2 + (o + t + 2) * 10];
						float u = this.properties.biomeDepthOffset + biome2.depth * this.properties.biomeDepthWeight;
						float v = this.properties.biomeScaleOffset + biome2.variationModifier * this.properties.biomeScaleWeight;
						if (this.type == LevelGeneratorType.AMPLIFIED && u > 0.0F) {
							u = 1.0F + u * 2.0F;
							v = 1.0F + v * 4.0F;
						}

						float w = this.field_7517[s + 2 + (t + 2) * 5] / (u + 2.0F);
						if (biome2.depth > biome.depth) {
							w /= 2.0F;
						}

						h += v * w;
						p += u * w;
						q += w;
					}
				}

				h /= q;
				p /= q;
				h = h * 0.9F + 0.1F;
				p = (p * 4.0F - 1.0F) / 8.0F;
				double d = this.field_4828[m] / 8000.0;
				if (d < 0.0) {
					d = -d * 0.3;
				}

				d = d * 3.0 - 2.0;
				if (d < 0.0) {
					d /= 2.0;
					if (d < -1.0) {
						d = -1.0;
					}

					d /= 1.4;
					d /= 2.0;
				} else {
					if (d > 1.0) {
						d = 1.0;
					}

					d /= 8.0;
				}

				m++;
				double e = (double)p;
				double x = (double)h;
				e += d * 0.2;
				e = e * (double)this.properties.baseSize / 8.0;
				double y = (double)this.properties.baseSize + e * 4.0;

				for (int z = 0; z < 33; z++) {
					double aa = ((double)z - y) * (double)this.properties.stretchY * 128.0 / 256.0 / x;
					if (aa < 0.0) {
						aa *= 4.0;
					}

					double ab = this.field_7512[l] / (double)this.properties.lowerLimitScale;
					double ac = this.field_7513[l] / (double)this.properties.upperLimitScale;
					double ad = (this.field_7511[l] / 10.0 + 1.0) / 2.0;
					double ae = MathHelper.clampedLerp(ab, ac, ad) - aa;
					if (z > 29) {
						double af = (double)((float)(z - 29) / 3.0F);
						ae = ae * (1.0 - af) + -10.0 * af;
					}

					this.field_7516[l] = ae;
					l++;
				}
			}
		}
	}

	@Override
	public boolean chunkExists(int x, int z) {
		return true;
	}

	@Override
	public void decorateChunk(ChunkProvider provider, int x, int z) {
		FallingBlock.instantFall = true;
		int i = x * 16;
		int j = z * 16;
		BlockPos blockPos = new BlockPos(i, 0, j);
		Biome biome = this.world.getBiome(blockPos.add(16, 0, 16));
		this.random.setSeed(this.world.getSeed());
		long l = this.random.nextLong() / 2L * 2L + 1L;
		long m = this.random.nextLong() / 2L * 2L + 1L;
		this.random.setSeed((long)x * l + (long)z * m ^ this.world.getSeed());
		boolean bl = false;
		ChunkPos chunkPos = new ChunkPos(x, z);
		if (this.properties.useMineshafts && this.hasStructures) {
			this.mineshaft.populate(this.world, this.random, chunkPos);
		}

		if (this.properties.useVillages && this.hasStructures) {
			bl = this.village.populate(this.world, this.random, chunkPos);
		}

		if (this.properties.useStrongholds && this.hasStructures) {
			this.strongholdGenerator.populate(this.world, this.random, chunkPos);
		}

		if (this.properties.useTemples && this.hasStructures) {
			this.witchHut.populate(this.world, this.random, chunkPos);
		}

		if (this.properties.useMonuments && this.hasStructures) {
			this.oceanMonument.populate(this.world, this.random, chunkPos);
		}

		if (biome != Biome.DESERT && biome != Biome.DESERT_HILLS && this.properties.useWaterLakes && !bl && this.random.nextInt(this.properties.waterLakeChance) == 0
			)
		 {
			int k = this.random.nextInt(16) + 8;
			int n = this.random.nextInt(256);
			int o = this.random.nextInt(16) + 8;
			new LakesFeature(Blocks.WATER).generate(this.world, this.random, blockPos.add(k, n, o));
		}

		if (!bl && this.random.nextInt(this.properties.lavaLakeChance / 10) == 0 && this.properties.useLavaLakes) {
			int p = this.random.nextInt(16) + 8;
			int q = this.random.nextInt(this.random.nextInt(248) + 8);
			int r = this.random.nextInt(16) + 8;
			if (q < this.world.getSeaLevel() || this.random.nextInt(this.properties.lavaLakeChance / 8) == 0) {
				new LakesFeature(Blocks.LAVA).generate(this.world, this.random, blockPos.add(p, q, r));
			}
		}

		if (this.properties.useDungeons) {
			for (int s = 0; s < this.properties.dungeonChance; s++) {
				int t = this.random.nextInt(16) + 8;
				int u = this.random.nextInt(256);
				int v = this.random.nextInt(16) + 8;
				new DungeonFeature().generate(this.world, this.random, blockPos.add(t, u, v));
			}
		}

		biome.decorate(this.world, this.random, new BlockPos(i, 0, j));
		MobSpawnerHelper.spawnMobs(this.world, biome, i + 8, j + 8, 16, 16, this.random);
		blockPos = blockPos.add(8, 0, 8);

		for (int w = 0; w < 16; w++) {
			for (int y = 0; y < 16; y++) {
				BlockPos blockPos2 = this.world.method_8562(blockPos.add(w, 0, y));
				BlockPos blockPos3 = blockPos2.down();
				if (this.world.canWaterFreezeAt(blockPos3)) {
					this.world.setBlockState(blockPos3, Blocks.ICE.getDefaultState(), 2);
				}

				if (this.world.method_8552(blockPos2, true)) {
					this.world.setBlockState(blockPos2, Blocks.SNOW_LAYER.getDefaultState(), 2);
				}
			}
		}

		FallingBlock.instantFall = false;
	}

	@Override
	public boolean isChunkModified(ChunkProvider chunkProvider, Chunk chunk, int x, int z) {
		boolean bl = false;
		if (this.properties.useMonuments && this.hasStructures && chunk.getInhabitedTime() < 3600L) {
			bl |= this.oceanMonument.populate(this.world, this.random, new ChunkPos(x, z));
		}

		return bl;
	}

	@Override
	public boolean saveChunks(boolean saveEntities, ProgressListener progressListener) {
		return true;
	}

	@Override
	public void flushChunks() {
	}

	@Override
	public boolean tickChunks() {
		return false;
	}

	@Override
	public boolean isSavingEnabled() {
		return true;
	}

	@Override
	public String getChunkProviderName() {
		return "RandomLevelSource";
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		Biome biome = this.world.getBiome(pos);
		if (this.hasStructures) {
			if (category == EntityCategory.MONSTER && this.witchHut.isSwampHut(pos)) {
				return this.witchHut.getMonsterSpawns();
			}

			if (category == EntityCategory.MONSTER && this.properties.useMonuments && this.oceanMonument.method_9267(this.world, pos)) {
				return this.oceanMonument.getSpawnableMobs();
			}
		}

		return biome.getSpawnEntries(category);
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos) {
		return "Stronghold".equals(structureName) && this.strongholdGenerator != null ? this.strongholdGenerator.method_9269(world, pos) : null;
	}

	@Override
	public int getLoadedChunksCount() {
		return 0;
	}

	@Override
	public void handleInitialLoad(Chunk chunk, int x, int z) {
		if (this.properties.useMineshafts && this.hasStructures) {
			this.mineshaft.carveRegion(this, this.world, x, z, null);
		}

		if (this.properties.useVillages && this.hasStructures) {
			this.village.carveRegion(this, this.world, x, z, null);
		}

		if (this.properties.useStrongholds && this.hasStructures) {
			this.strongholdGenerator.carveRegion(this, this.world, x, z, null);
		}

		if (this.properties.useTemples && this.hasStructures) {
			this.witchHut.carveRegion(this, this.world, x, z, null);
		}

		if (this.properties.useMonuments && this.hasStructures) {
			this.oceanMonument.carveRegion(this, this.world, x, z, null);
		}
	}

	@Override
	public Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}
}
