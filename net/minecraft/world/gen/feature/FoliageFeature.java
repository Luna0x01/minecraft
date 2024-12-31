package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class FoliageFeature extends Feature {
	public FoliageFeature(boolean bl) {
		super(bl);
	}

	protected boolean isBlockReplaceable(Block block) {
		Material material = block.getMaterial();
		return material == Material.AIR
			|| material == Material.FOLIAGE
			|| block == Blocks.GRASS
			|| block == Blocks.DIRT
			|| block == Blocks.LOG
			|| block == Blocks.LOG2
			|| block == Blocks.SAPLING
			|| block == Blocks.VINE;
	}

	public void generateSurroundingFeatures(World world, Random random, BlockPos pos) {
	}

	protected void setDirt(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() != Blocks.DIRT) {
			this.setBlockStateWithoutUpdatingNeighbors(world, pos, Blocks.DIRT.getDefaultState());
		}
	}
}
