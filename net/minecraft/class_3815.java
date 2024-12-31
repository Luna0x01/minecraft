package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.class_2737;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3815 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		for (BlockState blockState = iWorld.getBlockState(blockPos);
			(blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) && blockPos.getY() > 1;
			blockState = iWorld.getBlockState(blockPos)
		) {
			blockPos = blockPos.down();
		}

		if (blockPos.getY() < 1) {
			return false;
		} else {
			blockPos = blockPos.up();

			for (int i = 0; i < 4; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
				if (iWorld.method_8579(blockPos2) && iWorld.getBlockState(blockPos2.down()).method_16913()) {
					iWorld.setBlockState(blockPos2, Blocks.CHEST.getDefaultState(), 2);
					class_2737.method_16833(iWorld, random, blockPos2, LootTables.SPAWN_BONUS_CHEST_CHEST);
					BlockPos blockPos3 = blockPos2.east();
					BlockPos blockPos4 = blockPos2.west();
					BlockPos blockPos5 = blockPos2.north();
					BlockPos blockPos6 = blockPos2.south();
					if (iWorld.method_8579(blockPos4) && iWorld.getBlockState(blockPos4.down()).method_16913()) {
						iWorld.setBlockState(blockPos4, Blocks.TORCH.getDefaultState(), 2);
					}

					if (iWorld.method_8579(blockPos3) && iWorld.getBlockState(blockPos3.down()).method_16913()) {
						iWorld.setBlockState(blockPos3, Blocks.TORCH.getDefaultState(), 2);
					}

					if (iWorld.method_8579(blockPos5) && iWorld.getBlockState(blockPos5.down()).method_16913()) {
						iWorld.setBlockState(blockPos5, Blocks.TORCH.getDefaultState(), 2);
					}

					if (iWorld.method_8579(blockPos6) && iWorld.getBlockState(blockPos6.down()).method_16913()) {
						iWorld.setBlockState(blockPos6, Blocks.TORCH.getDefaultState(), 2);
					}

					return true;
				}
			}

			return false;
		}
	}
}
