package net.minecraft.world.chunk;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.EndCityStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.NoiseSampler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.feature.EndGatewayFeature;
import net.minecraft.world.gen.feature.class_2754;

public class EndChunkGenerator implements ChunkGenerator {
	private final Random random;
	protected static final BlockState END_STONE = Blocks.END_STONE.getDefaultState();
	protected static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final NoiseGenerator field_4856;
	private final NoiseGenerator field_4857;
	private final NoiseGenerator field_4858;
	public NoiseGenerator field_12973;
	public NoiseGenerator field_12974;
	private final World world;
	private final boolean hasStructures;
	private final BlockPos field_15189;
	private final EndCityStructure endCityFeature = new EndCityStructure(this);
	private final NoiseSampler field_12977;
	private double[] field_4860;
	private Biome[] field_4861;
	double[] field_4849;
	double[] field_4850;
	double[] field_4851;
	private final class_2754 field_12978 = new class_2754();

	public EndChunkGenerator(World world, boolean bl, long l, BlockPos blockPos) {
		this.world = world;
		this.hasStructures = bl;
		this.field_15189 = blockPos;
		this.random = new Random(l);
		this.field_4856 = new NoiseGenerator(this.random, 16);
		this.field_4857 = new NoiseGenerator(this.random, 16);
		this.field_4858 = new NoiseGenerator(this.random, 8);
		this.field_12973 = new NoiseGenerator(this.random, 10);
		this.field_12974 = new NoiseGenerator(this.random, 16);
		this.field_12977 = new NoiseSampler(this.random);
	}

	public void method_9195(int i, int j, ChunkBlockStateStorage chunkBlockStateStorage) {
		int k = 2;
		int l = 3;
		int m = 33;
		int n = 3;
		this.field_4860 = this.method_4011(this.field_4860, i * 2, 0, j * 2, 3, 33, 3);

		for (int o = 0; o < 2; o++) {
			for (int p = 0; p < 2; p++) {
				for (int q = 0; q < 32; q++) {
					double d = 0.25;
					double e = this.field_4860[((o + 0) * 3 + p + 0) * 33 + q + 0];
					double f = this.field_4860[((o + 0) * 3 + p + 1) * 33 + q + 0];
					double g = this.field_4860[((o + 1) * 3 + p + 0) * 33 + q + 0];
					double h = this.field_4860[((o + 1) * 3 + p + 1) * 33 + q + 0];
					double r = (this.field_4860[((o + 0) * 3 + p + 0) * 33 + q + 1] - e) * 0.25;
					double s = (this.field_4860[((o + 0) * 3 + p + 1) * 33 + q + 1] - f) * 0.25;
					double t = (this.field_4860[((o + 1) * 3 + p + 0) * 33 + q + 1] - g) * 0.25;
					double u = (this.field_4860[((o + 1) * 3 + p + 1) * 33 + q + 1] - h) * 0.25;

					for (int v = 0; v < 4; v++) {
						double w = 0.125;
						double x = e;
						double y = f;
						double z = (g - e) * 0.125;
						double aa = (h - f) * 0.125;

						for (int ab = 0; ab < 8; ab++) {
							double ac = 0.125;
							double ad = x;
							double ae = (y - x) * 0.125;

							for (int af = 0; af < 8; af++) {
								BlockState blockState = AIR;
								if (ad > 0.0) {
									blockState = END_STONE;
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
				BlockState blockState = END_STONE;
				BlockState blockState2 = END_STONE;

				for (int m = 127; m >= 0; m--) {
					BlockState blockState3 = chunkBlockStateStorage.get(i, m, j);
					if (blockState3.getMaterial() == Material.AIR) {
						l = -1;
					} else if (blockState3.getBlock() == Blocks.STONE) {
						if (l == -1) {
							l = 1;
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
	public Chunk generate(int x, int z) {
		this.random.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
		ChunkBlockStateStorage chunkBlockStateStorage = new ChunkBlockStateStorage();
		this.field_4861 = this.world.method_3726().method_11540(this.field_4861, x * 16, z * 16, 16, 16);
		this.method_9195(x, z, chunkBlockStateStorage);
		this.method_9196(chunkBlockStateStorage);
		if (this.hasStructures) {
			this.endCityFeature.method_4004(this.world, x, z, chunkBlockStateStorage);
		}

		Chunk chunk = new Chunk(this.world, chunkBlockStateStorage, x, z);
		byte[] bs = chunk.getBiomeArray();

		for (int i = 0; i < bs.length; i++) {
			bs[i] = (byte)Biome.getBiomeIndex(this.field_4861[i]);
		}

		chunk.calculateSkyLight();
		return chunk;
	}

	private float method_11821(int i, int j, int k, int l) {
		float f = (float)(i * 2 + k);
		float g = (float)(j * 2 + l);
		float h = 100.0F - MathHelper.sqrt(f * f + g * g) * 8.0F;
		if (h > 80.0F) {
			h = 80.0F;
		}

		if (h < -100.0F) {
			h = -100.0F;
		}

		for (int m = -12; m <= 12; m++) {
			for (int n = -12; n <= 12; n++) {
				long o = (long)(i + m);
				long p = (long)(j + n);
				if (o * o + p * p > 4096L && this.field_12977.sample((double)o, (double)p) < -0.9F) {
					float q = (MathHelper.abs((float)o) * 3439.0F + MathHelper.abs((float)p) * 147.0F) % 13.0F + 9.0F;
					f = (float)(k - m * 2);
					g = (float)(l - n * 2);
					float r = 100.0F - MathHelper.sqrt(f * f + g * g) * q;
					if (r > 80.0F) {
						r = 80.0F;
					}

					if (r < -100.0F) {
						r = -100.0F;
					}

					if (r > h) {
						h = r;
					}
				}
			}
		}

		return h;
	}

	public boolean method_11822(int i, int j) {
		return (long)i * (long)i + (long)j * (long)j > 4096L && this.method_11821(i, j, 1, 1) >= 0.0F;
	}

	private double[] method_4011(double[] ds, int i, int j, int k, int l, int m, int n) {
		if (ds == null) {
			ds = new double[l * m * n];
		}

		double d = 684.412;
		double e = 684.412;
		d *= 2.0;
		this.field_4849 = this.field_4858.method_122(this.field_4849, i, j, k, l, m, n, d / 80.0, 4.277575000000001, d / 80.0);
		this.field_4850 = this.field_4856.method_122(this.field_4850, i, j, k, l, m, n, d, 684.412, d);
		this.field_4851 = this.field_4857.method_122(this.field_4851, i, j, k, l, m, n, d, 684.412, d);
		int o = i / 2;
		int p = k / 2;
		int q = 0;

		for (int r = 0; r < l; r++) {
			for (int s = 0; s < n; s++) {
				float f = this.method_11821(o, p, r, s);

				for (int t = 0; t < m; t++) {
					double g = this.field_4850[q] / 512.0;
					double h = this.field_4851[q] / 512.0;
					double u = (this.field_4849[q] / 10.0 + 1.0) / 2.0;
					double v;
					if (u < 0.0) {
						v = g;
					} else if (u > 1.0) {
						v = h;
					} else {
						v = g + (h - g) * u;
					}

					v -= 8.0;
					v += (double)f;
					int y = 2;
					if (t > m / 2 - y) {
						double z = (double)((float)(t - (m / 2 - y)) / 64.0F);
						z = MathHelper.clamp(z, 0.0, 1.0);
						v = v * (1.0 - z) + -3000.0 * z;
					}

					int var33 = 8;
					if (t < var33) {
						double aa = (double)((float)(var33 - t) / ((float)var33 - 1.0F));
						v = v * (1.0 - aa) + -30.0 * aa;
					}

					ds[q] = v;
					q++;
				}
			}
		}

		return ds;
	}

	@Override
	public void populate(int x, int z) {
		FallingBlock.instantFall = true;
		BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
		if (this.hasStructures) {
			this.endCityFeature.populate(this.world, this.random, new ChunkPos(x, z));
		}

		this.world.getBiome(blockPos.add(16, 0, 16)).decorate(this.world, this.world.random, blockPos);
		long l = (long)x * (long)x + (long)z * (long)z;
		if (l > 4096L) {
			float f = this.method_11821(x, z, 1, 1);
			if (f < -20.0F && this.random.nextInt(14) == 0) {
				this.field_12978.generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, 55 + this.random.nextInt(16), this.random.nextInt(16) + 8));
				if (this.random.nextInt(4) == 0) {
					this.field_12978.generate(this.world, this.random, blockPos.add(this.random.nextInt(16) + 8, 55 + this.random.nextInt(16), this.random.nextInt(16) + 8));
				}
			}

			if (this.method_11821(x, z, 1, 1) > 40.0F) {
				int i = this.random.nextInt(5);

				for (int j = 0; j < i; j++) {
					int k = this.random.nextInt(16) + 8;
					int m = this.random.nextInt(16) + 8;
					int n = this.world.getHighestBlock(blockPos.add(k, 0, m)).getY();
					if (n > 0) {
						int o = n - 1;
						if (this.world.isAir(blockPos.add(k, o + 1, m)) && this.world.getBlockState(blockPos.add(k, o, m)).getBlock() == Blocks.END_STONE) {
							ChorusFlowerBlock.method_11586(this.world, blockPos.add(k, o + 1, m), this.random, 8);
						}
					}
				}

				if (this.random.nextInt(700) == 0) {
					int p = this.random.nextInt(16) + 8;
					int q = this.random.nextInt(16) + 8;
					int r = this.world.getHighestBlock(blockPos.add(p, 0, q)).getY();
					if (r > 0) {
						int s = r + 3 + this.random.nextInt(7);
						BlockPos blockPos2 = blockPos.add(p, s, q);
						new EndGatewayFeature().generate(this.world, this.random, blockPos2);
						BlockEntity blockEntity = this.world.getBlockEntity(blockPos2);
						if (blockEntity instanceof EndGatewayBlockEntity) {
							EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
							endGatewayBlockEntity.setExitPortal(this.field_15189);
						}
					}
				}
			}
		}

		FallingBlock.instantFall = false;
	}

	@Override
	public boolean method_11762(Chunk chunk, int x, int z) {
		return false;
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		return this.world.getBiome(pos).getSpawnEntries(category);
	}

	@Nullable
	@Override
	public BlockPos method_3866(World world, String string, BlockPos pos, boolean bl) {
		return "EndCity".equals(string) && this.endCityFeature != null ? this.endCityFeature.method_9269(world, pos, bl) : null;
	}

	@Override
	public boolean method_14387(World world, String string, BlockPos pos) {
		return "EndCity".equals(string) && this.endCityFeature != null ? this.endCityFeature.method_9270(pos) : false;
	}

	@Override
	public void method_4702(Chunk chunk, int x, int z) {
	}
}
