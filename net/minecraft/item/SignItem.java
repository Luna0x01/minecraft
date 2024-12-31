package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SignItem extends Item {
	public SignItem() {
		this.maxCount = 16;
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = blockState.getBlock().method_8638(world, blockPos);
		if (direction != Direction.DOWN && (blockState.getMaterial().isSolid() || bl) && (!bl || direction == Direction.UP)) {
			blockPos = blockPos.offset(direction);
			if (!playerEntity.canModify(blockPos, direction, itemStack) || !Blocks.STANDING_SIGN.canBePlacedAtPos(world, blockPos)) {
				return ActionResult.FAIL;
			} else if (world.isClient) {
				return ActionResult.SUCCESS;
			} else {
				blockPos = bl ? blockPos.down() : blockPos;
				if (direction == Direction.UP) {
					int i = MathHelper.floor((double)((playerEntity.yaw + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
					world.setBlockState(blockPos, Blocks.STANDING_SIGN.getDefaultState().with(StandingSignBlock.ROTATION, i), 11);
				} else {
					world.setBlockState(blockPos, Blocks.WALL_SIGN.getDefaultState().with(WallSignBlock.FACING, direction), 11);
				}

				itemStack.count--;
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof SignBlockEntity && !BlockItem.setBlockEntityNbt(world, playerEntity, blockPos, itemStack)) {
					playerEntity.openEditSignScreen((SignBlockEntity)blockEntity);
				}

				return ActionResult.SUCCESS;
			}
		} else {
			return ActionResult.FAIL;
		}
	}
}
