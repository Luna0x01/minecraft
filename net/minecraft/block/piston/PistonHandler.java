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
		if (!PistonBlock.method_9001(blockState, this.world, this.posTo, this.direction, false)) {
			if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
				this.brokenBlocks.add(this.posTo);
				return true;
			} else {
				return false;
			}
		} else if (!this.tryMove(this.posTo)) {
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

	private boolean tryMove(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (blockState.getMaterial() == Material.AIR) {
			return true;
		} else if (!PistonBlock.method_9001(blockState, this.world, pos, this.direction, false)) {
			return true;
		} else if (pos.equals(this.posFrom)) {
			return true;
		} else if (this.movedBlocks.contains(pos)) {
			return true;
		} else {
			int i = 1;
			if (i + this.movedBlocks.size() > 12) {
				return false;
			} else {
				while (block == Blocks.SLIME_BLOCK) {
					BlockPos blockPos = pos.offset(this.direction.getOpposite(), i);
					blockState = this.world.getBlockState(blockPos);
					block = blockState.getBlock();
					if (blockState.getMaterial() == Material.AIR
						|| !PistonBlock.method_9001(blockState, this.world, blockPos, this.direction, false)
						|| blockPos.equals(this.posFrom)) {
						break;
					}

					if (++i + this.movedBlocks.size() > 12) {
						return false;
					}
				}

				int j = 0;

				for (int k = i - 1; k >= 0; k--) {
					this.movedBlocks.add(pos.offset(this.direction.getOpposite(), k));
					j++;
				}

				int l = 1;

				while (true) {
					BlockPos blockPos2 = pos.offset(this.direction, l);
					int m = this.movedBlocks.indexOf(blockPos2);
					if (m > -1) {
						this.setMovedBlocks(j, m);

						for (int n = 0; n <= m + j; n++) {
							BlockPos blockPos3 = (BlockPos)this.movedBlocks.get(n);
							if (this.world.getBlockState(blockPos3).getBlock() == Blocks.SLIME_BLOCK && !this.canMoveAdjacentBlock(blockPos3)) {
								return false;
							}
						}

						return true;
					}

					blockState = this.world.getBlockState(blockPos2);
					if (blockState.getMaterial() == Material.AIR) {
						return true;
					}

					if (!PistonBlock.method_9001(blockState, this.world, blockPos2, this.direction, true) || blockPos2.equals(this.posFrom)) {
						return false;
					}

					if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
						this.brokenBlocks.add(blockPos2);
						return true;
					}

					if (this.movedBlocks.size() >= 12) {
						return false;
					}

					this.movedBlocks.add(blockPos2);
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
			if (direction.getAxis() != this.direction.getAxis() && !this.tryMove(pos.offset(direction))) {
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
