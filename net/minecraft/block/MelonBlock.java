package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MelonBlock extends GourdBlock {
	protected MelonBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.MELON;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 3 + random.nextInt(5);
	}

	@Override
	public int method_397(BlockState blockState, int i, World world, BlockPos blockPos, Random random) {
		return Math.min(9, this.getDropCount(blockState, random) + random.nextInt(1 + i));
	}

	@Override
	public StemBlock getStem() {
		return (StemBlock)Blocks.MELON_STEM;
	}

	@Override
	public AttachedStemBlock getAttachedStem() {
		return (AttachedStemBlock)Blocks.ATTACHED_MELON_STEM;
	}
}
