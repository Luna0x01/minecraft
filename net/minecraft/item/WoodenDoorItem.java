package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WoodenDoorItem extends Item {
	private Block doorBlock;

	public WoodenDoorItem(Block block) {
		this.doorBlock = block;
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (direction != Direction.UP) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (!block.isReplaceable(world, pos)) {
				pos = pos.offset(direction);
			}

			if (!player.canModify(pos, direction, itemStack)) {
				return false;
			} else if (!this.doorBlock.canBePlacedAtPos(world, pos)) {
				return false;
			} else {
				place(world, pos, Direction.fromRotation((double)player.yaw), this.doorBlock);
				itemStack.count--;
				return true;
			}
		}
	}

	public static void place(World world, BlockPos pos, Direction direction, Block block) {
		BlockPos blockPos = pos.offset(direction.rotateYClockwise());
		BlockPos blockPos2 = pos.offset(direction.rotateYCounterclockwise());
		int i = (world.getBlockState(blockPos2).getBlock().isFullCube() ? 1 : 0) + (world.getBlockState(blockPos2.up()).getBlock().isFullCube() ? 1 : 0);
		int j = (world.getBlockState(blockPos).getBlock().isFullCube() ? 1 : 0) + (world.getBlockState(blockPos.up()).getBlock().isFullCube() ? 1 : 0);
		boolean bl = world.getBlockState(blockPos2).getBlock() == block || world.getBlockState(blockPos2.up()).getBlock() == block;
		boolean bl2 = world.getBlockState(blockPos).getBlock() == block || world.getBlockState(blockPos.up()).getBlock() == block;
		boolean bl3 = false;
		if (bl && !bl2 || j > i) {
			bl3 = true;
		}

		BlockPos blockPos3 = pos.up();
		BlockState blockState = block.getDefaultState()
			.with(DoorBlock.FACING, direction)
			.with(DoorBlock.HINGE, bl3 ? DoorBlock.DoorType.RIGHT : DoorBlock.DoorType.LEFT);
		world.setBlockState(pos, blockState.with(DoorBlock.HALF, DoorBlock.HalfType.LOWER), 2);
		world.setBlockState(blockPos3, blockState.with(DoorBlock.HALF, DoorBlock.HalfType.UPPER), 2);
		world.updateNeighborsAlways(pos, block);
		world.updateNeighborsAlways(blockPos3, block);
	}
}
