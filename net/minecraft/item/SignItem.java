package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
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
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (direction == Direction.DOWN) {
			return false;
		} else if (!world.getBlockState(pos).getBlock().getMaterial().isSolid()) {
			return false;
		} else {
			pos = pos.offset(direction);
			if (!player.canModify(pos, direction, itemStack)) {
				return false;
			} else if (!Blocks.STANDING_SIGN.canBePlacedAtPos(world, pos)) {
				return false;
			} else if (world.isClient) {
				return true;
			} else {
				if (direction == Direction.UP) {
					int i = MathHelper.floor((double)((player.yaw + 180.0F) * 16.0F / 360.0F) + 0.5) & 15;
					world.setBlockState(pos, Blocks.STANDING_SIGN.getDefaultState().with(StandingSignBlock.ROTATION, i), 3);
				} else {
					world.setBlockState(pos, Blocks.WALL_SIGN.getDefaultState().with(WallSignBlock.FACING, direction), 3);
				}

				itemStack.count--;
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof SignBlockEntity && !BlockItem.setBlockEntityNbt(world, player, pos, itemStack)) {
					player.openEditSignScreen((SignBlockEntity)blockEntity);
				}

				return true;
			}
		}
	}
}
