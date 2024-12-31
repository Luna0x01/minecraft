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

public class class_3971 extends class_3969<class_3877> {
	private final float[] field_19345 = new float[1024];

	public boolean method_17679(BlockView blockView, Random random, int i, int j, class_3877 arg) {
		return random.nextFloat() <= arg.field_19231;
	}

	public boolean method_17680(IWorld iWorld, Random random, int i, int j, int k, int l, BitSet bitSet, class_3877 arg) {
		int m = (this.method_17583() * 2 - 1) * 16;
		double d = (double)(i * 16 + random.nextInt(16));
		double e = (double)(random.nextInt(random.nextInt(40) + 8) + 20);
		double f = (double)(j * 16 + random.nextInt(16));
		float g = random.nextFloat() * (float) (Math.PI * 2);
		float h = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
		double n = 3.0;
		float o = (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
		int p = m - random.nextInt(m / 4);
		int q = 0;
		this.method_17592(iWorld, random.nextLong(), k, l, d, e, f, o, g, h, 0, p, 3.0, bitSet);
		return true;
	}

	private void method_17592(IWorld iWorld, long l, int i, int j, double d, double e, double f, float g, float h, float k, int m, int n, double o, BitSet bitSet) {
		Random random = new Random(l);
		float p = 1.0F;

		for (int q = 0; q < 256; q++) {
			if (q == 0 || random.nextInt(3) == 0) {
				p = 1.0F + random.nextFloat() * random.nextFloat();
			}

			this.field_19345[q] = p * p;
		}

		float r = 0.0F;
		float s = 0.0F;

		for (int t = m; t < n; t++) {
			double u = 1.5 + (double)(MathHelper.sin((float)t * (float) Math.PI / (float)n) * g);
			double v = u * o;
			u *= (double)random.nextFloat() * 0.25 + 0.75;
			v *= (double)random.nextFloat() * 0.25 + 0.75;
			float w = MathHelper.cos(k);
			float x = MathHelper.sin(k);
			d += (double)(MathHelper.cos(h) * w);
			e += (double)x;
			f += (double)(MathHelper.sin(h) * w);
			k *= 0.7F;
			k += s * 0.05F;
			h += r * 0.05F;
			s *= 0.8F;
			r *= 0.5F;
			s += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			r += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (random.nextInt(4) != 0) {
				if (!this.method_17584(i, j, d, f, t, n, g)) {
					return;
				}

				this.method_17586(iWorld, l, i, j, d, e, f, u, v, bitSet);
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
			} else if (n <= o && p <= q && r <= s) {
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
						if (v * v + y * y < 1.0) {
							boolean bl2 = false;

							for (int z = q; z > p; z--) {
								double aa = ((double)(z - 1) + 0.5 - e) / h;
								if ((v * v + y * y) * (double)this.field_19345[z - 1] + aa * aa / 6.0 < 1.0) {
									int ab = t | w << 4 | z << 8;
									if (!bitSet.get(ab)) {
										bitSet.set(ab);
										mutable.setPosition(u, z, x);
										BlockState blockState = iWorld.getBlockState(mutable);
										mutable2.set(mutable).move(Direction.UP);
										mutable3.set(mutable).move(Direction.DOWN);
										BlockState blockState2 = iWorld.getBlockState(mutable2);
										if (blockState.getBlock() == Blocks.GRASS_BLOCK || blockState.getBlock() == Blocks.MYCELIUM) {
											bl2 = true;
										}

										if (this.method_17589(blockState, blockState2)) {
											if (z - 1 < 10) {
												iWorld.setBlockState(mutable, field_19342.method_17813(), 2);
											} else {
												iWorld.setBlockState(mutable, field_19340, 2);
												if (bl2 && iWorld.getBlockState(mutable3).getBlock() == Blocks.DIRT) {
													iWorld.setBlockState(mutable3, iWorld.method_8577(mutable).method_16450().method_17720(), 2);
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
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
