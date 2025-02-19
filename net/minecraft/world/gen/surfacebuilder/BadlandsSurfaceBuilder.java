package net.minecraft.world.gen.surfacebuilder;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;

public class BadlandsSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
	protected static final int field_31699 = 15;
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
		int m,
		long n,
		TernarySurfaceConfig ternarySurfaceConfig
	) {
		int o = i & 15;
		int p = j & 15;
		BlockState blockState3 = WHITE_TERRACOTTA;
		SurfaceConfig surfaceConfig = biome.getGenerationSettings().getSurfaceConfig();
		BlockState blockState4 = surfaceConfig.getUnderMaterial();
		BlockState blockState5 = surfaceConfig.getTopMaterial();
		BlockState blockState6 = blockState4;
		int q = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int r = -1;
		boolean bl2 = false;
		int s = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int t = k; t >= m; t--) {
			if (s < 15) {
				mutable.set(o, t, p);
				BlockState blockState7 = chunk.getBlockState(mutable);
				if (blockState7.isAir()) {
					r = -1;
				} else if (blockState7.isOf(blockState.getBlock())) {
					if (r == -1) {
						bl2 = false;
						if (q <= 0) {
							blockState3 = Blocks.AIR.getDefaultState();
							blockState6 = blockState;
						} else if (t >= l - 4 && t <= l + 1) {
							blockState3 = WHITE_TERRACOTTA;
							blockState6 = blockState4;
						}

						if (t < l && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						r = q + Math.max(0, t - l);
						if (t >= l - 1) {
							if (t <= l + 3 + q) {
								chunk.setBlockState(mutable, blockState5, false);
								bl2 = true;
							} else {
								BlockState blockState8;
								if (t < 64 || t > 127) {
									blockState8 = ORANGE_TERRACOTTA;
								} else if (bl) {
									blockState8 = TERRACOTTA;
								} else {
									blockState8 = this.calculateLayerBlockState(i, t, j);
								}

								chunk.setBlockState(mutable, blockState8, false);
							}
						} else {
							chunk.setBlockState(mutable, blockState6, false);
							if (blockState6.isOf(Blocks.WHITE_TERRACOTTA)
								|| blockState6.isOf(Blocks.ORANGE_TERRACOTTA)
								|| blockState6.isOf(Blocks.MAGENTA_TERRACOTTA)
								|| blockState6.isOf(Blocks.LIGHT_BLUE_TERRACOTTA)
								|| blockState6.isOf(Blocks.YELLOW_TERRACOTTA)
								|| blockState6.isOf(Blocks.LIME_TERRACOTTA)
								|| blockState6.isOf(Blocks.PINK_TERRACOTTA)
								|| blockState6.isOf(Blocks.GRAY_TERRACOTTA)
								|| blockState6.isOf(Blocks.LIGHT_GRAY_TERRACOTTA)
								|| blockState6.isOf(Blocks.CYAN_TERRACOTTA)
								|| blockState6.isOf(Blocks.PURPLE_TERRACOTTA)
								|| blockState6.isOf(Blocks.BLUE_TERRACOTTA)
								|| blockState6.isOf(Blocks.BROWN_TERRACOTTA)
								|| blockState6.isOf(Blocks.GREEN_TERRACOTTA)
								|| blockState6.isOf(Blocks.RED_TERRACOTTA)
								|| blockState6.isOf(Blocks.BLACK_TERRACOTTA)) {
								chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
							}
						}
					} else if (r > 0) {
						r--;
						if (bl2) {
							chunk.setBlockState(mutable, ORANGE_TERRACOTTA, false);
						} else {
							chunk.setBlockState(mutable, this.calculateLayerBlockState(i, t, j), false);
						}
					}

					s++;
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
