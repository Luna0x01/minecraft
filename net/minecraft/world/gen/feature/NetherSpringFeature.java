package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetherSpringFeature extends Feature {
	private final Block block;
	private final boolean denySurfaceSpawn;

	public NetherSpringFeature(Block block, boolean bl) {
		this.block = block;
		this.denySurfaceSpawn = bl;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		if (world.getBlockState(blockPos.up()).getBlock() != Blocks.NETHERRACK) {
			return false;
		} else if (world.getBlockState(blockPos).getMaterial() != Material.AIR && world.getBlockState(blockPos).getBlock() != Blocks.NETHERRACK) {
			return false;
		} else {
			int i = 0;
			if (world.getBlockState(blockPos.west()).getBlock() == Blocks.NETHERRACK) {
				i++;
			}

			if (world.getBlockState(blockPos.east()).getBlock() == Blocks.NETHERRACK) {
				i++;
			}

			if (world.getBlockState(blockPos.north()).getBlock() == Blocks.NETHERRACK) {
				i++;
			}

			if (world.getBlockState(blockPos.south()).getBlock() == Blocks.NETHERRACK) {
				i++;
			}

			if (world.getBlockState(blockPos.down()).getBlock() == Blocks.NETHERRACK) {
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

			if (world.isAir(blockPos.down())) {
				j++;
			}

			if (!this.denySurfaceSpawn && i == 4 && j == 1 || i == 5) {
				BlockState blockState = this.block.getDefaultState();
				world.setBlockState(blockPos, blockState, 2);
				world.method_11482(blockPos, blockState, random);
			}

			return true;
		}
	}
}
