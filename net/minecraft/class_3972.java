package net.minecraft;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class class_3972 extends class_3969<class_3877> {
	public boolean method_17679(BlockView blockView, Random random, int i, int j, class_3877 arg) {
		return random.nextFloat() <= arg.field_19231;
	}

	public boolean method_17680(IWorld iWorld, Random random, int i, int j, int k, int l, BitSet bitSet, class_3877 arg) {
		int m = (this.method_17583() * 2 - 1) * 16;
		int n = random.nextInt(random.nextInt(random.nextInt(15) + 1) + 1);

		for (int o = 0; o < n; o++) {
			double d = (double)(i * 16 + random.nextInt(16));
			double e = (double)random.nextInt(random.nextInt(120) + 8);
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
				float s = (random.nextFloat() - 0.5F) / 4.0F;
				double t = 1.0;
				float u = random.nextFloat() * 2.0F + random.nextFloat();
				if (random.nextInt(10) == 0) {
					u *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
				}

				int v = m - random.nextInt(m / 4);
				int w = 0;
				this.method_17596(iWorld, random.nextLong(), k, l, d, e, f, u, r, s, 0, v, 1.0, bitSet);
			}
		}

		return true;
	}

	protected void method_17595(IWorld iWorld, long l, int i, int j, double d, double e, double f, float g, double h, BitSet bitSet) {
		double k = 1.5 + (double)(MathHelper.sin((float) (Math.PI / 2)) * g);
		double m = k * h;
		this.method_17586(iWorld, l, i, j, d + 1.0, e, f, k, m, bitSet);
	}

	protected void method_17596(
		IWorld iWorld, long l, int i, int j, double d, double e, double f, float g, float h, float k, int m, int n, double o, BitSet bitSet
	) {
		Random random = new Random(l);
		int p = random.nextInt(n / 2) + n / 4;
		boolean bl = random.nextInt(6) == 0;
		float q = 0.0F;
		float r = 0.0F;

		for (int s = m; s < n; s++) {
			double t = 1.5 + (double)(MathHelper.sin((float) Math.PI * (float)s / (float)n) * g);
			double u = t * o;
			float v = MathHelper.cos(k);
			d += (double)(MathHelper.cos(h) * v);
			e += (double)MathHelper.sin(k);
			f += (double)(MathHelper.sin(h) * v);
			k *= bl ? 0.92F : 0.7F;
			k += r * 0.1F;
			h += q * 0.1F;
			r *= 0.9F;
			q *= 0.75F;
			r += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			q += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (s == p && g > 1.0F) {
				this.method_17596(iWorld, random.nextLong(), i, j, d, e, f, random.nextFloat() * 0.5F + 0.5F, h - (float) (Math.PI / 2), k / 3.0F, s, n, 1.0, bitSet);
				this.method_17596(iWorld, random.nextLong(), i, j, d, e, f, random.nextFloat() * 0.5F + 0.5F, h + (float) (Math.PI / 2), k / 3.0F, s, n, 1.0, bitSet);
				return;
			}

			if (random.nextInt(4) != 0) {
				if (!this.method_17584(i, j, d, f, s, n, g)) {
					return;
				}

				this.method_17586(iWorld, l, i, j, d, e, f, t, u, bitSet);
			}
		}
	}

	@Override
	protected boolean method_17586(IWorld iWorld, long l, int i, int j, double d, double e, double f, double g, double h, BitSet bitSet) {
		double k = (double)(i * 16 + 8);
		double m = (double)(j * 16 + 8);
		if (!(d < k - 16.0 - g * 2.0) && !(f < m - 16.0 - g * 2.0) && !(d > k + 16.0 + g * 2.0) && !(f > m + 16.0 + g * 2.0)) {
			int n = Math.max(MathHelper.floor(d - g) - i * 16 - 1, 0);
			int o = Math.min(MathHelper.floor(d + g) - i * 16 + 1, 16);
			int p = Math.max(MathHelper.floor(e - h) - 1, 1);
			int q = Math.min(MathHelper.floor(e + h) + 1, 248);
			int r = Math.max(MathHelper.floor(f - g) - j * 16 - 1, 0);
			int s = Math.min(MathHelper.floor(f + g) - j * 16 + 1, 16);
			if (this.method_17587(iWorld, i, j, n, o, p, q, r, s)) {
				return false;
			} else {
				boolean bl = false;
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				BlockPos.Mutable mutable2 = new BlockPos.Mutable();
				BlockPos.Mutable mutable3 = new BlockPos.Mutable();

				for (int t = n; t < o; t++) {
					int u = t + i * 16;
					double v = ((double)u + 0.5 - d) / g;

					for (int w = r; w < s; w++) {
						int x = w + j * 16;
						double y = ((double)x + 0.5 - f) / g;
						if (!(v * v + y * y >= 1.0)) {
							boolean bl2 = false;

							for (int z = q; z > p; z--) {
								double aa = ((double)z - 0.5 - e) / h;
								if (!(aa <= -0.7) && !(v * v + aa * aa + y * y >= 1.0)) {
									int ab = t | w << 4 | z << 8;
									if (!bitSet.get(ab)) {
										bitSet.set(ab);
										mutable.setPosition(u, z, x);
										BlockState blockState = iWorld.getBlockState(mutable);
										BlockState blockState2 = iWorld.getBlockState(mutable2.set(mutable).move(Direction.UP));
										if (blockState.getBlock() == Blocks.GRASS_BLOCK || blockState.getBlock() == Blocks.MYCELIUM) {
											bl2 = true;
										}

										if (this.method_17589(blockState, blockState2)) {
											if (z < 11) {
												iWorld.setBlockState(mutable, field_19342.method_17813(), 2);
											} else {
												iWorld.setBlockState(mutable, field_19340, 2);
												if (bl2) {
													mutable3.set(mutable).move(Direction.DOWN);
													if (iWorld.getBlockState(mutable3).getBlock() == Blocks.DIRT) {
														BlockState blockState3 = iWorld.method_8577(mutable).method_16450().method_17720();
														iWorld.setBlockState(mutable3, blockState3, 2);
													}
												}
											}

											bl = true;
										}
									}
								}
							}
						}
					}
				}

				return bl;
			}
		} else {
			return false;
		}
	}
}
