package net.minecraft.world.gen.tree;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;

public class LeaveVineTreeDecorator extends TreeDecorator {
	public static final Codec<LeaveVineTreeDecorator> CODEC = Codec.unit(() -> LeaveVineTreeDecorator.INSTANCE);
	public static final LeaveVineTreeDecorator INSTANCE = new LeaveVineTreeDecorator();

	@Override
	protected TreeDecoratorType<?> getType() {
		return TreeDecoratorType.LEAVE_VINE;
	}

	@Override
	public void generate(
		StructureWorldAccess world, Random random, List<BlockPos> logPositions, List<BlockPos> leavesPositions, Set<BlockPos> placedStates, BlockBox box
	) {
		leavesPositions.forEach(pos -> {
			if (random.nextInt(4) == 0) {
				BlockPos blockPos = pos.west();
				if (Feature.isAir(world, blockPos)) {
					this.placeVines(world, blockPos, VineBlock.EAST, placedStates, box);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos2 = pos.east();
				if (Feature.isAir(world, blockPos2)) {
					this.placeVines(world, blockPos2, VineBlock.WEST, placedStates, box);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos3 = pos.north();
				if (Feature.isAir(world, blockPos3)) {
					this.placeVines(world, blockPos3, VineBlock.SOUTH, placedStates, box);
				}
			}

			if (random.nextInt(4) == 0) {
				BlockPos blockPos4 = pos.south();
				if (Feature.isAir(world, blockPos4)) {
					this.placeVines(world, blockPos4, VineBlock.NORTH, placedStates, box);
				}
			}
		});
	}

	private void placeVines(ModifiableTestableWorld world, BlockPos pos, BooleanProperty side, Set<BlockPos> placedStates, BlockBox box) {
		this.placeVine(world, pos, side, placedStates, box);
		int i = 4;

		for (BlockPos var7 = pos.down(); Feature.isAir(world, var7) && i > 0; i--) {
			this.placeVine(world, var7, side, placedStates, box);
			var7 = var7.down();
		}
	}
}
