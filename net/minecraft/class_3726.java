package net.minecraft;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public abstract class class_3726 extends class_3725 {
	protected class_3726(Block.Builder builder) {
		super(builder);
	}

	private static boolean method_16737(RenderBlockView renderBlockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return renderBlockView.method_16358(blockPos2) >= 4
			|| renderBlockView.getBlockState(blockPos2).method_16885(renderBlockView, blockPos2) < renderBlockView.getMaxLightLevel();
	}

	private static boolean method_16738(RenderBlockView renderBlockView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.up();
		return renderBlockView.method_16358(blockPos2) >= 4
			&& renderBlockView.getBlockState(blockPos2).method_16885(renderBlockView, blockPos2) < renderBlockView.getMaxLightLevel()
			&& !renderBlockView.getFluidState(blockPos2).matches(FluidTags.WATER);
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) {
			if (!method_16737(world, pos)) {
				world.setBlockState(pos, Blocks.DIRT.getDefaultState());
			} else {
				if (world.method_16358(pos.up()) >= 9) {
					for (int i = 0; i < 4; i++) {
						BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
						if (!world.method_16338(blockPos)) {
							return;
						}

						if (world.getBlockState(blockPos).getBlock() == Blocks.DIRT && method_16738(world, blockPos)) {
							world.setBlockState(blockPos, this.getDefaultState());
						}
					}
				}
			}
		}
	}
}
