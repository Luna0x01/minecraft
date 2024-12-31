package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BigTreeFeature extends FoliageFeature {
	private Random random;
	private World world;
	private BlockPos origin = BlockPos.ORIGIN;
	int maxHeight;
	int height;
	double heightModifier = 0.618;
	double branchAngle = 0.381;
	double leafSizeModifier = 1.0;
	double leafHeightModifier = 1.0;
	int trunkSize = 1;
	int heightLimit = 12;
	int leafRadius = 4;
	List<BigTreeFeature.BigTreeBlockPos> blockCoords;

	public BigTreeFeature(boolean bl) {
		super(bl);
	}

	void addBlockCoords() {
		this.height = (int)((double)this.maxHeight * this.heightModifier);
		if (this.height >= this.maxHeight) {
			this.height = this.maxHeight - 1;
		}

		int i = (int)(1.382 + Math.pow(this.leafHeightModifier * (double)this.maxHeight / 13.0, 2.0));
		if (i < 1) {
			i = 1;
		}

		int j = this.origin.getY() + this.height;
		int k = this.maxHeight - this.leafRadius;
		this.blockCoords = Lists.newArrayList();
		this.blockCoords.add(new BigTreeFeature.BigTreeBlockPos(this.origin.up(k), j));

		for (; k >= 0; k--) {
			float f = this.getLeafSizeAtHeight(k);
			if (!(f < 0.0F)) {
				for (int l = 0; l < i; l++) {
					double d = this.leafSizeModifier * (double)f * ((double)this.random.nextFloat() + 0.328);
					double e = (double)(this.random.nextFloat() * 2.0F) * Math.PI;
					double g = d * Math.sin(e) + 0.5;
					double h = d * Math.cos(e) + 0.5;
					BlockPos blockPos = this.origin.add(g, (double)(k - 1), h);
					BlockPos blockPos2 = blockPos.up(this.leafRadius);
					if (this.getGenerateConflictRadius(blockPos, blockPos2) == -1) {
						int m = this.origin.getX() - blockPos.getX();
						int n = this.origin.getZ() - blockPos.getZ();
						double o = (double)blockPos.getY() - Math.sqrt((double)(m * m + n * n)) * this.branchAngle;
						int p = o > (double)j ? j : (int)o;
						BlockPos blockPos3 = new BlockPos(this.origin.getX(), p, this.origin.getZ());
						if (this.getGenerateConflictRadius(blockPos3, blockPos) == -1) {
							this.blockCoords.add(new BigTreeFeature.BigTreeBlockPos(blockPos, blockPos3.getY()));
						}
					}
				}
			}
		}
	}

	void generateLeaves(BlockPos blockPos, float radius, BlockState blockState) {
		int i = (int)((double)radius + 0.618);

		for (int j = -i; j <= i; j++) {
			for (int k = -i; k <= i; k++) {
				if (Math.pow((double)Math.abs(j) + 0.5, 2.0) + Math.pow((double)Math.abs(k) + 0.5, 2.0) <= (double)(radius * radius)) {
					BlockPos blockPos2 = blockPos.add(j, 0, k);
					Material material = this.world.getBlockState(blockPos2).getMaterial();
					if (material == Material.AIR || material == Material.FOLIAGE) {
						this.setBlockStateWithoutUpdatingNeighbors(this.world, blockPos2, blockState);
					}
				}
			}
		}
	}

	float getLeafSizeAtHeight(int height) {
		if ((float)height < (float)this.maxHeight * 0.3F) {
			return -1.0F;
		} else {
			float f = (float)this.maxHeight / 2.0F;
			float g = f - (float)height;
			float h = MathHelper.sqrt(f * f - g * g);
			if (g == 0.0F) {
				h = f;
			} else if (Math.abs(g) >= f) {
				return 0.0F;
			}

			return h * 0.5F;
		}
	}

	float getLeafRadius(int radius) {
		if (radius < 0 || radius >= this.leafRadius) {
			return -1.0F;
		} else {
			return radius != 0 && radius != this.leafRadius - 1 ? 3.0F : 2.0F;
		}
	}

	void generateLeaves(BlockPos blockPos) {
		for (int i = 0; i < this.leafRadius; i++) {
			this.generateLeaves(blockPos.up(i), this.getLeafRadius(i), Blocks.LEAVES.getDefaultState().with(LeavesBlock.CHECK_DECAY, false));
		}
	}

	void setLogsAt(BlockPos startPos, BlockPos endPos, Block block) {
		BlockPos blockPos = endPos.add(-startPos.getX(), -startPos.getY(), -startPos.getZ());
		int i = this.getLargestPosComponent(blockPos);
		float f = (float)blockPos.getX() / (float)i;
		float g = (float)blockPos.getY() / (float)i;
		float h = (float)blockPos.getZ() / (float)i;

		for (int j = 0; j <= i; j++) {
			BlockPos blockPos2 = startPos.add((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * g), (double)(0.5F + (float)j * h));
			LogBlock.Axis axis = this.getLogOrientation(startPos, blockPos2);
			this.setBlockStateWithoutUpdatingNeighbors(this.world, blockPos2, block.getDefaultState().with(LogBlock.LOG_AXIS, axis));
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

	private LogBlock.Axis getLogOrientation(BlockPos startPos, BlockPos endPos) {
		LogBlock.Axis axis = LogBlock.Axis.Y;
		int i = Math.abs(endPos.getX() - startPos.getX());
		int j = Math.abs(endPos.getZ() - startPos.getZ());
		int k = Math.max(i, j);
		if (k > 0) {
			if (i == k) {
				axis = LogBlock.Axis.X;
			} else if (j == k) {
				axis = LogBlock.Axis.Z;
			}
		}

		return axis;
	}

	void generateLeaves() {
		for (BigTreeFeature.BigTreeBlockPos bigTreeBlockPos : this.blockCoords) {
			this.generateLeaves(bigTreeBlockPos);
		}
	}

	boolean moreLogsRequired(int radius) {
		return (double)radius >= (double)this.maxHeight * 0.2;
	}

	void generateTrunk() {
		BlockPos blockPos = this.origin;
		BlockPos blockPos2 = this.origin.up(this.height);
		Block block = Blocks.LOG;
		this.setLogsAt(blockPos, blockPos2, block);
		if (this.trunkSize == 2) {
			this.setLogsAt(blockPos.east(), blockPos2.east(), block);
			this.setLogsAt(blockPos.east().south(), blockPos2.east().south(), block);
			this.setLogsAt(blockPos.south(), blockPos2.south(), block);
		}
	}

	void generateBranches() {
		for (BigTreeFeature.BigTreeBlockPos bigTreeBlockPos : this.blockCoords) {
			int i = bigTreeBlockPos.getBranchBaseY();
			BlockPos blockPos = new BlockPos(this.origin.getX(), i, this.origin.getZ());
			if (!blockPos.equals(bigTreeBlockPos) && this.moreLogsRequired(i - this.origin.getY())) {
				this.setLogsAt(blockPos, bigTreeBlockPos, Blocks.LOG);
			}
		}
	}

	int getGenerateConflictRadius(BlockPos startPos, BlockPos endPos) {
		BlockPos blockPos = endPos.add(-startPos.getX(), -startPos.getY(), -startPos.getZ());
		int i = this.getLargestPosComponent(blockPos);
		float f = (float)blockPos.getX() / (float)i;
		float g = (float)blockPos.getY() / (float)i;
		float h = (float)blockPos.getZ() / (float)i;
		if (i == 0) {
			return -1;
		} else {
			for (int j = 0; j <= i; j++) {
				BlockPos blockPos2 = startPos.add((double)(0.5F + (float)j * f), (double)(0.5F + (float)j * g), (double)(0.5F + (float)j * h));
				if (!this.isBlockReplaceable(this.world.getBlockState(blockPos2).getBlock())) {
					return j;
				}
			}

			return -1;
		}
	}

	@Override
	public void setLeafRadius() {
		this.leafRadius = 5;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		this.world = world;
		this.origin = blockPos;
		this.random = new Random(random.nextLong());
		if (this.maxHeight == 0) {
			this.maxHeight = 5 + this.random.nextInt(this.heightLimit);
		}

		if (!this.canGenerate()) {
			return false;
		} else {
			this.addBlockCoords();
			this.generateLeaves();
			this.generateTrunk();
			this.generateBranches();
			return true;
		}
	}

	private boolean canGenerate() {
		Block block = this.world.getBlockState(this.origin.down()).getBlock();
		if (block != Blocks.DIRT && block != Blocks.GRASS && block != Blocks.FARMLAND) {
			return false;
		} else {
			int i = this.getGenerateConflictRadius(this.origin, this.origin.up(this.maxHeight - 1));
			if (i == -1) {
				return true;
			} else if (i < 6) {
				return false;
			} else {
				this.maxHeight = i;
				return true;
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
