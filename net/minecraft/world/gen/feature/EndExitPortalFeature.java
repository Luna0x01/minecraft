package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EndExitPortalFeature extends Feature {
	public static final BlockPos ORIGIN = BlockPos.ORIGIN;
	public static final BlockPos field_12980 = new BlockPos(ORIGIN.getX() - 4 & -16, 0, ORIGIN.getZ() - 4 & -16);
	private final boolean open;

	public EndExitPortalFeature(boolean bl) {
		this.open = bl;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(
			new BlockPos(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4), new BlockPos(blockPos.getX() + 4, blockPos.getY() + 32, blockPos.getZ() + 4)
		)) {
			double d = mutable.distanceTo(blockPos.getX(), mutable.getY(), blockPos.getZ());
			if (d <= 3.5) {
				if (mutable.getY() < blockPos.getY()) {
					if (d <= 2.5) {
						this.setBlockStateWithoutUpdatingNeighbors(world, mutable, Blocks.BEDROCK.getDefaultState());
					} else if (mutable.getY() < blockPos.getY()) {
						this.setBlockStateWithoutUpdatingNeighbors(world, mutable, Blocks.END_STONE.getDefaultState());
					}
				} else if (mutable.getY() > blockPos.getY()) {
					this.setBlockStateWithoutUpdatingNeighbors(world, mutable, Blocks.AIR.getDefaultState());
				} else if (d > 2.5) {
					this.setBlockStateWithoutUpdatingNeighbors(world, mutable, Blocks.BEDROCK.getDefaultState());
				} else if (this.open) {
					this.setBlockStateWithoutUpdatingNeighbors(world, new BlockPos(mutable), Blocks.END_PORTAL.getDefaultState());
				} else {
					this.setBlockStateWithoutUpdatingNeighbors(world, new BlockPos(mutable), Blocks.AIR.getDefaultState());
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.up(i), Blocks.BEDROCK.getDefaultState());
		}

		BlockPos blockPos2 = blockPos.up(2);

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2.offset(direction), Blocks.TORCH.getDefaultState().with(TorchBlock.FACING, direction));
		}

		return true;
	}
}
