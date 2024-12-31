package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CropItem extends FoodItem {
	private Block crop;
	private Block soil;

	public CropItem(int i, float f, Block block, Block block2) {
		super(i, f, false);
		this.crop = block;
		this.soil = block2;
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		if (direction == Direction.UP
			&& playerEntity.canModify(blockPos.offset(direction), direction, itemStack)
			&& world.getBlockState(blockPos).getBlock() == this.soil
			&& world.isAir(blockPos.up())) {
			world.setBlockState(blockPos.up(), this.crop.getDefaultState(), 11);
			itemStack.count--;
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}
}
