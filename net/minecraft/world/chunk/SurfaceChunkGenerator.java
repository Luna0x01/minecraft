package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.class_3781;
import net.minecraft.class_3782;
import net.minecraft.class_3786;
import net.minecraft.class_3804;
import net.minecraft.class_3809;
import net.minecraft.class_3810;
import net.minecraft.class_3812;
import net.minecraft.class_3844;
import net.minecraft.class_3906;
import net.minecraft.class_4441;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseGenerator;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SurfaceChunkGenerator extends class_3782<class_3809> {
	private static final Logger field_19056 = LogManager.getLogger();
	private final NoiseGenerator field_4832;
	private final NoiseGenerator field_4833;
	private final NoiseGenerator field_4834;
	private final PerlinNoiseGenerator field_7514;
	private final class_3809 field_19057;
	private final NoiseGenerator field_4821;
	private final NoiseGenerator field_4822;
	private final LevelGeneratorType type;
	private final float[] field_7517;
	private final class_3810 field_19058 = new class_3810();
	private final BlockState field_19059;
	private final BlockState field_19060;

	public SurfaceChunkGenerator(IWorld iWorld, SingletonBiomeSource singletonBiomeSource, class_3809 arg) {
		super(iWorld, singletonBiomeSource);
		this.type = iWorld.method_3588().getGeneratorType();
		class_3812 lv = new class_3812(this.field_18839);
		this.field_4832 = new NoiseGenerator(lv, 16);
		this.field_4833 = new NoiseGenerator(lv, 16);
		this.field_4834 = new NoiseGenerator(lv, 8);
		this.field_7514 = new PerlinNoiseGenerator(lv, 4);
		this.field_4821 = new NoiseGenerator(lv, 10);
		this.field_4822 = new NoiseGenerator(lv, 16);
		this.field_7517 = new float[25];

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				float f = 10.0F / MathHelper.sqrt((float)(i * i + j * j) + 0.2F);
				this.field_7517[i + 2 + (j + 2) * 5] = f;
			}
		}

		this.field_19057 = arg;
		this.field_19059 = this.field_19057.method_17231();
		this.field_19060 = this.field_19057.method_17232();
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
		this.method_17275(i, j, arg);
		arg.method_17000(class_3804.class_3805.WORLD_SURFACE_WG, class_3804.class_3805.OCEAN_FLOOR_WG);
		this.method_17029(arg, biomes, lv, this.field_18838.method_8483());
		this.method_17028(arg, lv);
		arg.method_17000(class_3804.class_3805.WORLD_SURFACE_WG, class_3804.class_3805.OCEAN_FLOOR_WG);
		arg.method_16990(class_3786.BASE);
	}

	@Override
	public void method_17023(class_4441 arg) {
		int i = arg.method_21286();
		int j = arg.method_21288();
		Biome biome = arg.method_16347(i, j).method_17007()[0];
		class_3812 lv = new class_3812();
		lv.method_17288(arg.method_3581(), i << 4, j << 4);
		MobSpawnerHelper.method_16406(arg, biome, i, j, lv);
	}

	public void method_17275(int i, int j, class_3781 arg) {
		Biome[] biomes = this.field_18840.method_16476(arg.method_3920().x * 4 - 2, arg.method_3920().z * 4 - 2, 10, 10);
		double[] ds = new double[825];
		this.method_17276(biomes, arg.method_3920().x * 4, 0, arg.method_3920().z * 4, ds);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

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
					double e = ds[o + s];
					double f = ds[p + s];
					double g = ds[q + s];
					double h = ds[r + s];
					double t = (ds[o + s + 1] - e) * 0.125;
					double u = (ds[p + s + 1] - f) * 0.125;
					double v = (ds[q + s + 1] - g) * 0.125;
					double w = (ds[r + s + 1] - h) * 0.125;

					for (int x = 0; x < 8; x++) {
						double y = 0.25;
						double z = e;
						double aa = f;
						double ab = (g - e) * 0.25;
						double ac = (h - f) * 0.25;

						for (int ad = 0; ad < 4; ad++) {
							double ae = 0.25;
							double ag = (aa - z) * 0.25;
							double af = z - ag;

							for (int ah = 0; ah < 4; ah++) {
								mutable.setPosition(k * 4 + ad, s * 8 + x, n * 4 + ah);
								if ((af += ag) > 0.0) {
									arg.method_16994(mutable, this.field_19059, false);
								} else if (s * 8 + x < this.field_19057.method_17271()) {
									arg.method_16994(mutable, this.field_19060, false);
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

	private void method_17276(Biome[] biomes, int i, int j, int k, double[] ds) {
		double[] es = this.field_4822.method_121(i, k, 5, 5, this.field_19057.method_17272(), this.field_19057.method_17273(), this.field_19057.method_17274());
		float f = this.field_19057.method_17255();
		float g = this.field_19057.method_17256();
		double[] fs = this.field_4834
			.method_122(
				i,
				j,
				k,
				5,
				33,
				5,
				(double)(f / this.field_19057.method_17257()),
				(double)(g / this.field_19057.method_17258()),
				(double)(f / this.field_19057.method_17259())
			);
		double[] gs = this.field_4832.method_122(i, j, k, 5, 33, 5, (double)f, (double)g, (double)f);
		double[] hs = this.field_4833.method_122(i, j, k, 5, 33, 5, (double)f, (double)g, (double)f);
		int l = 0;
		int m = 0;

		for (int n = 0; n < 5; n++) {
			for (int o = 0; o < 5; o++) {
				float h = 0.0F;
				float p = 0.0F;
				float q = 0.0F;
				int r = 2;
				Biome biome = biomes[n + 2 + (o + 2) * 10];

				for (int s = -2; s <= 2; s++) {
					for (int t = -2; t <= 2; t++) {
						Biome biome2 = biomes[n + s + 2 + (o + t + 2) * 10];
						float u = this.field_19057.method_17260() + biome2.getDepth() * this.field_19057.method_17261();
						float v = this.field_19057.method_17262() + biome2.getVariationModifier() * this.field_19057.method_17263();
						if (this.type == LevelGeneratorType.AMPLIFIED && u > 0.0F) {
							u = 1.0F + u * 2.0F;
							v = 1.0F + v * 4.0F;
						}

						float w = this.field_7517[s + 2 + (t + 2) * 5] / (u + 2.0F);
						if (biome2.getDepth() > biome.getDepth()) {
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
				double d = es[m] / 8000.0;
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
				e = e * this.field_19057.method_17264() / 8.0;
				double y = this.field_19057.method_17264() + e * 4.0;

				for (int z = 0; z < 33; z++) {
					double aa = ((double)z - y) * this.field_19057.method_17265() * 128.0 / 256.0 / x;
					if (aa < 0.0) {
						aa *= 4.0;
					}

					double ab = gs[l] / this.field_19057.method_17266();
					double ac = hs[l] / this.field_19057.method_17267();
					double ad = (fs[l] / 10.0 + 1.0) / 2.0;
					double ae = MathHelper.clampedLerp(ab, ac, ad) - aa;
					if (z > 29) {
						double af = (double)((float)(z - 29) / 3.0F);
						ae = ae * (1.0 - af) - 10.0 * af;
					}

					ds[l] = ae;
					l++;
				}
			}
		}
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		Biome biome = this.field_18838.method_8577(pos);
		if (category == EntityCategory.MONSTER && ((class_3906)class_3844.field_19188).method_17437(this.field_18838, pos)) {
			return class_3844.field_19188.method_17347();
		} else {
			return category == EntityCategory.MONSTER && class_3844.field_19190.method_17434(this.field_18838, pos)
				? class_3844.field_19190.method_17347()
				: biome.getSpawnEntries(category);
		}
	}

	@Override
	public int method_17014(World world, boolean bl, boolean bl2) {
		int i = 0;
		return i + this.field_19058.method_17278(world, bl, bl2);
	}

	public class_3809 method_17013() {
		return this.field_19057;
	}

	@Override
	public double[] method_17027(int i, int j) {
		double d = 0.03125;
		return this.field_7514.method_6580((double)(i << 4), (double)(j << 4), 16, 16, 0.0625, 0.0625, 1.0);
	}

	@Override
	public int method_17025() {
		return this.field_18838.method_8483() + 1;
	}
}
