package net.minecraft.world.gen.feature;

import com.google.common.base.Predicates;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DesertWellFeature extends Feature {
	private static final BlockStatePredicate sandType = BlockStatePredicate.create(Blocks.SAND)
		.setProperty(SandBlock.sandType, Predicates.equalTo(SandBlock.SandType.SAND));
	private final BlockState sandstoneSlab = Blocks.STONE_SLAB
		.getDefaultState()
		.with(StoneSlabBlock.VARIANT, StoneSlabBlock.SlabType.SANDSTONE)
		.with(SlabBlock.HALF, SlabBlock.SlabType.BOTTOM);
	private final BlockState sandstone = Blocks.SANDSTONE.getDefaultState();
	private final BlockState flowingWater = Blocks.FLOWING_WATER.getDefaultState();

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		while (world.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (!sandType.apply(world.getBlockState(blockPos))) {
			return false;
		} else {
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if (world.isAir(blockPos.add(i, -1, j)) && world.isAir(blockPos.add(i, -2, j))) {
						return false;
					}
				}
			}

			for (int k = -1; k <= 0; k++) {
				for (int l = -2; l <= 2; l++) {
					for (int m = -2; m <= 2; m++) {
						world.setBlockState(blockPos.add(l, k, m), this.sandstone, 2);
					}
				}
			}

			world.setBlockState(blockPos, this.flowingWater, 2);

			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				world.setBlockState(blockPos.offset(direction), this.flowingWater, 2);
			}

			for (int n = -2; n <= 2; n++) {
				for (int o = -2; o <= 2; o++) {
					if (n == -2 || n == 2 || o == -2 || o == 2) {
						world.setBlockState(blockPos.add(n, 1, o), this.sandstone, 2);
					}
				}
			}

			world.setBlockState(blockPos.add(2, 1, 0), this.sandstoneSlab, 2);
			world.setBlockState(blockPos.add(-2, 1, 0), this.sandstoneSlab, 2);
			world.setBlockState(blockPos.add(0, 1, 2), this.sandstoneSlab, 2);
			world.setBlockState(blockPos.add(0, 1, -2), this.sandstoneSlab, 2);

			for (int p = -1; p <= 1; p++) {
				for (int q = -1; q <= 1; q++) {
					if (p == 0 && q == 0) {
						world.setBlockState(blockPos.add(p, 4, q), this.sandstone, 2);
					} else {
						world.setBlockState(blockPos.add(p, 4, q), this.sandstoneSlab, 2);
					}
				}
			}

			for (int r = 1; r <= 3; r++) {
				world.setBlockState(blockPos.add(-1, r, -1), this.sandstone, 2);
				world.setBlockState(blockPos.add(-1, r, 1), this.sandstone, 2);
				world.setBlockState(blockPos.add(1, r, -1), this.sandstone, 2);
				world.setBlockState(blockPos.add(1, r, 1), this.sandstone, 2);
			}

			return true;
		}
	}
}
