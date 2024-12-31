package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class DesertWellFeature extends Feature<DefaultFeatureConfig> {
	private static final BlockStatePredicate CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.field_10102);
	private final BlockState slab = Blocks.field_10007.getDefaultState();
	private final BlockState wall = Blocks.field_9979.getDefaultState();
	private final BlockState fluidInside = Blocks.field_10382.getDefaultState();

	public DesertWellFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		blockPos = blockPos.up();

		while (iWorld.isAir(blockPos) && blockPos.getY() > 2) {
			blockPos = blockPos.down();
		}

		if (!CAN_GENERATE.test(iWorld.getBlockState(blockPos))) {
			return false;
		} else {
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					if (iWorld.isAir(blockPos.add(i, -1, j)) && iWorld.isAir(blockPos.add(i, -2, j))) {
						return false;
					}
				}
			}

			for (int k = -1; k <= 0; k++) {
				for (int l = -2; l <= 2; l++) {
					for (int m = -2; m <= 2; m++) {
						iWorld.setBlockState(blockPos.add(l, k, m), this.wall, 2);
					}
				}
			}

			iWorld.setBlockState(blockPos, this.fluidInside, 2);

			for (Direction direction : Direction.Type.field_11062) {
				iWorld.setBlockState(blockPos.offset(direction), this.fluidInside, 2);
			}

			for (int n = -2; n <= 2; n++) {
				for (int o = -2; o <= 2; o++) {
					if (n == -2 || n == 2 || o == -2 || o == 2) {
						iWorld.setBlockState(blockPos.add(n, 1, o), this.wall, 2);
					}
				}
			}

			iWorld.setBlockState(blockPos.add(2, 1, 0), this.slab, 2);
			iWorld.setBlockState(blockPos.add(-2, 1, 0), this.slab, 2);
			iWorld.setBlockState(blockPos.add(0, 1, 2), this.slab, 2);
			iWorld.setBlockState(blockPos.add(0, 1, -2), this.slab, 2);

			for (int p = -1; p <= 1; p++) {
				for (int q = -1; q <= 1; q++) {
					if (p == 0 && q == 0) {
						iWorld.setBlockState(blockPos.add(p, 4, q), this.wall, 2);
					} else {
						iWorld.setBlockState(blockPos.add(p, 4, q), this.slab, 2);
					}
				}
			}

			for (int r = 1; r <= 3; r++) {
				iWorld.setBlockState(blockPos.add(-1, r, -1), this.wall, 2);
				iWorld.setBlockState(blockPos.add(-1, r, 1), this.wall, 2);
				iWorld.setBlockState(blockPos.add(1, r, -1), this.wall, 2);
				iWorld.setBlockState(blockPos.add(1, r, 1), this.wall, 2);
			}

			return true;
		}
	}
}
