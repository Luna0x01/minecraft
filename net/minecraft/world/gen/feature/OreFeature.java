package net.minecraft.world.gen.feature;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StoneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class OreFeature extends Feature {
	private final BlockState blockState;
	private final int amount;
	private final Predicate<BlockState> baseBlockState;

	public OreFeature(BlockState blockState, int i) {
		this(blockState, i, new OreFeature.class_3069());
	}

	public OreFeature(BlockState blockState, int i, Predicate<BlockState> predicate) {
		this.blockState = blockState;
		this.amount = i;
		this.baseBlockState = predicate;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		float f = random.nextFloat() * (float) Math.PI;
		double d = (double)((float)(blockPos.getX() + 8) + MathHelper.sin(f) * (float)this.amount / 8.0F);
		double e = (double)((float)(blockPos.getX() + 8) - MathHelper.sin(f) * (float)this.amount / 8.0F);
		double g = (double)((float)(blockPos.getZ() + 8) + MathHelper.cos(f) * (float)this.amount / 8.0F);
		double h = (double)((float)(blockPos.getZ() + 8) - MathHelper.cos(f) * (float)this.amount / 8.0F);
		double i = (double)(blockPos.getY() + random.nextInt(3) - 2);
		double j = (double)(blockPos.getY() + random.nextInt(3) - 2);

		for (int k = 0; k < this.amount; k++) {
			float l = (float)k / (float)this.amount;
			double m = d + (e - d) * (double)l;
			double n = i + (j - i) * (double)l;
			double o = g + (h - g) * (double)l;
			double p = random.nextDouble() * (double)this.amount / 16.0;
			double q = (double)(MathHelper.sin((float) Math.PI * l) + 1.0F) * p + 1.0;
			double r = (double)(MathHelper.sin((float) Math.PI * l) + 1.0F) * p + 1.0;
			int s = MathHelper.floor(m - q / 2.0);
			int t = MathHelper.floor(n - r / 2.0);
			int u = MathHelper.floor(o - q / 2.0);
			int v = MathHelper.floor(m + q / 2.0);
			int w = MathHelper.floor(n + r / 2.0);
			int x = MathHelper.floor(o + q / 2.0);

			for (int y = s; y <= v; y++) {
				double z = ((double)y + 0.5 - m) / (q / 2.0);
				if (z * z < 1.0) {
					for (int aa = t; aa <= w; aa++) {
						double ab = ((double)aa + 0.5 - n) / (r / 2.0);
						if (z * z + ab * ab < 1.0) {
							for (int ac = u; ac <= x; ac++) {
								double ad = ((double)ac + 0.5 - o) / (q / 2.0);
								if (z * z + ab * ab + ad * ad < 1.0) {
									BlockPos blockPos2 = new BlockPos(y, aa, ac);
									if (this.baseBlockState.apply(world.getBlockState(blockPos2))) {
										world.setBlockState(blockPos2, this.blockState, 2);
									}
								}
							}
						}
					}
				}
			}
		}

		return true;
	}

	static class class_3069 implements Predicate<BlockState> {
		private class_3069() {
		}

		public boolean apply(BlockState blockState) {
			if (blockState != null && blockState.getBlock() == Blocks.STONE) {
				StoneBlock.StoneType stoneType = blockState.get(StoneBlock.VARIANT);
				return stoneType.method_13719();
			} else {
				return false;
			}
		}
	}
}
