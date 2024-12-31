package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;

public class class_3548 extends BlockItem {
	public class_3548(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	protected boolean method_16013(ItemPlacementContext itemPlacementContext, BlockState blockState) {
		itemPlacementContext.getWorld().setBlockState(itemPlacementContext.getBlockPos().up(), Blocks.AIR.getDefaultState(), 27);
		return super.method_16013(itemPlacementContext, blockState);
	}
}
