package net.minecraft.block;

import net.minecraft.item.Itemable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StoneBlock extends Block {
	public StoneBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Blocks.COBBLESTONE;
	}
}
