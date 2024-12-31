package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class OreFeature extends Feature<OreFeatureConfig> {
	public OreFeature(Function<Dynamic<?>, ? extends OreFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, OreFeatureConfig oreFeatureConfig
	) {
		float f = random.nextFloat() * (float) Math.PI;
		float g = (float)oreFeatureConfig.size / 8.0F;
		int i = MathHelper.ceil(((float)oreFeatureConfig.size / 16.0F * 2.0F + 1.0F) / 2.0F);
		double d = (double)((float)blockPos.getX() + MathHelper.sin(f) * g);
		double e = (double)((float)blockPos.getX() - MathHelper.sin(f) * g);
		double h = (double)((float)blockPos.getZ() + MathHelper.cos(f) * g);
		double j = (double)((float)blockPos.getZ() - MathHelper.cos(f) * g);
		int k = 2;
		double l = (double)(blockPos.getY() + random.nextInt(3) - 2);
		double m = (double)(blockPos.getY() + random.nextInt(3) - 2);
		int n = blockPos.getX() - MathHelper.ceil(g) - i;
		int o = blockPos.getY() - 2 - i;
		int p = blockPos.getZ() - MathHelper.ceil(g) - i;
		int q = 2 * (MathHelper.ceil(g) + i);
		int r = 2 * (2 + i);

		for (int s = n; s <= n + q; s++) {
			for (int t = p; t <= p + q; t++) {
				if (o <= iWorld.getTopY(Heightmap.Type.field_13195, s, t)) {
					return this.generateVeinPart(iWorld, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
				}
			}
		}

		return false;
	}

	protected boolean generateVeinPart(
		IWorld iWorld,
		Random random,
		OreFeatureConfig oreFeatureConfig,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		int j,
		int k,
		int l,
		int m,
		int n
	) {
		int o = 0;
		BitSet bitSet = new BitSet(m * n * m);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		double[] ds = new double[oreFeatureConfig.size * 4];

		for (int p = 0; p < oreFeatureConfig.size; p++) {
			float q = (float)p / (float)oreFeatureConfig.size;
			double r = MathHelper.lerp((double)q, d, e);
			double s = MathHelper.lerp((double)q, h, i);
			double t = MathHelper.lerp((double)q, f, g);
			double u = random.nextDouble() * (double)oreFeatureConfig.size / 16.0;
			double v = ((double)(MathHelper.sin((float) Math.PI * q) + 1.0F) * u + 1.0) / 2.0;
			ds[p * 4 + 0] = r;
			ds[p * 4 + 1] = s;
			ds[p * 4 + 2] = t;
			ds[p * 4 + 3] = v;
		}

		for (int w = 0; w < oreFeatureConfig.size - 1; w++) {
			if (!(ds[w * 4 + 3] <= 0.0)) {
				for (int x = w + 1; x < oreFeatureConfig.size; x++) {
					if (!(ds[x * 4 + 3] <= 0.0)) {
						double y = ds[w * 4 + 0] - ds[x * 4 + 0];
						double z = ds[w * 4 + 1] - ds[x * 4 + 1];
						double aa = ds[w * 4 + 2] - ds[x * 4 + 2];
						double ab = ds[w * 4 + 3] - ds[x * 4 + 3];
						if (ab * ab > y * y + z * z + aa * aa) {
							if (ab > 0.0) {
								ds[x * 4 + 3] = -1.0;
							} else {
								ds[w * 4 + 3] = -1.0;
							}
						}
					}
				}
			}
		}

		for (int ac = 0; ac < oreFeatureConfig.size; ac++) {
			double ad = ds[ac * 4 + 3];
			if (!(ad < 0.0)) {
				double ae = ds[ac * 4 + 0];
				double af = ds[ac * 4 + 1];
				double ag = ds[ac * 4 + 2];
				int ah = Math.max(MathHelper.floor(ae - ad), j);
				int ai = Math.max(MathHelper.floor(af - ad), k);
				int aj = Math.max(MathHelper.floor(ag - ad), l);
				int ak = Math.max(MathHelper.floor(ae + ad), ah);
				int al = Math.max(MathHelper.floor(af + ad), ai);
				int am = Math.max(MathHelper.floor(ag + ad), aj);

				for (int an = ah; an <= ak; an++) {
					double ao = ((double)an + 0.5 - ae) / ad;
					if (ao * ao < 1.0) {
						for (int ap = ai; ap <= al; ap++) {
							double aq = ((double)ap + 0.5 - af) / ad;
							if (ao * ao + aq * aq < 1.0) {
								for (int ar = aj; ar <= am; ar++) {
									double as = ((double)ar + 0.5 - ag) / ad;
									if (ao * ao + aq * aq + as * as < 1.0) {
										int at = an - j + (ap - k) * m + (ar - l) * m * n;
										if (!bitSet.get(at)) {
											bitSet.set(at);
											mutable.set(an, ap, ar);
											if (oreFeatureConfig.target.getCondition().test(iWorld.getBlockState(mutable))) {
												iWorld.setBlockState(mutable, oreFeatureConfig.state, 2);
												o++;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return o > 0;
	}
}
