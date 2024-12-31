package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpringFeature extends Feature {
	private Block block;

	public SpringFeature(Block block) {
		this.block = block;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		if (world.getBlockState(blockPos.up()).getBlock() != Blocks.STONE) {
			return false;
		} else if (world.getBlockState(blockPos.down()).getBlock() != Blocks.STONE) {
			return false;
		} else if (world.getBlockState(blockPos).getBlock().getMaterial() != Material.AIR && world.getBlockState(blockPos).getBlock() != Blocks.STONE) {
			return false;
		} else {
			int i = 0;
			if (world.getBlockState(blockPos.west()).getBlock() == Blocks.STONE) {
				i++;
			}

			if (world.getBlockState(blockPos.east()).getBlock() == Blocks.STONE) {
				i++;
			}

			if (world.getBlockState(blockPos.north()).getBlock() == Blocks.STONE) {
				i++;
			}

			if (world.getBlockState(blockPos.south()).getBlock() == Blocks.STONE) {
				i++;
			}

			int j = 0;
			if (world.isAir(blockPos.west())) {
				j++;
			}

			if (world.isAir(blockPos.east())) {
				j++;
			}

			if (world.isAir(blockPos.north())) {
				j++;
			}

			if (world.isAir(blockPos.south())) {
				j++;
			}

			if (i == 3 && j == 1) {
				world.setBlockState(blockPos, this.block.getDefaultState(), 2);
				world.scheduleTick(this.block, blockPos, random);
			}

			return true;
		}
	}
}
