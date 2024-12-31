package net.minecraft.world.gen.carver;

import com.google.common.base.Objects;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkBlockStateStorage;

public class CaveCarver extends Carver {
	protected void carveCave(long seed, int mainChunkX, int mainChunkZ, ChunkBlockStateStorage chunkStorage, double x, double y, double z) {
		this.carveCave(seed, mainChunkX, mainChunkZ, chunkStorage, x, y, z, 1.0F + this.random.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
	}

	protected void carveCave(
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
		double d = (double)(mainChunkX * 16 + 8);
		double e = (double)(mainChunkZ * 16 + 8);
		float f = 0.0F;
		float g = 0.0F;
		Random random = new Random(seed);
		if (branchCount <= 0) {
			int i = this.complexity * 16 - 16;
			branchCount = i - random.nextInt(i / 4);
		}

		boolean bl = false;
		if (branch == -1) {
			branch = branchCount / 2;
			bl = true;
		}

		int j = random.nextInt(branchCount / 2) + branchCount / 4;

		for (boolean bl2 = random.nextInt(6) == 0; branch < branchCount; branch++) {
			double h = 1.5 + (double)(MathHelper.sin((float)branch * (float) Math.PI / (float)branchCount) * baseWidth * 1.0F);
			double k = h * heightWidthRatio;
			float l = MathHelper.cos(yAngle);
			float m = MathHelper.sin(yAngle);
			x += (double)(MathHelper.cos(xzAngle) * l);
			y += (double)m;
			z += (double)(MathHelper.sin(xzAngle) * l);
			if (bl2) {
				yAngle *= 0.92F;
			} else {
				yAngle *= 0.7F;
			}

			yAngle += g * 0.1F;
			xzAngle += f * 0.1F;
			g *= 0.9F;
			f *= 0.75F;
			g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
			if (!bl && branch == j && baseWidth > 1.0F && branchCount > 0) {
				this.carveCave(
					random.nextLong(),
					mainChunkX,
					mainChunkZ,
					chunkStorage,
					x,
					y,
					z,
					random.nextFloat() * 0.5F + 0.5F,
					xzAngle - (float) (Math.PI / 2),
					yAngle / 3.0F,
					branch,
					branchCount,
					1.0
				);
				this.carveCave(
					random.nextLong(),
					mainChunkX,
					mainChunkZ,
					chunkStorage,
					x,
					y,
					z,
					random.nextFloat() * 0.5F + 0.5F,
					xzAngle + (float) (Math.PI / 2),
					yAngle / 3.0F,
					branch,
					branchCount,
					1.0
				);
				return;
			}

			if (bl || random.nextInt(4) != 0) {
				double n = x - d;
				double o = z - e;
				double p = (double)(branchCount - branch);
				double q = (double)(baseWidth + 2.0F + 16.0F);
				if (n * n + o * o - p * p > q * q) {
					return;
				}

				if (!(x < d - 16.0 - h * 2.0) && !(z < e - 16.0 - h * 2.0) && !(x > d + 16.0 + h * 2.0) && !(z > e + 16.0 + h * 2.0)) {
					int r = MathHelper.floor(x - h) - mainChunkX * 16 - 1;
					int s = MathHelper.floor(x + h) - mainChunkX * 16 + 1;
					int t = MathHelper.floor(y - k) - 1;
					int u = MathHelper.floor(y + k) + 1;
					int v = MathHelper.floor(z - h) - mainChunkZ * 16 - 1;
					int w = MathHelper.floor(z + h) - mainChunkZ * 16 + 1;
					if (r < 0) {
						r = 0;
					}

					if (s > 16) {
						s = 16;
					}

					if (t < 1) {
						t = 1;
					}

					if (u > 248) {
						u = 248;
					}

					if (v < 0) {
						v = 0;
					}

					if (w > 16) {
						w = 16;
					}

					boolean bl3 = false;

					for (int aa = r; !bl3 && aa < s; aa++) {
						for (int ab = v; !bl3 && ab < w; ab++) {
							for (int ac = u + 1; !bl3 && ac >= t - 1; ac--) {
								if (ac >= 0 && ac < 256) {
									BlockState blockState = chunkStorage.get(aa, ac, ab);
									if (blockState.getBlock() == Blocks.FLOWING_WATER || blockState.getBlock() == Blocks.WATER) {
										bl3 = true;
									}

									if (ac != t - 1 && aa != r && aa != s - 1 && ab != v && ab != w - 1) {
										ac = t;
									}
								}
							}
						}
					}

					if (!bl3) {
						BlockPos.Mutable mutable = new BlockPos.Mutable();

						for (int ad = r; ad < s; ad++) {
							double ae = ((double)(ad + mainChunkX * 16) + 0.5 - x) / h;

							for (int af = v; af < w; af++) {
								double ag = ((double)(af + mainChunkZ * 16) + 0.5 - z) / h;
								boolean bl4 = false;
								if (ae * ae + ag * ag < 1.0) {
									for (int ah = u; ah > t; ah--) {
										double ai = ((double)(ah - 1) + 0.5 - y) / k;
										if (ai > -0.7 && ae * ae + ai * ai + ag * ag < 1.0) {
											BlockState blockState2 = chunkStorage.get(ad, ah, af);
											BlockState blockState3 = (BlockState)Objects.firstNonNull(chunkStorage.get(ad, ah + 1, af), Blocks.AIR.getDefaultState());
											if (blockState2.getBlock() == Blocks.GRASS || blockState2.getBlock() == Blocks.MYCELIUM) {
												bl4 = true;
											}

											if (this.canCarveBlock(blockState2, blockState3)) {
												if (ah - 1 < 10) {
													chunkStorage.set(ad, ah, af, Blocks.LAVA.getDefaultState());
												} else {
													chunkStorage.set(ad, ah, af, Blocks.AIR.getDefaultState());
													if (blockState3.getBlock() == Blocks.SAND) {
														chunkStorage.set(
															ad,
															ah + 1,
															af,
															blockState3.get(SandBlock.sandType) == SandBlock.SandType.RED_SAND ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState()
														);
													}

													if (bl4 && chunkStorage.get(ad, ah - 1, af).getBlock() == Blocks.DIRT) {
														mutable.setPosition(ad + mainChunkX * 16, 0, af + mainChunkZ * 16);
														chunkStorage.set(ad, ah - 1, af, this.world.getBiome(mutable).topBlock.getBlock().getDefaultState());
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

	protected boolean canCarveBlock(BlockState block, BlockState blockBelow) {
		if (block.getBlock() == Blocks.STONE) {
			return true;
		} else if (block.getBlock() == Blocks.DIRT) {
			return true;
		} else if (block.getBlock() == Blocks.GRASS) {
			return true;
		} else if (block.getBlock() == Blocks.TERRACOTTA) {
			return true;
		} else if (block.getBlock() == Blocks.STAINED_TERRACOTTA) {
			return true;
		} else if (block.getBlock() == Blocks.SANDSTONE) {
			return true;
		} else if (block.getBlock() == Blocks.RED_SANDSTONE) {
			return true;
		} else if (block.getBlock() == Blocks.MYCELIUM) {
			return true;
		} else {
			return block.getBlock() == Blocks.SNOW_LAYER
				? true
				: (block.getBlock() == Blocks.SAND || block.getBlock() == Blocks.GRAVEL) && blockBelow.getBlock().getMaterial() != Material.WATER;
		}
	}

	@Override
	protected void carve(World world, int chunkX, int chunkZ, int mainChunkX, int mainChunkZ, ChunkBlockStateStorage chunkStorage) {
		int i = this.random.nextInt(this.random.nextInt(this.random.nextInt(15) + 1) + 1);
		if (this.random.nextInt(7) != 0) {
			i = 0;
		}

		for (int j = 0; j < i; j++) {
			double d = (double)(chunkX * 16 + this.random.nextInt(16));
			double e = (double)this.random.nextInt(this.random.nextInt(120) + 8);
			double f = (double)(chunkZ * 16 + this.random.nextInt(16));
			int k = 1;
			if (this.random.nextInt(4) == 0) {
				this.carveCave(this.random.nextLong(), mainChunkX, mainChunkZ, chunkStorage, d, e, f);
				k += this.random.nextInt(4);
			}

			for (int l = 0; l < k; l++) {
				float g = this.random.nextFloat() * (float) Math.PI * 2.0F;
				float h = (this.random.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float m = this.random.nextFloat() * 2.0F + this.random.nextFloat();
				if (this.random.nextInt(10) == 0) {
					m *= this.random.nextFloat() * this.random.nextFloat() * 3.0F + 1.0F;
				}

				this.carveCave(this.random.nextLong(), mainChunkX, mainChunkZ, chunkStorage, d, e, f, m, g, h, 0, 0, 1.0);
			}
		}
	}
}
