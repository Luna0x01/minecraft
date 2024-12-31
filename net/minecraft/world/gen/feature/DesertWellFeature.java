package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DesertWellFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.SAND);
	private final BlockState slab = Blocks.SANDSTONE_SLAB.getDefaultState();
	private final BlockState wall = Blocks.SANDSTONE.getDefaultState();
	private final BlockState fluidInside = Blocks.WATER.getDefaultState();

	public DesertWellFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		blockPos = blockPos.up();

		while (structureWorldAccess.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (!CAN_GENERATE.test(structureWorldAccess.getBlockState(blockPos))) {
			return false;
		} else {
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if (structureWorldAccess.isAir(blockPos.add(i, -1, j)) && structureWorldAccess.isAir(blockPos.add(i, -2, j))) {
						return false;
					}
				}
			}

			for (int k = -1; k <= 0; k++) {
				for (int l = -2; l <= 2; l++) {
					for (int m = -2; m <= 2; m++) {
						structureWorldAccess.setBlockState(blockPos.add(l, k, m), this.wall, 2);
					}
				}
			}

			structureWorldAccess.setBlockState(blockPos, this.fluidInside, 2);

			for (Direction direction : Direction.Type.HORIZONTAL) {
				structureWorldAccess.setBlockState(blockPos.offset(direction), this.fluidInside, 2);
			}

			for (int n = -2; n <= 2; n++) {
				for (int o = -2; o <= 2; o++) {
					if (n == -2 || n == 2 || o == -2 || o == 2) {
						structureWorldAccess.setBlockState(blockPos.add(n, 1, o), this.wall, 2);
					}
				}
			}

			structureWorldAccess.setBlockState(blockPos.add(2, 1, 0), this.slab, 2);
			structureWorldAccess.setBlockState(blockPos.add(-2, 1, 0), this.slab, 2);
			structureWorldAccess.setBlockState(blockPos.add(0, 1, 2), this.slab, 2);
			structureWorldAccess.setBlockState(blockPos.add(0, 1, -2), this.slab, 2);

			for (int p = -1; p <= 1; p++) {
				for (int q = -1; q <= 1; q++) {
					if (p == 0 && q == 0) {
						structureWorldAccess.setBlockState(blockPos.add(p, 4, q), this.wall, 2);
					} else {
						structureWorldAccess.setBlockState(blockPos.add(p, 4, q), this.slab, 2);
					}
				}
			}

			for (int r = 1; r <= 3; r++) {
				structureWorldAccess.setBlockState(blockPos.add(-1, r, -1), this.wall, 2);
				structureWorldAccess.setBlockState(blockPos.add(-1, r, 1), this.wall, 2);
				structureWorldAccess.setBlockState(blockPos.add(1, r, -1), this.wall, 2);
				structureWorldAccess.setBlockState(blockPos.add(1, r, 1), this.wall, 2);
			}

			return true;
		}
	}
}
