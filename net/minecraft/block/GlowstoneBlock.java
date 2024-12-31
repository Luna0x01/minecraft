package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class GlowstoneBlock extends Block {
	public GlowstoneBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public int method_397(BlockState blockState, int i, World world, BlockPos blockPos, Random random) {
		return MathHelper.clamp(this.getDropCount(blockState, random) + random.nextInt(i + 1), 1, 4);
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 2 + random.nextInt(3);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.GLOWSTONE_DUST;
	}
}
