package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Leaves1Block;
import net.minecraft.block.Log1Block;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BirchTreeFeature extends FoliageFeature {
	private static final BlockState LOG = Blocks.LOG.getDefaultState().with(Log1Block.VARIANT, PlanksBlock.WoodType.BIRCH);
	private static final BlockState LEAVES = Blocks.LEAVES
		.getDefaultState()
		.with(Leaves1Block.VARIANT, PlanksBlock.WoodType.BIRCH)
		.with(Leaves1Block.CHECK_DECAY, false);
	private boolean generateTall;

	public BirchTreeFeature(boolean bl, boolean bl2) {
		super(bl);
		this.generateTall = bl2;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int i = random.nextInt(3) + 5;
		if (this.generateTall) {
			i += random.nextInt(7);
		}

		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == blockPos.getY()) {
					k = 0;
				}

				if (j >= blockPos.getY() + 1 + i - 2) {
					k = 2;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; l++) {
					for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; m++) {
						if (j < 0 || j >= 256) {
							bl = false;
						} else if (!this.isBlockReplaceable(world.getBlockState(mutable.setPosition(l, j, m)).getBlock())) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else {
				Block block = world.getBlockState(blockPos.down()).getBlock();
				if ((block == Blocks.GRASS || block == Blocks.DIRT || block == Blocks.FARMLAND) && blockPos.getY() < 256 - i - 1) {
					this.setDirt(world, blockPos.down());

					for (int n = blockPos.getY() - 3 + i; n <= blockPos.getY() + i; n++) {
						int o = n - (blockPos.getY() + i);
						int p = 1 - o / 2;

						for (int q = blockPos.getX() - p; q <= blockPos.getX() + p; q++) {
							int r = q - blockPos.getX();

							for (int s = blockPos.getZ() - p; s <= blockPos.getZ() + p; s++) {
								int t = s - blockPos.getZ();
								if (Math.abs(r) != p || Math.abs(t) != p || random.nextInt(2) != 0 && o != 0) {
									BlockPos blockPos2 = new BlockPos(q, n, s);
									Material material = world.getBlockState(blockPos2).getMaterial();
									if (material == Material.AIR || material == Material.FOLIAGE) {
										this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, LEAVES);
									}
								}
							}
						}
					}

					for (int u = 0; u < i; u++) {
						Material material2 = world.getBlockState(blockPos.up(u)).getMaterial();
						if (material2 == Material.AIR || material2 == Material.FOLIAGE) {
							this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.up(u), LOG);
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
}
