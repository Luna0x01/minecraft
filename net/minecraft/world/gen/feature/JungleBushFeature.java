package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class JungleBushFeature extends JungleTreeFeature {
	private final BlockState bushLeafState;
	private final BlockState bushLogState;

	public JungleBushFeature(BlockState blockState, BlockState blockState2) {
		super(false);
		this.bushLogState = blockState;
		this.bushLeafState = blockState2;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (BlockState blockState = world.getBlockState(blockPos);
			(blockState.getMaterial() == Material.AIR || blockState.getMaterial() == Material.FOLIAGE) && blockPos.getY() > 0;
			blockState = world.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		Block block = world.getBlockState(blockPos).getBlock();
		if (block == Blocks.DIRT || block == Blocks.GRASS) {
			blockPos = blockPos.up();
			this.setBlockStateWithoutUpdatingNeighbors(world, blockPos, this.bushLogState);

			for (int i = blockPos.getY(); i <= blockPos.getY() + 2; i++) {
				int j = i - blockPos.getY();
				int k = 2 - j;

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k; l++) {
					int m = l - blockPos.getX();

					for (int n = blockPos.getZ() - k; n <= blockPos.getZ() + k; n++) {
						int o = n - blockPos.getZ();
						if (Math.abs(m) != k || Math.abs(o) != k || random.nextInt(2) != 0) {
							BlockPos blockPos2 = new BlockPos(l, i, n);
							Material material = world.getBlockState(blockPos2).getMaterial();
							if (material == Material.AIR || material == Material.FOLIAGE) {
								this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, this.bushLeafState);
							}
						}
					}
				}
			}
		}

		return true;
	}
}
