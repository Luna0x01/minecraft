package net.minecraft.block.piston;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonHandler {
	private final World world;
	private final BlockPos posFrom;
	private final boolean retracted;
	private final BlockPos posTo;
	private final Direction motionDirection;
	private final List<BlockPos> movedBlocks = Lists.newArrayList();
	private final List<BlockPos> brokenBlocks = Lists.newArrayList();
	private final Direction pistonDirection;

	public PistonHandler(World world, BlockPos blockPos, Direction direction, boolean bl) {
		this.world = world;
		this.posFrom = blockPos;
		this.pistonDirection = direction;
		this.retracted = bl;
		if (bl) {
			this.motionDirection = direction;
			this.posTo = blockPos.offset(direction);
		} else {
			this.motionDirection = direction.getOpposite();
			this.posTo = blockPos.offset(direction, 2);
		}
	}

	public boolean calculatePush() {
		this.movedBlocks.clear();
		this.brokenBlocks.clear();
		BlockState blockState = this.world.getBlockState(this.posTo);
		if (!PistonBlock.isMovable(blockState, this.world, this.posTo, this.motionDirection, false, this.pistonDirection)) {
			if (this.retracted && blockState.getPistonBehavior() == PistonBehavior.field_15971) {
				this.brokenBlocks.add(this.posTo);
				return true;
			} else {
				return false;
			}
		} else if (!this.tryMove(this.posTo, this.motionDirection)) {
			return false;
		} else {
			for (int i = 0; i < this.movedBlocks.size(); i++) {
				BlockPos blockPos = (BlockPos)this.movedBlocks.get(i);
				if (isBlockSticky(this.world.getBlockState(blockPos).getBlock()) && !this.canMoveAdjacentBlock(blockPos)) {
					return false;
				}
			}

			return true;
		}
	}

	private static boolean isBlockSticky(Block block) {
		return block == Blocks.field_10030 || block == Blocks.field_21211;
	}

	private static boolean isAdjacentBlockStuck(Block block, Block block2) {
		if (block == Blocks.field_21211 && block2 == Blocks.field_10030) {
			return false;
		} else {
			return block == Blocks.field_10030 && block2 == Blocks.field_21211 ? false : isBlockSticky(block) || isBlockSticky(block2);
		}
	}

	private boolean tryMove(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (blockState.isAir()) {
			return true;
		} else if (!PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, false, direction)) {
			return true;
		} else if (blockPos.equals(this.posFrom)) {
			return true;
		} else if (this.movedBlocks.contains(blockPos)) {
			return true;
		} else {
			int i = 1;
			if (i + this.movedBlocks.size() > 12) {
				return false;
			} else {
				while (isBlockSticky(block)) {
					BlockPos blockPos2 = blockPos.offset(this.motionDirection.getOpposite(), i);
					Block block2 = block;
					blockState = this.world.getBlockState(blockPos2);
					block = blockState.getBlock();
					if (blockState.isAir()
						|| !isAdjacentBlockStuck(block2, block)
						|| !PistonBlock.isMovable(blockState, this.world, blockPos2, this.motionDirection, false, this.motionDirection.getOpposite())
						|| blockPos2.equals(this.posFrom)) {
						break;
					}

					if (++i + this.movedBlocks.size() > 12) {
						return false;
					}
				}

				int j = 0;

				for (int k = i - 1; k >= 0; k--) {
					this.movedBlocks.add(blockPos.offset(this.motionDirection.getOpposite(), k));
					j++;
				}

				int l = 1;

				while (true) {
					BlockPos blockPos3 = blockPos.offset(this.motionDirection, l);
					int m = this.movedBlocks.indexOf(blockPos3);
					if (m > -1) {
						this.setMovedBlocks(j, m);

						for (int n = 0; n <= m + j; n++) {
							BlockPos blockPos4 = (BlockPos)this.movedBlocks.get(n);
							if (isBlockSticky(this.world.getBlockState(blockPos4).getBlock()) && !this.canMoveAdjacentBlock(blockPos4)) {
								return false;
							}
						}

						return true;
					}

					blockState = this.world.getBlockState(blockPos3);
					if (blockState.isAir()) {
						return true;
					}

					if (!PistonBlock.isMovable(blockState, this.world, blockPos3, this.motionDirection, true, this.motionDirection) || blockPos3.equals(this.posFrom)) {
						return false;
					}

					if (blockState.getPistonBehavior() == PistonBehavior.field_15971) {
						this.brokenBlocks.add(blockPos3);
						return true;
					}

					if (this.movedBlocks.size() >= 12) {
						return false;
					}

					this.movedBlocks.add(blockPos3);
					j++;
					l++;
				}
			}
		}
	}

	private void setMovedBlocks(int i, int j) {
		List<BlockPos> list = Lists.newArrayList();
		List<BlockPos> list2 = Lists.newArrayList();
		List<BlockPos> list3 = Lists.newArrayList();
		list.addAll(this.movedBlocks.subList(0, j));
		list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - i, this.movedBlocks.size()));
		list3.addAll(this.movedBlocks.subList(j, this.movedBlocks.size() - i));
		this.movedBlocks.clear();
		this.movedBlocks.addAll(list);
		this.movedBlocks.addAll(list2);
		this.movedBlocks.addAll(list3);
	}

	private boolean canMoveAdjacentBlock(BlockPos blockPos) {
		BlockState blockState = this.world.getBlockState(blockPos);

		for (Direction direction : Direction.values()) {
			if (direction.getAxis() != this.motionDirection.getAxis()) {
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState2 = this.world.getBlockState(blockPos2);
				if (isAdjacentBlockStuck(blockState2.getBlock(), blockState.getBlock()) && !this.tryMove(blockPos2, direction)) {
					return false;
				}
			}
		}

		return true;
	}

	public List<BlockPos> getMovedBlocks() {
		return this.movedBlocks;
	}

	public List<BlockPos> getBrokenBlocks() {
		return this.brokenBlocks;
	}
}
