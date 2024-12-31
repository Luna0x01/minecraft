package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public class BadlandsSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
	private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
	private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();
	private static final BlockState YELLOW_TERRACOTTA = Blocks.YELLOW_TERRACOTTA.getDefaultState();
	private static final BlockState BROWN_TERRACOTTA = Blocks.BROWN_TERRACOTTA.getDefaultState();
	private static final BlockState RED_TERRACOTTA = Blocks.RED_TERRACOTTA.getDefaultState();
	private static final BlockState LIGHT_GRAY_TERRACOTTA = Blocks.LIGHT_GRAY_TERRACOTTA.getDefaultState();
	protected BlockState[] layerBlocks;
	protected long seed;
	protected OctaveSimplexNoiseSampler heightCutoffNoise;
	protected OctaveSimplexNoiseSampler heightNoise;
	protected OctaveSimplexNoiseSampler layerNoise;

	public BadlandsSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
		super(codec);
	}

	public void generate(
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
		BlockState blockState3 = WHITE_TERRACOTTA;
		SurfaceConfig surfaceConfig = biome.getGenerationSettings().getSurfaceConfig();
		BlockState blockState4 = surfaceConfig.getUnderMaterial();
		BlockState blockState5 = surfaceConfig.getTopMaterial();
		BlockState blockState6 = blockState4;
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;
		int r = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int s = k; s >= 0; s--) {
			if (r < 15) {
				mutable.set(n, s, o);
				BlockState blockState7 = chunk.getBlockState(mutable);
				if (blockState7.isAir()) {
					q = -1;
				} else if (blockState7.isOf(blockState.getBlock())) {
					if (q == -1) {
						bl2 = false;
						if (p <= 0) {
							blockState3 = Blocks.AIR.getDefaultState();
							blockState6 = blockState;
						} else if (s >= l - 4 && s <= l + 1) {
							blockState3 = WHITE_TERRACOTTA;
							blockState6 = blockState4;
						}

						if (s < l && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						q = p + Math.max(0, s - l);
						if (s >= l - 1) {
							if (s <= l + 3 + p) {
								chunk.setBlockState(mutable, blockState5, false);
								bl2 = true;
							} else {
								BlockState blockState8;
								if (s < 64 || s > 127) {
									blockState8 = ORANGE_TERRACOTTA;
								} else if (bl) {
									blockState8 = TERRACOTTA;
								} else {
									blockState8 = this.calculateLayerBlockState(i, s, j);
								}

								chunk.setBlockState(mutable, blockState8, false);
							}
						} else {
							chunk.setBlockState(mutable, blockState6, false);
							Block block = blockState6.getBlock();
							if (block == Blocks.WHITE_TERRACOTTA
								|| block == Blocks.ORANGE_TERRACOTTA
								|| block == Blocks.MAGENTA_TERRACOTTA
								|| block == Blocks.LIGHT_BLUE_TERRACOTTA
								|| block == Blocks.YELLOW_TERRACOTTA
								|| block == Blocks.LIME_TERRACOTTA
								|| block == Blocks.PINK_TERRACOTTA
								|| block == Blocks.GRAY_TERRACOTTA
								|| block == Blocks.LIGHT_GRAY_TERRACOTTA
								|| block == Blocks.CYAN_TERRACOTTA
								|| block == Blocks.PURPLE_TERRACOTTA
								|| block == Blocks.BLUE_TERRACOTTA
								|| block == Blocks.BROWN_TERRACOTTA
								|| block == Blocks.GREEN_TERRACOTTA
								|| block == Blocks.RED_TERRACOTTA
								|| block == Blocks.BLACK_TERRACOTTA) {
								chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
							}
						}
					} else if (q > 0) {
						q--;
						if (bl2) {
							chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
						} else {
							chunk.setBlockState(mutable, this.calculateLayerBlockState(i, s, j), false);
						}
					}

					r++;
				}
			}
		}
	}

	@Override
	public void initSeed(long seed) {
		if (this.seed != seed || this.layerBlocks == null) {
			this.initLayerBlocks(seed);
		}

		if (this.seed != seed || this.heightCutoffNoise == null || this.heightNoise == null) {
			ChunkRandom chunkRandom = new ChunkRandom(seed);
			this.heightCutoffNoise = new OctaveSimplexNoiseSampler(chunkRandom, IntStream.rangeClosed(-3, 0));
			this.heightNoise = new OctaveSimplexNoiseSampler(chunkRandom, ImmutableList.of(0));
		}

		this.seed = seed;
	}

	protected void initLayerBlocks(long seed) {
		this.layerBlocks = new BlockState[64];
		Arrays.fill(this.layerBlocks, TERRACOTTA);
		ChunkRandom chunkRandom = new ChunkRandom(seed);
		this.layerNoise = new OctaveSimplexNoiseSampler(chunkRandom, ImmutableList.of(0));

		for (int i = 0; i < 64; i++) {
			i += chunkRandom.nextInt(5) + 1;
			if (i < 64) {
				this.layerBlocks[i] = ORANGE_TERRACOTTA;
			}
		}

		int j = chunkRandom.nextInt(4) + 2;

		for (int k = 0; k < j; k++) {
			int l = chunkRandom.nextInt(3) + 1;
			int m = chunkRandom.nextInt(64);

			for (int n = 0; m + n < 64 && n < l; n++) {
				this.layerBlocks[m + n] = YELLOW_TERRACOTTA;
			}
		}

		int o = chunkRandom.nextInt(4) + 2;

		for (int p = 0; p < o; p++) {
			int q = chunkRandom.nextInt(3) + 2;
			int r = chunkRandom.nextInt(64);

			for (int s = 0; r + s < 64 && s < q; s++) {
				this.layerBlocks[r + s] = BROWN_TERRACOTTA;
			}
		}

		int t = chunkRandom.nextInt(4) + 2;

		for (int u = 0; u < t; u++) {
			int v = chunkRandom.nextInt(3) + 1;
			int w = chunkRandom.nextInt(64);

			for (int x = 0; w + x < 64 && x < v; x++) {
				this.layerBlocks[w + x] = RED_TERRACOTTA;
			}
		}

		int y = chunkRandom.nextInt(3) + 3;
		int z = 0;

		for (int aa = 0; aa < y; aa++) {
			int ab = 1;
			z += chunkRandom.nextInt(16) + 4;

			for (int ac = 0; z + ac < 64 && ac < 1; ac++) {
				this.layerBlocks[z + ac] = WHITE_TERRACOTTA;
				if (z + ac > 1 && chunkRandom.nextBoolean()) {
					this.layerBlocks[z + ac - 1] = LIGHT_GRAY_TERRACOTTA;
				}

				if (z + ac < 63 && chunkRandom.nextBoolean()) {
					this.layerBlocks[z + ac + 1] = LIGHT_GRAY_TERRACOTTA;
				}
			}
		}
	}

	protected BlockState calculateLayerBlockState(int x, int y, int z) {
		int i = (int)Math.round(this.layerNoise.sample((double)x / 512.0, (double)z / 512.0, false) * 2.0);
		return this.layerBlocks[(y + i + 64) % 64];
	}
}
