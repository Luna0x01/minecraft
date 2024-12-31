package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class class_3721 extends DoublePlantBlock {
	public static final EnumProperty<DoubleBlockHalf> field_18472 = DoublePlantBlock.field_18302;
	private final Block field_18473;

	public class_3721(Block block, Block.Builder builder) {
		super(builder);
		this.field_18473 = block;
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext itemPlacementContext) {
		boolean bl = super.canReplace(state, itemPlacementContext);
		return bl && itemPlacementContext.getItemStack().getItem() == this.getItem() ? false : bl;
	}

	@Override
	protected void method_16670(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
		if (itemStack.getItem() == Items.SHEARS) {
			onBlockBreak(world, blockPos, new ItemStack(this.field_18473, 2));
		} else {
			super.method_16670(blockState, world, blockPos, itemStack);
		}
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return state.getProperty(field_18472) == DoubleBlockHalf.LOWER && this == Blocks.TALL_GRASS && world.random.nextInt(8) == 0 ? Items.WHEAT_SEEDS : Items.AIR;
	}
}
