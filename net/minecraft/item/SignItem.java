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
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		BlockState blockState = world.getBlockState(pos);
		boolean bl = blockState.getBlock().method_8638(world, pos);
		if (direction != Direction.DOWN && (blockState.getMaterial().isSolid() || bl) && (!bl || direction == Direction.UP)) {
			pos = pos.offset(direction);
			ItemStack itemStack = player.getStackInHand(hand);
			if (!player.canModify(pos, direction, itemStack) || !Blocks.STANDING_SIGN.canBePlacedAtPos(world, pos)) {
				return ActionResult.FAIL;
			} else if (world.isClient) {
				return ActionResult.SUCCESS;
			} else {
				pos = bl ? pos.down() : pos;
				if (direction == Direction.UP) {
					int i = MathHelper.floor((double)((player.yaw + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
					world.setBlockState(pos, Blocks.STANDING_SIGN.getDefaultState().with(StandingSignBlock.ROTATION, i), 11);
				} else {
					world.setBlockState(pos, Blocks.WALL_SIGN.getDefaultState().with(WallSignBlock.FACING, direction), 11);
				}

				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof SignBlockEntity && !BlockItem.setBlockEntityNbt(world, player, pos, itemStack)) {
					player.openEditSignScreen((SignBlockEntity)blockEntity);
				}

				itemStack.decrement(1);
				return ActionResult.SUCCESS;
			}
		} else {
			return ActionResult.FAIL;
		}
	}
}
