package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BookshelfBlock extends Block {
	public BookshelfBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 3;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.BOOK;
	}
}
