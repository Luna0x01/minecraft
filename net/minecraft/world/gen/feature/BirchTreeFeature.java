package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.class_3871;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BirchTreeFeature extends FoliageFeature<class_3871> {
	private static final BlockState LOG = Blocks.BIRCH_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.BIRCH_LEAVES.getDefaultState();
	private final boolean generateTall;

	public BirchTreeFeature(boolean bl, boolean bl2) {
		super(bl);
		this.generateTall = bl2;
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
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
						} else if (!this.isBlockReplaceable(iWorld.getBlockState(mutable.setPosition(l, j, m)).getBlock())) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else {
				Block block = iWorld.getBlockState(blockPos.down()).getBlock();
				if ((block == Blocks.GRASS_BLOCK || Block.method_16588(block) || block == Blocks.FARMLAND) && blockPos.getY() < 256 - i - 1) {
					this.method_17292(iWorld, blockPos.down());

					for (int n = blockPos.getY() - 3 + i; n <= blockPos.getY() + i; n++) {
						int o = n - (blockPos.getY() + i);
						int p = 1 - o / 2;

						for (int q = blockPos.getX() - p; q <= blockPos.getX() + p; q++) {
							int r = q - blockPos.getX();

							for (int s = blockPos.getZ() - p; s <= blockPos.getZ() + p; s++) {
								int t = s - blockPos.getZ();
								if (Math.abs(r) != p || Math.abs(t) != p || random.nextInt(2) != 0 && o != 0) {
									BlockPos blockPos2 = new BlockPos(q, n, s);
									BlockState blockState = iWorld.getBlockState(blockPos2);
									if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
										this.method_17344(iWorld, blockPos2, LEAVES);
									}
								}
							}
						}
					}

					for (int u = 0; u < i; u++) {
						BlockState blockState2 = iWorld.getBlockState(blockPos.up(u));
						if (blockState2.isAir() || blockState2.isIn(BlockTags.LEAVES)) {
							this.method_17293(set, iWorld, blockPos.up(u), LOG);
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
