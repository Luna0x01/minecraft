package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3846;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class IceDiskFeature extends class_3844<class_3846> {
	private final Block iceBlock = Blocks.PACKED_ICE;

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3846 arg) {
		while (iWorld.method_8579(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (iWorld.getBlockState(blockPos).getBlock() != Blocks.SNOW_BLOCK) {
			return false;
		} else {
			int i = random.nextInt(arg.field_19204) + 2;
			int j = 1;

			for (int k = blockPos.getX() - i; k <= blockPos.getX() + i; k++) {
				for (int l = blockPos.getZ() - i; l <= blockPos.getZ() + i; l++) {
					int m = k - blockPos.getX();
					int n = l - blockPos.getZ();
					if (m * m + n * n <= i * i) {
						for (int o = blockPos.getY() - 1; o <= blockPos.getY() + 1; o++) {
							BlockPos blockPos2 = new BlockPos(k, o, l);
							Block block = iWorld.getBlockState(blockPos2).getBlock();
							if (Block.method_16588(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
								iWorld.setBlockState(blockPos2, this.iceBlock.getDefaultState(), 2);
							}
						}
					}
				}
			}

			return true;
		}
	}
}
