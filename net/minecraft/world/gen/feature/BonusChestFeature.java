package net.minecraft.world.gen.feature;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BonusChestFeature extends Feature {
	private final List<WeightedRandomChestContent> lootTable;
	private final int maxItemsToGenerate;

	public BonusChestFeature(List<WeightedRandomChestContent> list, int i) {
		this.lootTable = list;
		this.maxItemsToGenerate = i;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		Block block;
		while (((block = world.getBlockState(blockPos).getBlock()).getMaterial() == Material.AIR || block.getMaterial() == Material.FOLIAGE) && blockPos.getY() > 1) {
			blockPos = blockPos.down();
		}

		if (blockPos.getY() < 1) {
			return false;
		} else {
			blockPos = blockPos.up();

			for (int i = 0; i < 4; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
				if (world.isAir(blockPos2) && World.isOpaque(world, blockPos2.down())) {
					world.setBlockState(blockPos2, Blocks.CHEST.getDefaultState(), 2);
					BlockEntity blockEntity = world.getBlockEntity(blockPos2);
					if (blockEntity instanceof ChestBlockEntity) {
						WeightedRandomChestContent.fillInventory(random, this.lootTable, (ChestBlockEntity)blockEntity, this.maxItemsToGenerate);
					}

					BlockPos blockPos3 = blockPos2.east();
					BlockPos blockPos4 = blockPos2.west();
					BlockPos blockPos5 = blockPos2.north();
					BlockPos blockPos6 = blockPos2.south();
					if (world.isAir(blockPos4) && World.isOpaque(world, blockPos4.down())) {
						world.setBlockState(blockPos4, Blocks.TORCH.getDefaultState(), 2);
					}

					if (world.isAir(blockPos3) && World.isOpaque(world, blockPos3.down())) {
						world.setBlockState(blockPos3, Blocks.TORCH.getDefaultState(), 2);
					}

					if (world.isAir(blockPos5) && World.isOpaque(world, blockPos5.down())) {
						world.setBlockState(blockPos5, Blocks.TORCH.getDefaultState(), 2);
					}

					if (world.isAir(blockPos6) && World.isOpaque(world, blockPos6.down())) {
						world.setBlockState(blockPos6, Blocks.TORCH.getDefaultState(), 2);
					}

					return true;
				}
			}

			return false;
		}
	}
}
