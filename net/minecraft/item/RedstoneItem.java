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
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		boolean bl = world.getBlockState(pos).getBlock().method_8638(world, pos);
		BlockPos blockPos = bl ? pos : pos.offset(direction);
		ItemStack itemStack = player.getStackInHand(hand);
		if (player.canModify(blockPos, direction, itemStack)
			&& world.method_8493(world.getBlockState(blockPos).getBlock(), blockPos, false, direction, null)
			&& Blocks.REDSTONE_WIRE.canBePlacedAtPos(world, blockPos)) {
			itemStack.decrement(1);
			world.setBlockState(blockPos, Blocks.REDSTONE_WIRE.getDefaultState());
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}
}
