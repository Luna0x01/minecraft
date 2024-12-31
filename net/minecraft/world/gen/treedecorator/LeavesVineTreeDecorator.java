package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;

public class LeavesVineTreeDecorator extends TreeDecorator {
	public static final Codec<LeavesVineTreeDecorator> CODEC = Codec.unit(() -> LeavesVineTreeDecorator.INSTANCE);
	public static final LeavesVineTreeDecorator INSTANCE = new LeavesVineTreeDecorator();

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.LEAVE_VINE;
	}

	@Override
	public void generate(
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions
	) {
		leavesPositions.forEach(pos -> {
			if (random.nextInt(4) == 0) {
				BlockPos blockPos = pos.west();
				if (Feature.isAir(world, blockPos)) {
					placeVines(world, blockPos, VineBlock.EAST, replacer);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = pos.east();
				if (Feature.isAir(world, blockPos2)) {
					placeVines(world, blockPos2, VineBlock.WEST, replacer);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos3 = pos.north();
				if (Feature.isAir(world, blockPos3)) {
					placeVines(world, blockPos3, VineBlock.SOUTH, replacer);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos4 = pos.south();
				if (Feature.isAir(world, blockPos4)) {
					placeVines(world, blockPos4, VineBlock.NORTH, replacer);
				}
			}
		});
	}

	private static void placeVines(TestableWorld world, BlockPos pos, BooleanProperty facing, BiConsumer<BlockPos, BlockState> replacer) {
		placeVine(replacer, pos, facing);
		int i = 4;

		for (BlockPos var5 = pos.down(); Feature.isAir(world, var5) && i > 0; i--) {
			placeVine(replacer, var5, facing);
			var5 = var5.down();
		}
	}
}
