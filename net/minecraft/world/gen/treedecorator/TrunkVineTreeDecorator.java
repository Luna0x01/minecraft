package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.Feature;

public class TrunkVineTreeDecorator extends TreeDecorator {
	public static final Codec<TrunkVineTreeDecorator> CODEC = Codec.unit(() -> TrunkVineTreeDecorator.INSTANCE);
	public static final TrunkVineTreeDecorator INSTANCE = new TrunkVineTreeDecorator();

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.TRUNK_VINE;
	}

	@Override
	public void generate(
		TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions
	) {
		logPositions.forEach(pos -> {
			if (random.nextInt(3) > 0) {
				BlockPos blockPos = pos.west();
				if (Feature.isAir(world, blockPos)) {
					placeVine(replacer, blockPos, VineBlock.EAST);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos2 = pos.east();
				if (Feature.isAir(world, blockPos2)) {
					placeVine(replacer, blockPos2, VineBlock.WEST);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos3 = pos.north();
				if (Feature.isAir(world, blockPos3)) {
					placeVine(replacer, blockPos3, VineBlock.SOUTH);
				}
			}

			if (random.nextInt(3) > 0) {
				BlockPos blockPos4 = pos.south();
				if (Feature.isAir(world, blockPos4)) {
					placeVine(replacer, blockPos4, VineBlock.NORTH);
				}
			}
		});
	}
}
