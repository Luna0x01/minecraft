package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CropItem extends FoodItem {
	private final Block crop;
	private final Block soil;

	public CropItem(int i, float f, Block block, Block block2) {
		super(i, f, false);
		this.crop = block;
		this.soil = block2;
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (direction == Direction.UP
			&& player.canModify(pos.offset(direction), direction, itemStack)
			&& world.getBlockState(pos).getBlock() == this.soil
			&& world.isAir(pos.up())) {
			world.setBlockState(pos.up(), this.crop.getDefaultState(), 11);
			itemStack.decrement(1);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}
}
