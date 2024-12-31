package net.minecraft;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;
import net.minecraft.world.biome.Biome;

public class class_4007 implements class_4012<class_4013> {
	private static final BlockState field_19458 = Blocks.WHITE_TERRACOTTA.getDefaultState();
	private static final BlockState field_19459 = Blocks.ORANGE_TERRACOTTA.getDefaultState();
	private static final BlockState field_19460 = Blocks.TERRACOTTA.getDefaultState();
	private static final BlockState field_19461 = Blocks.YELLOW_TERRACOTTA.getDefaultState();
	private static final BlockState field_19462 = Blocks.BROWN_TERRACOTTA.getDefaultState();
	private static final BlockState field_19463 = Blocks.RED_TERRACOTTA.getDefaultState();
	private static final BlockState field_19464 = Blocks.LIGHT_GRAY_TERRACOTTA.getDefaultState();
	protected BlockState[] field_19453;
	protected long field_19454;
	protected PerlinNoiseGenerator field_19455;
	protected PerlinNoiseGenerator field_19456;
	protected PerlinNoiseGenerator field_19457;

	public void method_17718(
		Random random, class_3781 arg, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, class_4013 arg2
	) {
		int n = i & 15;
		int o = j & 15;
		BlockState blockState3 = field_19458;
		BlockState blockState4 = biome.method_16450().method_17721();
		int p = (int)(d / 3.0 + 3.0 + random.nextDouble() * 0.25);
		boolean bl = Math.cos(d / 3.0 * Math.PI) > 0.0;
		int q = -1;
		boolean bl2 = false;
		int r = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int s = k; s >= 0; s--) {
			if (r < 15) {
				mutable.setPosition(n, s, o);
				BlockState blockState5 = arg.getBlockState(mutable);
				if (blockState5.isAir()) {
					q = -1;
				} else if (blockState5.getBlock() == blockState.getBlock()) {
					if (q == -1) {
						bl2 = false;
						if (p <= 0) {
							blockState3 = Blocks.AIR.getDefaultState();
							blockState4 = blockState;
						} else if (s >= l - 4 && s <= l + 1) {
							blockState3 = field_19458;
							blockState4 = biome.method_16450().method_17721();
						}

						if (s < l && (blockState3 == null || blockState3.isAir())) {
							blockState3 = blockState2;
						}

						q = p + Math.max(0, s - l);
						if (s >= l - 1) {
							if (s <= l + 3 + p) {
								arg.method_16994(mutable, biome.method_16450().method_17720(), false);
								bl2 = true;
							} else {
								BlockState blockState6;
								if (s < 64 || s > 127) {
									blockState6 = field_19459;
								} else if (bl) {
									blockState6 = field_19460;
								} else {
									blockState6 = this.method_17707(i, s, j);
								}

								arg.method_16994(mutable, blockState6, false);
							}
						} else {
							arg.method_16994(mutable, blockState4, false);
							Block block = blockState4.getBlock();
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
								arg.method_16994(mutable, field_19459, false);
							}
						}
					} else if (q > 0) {
						q--;
						if (bl2) {
							arg.method_16994(mutable, field_19459, false);
						} else {
							arg.method_16994(mutable, this.method_17707(i, s, j), false);
						}
					}

					r++;
				}
			}
		}
	}

	@Override
	public void method_17717(long l) {
		if (this.field_19454 != l || this.field_19453 == null) {
			this.method_17709(l);
		}

		if (this.field_19454 != l || this.field_19455 == null || this.field_19456 == null) {
			Random random = new class_3812(l);
			this.field_19455 = new PerlinNoiseGenerator(random, 4);
			this.field_19456 = new PerlinNoiseGenerator(random, 1);
		}

		this.field_19454 = l;
	}

	protected void method_17709(long l) {
		this.field_19453 = new BlockState[64];
		Arrays.fill(this.field_19453, field_19460);
		Random random = new class_3812(l);
		this.field_19457 = new PerlinNoiseGenerator(random, 1);

		for (int i = 0; i < 64; i++) {
			i += random.nextInt(5) + 1;
			if (i < 64) {
				this.field_19453[i] = field_19459;
			}
		}

		int j = random.nextInt(4) + 2;

		for (int k = 0; k < j; k++) {
			int m = random.nextInt(3) + 1;
			int n = random.nextInt(64);

			for (int o = 0; n + o < 64 && o < m; o++) {
				this.field_19453[n + o] = field_19461;
			}
		}

		int p = random.nextInt(4) + 2;

		for (int q = 0; q < p; q++) {
			int r = random.nextInt(3) + 2;
			int s = random.nextInt(64);

			for (int t = 0; s + t < 64 && t < r; t++) {
				this.field_19453[s + t] = field_19462;
			}
		}

		int u = random.nextInt(4) + 2;

		for (int v = 0; v < u; v++) {
			int w = random.nextInt(3) + 1;
			int x = random.nextInt(64);

			for (int y = 0; x + y < 64 && y < w; y++) {
				this.field_19453[x + y] = field_19463;
			}
		}

		int z = random.nextInt(3) + 3;
		int aa = 0;

		for (int ab = 0; ab < z; ab++) {
			int ac = 1;
			aa += random.nextInt(16) + 4;

			for (int ad = 0; aa + ad < 64 && ad < 1; ad++) {
				this.field_19453[aa + ad] = field_19458;
				if (aa + ad > 1 && random.nextBoolean()) {
					this.field_19453[aa + ad - 1] = field_19464;
				}

				if (aa + ad < 63 && random.nextBoolean()) {
					this.field_19453[aa + ad + 1] = field_19464;
				}
			}
		}
	}

	protected BlockState method_17707(int i, int j, int k) {
		int l = (int)Math.round(this.field_19457.noise((double)i / 512.0, (double)k / 512.0) * 2.0);
		return this.field_19453[(j + l + 64) % 64];
	}
}
