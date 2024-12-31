package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClayBlock extends Block {
	public ClayBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.CLAY_BALL;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 4;
	}
}
