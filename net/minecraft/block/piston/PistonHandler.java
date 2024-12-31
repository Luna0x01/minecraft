package net.minecraft.block.piston;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PistonHandler {
	private final World world;
	private final BlockPos posFrom;
	private final BlockPos posTo;
	private final Direction direction;
	private final List<BlockPos> movedBlocks = Lists.newArrayList();
	private final List<BlockPos> brokenBlocks = Lists.newArrayList();

	public PistonHandler(World world, BlockPos blockPos, Direction direction, boolean bl) {
		this.world = world;
		this.posFrom = blockPos;
		if (bl) {
			this.direction = direction;
			this.posTo = blockPos.offset(direction);
		} else {
			this.direction = direction.getOpposite();
			this.posTo = blockPos.offset(direction, 2);
		}
	}

	public boolean calculatePush() {
		this.movedBlocks.clear();
		this.brokenBlocks.clear();
		BlockState blockState = this.world.getBlockState(this.posTo);
		if (!PistonBlock.method_9001(blockState, this.world, this.posTo, this.direction, false, this.direction)) {
			if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
				this.brokenBlocks.add(this.posTo);
				return true;
			} else {
				return false;
			}
		} else if (!this.method_9017(this.posTo, this.direction)) {
			return false;
		} else {
			for (int i = 0; i < this.movedBlocks.size(); i++) {
				BlockPos blockPos = (BlockPos)this.movedBlocks.get(i);
				if (this.world.getBlockState(blockPos).getBlock() == Blocks.SLIME_BLOCK && !this.canMoveAdjacentBlock(blockPos)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean method_9017(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (blockState.getMaterial() == Material.AIR) {
			return true;
		} else if (!PistonBlock.method_9001(blockState, this.world, blockPos, this.direction, false, direction)) {
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
				while (block == Blocks.SLIME_BLOCK) {
					BlockPos blockPos2 = blockPos.offset(this.direction.getOpposite(), i);
					blockState = this.world.getBlockState(blockPos2);
					block = blockState.getBlock();
					if (blockState.getMaterial() == Material.AIR
						|| !PistonBlock.method_9001(blockState, this.world, blockPos2, this.direction, false, this.direction.getOpposite())
						|| blockPos2.equals(this.posFrom)) {
						break;
					}

					if (++i + this.movedBlocks.size() > 12) {
						return false;
					}
				}

				int j = 0;

				for (int k = i - 1; k >= 0; k--) {
					this.movedBlocks.add(blockPos.offset(this.direction.getOpposite(), k));
					j++;
				}

				int l = 1;

				while (true) {
					BlockPos blockPos3 = blockPos.offset(this.direction, l);
					int m = this.movedBlocks.indexOf(blockPos3);
					if (m > -1) {
						this.setMovedBlocks(j, m);

						for (int n = 0; n <= m + j; n++) {
							BlockPos blockPos4 = (BlockPos)this.movedBlocks.get(n);
							if (this.world.getBlockState(blockPos4).getBlock() == Blocks.SLIME_BLOCK && !this.canMoveAdjacentBlock(blockPos4)) {
								return false;
							}
						}

						return true;
					}

					blockState = this.world.getBlockState(blockPos3);
					if (blockState.getMaterial() == Material.AIR) {
						return true;
					}

					if (!PistonBlock.method_9001(blockState, this.world, blockPos3, this.direction, true, this.direction) || blockPos3.equals(this.posFrom)) {
						return false;
					}

					if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
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

	private void setMovedBlocks(int from, int to) {
		List<BlockPos> list = Lists.newArrayList();
		List<BlockPos> list2 = Lists.newArrayList();
		List<BlockPos> list3 = Lists.newArrayList();
		list.addAll(this.movedBlocks.subList(0, to));
		list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - from, this.movedBlocks.size()));
		list3.addAll(this.movedBlocks.subList(to, this.movedBlocks.size() - from));
		this.movedBlocks.clear();
		this.movedBlocks.addAll(list);
		this.movedBlocks.addAll(list2);
		this.movedBlocks.addAll(list3);
	}

	private boolean canMoveAdjacentBlock(BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (direction.getAxis() != this.direction.getAxis() && !this.method_9017(pos.offset(direction), direction)) {
				return false;
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
