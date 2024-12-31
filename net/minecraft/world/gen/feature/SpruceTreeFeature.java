package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Leaves1Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Log1Block;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpruceTreeFeature extends FoliageFeature {
	private static final BlockState LOG = Blocks.LOG.getDefaultState().with(Log1Block.VARIANT, PlanksBlock.WoodType.SPRUCE);
	private static final BlockState LEAVES = Blocks.LEAVES
		.getDefaultState()
		.with(Leaves1Block.VARIANT, PlanksBlock.WoodType.SPRUCE)
		.with(LeavesBlock.CHECK_DECAY, false);

	public SpruceTreeFeature(boolean bl) {
		super(bl);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int i = random.nextInt(4) + 6;
		int j = 1 + random.nextInt(2);
		int k = i - j;
		int l = 2 + random.nextInt(2);
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int m = blockPos.getY(); m <= blockPos.getY() + 1 + i && bl; m++) {
				int n;
				if (m - blockPos.getY() < j) {
					n = 0;
				} else {
					n = l;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int p = blockPos.getX() - n; p <= blockPos.getX() + n && bl; p++) {
					for (int q = blockPos.getZ() - n; q <= blockPos.getZ() + n && bl; q++) {
						if (m >= 0 && m < 256) {
							Material material = world.getBlockState(mutable.setPosition(p, m, q)).getMaterial();
							if (material != Material.AIR && material != Material.FOLIAGE) {
								bl = false;
							}
						} else {
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
					int r = random.nextInt(2);
					int s = 1;
					int t = 0;

					for (int u = 0; u <= k; u++) {
						int v = blockPos.getY() + i - u;

						for (int w = blockPos.getX() - r; w <= blockPos.getX() + r; w++) {
							int x = w - blockPos.getX();

							for (int y = blockPos.getZ() - r; y <= blockPos.getZ() + r; y++) {
								int z = y - blockPos.getZ();
								if (Math.abs(x) != r || Math.abs(z) != r || r <= 0) {
									BlockPos blockPos2 = new BlockPos(w, v, y);
									if (!world.getBlockState(blockPos2).isFullBlock()) {
										this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, LEAVES);
									}
								}
							}
						}

						if (r >= s) {
							r = t;
							t = 1;
							if (++s > l) {
								s = l;
							}
						} else {
							r++;
						}
					}

					int aa = random.nextInt(3);

					for (int ab = 0; ab < i - aa; ab++) {
						Material material2 = world.getBlockState(blockPos.up(ab)).getMaterial();
						if (material2 == Material.AIR || material2 == Material.FOLIAGE) {
							this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.up(ab), LOG);
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
