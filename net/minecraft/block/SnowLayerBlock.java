package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SnowLayerBlock extends Block {
	protected SnowLayerBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.SNOWBALL;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 4;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.method_16370(LightType.BLOCK, pos) > 11) {
			state.method_16867(world, pos, 0);
			world.method_8553(pos);
		}
	}
}
