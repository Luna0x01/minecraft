package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
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
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (direction != Direction.UP) {
			return false;
		} else if (!player.canModify(pos.offset(direction), direction, itemStack)) {
			return false;
		} else if (world.getBlockState(pos).getBlock() == this.soil && world.isAir(pos.up())) {
			world.setBlockState(pos.up(), this.crop.getDefaultState());
			itemStack.count--;
			return true;
		} else {
			return false;
		}
	}
}
