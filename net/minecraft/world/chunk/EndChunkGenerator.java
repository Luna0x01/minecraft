package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.class_3781;
import net.minecraft.class_3782;
import net.minecraft.class_3786;
import net.minecraft.class_3804;
import net.minecraft.class_3811;
import net.minecraft.class_3812;
import net.minecraft.class_4441;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.gen.NoiseGenerator;

public class EndChunkGenerator extends class_3782<class_3811> {
	protected static final BlockState AIR = Blocks.AIR.getDefaultState();
	private final NoiseGenerator field_4856;
	private final NoiseGenerator field_4857;
	private final NoiseGenerator field_4858;
	private final NoiseGenerator field_12973;
	private final NoiseGenerator field_12974;
	private final PerlinNoiseGenerator field_19063;
	private final BlockPos field_15189;
	private final class_3811 field_19064;
	private final BlockState field_19065;
	private final BlockState field_19066;

	public EndChunkGenerator(IWorld iWorld, SingletonBiomeSource singletonBiomeSource, class_3811 arg) {
		super(iWorld, singletonBiomeSource);
		this.field_19064 = arg;
		this.field_19065 = this.field_19064.method_17231();
		this.field_19066 = this.field_19064.method_17232();
		this.field_15189 = arg.method_17280();
		class_3812 lv = new class_3812(this.field_18839);
		this.field_4856 = new NoiseGenerator(lv, 16);
		this.field_4857 = new NoiseGenerator(lv, 16);
		this.field_4858 = new NoiseGenerator(lv, 8);
		this.field_12973 = new NoiseGenerator(lv, 10);
		this.field_12974 = new NoiseGenerator(lv, 16);
		lv.method_17285(262);
		this.field_19063 = new PerlinNoiseGenerator(new class_3812(this.field_18839), 4);
	}

	public void method_17282(int i, int j, class_3781 arg) {
		int k = 2;
		int l = 3;
		int m = 33;
		int n = 3;
		double[] ds = this.method_17281(i * 2, 0, j * 2, 3, 33, 3);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = 0; o < 2; o++) {
			for (int p = 0; p < 2; p++) {
				for (int q = 0; q < 32; q++) {
					double d = 0.25;
					double e = ds[((o + 0) * 3 + p + 0) * 33 + q + 0];
					double f = ds[((o + 0) * 3 + p + 1) * 33 + q + 0];
					double g = ds[((o + 1) * 3 + p + 0) * 33 + q + 0];
					double h = ds[((o + 1) * 3 + p + 1) * 33 + q + 0];
					double r = (ds[((o + 0) * 3 + p + 0) * 33 + q + 1] - e) * 0.25;
					double s = (ds[((o + 0) * 3 + p + 1) * 33 + q + 1] - f) * 0.25;
					double t = (ds[((o + 1) * 3 + p + 0) * 33 + q + 1] - g) * 0.25;
					double u = (ds[((o + 1) * 3 + p + 1) * 33 + q + 1] - h) * 0.25;

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
									blockState = this.field_19065;
								}

								int ag = ab + o * 8;
								int ah = v + q * 4;
								int ai = af + p * 8;
								arg.method_16994(mutable.setPosition(ag, ah, ai), blockState, false);
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

	@Override
	public void method_17016(class_3781 arg) {
		ChunkPos chunkPos = arg.method_3920();
		int i = chunkPos.x;
		int j = chunkPos.z;
		class_3812 lv = new class_3812();
		lv.method_17286(i, j);
		Biome[] biomes = this.field_18840.method_11540(i * 16, j * 16, 16, 16);
		arg.method_16999(biomes);
		this.method_17282(i, j, arg);
		this.method_17029(arg, biomes, lv, 0);
		arg.method_17000(class_3804.class_3805.WORLD_SURFACE_WG, class_3804.class_3805.OCEAN_FLOOR_WG);
		arg.method_16990(class_3786.BASE);
	}

	private double[] method_17281(int i, int j, int k, int l, int m, int n) {
		double[] ds = new double[l * m * n];
		double d = 684.412;
		double e = 684.412;
		d *= 2.0;
		double[] es = this.field_4858.method_122(i, j, k, l, m, n, d / 80.0, 4.277575000000001, d / 80.0);
		double[] fs = this.field_4856.method_122(i, j, k, l, m, n, d, 684.412, d);
		double[] gs = this.field_4857.method_122(i, j, k, l, m, n, d, 684.412, d);
		int o = i / 2;
		int p = k / 2;
		int q = 0;

		for (int r = 0; r < l; r++) {
			for (int s = 0; s < n; s++) {
				float f = this.field_18840.method_16482(o, p, r, s);

				for (int t = 0; t < m; t++) {
					double g = fs[q] / 512.0;
					double h = gs[q] / 512.0;
					double u = (es[q] / 10.0 + 1.0) / 2.0;
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
						v = v * (1.0 - z) - 3000.0 * z;
					}

					int var36 = 8;
					if (t < var36) {
						double aa = (double)((float)(var36 - t) / ((float)var36 - 1.0F));
						v = v * (1.0 - aa) - 30.0 * aa;
					}

					ds[q] = v;
					q++;
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
		return this.field_18838.method_8577(pos).getSpawnEntries(category);
	}

	public BlockPos method_17283() {
		return this.field_15189;
	}

	@Override
	public int method_17014(World world, boolean bl, boolean bl2) {
		return 0;
	}

	public class_3811 method_17013() {
		return this.field_19064;
	}

	@Override
	public double[] method_17027(int i, int j) {
		double d = 0.03125;
		return this.field_19063.method_6580((double)(i << 4), (double)(j << 4), 16, 16, 0.0625, 0.0625, 1.0);
	}

	@Override
	public int method_17025() {
		return 50;
	}
}
