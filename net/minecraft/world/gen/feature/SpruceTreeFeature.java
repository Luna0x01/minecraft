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

public class SpruceTreeFeature extends FoliageFeature<class_3871> {
	private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
	private static final BlockState field_19256 = Blocks.SPRUCE_LEAVES.getDefaultState();

	public SpruceTreeFeature(boolean bl) {
		super(bl);
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
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
							BlockState blockState = iWorld.getBlockState(mutable.setPosition(p, m, q));
							if (!blockState.isAir() && !blockState.isIn(BlockTags.LEAVES)) {
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
				Block block = iWorld.getBlockState(blockPos.down()).getBlock();
				if ((block == Blocks.GRASS_BLOCK || Block.method_16588(block) || block == Blocks.FARMLAND) && blockPos.getY() < 256 - i - 1) {
					this.method_17292(iWorld, blockPos.down());
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
									if (!iWorld.getBlockState(blockPos2).isFullOpaque(iWorld, blockPos2)) {
										this.method_17344(iWorld, blockPos2, field_19256);
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
						BlockState blockState2 = iWorld.getBlockState(blockPos.up(ab));
						if (blockState2.isAir() || blockState2.isIn(BlockTags.LEAVES)) {
							this.method_17293(set, iWorld, blockPos.up(ab), LOG);
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
