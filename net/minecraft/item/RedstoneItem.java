package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneItem extends Item {
	public RedstoneItem() {
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		boolean bl = world.getBlockState(pos).getBlock().isReplaceable(world, pos);
		BlockPos blockPos = bl ? pos : pos.offset(direction);
		if (!player.canModify(blockPos, direction, itemStack)) {
			return false;
		} else {
			Block block = world.getBlockState(blockPos).getBlock();
			if (!world.canBlockBePlaced(block, blockPos, false, direction, null, itemStack)) {
				return false;
			} else if (Blocks.REDSTONE_WIRE.canBePlacedAtPos(world, blockPos)) {
				itemStack.count--;
				world.setBlockState(blockPos, Blocks.REDSTONE_WIRE.getDefaultState());
				return true;
			} else {
				return false;
			}
		}
	}
}
