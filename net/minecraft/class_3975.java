package net.minecraft;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class class_3975 extends class_3972 {
	public class_3975() {
		this.field_19343 = ImmutableSet.of(
			Blocks.STONE,
			Blocks.GRANITE,
			Blocks.DIORITE,
			Blocks.ANDESITE,
			Blocks.DIRT,
			Blocks.COARSE_DIRT,
			new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK}
		);
		this.field_19344 = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
	}

	@Override
	public boolean method_17679(BlockView blockView, Random random, int i, int j, class_3877 arg) {
		return random.nextFloat() <= arg.field_19231;
	}

	@Override
	public boolean method_17680(IWorld iWorld, Random random, int i, int j, int k, int l, BitSet bitSet, class_3877 arg) {
		int m = (this.method_17583() * 2 - 1) * 16;
		int n = random.nextInt(random.nextInt(random.nextInt(10) + 1) + 1);

		for (int o = 0; o < n; o++) {
			double d = (double)(i * 16 + random.nextInt(16));
			double e = (double)random.nextInt(128);
			double f = (double)(j * 16 + random.nextInt(16));
			int p = 1;
			if (random.nextInt(4) == 0) {
				double g = 0.5;
				float h = 1.0F + random.nextFloat() * 6.0F;
				this.method_17595(iWorld, random.nextLong(), k, l, d, e, f, h, 0.5, bitSet);
				p += random.nextInt(4);
			}

			for (int q = 0; q < p; q++) {
				float r = random.nextFloat() * (float) (Math.PI * 2);
				float s = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
				double t = 5.0;
				float u = (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
				int v = m - random.nextInt(m / 4);
				int w = 0;
				this.method_17596(iWorld, random.nextLong(), k, l, d, e, f, u, r, s, 0, v, 5.0, bitSet);
			}
		}

		return true;
	}

	@Override
	protected boolean method_17586(IWorld iWorld, long l, int i, int j, double d, double e, double f, double g, double h, BitSet bitSet) {
		double k = (double)(i * 16 + 8);
		double m = (double)(j * 16 + 8);
		if (!(d < k - 16.0 - g * 2.0) && !(f < m - 16.0 - g * 2.0) && !(d > k + 16.0 + g * 2.0) && !(f > m + 16.0 + g * 2.0)) {
			int n = Math.max(MathHelper.floor(d - g) - i * 16 - 1, 0);
			int o = Math.min(MathHelper.floor(d + g) - i * 16 + 1, 16);
			int p = Math.max(MathHelper.floor(e - h) - 1, 1);
			int q = Math.min(MathHelper.floor(e + h) + 1, 120);
			int r = Math.max(MathHelper.floor(f - g) - j * 16 - 1, 0);
			int s = Math.min(MathHelper.floor(f + g) - j * 16 + 1, 16);
			if (this.method_17587(iWorld, i, j, n, o, p, q, r, s)) {
				return false;
			} else if (n <= o && p <= q && r <= s) {
				boolean bl = false;

				for (int t = n; t < o; t++) {
					int u = t + i * 16;
					double v = ((double)u + 0.5 - d) / g;

					for (int w = r; w < s; w++) {
						int x = w + j * 16;
						double y = ((double)x + 0.5 - f) / g;

						for (int z = q; z > p; z--) {
							double aa = ((double)(z - 1) + 0.5 - e) / h;
							if (aa > -0.7 && v * v + aa * aa + y * y < 1.0) {
								int ab = t | w << 4 | z << 8;
								if (!bitSet.get(ab)) {
									bitSet.set(ab);
									if (this.method_17588(iWorld.getBlockState(new BlockPos(u, z, x)))) {
										if (z <= 31) {
											iWorld.setBlockState(new BlockPos(u, z, x), field_19342.method_17813(), 2);
										} else {
											iWorld.setBlockState(new BlockPos(u, z, x), field_19340, 2);
										}

										bl = true;
									}
								}
							}
						}
					}
				}

				return bl;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
