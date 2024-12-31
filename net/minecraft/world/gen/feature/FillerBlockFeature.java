package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FillerBlockFeature extends Feature {
	private Block block;

	public FillerBlockFeature(Block block) {
		this.block = block;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		if (world.isAir(blockPos) && world.getBlockState(blockPos.down()).getBlock() == this.block) {
			int i = random.nextInt(32) + 6;
			int j = random.nextInt(4) + 1;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int k = blockPos.getX() - j; k <= blockPos.getX() + j; k++) {
				for (int l = blockPos.getZ() - j; l <= blockPos.getZ() + j; l++) {
					int m = k - blockPos.getX();
					int n = l - blockPos.getZ();
					if (m * m + n * n <= j * j + 1 && world.getBlockState(mutable.setPosition(k, blockPos.getY() - 1, l)).getBlock() != this.block) {
						return false;
					}
				}
			}

			for (int o = blockPos.getY(); o < blockPos.getY() + i && o < 256; o++) {
				for (int p = blockPos.getX() - j; p <= blockPos.getX() + j; p++) {
					for (int q = blockPos.getZ() - j; q <= blockPos.getZ() + j; q++) {
						int r = p - blockPos.getX();
						int s = q - blockPos.getZ();
						if (r * r + s * s <= j * j + 1) {
							world.setBlockState(new BlockPos(p, o, q), Blocks.OBSIDIAN.getDefaultState(), 2);
						}
					}
				}
			}

			Entity entity = new EndCrystalEntity(world);
			entity.refreshPositionAndAngles(
				(double)((float)blockPos.getX() + 0.5F), (double)(blockPos.getY() + i), (double)((float)blockPos.getZ() + 0.5F), random.nextFloat() * 360.0F, 0.0F
			);
			world.spawnEntity(entity);
			world.setBlockState(blockPos.up(i), Blocks.BEDROCK.getDefaultState(), 2);
			return true;
		} else {
			return false;
		}
	}
}
