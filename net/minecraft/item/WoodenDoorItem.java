package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WoodenDoorItem extends Item {
	private final Block doorBlock;

	public WoodenDoorItem(Block block) {
		this.doorBlock = block;
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		if (direction != Direction.UP) {
			return ActionResult.FAIL;
		} else {
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (!block.method_8638(world, pos)) {
				pos = pos.offset(direction);
			}

			ItemStack itemStack = player.getStackInHand(hand);
			if (player.canModify(pos, direction, itemStack) && this.doorBlock.canBePlacedAtPos(world, pos)) {
				Direction direction2 = Direction.fromRotation((double)player.yaw);
				int i = direction2.getOffsetX();
				int j = direction2.getOffsetZ();
				boolean bl = i < 0 && z < 0.5F || i > 0 && z > 0.5F || j < 0 && x > 0.5F || j > 0 && x < 0.5F;
				method_11273(world, pos, direction2, this.doorBlock, bl);
				BlockSoundGroup blockSoundGroup = this.doorBlock.getSoundGroup();
				world.method_11486(
					player, pos, blockSoundGroup.method_4194(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F
				);
				itemStack.decrement(1);
				return ActionResult.SUCCESS;
			} else {
				return ActionResult.FAIL;
			}
		}
	}

	public static void method_11273(World world, BlockPos blockPos, Direction direction, Block block, boolean bl) {
		BlockPos blockPos2 = blockPos.offset(direction.rotateYClockwise());
		BlockPos blockPos3 = blockPos.offset(direction.rotateYCounterclockwise());
		int i = (world.getBlockState(blockPos3).method_11734() ? 1 : 0) + (world.getBlockState(blockPos3.up()).method_11734() ? 1 : 0);
		int j = (world.getBlockState(blockPos2).method_11734() ? 1 : 0) + (world.getBlockState(blockPos2.up()).method_11734() ? 1 : 0);
		boolean bl2 = world.getBlockState(blockPos3).getBlock() == block || world.getBlockState(blockPos3.up()).getBlock() == block;
		boolean bl3 = world.getBlockState(blockPos2).getBlock() == block || world.getBlockState(blockPos2.up()).getBlock() == block;
		if ((!bl2 || bl3) && j <= i) {
			if (bl3 && !bl2 || j < i) {
				bl = false;
			}
		} else {
			bl = true;
		}

		BlockPos blockPos4 = blockPos.up();
		boolean bl4 = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos4);
		BlockState blockState = block.getDefaultState()
			.with(DoorBlock.FACING, direction)
			.with(DoorBlock.HINGE, bl ? DoorBlock.DoorType.RIGHT : DoorBlock.DoorType.LEFT)
			.with(DoorBlock.POWERED, bl4)
			.with(DoorBlock.OPEN, bl4);
		world.setBlockState(blockPos, blockState.with(DoorBlock.HALF, DoorBlock.HalfType.LOWER), 2);
		world.setBlockState(blockPos4, blockState.with(DoorBlock.HALF, DoorBlock.HalfType.UPPER), 2);
		world.method_13692(blockPos, block, false);
		world.method_13692(blockPos4, block, false);
	}
}
