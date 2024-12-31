package net.minecraft.world.gen.feature;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3804;
import net.minecraft.class_3844;
import net.minecraft.class_3875;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class OreFeature extends class_3844<class_3875> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3875 arg) {
		float f = random.nextFloat() * (float) Math.PI;
		float g = (float)arg.field_19228 / 8.0F;
		int i = MathHelper.ceil(((float)arg.field_19228 / 16.0F * 2.0F + 1.0F) / 2.0F);
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
				if (o <= iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR_WG, s, t)) {
					return this.method_17393(iWorld, random, arg, d, e, h, j, l, m, n, o, p, q, r);
				}
			}
		}

		return false;
	}

	protected boolean method_17393(
		IWorld iWorld, Random random, class_3875 arg, double d, double e, double f, double g, double h, double i, int j, int k, int l, int m, int n
	) {
		int o = 0;
		BitSet bitSet = new BitSet(m * n * m);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		double[] ds = new double[arg.field_19228 * 4];

		for (int p = 0; p < arg.field_19228; p++) {
			float q = (float)p / (float)arg.field_19228;
			double r = d + (e - d) * (double)q;
			double s = h + (i - h) * (double)q;
			double t = f + (g - f) * (double)q;
			double u = random.nextDouble() * (double)arg.field_19228 / 16.0;
			double v = ((double)(MathHelper.sin((float) Math.PI * q) + 1.0F) * u + 1.0) / 2.0;
			ds[p * 4 + 0] = r;
			ds[p * 4 + 1] = s;
			ds[p * 4 + 2] = t;
			ds[p * 4 + 3] = v;
		}

		for (int w = 0; w < arg.field_19228 - 1; w++) {
			if (!(ds[w * 4 + 3] <= 0.0)) {
				for (int x = w + 1; x < arg.field_19228; x++) {
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

		for (int ac = 0; ac < arg.field_19228; ac++) {
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
											mutable.setPosition(an, ap, ar);
											if (arg.field_19227.test(iWorld.getBlockState(mutable))) {
												iWorld.setBlockState(mutable, arg.field_19229, 2);
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
