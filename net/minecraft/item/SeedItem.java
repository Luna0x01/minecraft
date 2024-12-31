package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SeedItem extends Item {
	private Block crop;
	private Block soil;

	public SeedItem(Block block, Block block2) {
		this.crop = block;
		this.soil = block2;
		this.setItemGroup(ItemGroup.MATERIALS);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		if (direction == Direction.UP
			&& playerEntity.canModify(blockPos.offset(direction), direction, itemStack)
			&& world.getBlockState(blockPos).getBlock() == this.soil
			&& world.isAir(blockPos.up())) {
			world.setBlockState(blockPos.up(), this.crop.getDefaultState());
			itemStack.count--;
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}
}
