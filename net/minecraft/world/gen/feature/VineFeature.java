package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class VineFeature extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (int i = blockPos.getY(); i < 256; i++) {
			mutable.set(blockPos);
			mutable.method_19934(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			mutable.setY(i);
			if (iWorld.method_8579(mutable)) {
				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					BlockState blockState = Blocks.VINE.getDefaultState().withProperty(VineBlock.method_16761(direction), Boolean.valueOf(true));
					if (blockState.canPlaceAt(iWorld, mutable)) {
						iWorld.setBlockState(mutable, blockState, 2);
						break;
					}
				}
			}
		}

		return true;
	}
}
