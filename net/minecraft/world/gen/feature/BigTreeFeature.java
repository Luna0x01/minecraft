package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.class_3871;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class BigTreeFeature extends FoliageFeature<class_3871> {
	private static final BlockState field_19068 = Blocks.OAK_LOG.getDefaultState();
	private static final BlockState field_19069 = Blocks.OAK_LEAVES.getDefaultState();

	public BigTreeFeature(boolean bl) {
		super(bl);
	}

	private void method_17298(IWorld iWorld, BlockPos blockPos, float f) {
		int i = (int)((double)f + 0.618);

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(f * f)) {
					BlockPos blockPos2 = blockPos.add(j, 0, k);
					BlockState blockState = iWorld.getBlockState(blockPos2);
					if (blockState.isAir() || blockState.getMaterial() == Material.FOLIAGE) {
						this.method_17344(iWorld, blockPos2, field_19069);
					}
				}
			}
		}
	}

	private float method_17296(int i, int j) {
		if ((float)j < (float)i * 0.3F) {
			return -1.0F;
		} else {
			float f = (float)i / 2.0F;
			float g = f - (float)j;
			float h = MathHelper.sqrt(f * f - g * g);
			if (g == 0.0F) {
				h = f;
			} else if (Math.abs(g) >= f) {
				return 0.0F;
			}

			return h * 0.5F;
		}
	}

	private float getLeafRadius(int radius) {
		if (radius < 0 || radius >= 5) {
			return -1.0F;
		} else {
			return radius != 0 && radius != 4 ? 3.0F : 2.0F;
		}
	}

	private void method_17304(IWorld iWorld, BlockPos blockPos) {
		for (int i = 0; i < 5; i++) {
			this.method_17298(iWorld, blockPos.up(i), this.getLeafRadius(i));
		}
	}

	private int method_17302(Set<BlockPos> set, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2, boolean bl) {
		if (!bl && Objects.equals(blockPos, blockPos2)) {
			return -1;
		} else {
			BlockPos blockPos3 = blockPos2.add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
			int i = this.getLargestPosComponent(blockPos3);
			float f = (float)blockPos3.getX() / (float)i;
			float g = (float)blockPos3.getY() / (float)i;
			float h = (float)blockPos3.getZ() / (float)i;

			for (int j = 0; j <= i; j++) {
				BlockPos blockPos4 = blockPos.add((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * g), (double)(0.5F + (float)j * h));
				if (bl) {
					this.method_17293(set, iWorld, blockPos4, field_19068.withProperty(LogBlock.PILLAR_AXIS, this.method_17299(blockPos, blockPos4)));
				} else if (!this.isBlockReplaceable(iWorld.getBlockState(blockPos4).getBlock())) {
					return j;
				}
			}

			return -1;
		}
	}

	private int getLargestPosComponent(BlockPos pos) {
		int i = MathHelper.abs(pos.getX());
		int j = MathHelper.abs(pos.getY());
		int k = MathHelper.abs(pos.getZ());
		if (k > i && k > j) {
			return k;
		} else {
			return j > i ? j : i;
		}
	}

	private Direction.Axis method_17299(BlockPos blockPos, BlockPos blockPos2) {
		Direction.Axis axis = Direction.Axis.Y;
		int i = Math.abs(blockPos2.getX() - blockPos.getX());
		int j = Math.abs(blockPos2.getZ() - blockPos.getZ());
		int k = Math.max(i, j);
		if (k > 0) {
			if (i == k) {
				axis = Direction.Axis.X;
			} else if (j == k) {
				axis = Direction.Axis.Z;
			}
		}

		return axis;
	}

	private void method_17297(IWorld iWorld, int i, BlockPos blockPos, List<BigTreeFeature.BigTreeBlockPos> list) {
		for (BigTreeFeature.BigTreeBlockPos bigTreeBlockPos : list) {
			if (this.method_17303(i, bigTreeBlockPos.getBranchBaseY() - blockPos.getY())) {
				this.method_17304(iWorld, bigTreeBlockPos);
			}
		}
	}

	private boolean method_17303(int i, int j) {
		return (double)j >= (double)i * 0.2;
	}

	private void method_17301(Set<BlockPos> set, IWorld iWorld, BlockPos blockPos, int i) {
		this.method_17302(set, iWorld, blockPos, blockPos.up(i), true);
	}

	private void method_17300(Set<BlockPos> set, IWorld iWorld, int i, BlockPos blockPos, List<BigTreeFeature.BigTreeBlockPos> list) {
		for (BigTreeFeature.BigTreeBlockPos bigTreeBlockPos : list) {
			int j = bigTreeBlockPos.getBranchBaseY();
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), j, blockPos.getZ());
			if (!blockPos2.equals(bigTreeBlockPos) && this.method_17303(i, j - blockPos.getY())) {
				this.method_17302(set, iWorld, blockPos2, bigTreeBlockPos, true);
			}
		}
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
		Random random2 = new Random(random.nextLong());
		int i = this.method_17305(set, iWorld, blockPos, 5 + random2.nextInt(12));
		if (i == -1) {
			return false;
		} else {
			this.method_17292(iWorld, blockPos.down());
			int j = (int)((double)i * 0.618);
			if (j >= i) {
				j = i - 1;
			}

			double d = 1.0;
			int k = (int)(1.382 + Math.pow(1.0 * (double)i / 13.0, 2.0));
			if (k < 1) {
				k = 1;
			}

			int l = blockPos.getY() + j;
			int m = i - 5;
			List<BigTreeFeature.BigTreeBlockPos> list = Lists.newArrayList();
			list.add(new BigTreeFeature.BigTreeBlockPos(blockPos.up(m), l));

			for (; m >= 0; m--) {
				float f = this.method_17296(i, m);
				if (!(f < 0.0F)) {
					for (int n = 0; n < k; n++) {
						double e = 1.0;
						double g = 1.0 * (double)f * ((double)random2.nextFloat() + 0.328);
						double h = (double)(random2.nextFloat() * 2.0F) * Math.PI;
						double o = g * Math.sin(h) + 0.5;
						double p = g * Math.cos(h) + 0.5;
						BlockPos blockPos2 = blockPos.add(o, (double)(m - 1), p);
						BlockPos blockPos3 = blockPos2.up(5);
						if (this.method_17302(set, iWorld, blockPos2, blockPos3, false) == -1) {
							int q = blockPos.getX() - blockPos2.getX();
							int r = blockPos.getZ() - blockPos2.getZ();
							double s = (double)blockPos2.getY() - Math.sqrt((double)(q * q + r * r)) * 0.381;
							int t = s > (double)l ? l : (int)s;
							BlockPos blockPos4 = new BlockPos(blockPos.getX(), t, blockPos.getZ());
							if (this.method_17302(set, iWorld, blockPos4, blockPos2, false) == -1) {
								list.add(new BigTreeFeature.BigTreeBlockPos(blockPos2, blockPos4.getY()));
							}
						}
					}
				}
			}

			this.method_17297(iWorld, i, blockPos, list);
			this.method_17301(set, iWorld, blockPos, j);
			this.method_17300(set, iWorld, i, blockPos, list);
			return true;
		}
	}

	private int method_17305(Set<BlockPos> set, IWorld iWorld, BlockPos blockPos, int i) {
		Block block = iWorld.getBlockState(blockPos.down()).getBlock();
		if (!Block.method_16588(block) && block != Blocks.GRASS_BLOCK && block != Blocks.FARMLAND) {
			return -1;
		} else {
			int j = this.method_17302(set, iWorld, blockPos, blockPos.up(i - 1), false);
			if (j == -1) {
				return i;
			} else {
				return j < 6 ? -1 : j;
			}
		}
	}

	static class BigTreeBlockPos extends BlockPos {
		private final int branchBaseY;

		public BigTreeBlockPos(BlockPos blockPos, int i) {
			super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
			this.branchBaseY = i;
		}

		public int getBranchBaseY() {
			return this.branchBaseY;
		}
	}
}
