package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class DesertWellFeature extends class_3844<class_3871> {
	private static final BlockStatePredicate sandType = BlockStatePredicate.create(Blocks.SAND);
	private final BlockState sandstoneSlab = Blocks.SANDSTONE_SLAB.getDefaultState();
	private final BlockState sandstone = Blocks.SANDSTONE.getDefaultState();
	private final BlockState flowingWater = Blocks.WATER.getDefaultState();

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		blockPos = blockPos.up();

		while (iWorld.method_8579(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (!sandType.test(iWorld.getBlockState(blockPos))) {
			return false;
		} else {
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if (iWorld.method_8579(blockPos.add(i, -1, j)) && iWorld.method_8579(blockPos.add(i, -2, j))) {
						return false;
					}
				}
			}

			for (int k = -1; k <= 0; k++) {
				for (int l = -2; l <= 2; l++) {
					for (int m = -2; m <= 2; m++) {
						iWorld.setBlockState(blockPos.add(l, k, m), this.sandstone, 2);
					}
				}
			}

			iWorld.setBlockState(blockPos, this.flowingWater, 2);

			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				iWorld.setBlockState(blockPos.offset(direction), this.flowingWater, 2);
			}

			for (int n = -2; n <= 2; n++) {
				for (int o = -2; o <= 2; o++) {
					if (n == -2 || n == 2 || o == -2 || o == 2) {
						iWorld.setBlockState(blockPos.add(n, 1, o), this.sandstone, 2);
					}
				}
			}

			iWorld.setBlockState(blockPos.add(2, 1, 0), this.sandstoneSlab, 2);
			iWorld.setBlockState(blockPos.add(-2, 1, 0), this.sandstoneSlab, 2);
			iWorld.setBlockState(blockPos.add(0, 1, 2), this.sandstoneSlab, 2);
			iWorld.setBlockState(blockPos.add(0, 1, -2), this.sandstoneSlab, 2);

			for (int p = -1; p <= 1; p++) {
				for (int q = -1; q <= 1; q++) {
					if (p == 0 && q == 0) {
						iWorld.setBlockState(blockPos.add(p, 4, q), this.sandstone, 2);
					} else {
						iWorld.setBlockState(blockPos.add(p, 4, q), this.sandstoneSlab, 2);
					}
				}
			}

			for (int r = 1; r <= 3; r++) {
				iWorld.setBlockState(blockPos.add(-1, r, -1), this.sandstone, 2);
				iWorld.setBlockState(blockPos.add(-1, r, 1), this.sandstone, 2);
				iWorld.setBlockState(blockPos.add(1, r, -1), this.sandstone, 2);
				iWorld.setBlockState(blockPos.add(1, r, 1), this.sandstone, 2);
			}

			return true;
		}
	}
}
