package net.minecraft.world.chunk;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCategory;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.NetherFortressStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.NetherCaveCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GlowstoneClusterFeature;
import net.minecraft.world.gen.feature.GlowstoneFeature;
import net.minecraft.world.gen.feature.MushroomFeature;
import net.minecraft.world.gen.feature.NetherFireFeature;
import net.minecraft.world.gen.feature.NetherSpringFeature;
import net.minecraft.world.gen.feature.OreFeature;

public class NetherChunkGenerator implements ChunkGenerator {
	protected static final BlockState field_12963 = Blocks.AIR.getDefaultState();
	protected static final BlockState field_12964 = Blocks.NETHERRACK.getDefaultState();
	protected static final BlockState field_12965 = Blocks.BEDROCK.getDefaultState();
	protected static final BlockState field_12966 = Blocks.LAVA.getDefaultState();
	protected static final BlockState field_12967 = Blocks.GRAVEL.getDefaultState();
	protected static final BlockState field_12968 = Blocks.SOULSAND.getDefaultState();
	private final World world;
	private final boolean hasStructures;
	private final Random random;
	private double[] field_4814 = new double[256];
	private double[] field_4815 = new double[256];
	private double[] field_4816 = new double[256];
	private double[] field_4813;
	private final NoiseGenerator field_4807;
	private final NoiseGenerator field_4808;
	private final NoiseGenerator field_4809;
	private final NoiseGenerator field_4810;
	private final NoiseGenerator field_4811;
	public final NoiseGenerator field_4798;
	public final NoiseGenerator field_4799;
	private final NetherFireFeature fireFeature = new NetherFireFeature();
	private final GlowstoneFeature glowstoneFeature = new GlowstoneFeature();
	private final GlowstoneClusterFeature glowstoneClusterFeature = new GlowstoneClusterFeature();
	private final Feature quartzFeature = new OreFeature(Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14, BlockPredicate.create(Blocks.NETHERRACK));
	private final Feature field_14845 = new OreFeature(Blocks.MAGMA.getDefaultState(), 33, BlockPredicate.create(Blocks.NETHERRACK));
	private final NetherSpringFeature field_10130 = new NetherSpringFeature(Blocks.FLOWING_LAVA, true);
	private final NetherSpringFeature field_10131 = new NetherSpringFeature(Blocks.FLOWING_LAVA, false);
	private final MushroomFeature brownMushroomFeature = new MushroomFeature(Blocks.BROWN_MUSHROOM);
	private final MushroomFeature redMushroomFeature = new MushroomFeature(Blocks.RED_MUSHROOM);
	private final NetherFortressStructure fortressFeature = new NetherFortressStructure();
	private final Carver cave = new NetherCaveCarver();
	double[] field_4801;
	double[] field_4802;
	double[] field_4803;
	double[] field_4804;
	double[] field_4805;

	public NetherChunkGenerator(World world, boolean bl, long l) {
		this.world = world;
		this.hasStructures = bl;
		this.random = new Random(l);
		this.field_4807 = new NoiseGenerator(this.random, 16);
		this.field_4808 = new NoiseGenerator(this.random, 16);
		this.field_4809 = new NoiseGenerator(this.random, 8);
		this.field_4810 = new NoiseGenerator(this.random, 4);
		this.field_4811 = new NoiseGenerator(this.random, 4);
		this.field_4798 = new NoiseGenerator(this.random, 10);
		this.field_4799 = new NoiseGenerator(this.random, 16);
		world.setSeaLevel(63);
	}

	public void method_9191(int i, int j, ChunkBlockStateStorage chunkBlockStateStorage) {
		int k = 4;
		int l = this.world.getSeaLevel() / 2 + 1;
		int m = 5;
		int n = 17;
		int o = 5;
		this.field_4813 = this.method_3999(this.field_4813, i * 4, 0, j * 4, 5, 17, 5);

		for (int p = 0; p < 4; p++) {
			for (int q = 0; q < 4; q++) {
				for (int r = 0; r < 16; r++) {
					double d = 0.125;
					double e = this.field_4813[((p + 0) * 5 + q + 0) * 17 + r + 0];
					double f = this.field_4813[((p + 0) * 5 + q + 1) * 17 + r + 0];
					double g = this.field_4813[((p + 1) * 5 + q + 0) * 17 + r + 0];
					double h = this.field_4813[((p + 1) * 5 + q + 1) * 17 + r + 0];
					double s = (this.field_4813[((p + 0) * 5 + q + 0) * 17 + r + 1] - e) * 0.125;
					double t = (this.field_4813[((p + 0) * 5 + q + 1) * 17 + r + 1] - f) * 0.125;
					double u = (this.field_4813[((p + 1) * 5 + q + 0) * 17 + r + 1] - g) * 0.125;
					double v = (this.field_4813[((p + 1) * 5 + q + 1) * 17 + r + 1] - h) * 0.125;

					for (int w = 0; w < 8; w++) {
						double x = 0.25;
						double y = e;
						double z = f;
						double aa = (g - e) * 0.25;
						double ab = (h - f) * 0.25;

						for (int ac = 0; ac < 4; ac++) {
							double ad = 0.25;
							double ae = y;
							double af = (z - y) * 0.25;

							for (int ag = 0; ag < 4; ag++) {
								BlockState blockState = null;
								if (r * 8 + w < l) {
									blockState = field_12966;
								}

								if (ae > 0.0) {
									blockState = field_12964;
								}

								int ah = ac + p * 4;
								int ai = w + r * 8;
								int aj = ag + q * 4;
								chunkBlockStateStorage.set(ah, ai, aj, blockState);
								ae += af;
							}

							y += aa;
							z += ab;
						}

						e += s;
						f += t;
						g += u;
						h += v;
					}
				}
			}
		}
	}

	public void method_9192(int i, int j, ChunkBlockStateStorage chunkBlockStateStorage) {
		int k = this.world.getSeaLevel() + 1;
		double d = 0.03125;
		this.field_4814 = this.field_4810.method_122(this.field_4814, i * 16, j * 16, 0, 16, 16, 1, 0.03125, 0.03125, 1.0);
		this.field_4815 = this.field_4810.method_122(this.field_4815, i * 16, 109, j * 16, 16, 1, 16, 0.03125, 1.0, 0.03125);
		this.field_4816 = this.field_4811.method_122(this.field_4816, i * 16, j * 16, 0, 16, 16, 1, 0.0625, 0.0625, 0.0625);

		for (int l = 0; l < 16; l++) {
			for (int m = 0; m < 16; m++) {
				boolean bl = this.field_4814[l + m * 16] + this.random.nextDouble() * 0.2 > 0.0;
				boolean bl2 = this.field_4815[l + m * 16] + this.random.nextDouble() * 0.2 > 0.0;
				int n = (int)(this.field_4816[l + m * 16] / 3.0 + 3.0 + this.random.nextDouble() * 0.25);
				int o = -1;
				BlockState blockState = field_12964;
				BlockState blockState2 = field_12964;

				for (int p = 127; p >= 0; p--) {
					if (p < 127 - this.random.nextInt(5) && p > this.random.nextInt(5)) {
						BlockState blockState3 = chunkBlockStateStorage.get(m, p, l);
						if (blockState3.getBlock() == null || blockState3.getMaterial() == Material.AIR) {
							o = -1;
						} else if (blockState3.getBlock() == Blocks.NETHERRACK) {
							if (o == -1) {
								if (n <= 0) {
									blockState = field_12963;
									blockState2 = field_12964;
								} else if (p >= k - 4 && p <= k + 1) {
									blockState = field_12964;
									blockState2 = field_12964;
									if (bl2) {
										blockState = field_12967;
										blockState2 = field_12964;
									}

									if (bl) {
										blockState = field_12968;
										blockState2 = field_12968;
									}
								}

								if (p < k && (blockState == null || blockState.getMaterial() == Material.AIR)) {
									blockState = field_12966;
								}

								o = n;
								if (p >= k - 1) {
									chunkBlockStateStorage.set(m, p, l, blockState);
								} else {
									chunkBlockStateStorage.set(m, p, l, blockState2);
								}
							} else if (o > 0) {
								o--;
								chunkBlockStateStorage.set(m, p, l, blockState2);
							}
						}
					} else {
						chunkBlockStateStorage.set(m, p, l, field_12965);
					}
				}
			}
		}
	}

	@Override
	public Chunk generate(int x, int z) {
		this.random.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage();
		this.method_9191(x, z, chunkBlockStateStorage);
		this.method_9192(x, z, chunkBlockStateStorage);
		this.cave.method_4004(this.world, x, z, chunkBlockStateStorage);
		if (this.hasStructures) {
			this.fortressFeature.method_4004(this.world, x, z, chunkBlockStateStorage);
		}

		Chunk chunk = new Chunk(this.world, chunkBlockStateStorage, x, z);
		Biome[] biomes = this.world.method_3726().method_11540(null, x * 16, z * 16, 16, 16);
		byte[] bs = chunk.getBiomeArray();

		for (int i = 0; i < bs.length; i++) {
			bs[i] = (byte)Biome.getBiomeIndex(biomes[i]);
		}

		chunk.method_3922();
		return chunk;
	}

	private double[] method_3999(double[] ds, int i, int j, int k, int l, int m, int n) {
		if (ds == null) {
			ds = new double[l * m * n];
		}

		double d = 684.412;
		double e = 2053.236;
		this.field_4804 = this.field_4798.method_122(this.field_4804, i, j, k, l, 1, n, 1.0, 0.0, 1.0);
		this.field_4805 = this.field_4799.method_122(this.field_4805, i, j, k, l, 1, n, 100.0, 0.0, 100.0);
		this.field_4801 = this.field_4809.method_122(this.field_4801, i, j, k, l, m, n, 8.555150000000001, 34.2206, 8.555150000000001);
		this.field_4802 = this.field_4807.method_122(this.field_4802, i, j, k, l, m, n, 684.412, 2053.236, 684.412);
		this.field_4803 = this.field_4808.method_122(this.field_4803, i, j, k, l, m, n, 684.412, 2053.236, 684.412);
		int o = 0;
		double[] es = new double[m];

		for (int p = 0; p < m; p++) {
			es[p] = Math.cos((double)p * Math.PI * 6.0 / (double)m) * 2.0;
			double f = (double)p;
			if (p > m / 2) {
				f = (double)(m - 1 - p);
			}

			if (f < 4.0) {
				f = 4.0 - f;
				es[p] -= f * f * f * 10.0;
			}
		}

		for (int q = 0; q < l; q++) {
			for (int r = 0; r < n; r++) {
				double g = 0.0;

				for (int s = 0; s < m; s++) {
					double h = es[s];
					double t = this.field_4802[o] / 512.0;
					double u = this.field_4803[o] / 512.0;
					double v = (this.field_4801[o] / 10.0 + 1.0) / 2.0;
					double w;
					if (v < 0.0) {
						w = t;
					} else if (v > 1.0) {
						w = u;
					} else {
						w = t + (u - t) * v;
					}

					w -= h;
					if (s > m - 4) {
						double z = (double)((float)(s - (m - 4)) / 3.0F);
						w = w * (1.0 - z) + -10.0 * z;
					}

					if ((double)s < 0.0) {
						double aa = (0.0 - (double)s) / 4.0;
						aa = MathHelper.clamp(aa, 0.0, 1.0);
						w = w * (1.0 - aa) + -10.0 * aa;
					}

					ds[o] = w;
					o++;
				}
			}
		}

		return ds;
	}

	@Override
	public void populate(int x, int z) {
		FallingBlock.instantFall = true;
		int i = x * 16;
		int j = z * 16;
		BlockPos blockPos = new BlockPos(i, 0, j);
		Biome biome = this.world.getBiome(blockPos.add(16, 0, 16));
		ChunkPos chunkPos = new ChunkPos(x, z);
		this.fortressFeature.populate(this.world, this.random, chunkPos);

		for (int k = 0; k < 8; k++) {
			this.field_10131.generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(120) + 4, this.random.nextInt(16) + 8));
		}

		for (int l = 0; l < this.random.nextInt(this.random.nextInt(10) + 1) + 1; l++) {
			this.fireFeature.generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(120) + 4, this.random.nextInt(16) + 8));
		}

		for (int m = 0; m < this.random.nextInt(this.random.nextInt(10) + 1); m++) {
			this.glowstoneFeature
				.generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(120) + 4, this.random.nextInt(16) + 8));
		}

		for (int n = 0; n < 10; n++) {
			this.glowstoneClusterFeature
				.generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(128), this.random.nextInt(16) + 8));
		}

		if (this.random.nextBoolean()) {
			this.brownMushroomFeature
				.generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(128), this.random.nextInt(16) + 8));
		}

		if (this.random.nextBoolean()) {
			this.redMushroomFeature.generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(128), this.random.nextInt(16) + 8));
		}

		for (int o = 0; o < 16; o++) {
			this.quartzFeature.generate(this.world, this.random, blockPos.add(this.random.nextInt(16), this.random.nextInt(108) + 10, this.random.nextInt(16)));
		}

		int p = this.world.getSeaLevel() / 2 + 1;

		for (int q = 0; q < 4; q++) {
			this.field_14845.generate(this.world, this.random, blockPos.add(this.random.nextInt(16), p - 5 + this.random.nextInt(10), this.random.nextInt(16)));
		}

		for (int r = 0; r < 16; r++) {
			this.field_10130.generate(this.world, this.random, blockPos.add(this.random.nextInt(16), this.random.nextInt(108) + 10, this.random.nextInt(16)));
		}

		biome.decorate(this.world, this.random, new BlockPos(i, 0, j));
		FallingBlock.instantFall = false;
	}

	@Override
	public boolean method_11762(Chunk chunk, int x, int z) {
		return false;
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		if (category == EntityCategory.MONSTER) {
			if (this.fortressFeature.method_9270(pos)) {
				return this.fortressFeature.getMonsterSpawns();
			}

			if (this.fortressFeature.method_9267(this.world, pos) && this.world.getBlockState(pos.down()).getBlock() == Blocks.NETHER_BRICKS) {
				return this.fortressFeature.getMonsterSpawns();
			}
		}

		Biome biome = this.world.getBiome(pos);
		return biome.getSpawnEntries(category);
	}

	@Nullable
	@Override
	public BlockPos method_3866(World world, String string, BlockPos pos, boolean bl) {
		return "Fortress".equals(string) && this.fortressFeature != null ? this.fortressFeature.method_9269(world, pos, bl) : null;
	}

	@Override
	public boolean method_14387(World world, String string, BlockPos pos) {
		return "Fortress".equals(string) && this.fortressFeature != null ? this.fortressFeature.method_9270(pos) : false;
	}

	@Override
	public void method_4702(Chunk chunk, int x, int z) {
		this.fortressFeature.method_4004(this.world, x, z, null);
	}
}
