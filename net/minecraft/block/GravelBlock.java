package net.minecraft.block;

import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GravelBlock extends FallingBlock {
	public GravelBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		if (fortuneLevel > 3) {
			fortuneLevel = 3;
		}

		return (Itemable)(world.random.nextInt(10 - fortuneLevel * 3) == 0 ? Items.FLINT : super.getDroppedItem(state, world, pos, fortuneLevel));
	}

	@Override
	public int getColor(BlockState state) {
		return -8356741;
	}
}
