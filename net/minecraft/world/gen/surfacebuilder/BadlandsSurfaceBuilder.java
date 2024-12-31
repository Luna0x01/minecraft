package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public class BadlandsSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final BlockState WHITE_TERACOTTA = Blocks.field_10611.getDefaultState();
	private static final BlockState ORANGE_TERRACOTTA = Blocks.field_10184.getDefaultState();
	private static final BlockState TERACOTTA = Blocks.field_10415.getDefaultState();
	private static final BlockState YELLOW_TERACOTTA = Blocks.field_10143.getDefaultState();
	private static final BlockState BROWN_TERACOTTA = Blocks.field_10123.getDefaultState();
	private static final BlockState RED_TERACOTTA = Blocks.field_10328.getDefaultState();
	private static final BlockState LIGHT_GRAY_TERACOTTA = Blocks.field_10590.getDefaultState();
	protected BlockState[] layerBlocks;
	protected long seed;
	protected OctaveSimplexNoiseSampler field_15623;
	protected OctaveSimplexNoiseSampler field_15618;
	protected OctaveSimplexNoiseSampler field_15619;

	public BadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function) {
		super(function);
	}

	public void method_15208(
		Random random,
		Chunk chunk,
		Biome biome,
		int i,
		int j,
		int k,
		double d,
		BlockState blockState,
		BlockState blockState2,
		int l,
		long m,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		int n = i & 15;
		int o = j & 15;
		BlockState blockState3 = WHITE_TERACOTTA;
		BlockState blockState4 = biome.getSurfaceConfig().getUnderMaterial();
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;
		int r = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int s = k; s >= 0; s--) {
			if (r < 15) {
				mutable.set(n, s, o);
				BlockState blockState5 = chunk.getBlockState(mutable);
				if (blockState5.isAir()) {
					q = -1;
				} else if (blockState5.getBlock() == blockState.getBlock()) {
					if (q == -1) {
						bl2 = false;
						if (p <= 0) {
							blockState3 = Blocks.field_10124.getDefaultState();
							blockState4 = blockState;
						} else if (s >= l - 4 && s <= l + 1) {
							blockState3 = WHITE_TERACOTTA;
							blockState4 = biome.getSurfaceConfig().getUnderMaterial();
						}

						if (s < l && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						q = p + Math.max(0, s - l);
						if (s >= l - 1) {
							if (s <= l + 3 + p) {
								chunk.setBlockState(mutable, biome.getSurfaceConfig().getTopMaterial(), false);
								bl2 = true;
							} else {
								BlockState blockState6;
								if (s < 64 || s > 127) {
									blockState6 = ORANGE_TERRACOTTA;
								} else if (bl) {
									blockState6 = TERACOTTA;
								} else {
									blockState6 = this.method_15207(i, s, j);
								}

								chunk.setBlockState(mutable, blockState6, false);
							}
						} else {
							chunk.setBlockState(mutable, blockState4, false);
							Block block = blockState4.getBlock();
							if (block == Blocks.field_10611
								|| block == Blocks.field_10184
								|| block == Blocks.field_10015
								|| block == Blocks.field_10325
								|| block == Blocks.field_10143
								|| block == Blocks.field_10014
								|| block == Blocks.field_10444
								|| block == Blocks.field_10349
								|| block == Blocks.field_10590
								|| block == Blocks.field_10235
								|| block == Blocks.field_10570
								|| block == Blocks.field_10409
								|| block == Blocks.field_10123
								|| block == Blocks.field_10526
								|| block == Blocks.field_10328
								|| block == Blocks.field_10626) {
								chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
							}
						}
					} else if (q > 0) {
						q--;
						if (bl2) {
							chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
						} else {
							chunk.setBlockState(mutable, this.method_15207(i, s, j), false);
						}
					}

					r++;
				}
			}
		}
	}

	@Override
	public void initSeed(long l) {
		if (this.seed != l || this.layerBlocks == null) {
			this.initLayerBlocks(l);
		}

		if (this.seed != l || this.field_15623 == null || this.field_15618 == null) {
			Random random = new ChunkRandom(l);
			this.field_15623 = new OctaveSimplexNoiseSampler(random, 4);
			this.field_15618 = new OctaveSimplexNoiseSampler(random, 1);
		}

		this.seed = l;
	}

	protected void initLayerBlocks(long l) {
		this.layerBlocks = new BlockState[64];
		Arrays.fill(this.layerBlocks, TERACOTTA);
		Random random = new ChunkRandom(l);
		this.field_15619 = new OctaveSimplexNoiseSampler(random, 1);

		for (int i = 0; i < 64; i++) {
			i += random.nextInt(5) + 1;
			if (i < 64) {
				this.layerBlocks[i] = ORANGE_TERRACOTTA;
			}
		}

		int j = random.nextInt(4) + 2;

		for (int k = 0; k < j; k++) {
			int m = random.nextInt(3) + 1;
			int n = random.nextInt(64);

			for (int o = 0; n + o < 64 && o < m; o++) {
				this.layerBlocks[n + o] = YELLOW_TERACOTTA;
			}
		}

		int p = random.nextInt(4) + 2;

		for (int q = 0; q < p; q++) {
			int r = random.nextInt(3) + 2;
			int s = random.nextInt(64);

			for (int t = 0; s + t < 64 && t < r; t++) {
				this.layerBlocks[s + t] = BROWN_TERACOTTA;
			}
		}

		int u = random.nextInt(4) + 2;

		for (int v = 0; v < u; v++) {
			int w = random.nextInt(3) + 1;
			int x = random.nextInt(64);

			for (int y = 0; x + y < 64 && y < w; y++) {
				this.layerBlocks[x + y] = RED_TERACOTTA;
			}
		}

		int z = random.nextInt(3) + 3;
		int aa = 0;

		for (int ab = 0; ab < z; ab++) {
			int ac = 1;
			aa += random.nextInt(16) + 4;

			for (int ad = 0; aa + ad < 64 && ad < 1; ad++) {
				this.layerBlocks[aa + ad] = WHITE_TERACOTTA;
				if (aa + ad > 1 && random.nextBoolean()) {
					this.layerBlocks[aa + ad - 1] = LIGHT_GRAY_TERACOTTA;
				}

				if (aa + ad < 63 && random.nextBoolean()) {
					this.layerBlocks[aa + ad + 1] = LIGHT_GRAY_TERACOTTA;
				}
			}
		}
	}

	protected BlockState method_15207(int i, int j, int k) {
		int l = (int)Math.round(this.field_15619.sample((double)i / 512.0, (double)k / 512.0) * 2.0);
		return this.layerBlocks[(j + l + 64) % 64];
	}
}
