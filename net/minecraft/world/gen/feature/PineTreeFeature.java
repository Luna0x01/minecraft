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

public class PineTreeFeature extends FoliageFeature {
	private static final BlockState LOG = Blocks.LOG.getDefaultState().with(Log1Block.VARIANT, PlanksBlock.WoodType.SPRUCE);
	private static final BlockState LEAVES = Blocks.LEAVES
		.getDefaultState()
		.with(Leaves1Block.VARIANT, PlanksBlock.WoodType.SPRUCE)
		.with(LeavesBlock.CHECK_DECAY, false);

	public PineTreeFeature() {
		super(false);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int i = random.nextInt(5) + 7;
		int j = i - random.nextInt(2) - 3;
		int k = i - j;
		int l = 1 + random.nextInt(k + 1);
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			boolean bl = true;

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
						if (m < 0 || m >= 256) {
							bl = false;
						} else if (!this.isBlockReplaceable(world.getBlockState(mutable.setPosition(o, m, p)).getBlock())) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else {
				Block block = world.getBlockState(blockPos.down()).getBlock();
				if ((block == Blocks.GRASS || block == Blocks.DIRT) && blockPos.getY() < 256 - i - 1) {
					this.setDirt(world, blockPos.down());
					int q = 0;

					for (int r = blockPos.getY() + i; r >= blockPos.getY() + j; r--) {
						for (int s = blockPos.getX() - q; s <= blockPos.getX() + q; s++) {
							int t = s - blockPos.getX();

							for (int u = blockPos.getZ() - q; u <= blockPos.getZ() + q; u++) {
								int v = u - blockPos.getZ();
								if (Math.abs(t) != q || Math.abs(v) != q || q <= 0) {
									BlockPos blockPos2 = new BlockPos(s, r, u);
									if (!world.getBlockState(blockPos2).isFullBlock()) {
										this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, LEAVES);
									}
								}
							}
						}

						if (q >= 1 && r == blockPos.getY() + j + 1) {
							q--;
						} else if (q < l) {
							q++;
						}
					}

					for (int w = 0; w < i - 1; w++) {
						Material material = world.getBlockState(blockPos.up(w)).getMaterial();
						if (material == Material.AIR || material == Material.FOLIAGE) {
							this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.up(w), LOG);
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
