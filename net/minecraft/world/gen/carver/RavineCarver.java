package net.minecraft.world.gen.carver;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class RavineCarver extends Carver {
	protected static final BlockState field_12953 = Blocks.FLOWING_LAVA.getDefaultState();
	protected static final BlockState field_12954 = Blocks.AIR.getDefaultState();
	private float[] heightToHorizontalStretchFactor = new float[1024];

	protected void carveRavine(
		long seed,
		int mainChunkX,
		int mainChunkZ,
		ChunkBlockStateStorage chunkStorage,
		double x,
		double y,
		double z,
		float baseWidth,
		float xzAngle,
		float yAngle,
		int branch,
		int branchCount,
		double heightWidthRatio
	) {
		Random random = new Random(seed);
		double d = (double)(mainChunkX * 16 + 8);
		double e = (double)(mainChunkZ * 16 + 8);
		float f = 0.0F;
		float g = 0.0F;
		if (branchCount <= 0) {
			int i = this.complexity * 16 - 16;
			branchCount = i - random.nextInt(i / 4);
		}

		boolean bl = false;
		if (branch == -1) {
			branch = branchCount / 2;
			bl = true;
		}

		float h = 1.0F;

		for (int j = 0; j < 256; j++) {
			if (j == 0 || random.nextInt(3) == 0) {
				h = 1.0F + random.nextFloat() * random.nextFloat();
			}

			this.heightToHorizontalStretchFactor[j] = h * h;
		}

		for (; branch < branchCount; branch++) {
			double k = 1.5 + (double)(MathHelper.sin((float)branch * (float) Math.PI / (float)branchCount) * baseWidth);
			double l = k * heightWidthRatio;
			k *= (double)random.nextFloat() * 0.25 + 0.75;
			l *= (double)random.nextFloat() * 0.25 + 0.75;
			float m = MathHelper.cos(yAngle);
			float n = MathHelper.sin(yAngle);
			x += (double)(MathHelper.cos(xzAngle) * m);
			y += (double)n;
			z += (double)(MathHelper.sin(xzAngle) * m);
			yAngle *= 0.7F;
			yAngle += g * 0.05F;
			xzAngle += f * 0.05F;
			g *= 0.8F;
			f *= 0.5F;
			g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (bl || random.nextInt(4) != 0) {
				double o = x - d;
				double p = z - e;
				double q = (double)(branchCount - branch);
				double r = (double)(baseWidth + 2.0F + 16.0F);
				if (o * o + p * p - q * q > r * r) {
					return;
				}

				if (!(x < d - 16.0 - k * 2.0) && !(z < e - 16.0 - k * 2.0) && !(x > d + 16.0 + k * 2.0) && !(z > e + 16.0 + k * 2.0)) {
					int s = MathHelper.floor(x - k) - mainChunkX * 16 - 1;
					int t = MathHelper.floor(x + k) - mainChunkX * 16 + 1;
					int u = MathHelper.floor(y - l) - 1;
					int v = MathHelper.floor(y + l) + 1;
					int w = MathHelper.floor(z - k) - mainChunkZ * 16 - 1;
					int aa = MathHelper.floor(z + k) - mainChunkZ * 16 + 1;
					if (s < 0) {
						s = 0;
					}

					if (t > 16) {
						t = 16;
					}

					if (u < 1) {
						u = 1;
					}

					if (v > 248) {
						v = 248;
					}

					if (w < 0) {
						w = 0;
					}

					if (aa > 16) {
						aa = 16;
					}

					boolean bl2 = false;

					for (int ab = s; !bl2 && ab < t; ab++) {
						for (int ac = w; !bl2 && ac < aa; ac++) {
							for (int ad = v + 1; !bl2 && ad >= u - 1; ad--) {
								if (ad >= 0 && ad < 256) {
									BlockState blockState = chunkStorage.get(ab, ad, ac);
									if (blockState.getBlock() == Blocks.FLOWING_WATER || blockState.getBlock() == Blocks.WATER) {
										bl2 = true;
									}

									if (ad != u - 1 && ab != s && ab != t - 1 && ac != w && ac != aa - 1) {
										ad = u;
									}
								}
							}
						}
					}

					if (!bl2) {
						BlockPos.Mutable mutable = new BlockPos.Mutable();

						for (int ae = s; ae < t; ae++) {
							double af = ((double)(ae + mainChunkX * 16) + 0.5 - x) / k;

							for (int ag = w; ag < aa; ag++) {
								double ah = ((double)(ag + mainChunkZ * 16) + 0.5 - z) / k;
								boolean bl3 = false;
								if (af * af + ah * ah < 1.0) {
									for (int ai = v; ai > u; ai--) {
										double aj = ((double)(ai - 1) + 0.5 - y) / l;
										if ((af * af + ah * ah) * (double)this.heightToHorizontalStretchFactor[ai - 1] + aj * aj / 6.0 < 1.0) {
											BlockState blockState2 = chunkStorage.get(ae, ai, ag);
											if (blockState2.getBlock() == Blocks.GRASS) {
												bl3 = true;
											}

											if (blockState2.getBlock() == Blocks.STONE || blockState2.getBlock() == Blocks.DIRT || blockState2.getBlock() == Blocks.GRASS) {
												if (ai - 1 < 10) {
													chunkStorage.set(ae, ai, ag, field_12953);
												} else {
													chunkStorage.set(ae, ai, ag, field_12954);
													if (bl3 && chunkStorage.get(ae, ai - 1, ag).getBlock() == Blocks.DIRT) {
														mutable.setPosition(ae + mainChunkX * 16, 0, ag + mainChunkZ * 16);
														chunkStorage.set(ae, ai - 1, ag, this.world.getBiome(mutable).topBlock);
													}
												}
											}
										}
									}
								}
							}
						}

						if (bl) {
							break;
						}
					}
				}
			}
		}
	}

	@Override
	protected void carve(World world, int chunkX, int chunkZ, int mainChunkX, int mainChunkZ, ChunkBlockStateStorage chunkStorage) {
		if (this.random.nextInt(50) == 0) {
			double d = (double)(chunkX * 16 + this.random.nextInt(16));
			double e = (double)(this.random.nextInt(this.random.nextInt(40) + 8) + 20);
			double f = (double)(chunkZ * 16 + this.random.nextInt(16));
			int i = 1;

			for (int j = 0; j < i; j++) {
				float g = this.random.nextFloat() * (float) (Math.PI * 2);
				float h = (this.random.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float k = (this.random.nextFloat() * 2.0F + this.random.nextFloat()) * 2.0F;
				this.carveRavine(this.random.nextLong(), mainChunkX, mainChunkZ, chunkStorage, d, e, f, k, g, h, 0, 0, 3.0);
			}
		}
	}
}
