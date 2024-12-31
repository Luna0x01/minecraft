package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneItem extends Item {
	public RedstoneItem() {
		this.setItemGroup(ItemGroup.REDSTONE);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		boolean bl = world.getBlockState(blockPos).getBlock().method_8638(world, blockPos);
		BlockPos blockPos2 = bl ? blockPos : blockPos.offset(direction);
		if (playerEntity.canModify(blockPos2, direction, itemStack)
			&& world.canBlockBePlaced(world.getBlockState(blockPos2).getBlock(), blockPos2, false, direction, null, itemStack)
			&& Blocks.REDSTONE_WIRE.canBePlacedAtPos(world, blockPos2)) {
			itemStack.count--;
			world.setBlockState(blockPos2, Blocks.REDSTONE_WIRE.getDefaultState());
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}
}
