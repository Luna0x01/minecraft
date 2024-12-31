package net.minecraft.world.chunk;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGenerator;

public class EndChunkGenerator implements ChunkProvider {
	private Random random;
	private NoiseGenerator field_4856;
	private NoiseGenerator field_4857;
	private NoiseGenerator field_4858;
	public NoiseGenerator field_4847;
	public NoiseGenerator field_4848;
	private World world;
	private double[] field_4860;
	private Biome[] field_4861;
	double[] field_4849;
	double[] field_4850;
	double[] field_4851;
	double[] field_4852;
	double[] field_4853;

	public EndChunkGenerator(World world, long l) {
		this.world = world;
		this.random = new Random(l);
		this.field_4856 = new NoiseGenerator(this.random, 16);
		this.field_4857 = new NoiseGenerator(this.random, 16);
		this.field_4858 = new NoiseGenerator(this.random, 8);
		this.field_4847 = new NoiseGenerator(this.random, 10);
		this.field_4848 = new NoiseGenerator(this.random, 16);
	}

	public void method_9195(int i, int j, ChunkBlockStateStorage chunkBlockStateStorage) {
		int k = 2;
		int l = k + 1;
		int m = 33;
		int n = k + 1;
		this.field_4860 = this.method_4011(this.field_4860, i * k, 0, j * k, l, m, n);

		for (int o = 0; o < k; o++) {
			for (int p = 0; p < k; p++) {
				for (int q = 0; q < 32; q++) {
					double d = 0.25;
					double e = this.field_4860[((o + 0) * n + p + 0) * m + q + 0];
					double f = this.field_4860[((o + 0) * n + p + 1) * m + q + 0];
					double g = this.field_4860[((o + 1) * n + p + 0) * m + q + 0];
					double h = this.field_4860[((o + 1) * n + p + 1) * m + q + 0];
					double r = (this.field_4860[((o + 0) * n + p + 0) * m + q + 1] - e) * d;
					double s = (this.field_4860[((o + 0) * n + p + 1) * m + q + 1] - f) * d;
					double t = (this.field_4860[((o + 1) * n + p + 0) * m + q + 1] - g) * d;
					double u = (this.field_4860[((o + 1) * n + p + 1) * m + q + 1] - h) * d;

					for (int v = 0; v < 4; v++) {
						double w = 0.125;
						double x = e;
						double y = f;
						double z = (g - e) * w;
						double aa = (h - f) * w;

						for (int ab = 0; ab < 8; ab++) {
							double ac = 0.125;
							double ad = x;
							double ae = (y - x) * ac;

							for (int af = 0; af < 8; af++) {
								BlockState blockState = null;
								if (ad > 0.0) {
									blockState = Blocks.END_STONE.getDefaultState();
								}

								int ag = ab + o * 8;
								int ah = v + q * 4;
								int ai = af + p * 8;
								chunkBlockStateStorage.set(ag, ah, ai, blockState);
								ad += ae;
							}

							x += z;
							y += aa;
						}

						e += r;
						f += s;
						g += t;
						h += u;
					}
				}
			}
		}
	}

	public void method_9196(ChunkBlockStateStorage chunkBlockStateStorage) {
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = 1;
				int l = -1;
				BlockState blockState = Blocks.END_STONE.getDefaultState();
				BlockState blockState2 = Blocks.END_STONE.getDefaultState();

				for (int m = 127; m >= 0; m--) {
					BlockState blockState3 = chunkBlockStateStorage.get(i, m, j);
					if (blockState3.getBlock().getMaterial() == Material.AIR) {
						l = -1;
					} else if (blockState3.getBlock() == Blocks.STONE) {
						if (l == -1) {
							if (k <= 0) {
								blockState = Blocks.AIR.getDefaultState();
								blockState2 = Blocks.END_STONE.getDefaultState();
							}

							l = k;
							if (m >= 0) {
								chunkBlockStateStorage.set(i, m, j, blockState);
							} else {
								chunkBlockStateStorage.set(i, m, j, blockState2);
							}
						} else if (l > 0) {
							l--;
							chunkBlockStateStorage.set(i, m, j, blockState2);
						}
					}
				}
			}
		}
	}

	@Override
	public Chunk getChunk(int x, int z) {
		this.random.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage();
		this.field_4861 = this.world.getBiomeSource().method_3861(this.field_4861, x * 16, z * 16, 16, 16);
		this.method_9195(x, z, chunkBlockStateStorage);
		this.method_9196(chunkBlockStateStorage);
		Chunk chunk = new Chunk(this.world, chunkBlockStateStorage, x, z);
		byte[] bs = chunk.getBiomeArray();

		for (int i = 0; i < bs.length; i++) {
			bs[i] = (byte)this.field_4861[i].id;
		}

		chunk.calculateSkyLight();
		return chunk;
	}

	private double[] method_4011(double[] ds, int i, int j, int k, int l, int m, int n) {
		if (ds == null) {
			ds = new double[l * m * n];
		}

		double d = 684.412;
		double e = 684.412;
		this.field_4852 = this.field_4847.method_121(this.field_4852, i, k, l, n, 1.121, 1.121, 0.5);
		this.field_4853 = this.field_4848.method_121(this.field_4853, i, k, l, n, 200.0, 200.0, 0.5);
		d *= 2.0;
		this.field_4849 = this.field_4858.method_122(this.field_4849, i, j, k, l, m, n, d / 80.0, e / 160.0, d / 80.0);
		this.field_4850 = this.field_4856.method_122(this.field_4850, i, j, k, l, m, n, d, e, d);
		this.field_4851 = this.field_4857.method_122(this.field_4851, i, j, k, l, m, n, d, e, d);
		int o = 0;

		for (int p = 0; p < l; p++) {
			for (int q = 0; q < n; q++) {
				float f = (float)(p + i) / 1.0F;
				float g = (float)(q + k) / 1.0F;
				float h = 100.0F - MathHelper.sqrt(f * f + g * g) * 8.0F;
				if (h > 80.0F) {
					h = 80.0F;
				}

				if (h < -100.0F) {
					h = -100.0F;
				}

				for (int r = 0; r < m; r++) {
					double s = 0.0;
					double t = this.field_4850[o] / 512.0;
					double u = this.field_4851[o] / 512.0;
					double v = (this.field_4849[o] / 10.0 + 1.0) / 2.0;
					if (v < 0.0) {
						s = t;
					} else if (v > 1.0) {
						s = u;
					} else {
						s = t + (u - t) * v;
					}

					s -= 8.0;
					s += (double)h;
					int w = 2;
					if (r > m / 2 - w) {
						double x = (double)((float)(r - (m / 2 - w)) / 64.0F);
						x = MathHelper.clamp(x, 0.0, 1.0);
						s = s * (1.0 - x) + -3000.0 * x;
					}

					int var34 = 8;
					if (r < var34) {
						double y = (double)((float)(var34 - r) / ((float)var34 - 1.0F));
						s = s * (1.0 - y) + -30.0 * y;
					}

					ds[o] = s;
					o++;
				}
			}
		}

		return ds;
	}

	@Override
	public boolean chunkExists(int x, int z) {
		return true;
	}

	@Override
	public void decorateChunk(ChunkProvider provider, int x, int z) {
		FallingBlock.instantFall = true;
		BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
		this.world.getBiome(blockPos.add(16, 0, 16)).decorate(this.world, this.world.random, blockPos);
		FallingBlock.instantFall = false;
	}

	@Override
	public boolean isChunkModified(ChunkProvider chunkProvider, Chunk chunk, int x, int z) {
		return false;
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
		return this.world.getBiome(pos).getSpawnEntries(category);
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos) {
		return null;
	}

	@Override
	public int getLoadedChunksCount() {
		return 0;
	}

	@Override
	public void handleInitialLoad(Chunk chunk, int x, int z) {
	}

	@Override
	public Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}
}
