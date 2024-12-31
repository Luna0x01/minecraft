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
				int n = 1;
				if (m - blockPos.getY() < j) {
					n = 0;
				} else {
					n = l;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int o = blockPos.getX() - n; o <= blockPos.getX() + n && bl; o++) {
					for (int p = blockPos.getZ() - n; p <= blockPos.getZ() + n && bl; p++) {
						if (m >= 0 && m < 256) {
							Block block = world.getBlockState(mutable.setPosition(o, m, p)).getBlock();
							if (block.getMaterial() != Material.AIR && block.getMaterial() != Material.FOLIAGE) {
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
				Block block2 = world.getBlockState(blockPos.down()).getBlock();
				if ((block2 == Blocks.GRASS || block2 == Blocks.DIRT || block2 == Blocks.FARMLAND) && blockPos.getY() < 256 - i - 1) {
					this.setDirt(world, blockPos.down());
					int q = random.nextInt(2);
					int r = 1;
					int s = 0;

					for (int t = 0; t <= k; t++) {
						int u = blockPos.getY() + i - t;

						for (int v = blockPos.getX() - q; v <= blockPos.getX() + q; v++) {
							int w = v - blockPos.getX();

							for (int x = blockPos.getZ() - q; x <= blockPos.getZ() + q; x++) {
								int y = x - blockPos.getZ();
								if (Math.abs(w) != q || Math.abs(y) != q || q <= 0) {
									BlockPos blockPos2 = new BlockPos(v, u, x);
									if (!world.getBlockState(blockPos2).getBlock().isFullBlock()) {
										this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, LEAVES);
									}
								}
							}
						}

						if (q >= r) {
							q = s;
							s = 1;
							if (++r > l) {
								r = l;
							}
						} else {
							q++;
						}
					}

					int z = random.nextInt(3);

					for (int aa = 0; aa < i - z; aa++) {
						Block block3 = world.getBlockState(blockPos.up(aa)).getBlock();
						if (block3.getMaterial() == Material.AIR || block3.getMaterial() == Material.FOLIAGE) {
							this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.up(aa), LOG);
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
