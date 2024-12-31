package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.loot.LootTables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BonusChestFeature extends Feature {
	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		BlockState blockState;
		while (((blockState = world.getBlockState(blockPos)).getMaterial() == Material.AIR || blockState.getMaterial() == Material.FOLIAGE) && blockPos.getY() > 1) {
			blockPos = blockPos.down();
		}

		if (blockPos.getY() < 1) {
			return false;
		} else {
			blockPos = blockPos.up();

			for (int i = 0; i < 4; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
				if (world.isAir(blockPos2) && world.getBlockState(blockPos2.down()).method_11739()) {
					world.setBlockState(blockPos2, Blocks.CHEST.getDefaultState(), 2);
					BlockEntity blockEntity = world.getBlockEntity(blockPos2);
					if (blockEntity instanceof ChestBlockEntity) {
						((ChestBlockEntity)blockEntity).method_11660(LootTables.SPAWN_BONUS_CHEST_CHEST, random.nextLong());
					}

					BlockPos blockPos3 = blockPos2.east();
					BlockPos blockPos4 = blockPos2.west();
					BlockPos blockPos5 = blockPos2.north();
					BlockPos blockPos6 = blockPos2.south();
					if (world.isAir(blockPos4) && world.getBlockState(blockPos4.down()).method_11739()) {
						world.setBlockState(blockPos4, Blocks.TORCH.getDefaultState(), 2);
					}

					if (world.isAir(blockPos3) && world.getBlockState(blockPos3.down()).method_11739()) {
						world.setBlockState(blockPos3, Blocks.TORCH.getDefaultState(), 2);
					}

					if (world.isAir(blockPos5) && world.getBlockState(blockPos5.down()).method_11739()) {
						world.setBlockState(blockPos5, Blocks.TORCH.getDefaultState(), 2);
					}

					if (world.isAir(blockPos6) && world.getBlockState(blockPos6.down()).method_11739()) {
						world.setBlockState(blockPos6, Blocks.TORCH.getDefaultState(), 2);
					}

					return true;
				}
			}

			return false;
		}
	}
}
