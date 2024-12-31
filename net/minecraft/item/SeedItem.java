package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
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
