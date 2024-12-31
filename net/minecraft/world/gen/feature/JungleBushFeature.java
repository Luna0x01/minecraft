package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.class_3871;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class JungleBushFeature extends FoliageFeature<class_3871> {
	private final BlockState bushLeafState;
	private final BlockState bushLogState;

	public JungleBushFeature(BlockState blockState, BlockState blockState2) {
		super(false);
		this.bushLogState = blockState;
		this.bushLeafState = blockState2;
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
		for (BlockState blockState = iWorld.getBlockState(blockPos);
			(blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) && blockPos.getY() > 0;
			blockState = iWorld.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		Block block = iWorld.getBlockState(blockPos).getBlock();
		if (Block.method_16588(block) || block == Blocks.GRASS_BLOCK) {
			blockPos = blockPos.up();
			this.method_17293(set, iWorld, blockPos, this.bushLogState);

			for (int i = blockPos.getY(); i <= blockPos.getY() + 2; i++) {
				int j = i - blockPos.getY();
				int k = 2 - j;

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k; l++) {
					int m = l - blockPos.getX();

					for (int n = blockPos.getZ() - k; n <= blockPos.getZ() + k; n++) {
						int o = n - blockPos.getZ();
						if (Math.abs(m) != k || Math.abs(o) != k || random.nextInt(2) != 0) {
							BlockPos blockPos2 = new BlockPos(l, i, n);
							BlockState blockState2 = iWorld.getBlockState(blockPos2);
							if (blockState2.isAir() || blockState2.isIn(BlockTags.LEAVES)) {
								this.method_17344(iWorld, blockPos2, this.bushLeafState);
							}
						}
					}
				}
			}
		}

		return true;
	}
}
