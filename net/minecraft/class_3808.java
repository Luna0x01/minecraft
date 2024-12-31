package net.minecraft;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.gen.NoiseGenerator;

public class class_3808 extends class_3782<class_3807> {
	protected static final BlockState field_19024 = Blocks.AIR.getDefaultState();
	protected static final BlockState field_19025 = Blocks.NETHERRACK.getDefaultState();
	protected static final BlockState field_19026 = Blocks.LAVA.getDefaultState();
	private final NoiseGenerator field_19027;
	private final NoiseGenerator field_19028;
	private final NoiseGenerator field_19029;
	private final NoiseGenerator field_19030;
	private final NoiseGenerator field_19031;
	private final NoiseGenerator field_19032;
	private final class_3807 field_19033;
	private final BlockState field_19034;
	private final BlockState field_19035;

	public class_3808(World world, SingletonBiomeSource singletonBiomeSource, class_3807 arg) {
		super(world, singletonBiomeSource);
		this.field_19033 = arg;
		this.field_19034 = this.field_19033.method_17231();
		this.field_19035 = this.field_19033.method_17232();
		class_3812 lv = new class_3812(this.field_18839);
		this.field_19027 = new NoiseGenerator(lv, 16);
		this.field_19028 = new NoiseGenerator(lv, 16);
		this.field_19029 = new NoiseGenerator(lv, 8);
		lv.method_17285(1048);
		this.field_19030 = new NoiseGenerator(lv, 4);
		this.field_19031 = new NoiseGenerator(lv, 10);
		this.field_19032 = new NoiseGenerator(lv, 16);
		world.setSeaLevel(63);
	}

	public void method_17253(int i, int j, class_3781 arg) {
		int k = 4;
		int l = this.field_18838.method_8483() / 2 + 1;
		int m = 5;
		int n = 17;
		int o = 5;
		double[] ds = this.method_17252(i * 4, 0, j * 4, 5, 17, 5);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int p = 0; p < 4; p++) {
			for (int q = 0; q < 4; q++) {
				for (int r = 0; r < 16; r++) {
					double d = 0.125;
					double e = ds[((p + 0) * 5 + q + 0) * 17 + r + 0];
					double f = ds[((p + 0) * 5 + q + 1) * 17 + r + 0];
					double g = ds[((p + 1) * 5 + q + 0) * 17 + r + 0];
					double h = ds[((p + 1) * 5 + q + 1) * 17 + r + 0];
					double s = (ds[((p + 0) * 5 + q + 0) * 17 + r + 1] - e) * 0.125;
					double t = (ds[((p + 0) * 5 + q + 1) * 17 + r + 1] - f) * 0.125;
					double u = (ds[((p + 1) * 5 + q + 0) * 17 + r + 1] - g) * 0.125;
					double v = (ds[((p + 1) * 5 + q + 1) * 17 + r + 1] - h) * 0.125;

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
								BlockState blockState = field_19024;
								if (r * 8 + w < l) {
									blockState = this.field_19035;
								}

								if (ae > 0.0) {
									blockState = this.field_19034;
								}

								int ah = ac + p * 4;
								int ai = w + r * 8;
								int aj = ag + q * 4;
								arg.method_16994(mutable.setPosition(ah, ai, aj), blockState, false);
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

	@Override
	protected void method_17028(class_3781 arg, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = arg.method_3920().getActualX();
		int j = arg.method_3920().getActualZ();

		for (BlockPos blockPos : BlockPos.iterate(i, 0, j, i + 16, 0, j + 16)) {
			for (int k = 127; k > 122; k--) {
				if (k >= 127 - random.nextInt(5)) {
					arg.method_16994(mutable.setPosition(blockPos.getX(), k, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
				}
			}

			for (int l = 4; l >= 0; l--) {
				if (l <= random.nextInt(5)) {
					arg.method_16994(mutable.setPosition(blockPos.getX(), l, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
				}
			}
		}
	}

	@Override
	public double[] method_17027(int i, int j) {
		double d = 0.03125;
		return this.field_19030.method_122(i << 4, j << 4, 0, 16, 16, 1, 0.0625, 0.0625, 0.0625);
	}

	@Override
	public void method_17016(class_3781 arg) {
		ChunkPos chunkPos = arg.method_3920();
		int i = chunkPos.x;
		int j = chunkPos.z;
		class_3812 lv = new class_3812();
		lv.method_17286(i, j);
		Biome[] biomes = this.field_18840.method_11540(i * 16, j * 16, 16, 16);
		arg.method_16999(biomes);
		this.method_17253(i, j, arg);
		this.method_17029(arg, biomes, lv, this.field_18838.method_8483());
		this.method_17028(arg, lv);
		arg.method_17000(class_3804.class_3805.WORLD_SURFACE_WG, class_3804.class_3805.OCEAN_FLOOR_WG);
		arg.method_16990(class_3786.BASE);
	}

	private double[] method_17252(int i, int j, int k, int l, int m, int n) {
		double[] ds = new double[l * m * n];
		double d = 684.412;
		double e = 2053.236;
		double[] es = this.field_19031.method_122(i, j, k, l, 1, n, 1.0, 0.0, 1.0);
		double[] fs = this.field_19032.method_122(i, j, k, l, 1, n, 100.0, 0.0, 100.0);
		double[] gs = this.field_19029.method_122(i, j, k, l, m, n, 8.555150000000001, 34.2206, 8.555150000000001);
		double[] hs = this.field_19027.method_122(i, j, k, l, m, n, 684.412, 2053.236, 684.412);
		double[] is = this.field_19028.method_122(i, j, k, l, m, n, 684.412, 2053.236, 684.412);
		double[] js = new double[m];

		for (int o = 0; o < m; o++) {
			js[o] = Math.cos((double)o * Math.PI * 6.0 / (double)m) * 2.0;
			double f = (double)o;
			if (o > m / 2) {
				f = (double)(m - 1 - o);
			}

			if (f < 4.0) {
				f = 4.0 - f;
				js[o] -= f * f * f * 10.0;
			}
		}

		int p = 0;

		for (int q = 0; q < l; q++) {
			for (int r = 0; r < n; r++) {
				double g = 0.0;

				for (int s = 0; s < m; s++) {
					double h = js[s];
					double t = hs[p] / 512.0;
					double u = is[p] / 512.0;
					double v = (gs[p] / 10.0 + 1.0) / 2.0;
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
						w = w * (1.0 - z) - 10.0 * z;
					}

					if ((double)s < 0.0) {
						double aa = (0.0 - (double)s) / 4.0;
						aa = MathHelper.clamp(aa, 0.0, 1.0);
						w = w * (1.0 - aa) - 10.0 * aa;
					}

					ds[p] = w;
					p++;
				}
			}
		}

		return ds;
	}

	@Override
	public void method_17023(class_4441 arg) {
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		if (category == EntityCategory.MONSTER) {
			if (class_3844.field_19192.method_17435(this.field_18838, pos)) {
				return class_3844.field_19192.method_17347();
			}

			if (class_3844.field_19192.method_17434(this.field_18838, pos) && this.field_18838.getBlockState(pos.down()).getBlock() == Blocks.NETHER_BRICKS) {
				return class_3844.field_19192.method_17347();
			}
		}

		Biome biome = this.field_18838.method_8577(pos);
		return biome.getSpawnEntries(category);
	}

	@Override
	public int method_17014(World world, boolean bl, boolean bl2) {
		return 0;
	}

	public class_3807 method_17013() {
		return this.field_19033;
	}

	@Override
	public int method_17025() {
		return this.field_18838.method_8483() + 1;
	}

	@Override
	public int method_17026() {
		return 128;
	}
}
