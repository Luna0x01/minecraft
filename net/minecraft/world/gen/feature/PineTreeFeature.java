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

public class PineTreeFeature extends FoliageFeature<class_3871> {
	private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
	private static final BlockState field_19230 = Blocks.SPRUCE_LEAVES.getDefaultState();

	public PineTreeFeature() {
		super(false);
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
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
						} else if (!this.isBlockReplaceable(iWorld.getBlockState(mutable.setPosition(o, m, p)).getBlock())) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else {
				Block block = iWorld.getBlockState(blockPos.down()).getBlock();
				if ((block == Blocks.GRASS_BLOCK || Block.method_16588(block)) && blockPos.getY() < 256 - i - 1) {
					this.method_17292(iWorld, blockPos.down());
					int q = 0;

					for (int r = blockPos.getY() + i; r >= blockPos.getY() + j; r--) {
						for (int s = blockPos.getX() - q; s <= blockPos.getX() + q; s++) {
							int t = s - blockPos.getX();

							for (int u = blockPos.getZ() - q; u <= blockPos.getZ() + q; u++) {
								int v = u - blockPos.getZ();
								if (Math.abs(t) != q || Math.abs(v) != q || q <= 0) {
									BlockPos blockPos2 = new BlockPos(s, r, u);
									if (!iWorld.getBlockState(blockPos2).isFullOpaque(iWorld, blockPos2)) {
										this.method_17344(iWorld, blockPos2, field_19230);
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
						BlockState blockState = iWorld.getBlockState(blockPos.up(w));
						if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
							this.method_17293(set, iWorld, blockPos.up(w), LOG);
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
