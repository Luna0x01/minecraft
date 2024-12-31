package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3735;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class EndExitPortalFeature extends class_3844<class_3871> {
	public static final BlockPos ORIGIN = BlockPos.ORIGIN;
	private final boolean open;

	public EndExitPortalFeature(boolean bl) {
		this.open = bl;
	}

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		for (BlockPos.Mutable mutable : BlockPos.mutableIterate(
			new BlockPos(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4), new BlockPos(blockPos.getX() + 4, blockPos.getY() + 32, blockPos.getZ() + 4)
		)) {
			double d = mutable.distanceTo(blockPos.getX(), mutable.getY(), blockPos.getZ());
			if (d <= 3.5) {
				if (mutable.getY() < blockPos.getY()) {
					if (d <= 2.5) {
						this.method_17344(iWorld, mutable, Blocks.BEDROCK.getDefaultState());
					} else if (mutable.getY() < blockPos.getY()) {
						this.method_17344(iWorld, mutable, Blocks.END_STONE.getDefaultState());
					}
				} else if (mutable.getY() > blockPos.getY()) {
					this.method_17344(iWorld, mutable, Blocks.AIR.getDefaultState());
				} else if (d > 2.5) {
					this.method_17344(iWorld, mutable, Blocks.BEDROCK.getDefaultState());
				} else if (this.open) {
					this.method_17344(iWorld, new BlockPos(mutable), Blocks.END_PORTAL.getDefaultState());
				} else {
					this.method_17344(iWorld, new BlockPos(mutable), Blocks.AIR.getDefaultState());
				}
			}
		}

		for (int i = 0; i < 4; i++) {
			this.method_17344(iWorld, blockPos.up(i), Blocks.BEDROCK.getDefaultState());
		}

		BlockPos blockPos2 = blockPos.up(2);

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			this.method_17344(iWorld, blockPos2.offset(direction), Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, direction));
		}

		return true;
	}
}
