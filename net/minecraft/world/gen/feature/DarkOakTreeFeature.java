package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.class_3871;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

public class DarkOakTreeFeature extends FoliageFeature<class_3871> {
	private static final BlockState LOG = Blocks.DARK_OAK_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.DARK_OAK_LEAVES.getDefaultState();

	public DarkOakTreeFeature(boolean bl) {
		super(bl);
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
		int i = random.nextInt(3) + random.nextInt(2) + 6;
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();
		if (k >= 1 && k + i + 1 < 256) {
			BlockPos blockPos2 = blockPos.down();
			Block block = iWorld.getBlockState(blockPos2).getBlock();
			if (block != Blocks.GRASS_BLOCK && !Block.method_16588(block)) {
				return false;
			} else if (!this.method_9219(iWorld, blockPos, i)) {
				return false;
			} else {
				this.method_17292(iWorld, blockPos2);
				this.method_17292(iWorld, blockPos2.east());
				this.method_17292(iWorld, blockPos2.south());
				this.method_17292(iWorld, blockPos2.south().east());
				Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
				int m = i - random.nextInt(4);
				int n = 2 - random.nextInt(3);
				int o = j;
				int p = l;
				int q = k + i - 1;

				for (int r = 0; r < i; r++) {
					if (r >= m && n > 0) {
						o += direction.getOffsetX();
						p += direction.getOffsetZ();
						n--;
					}

					int s = k + r;
					BlockPos blockPos3 = new BlockPos(o, s, p);
					BlockState blockState = iWorld.getBlockState(blockPos3);
					if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
						this.method_17407(set, iWorld, blockPos3);
						this.method_17407(set, iWorld, blockPos3.east());
						this.method_17407(set, iWorld, blockPos3.south());
						this.method_17407(set, iWorld, blockPos3.east().south());
					}
				}

				for (int t = -2; t <= 0; t++) {
					for (int u = -2; u <= 0; u++) {
						int v = -1;
						this.method_17406(iWorld, o + t, q + v, p + u);
						this.method_17406(iWorld, 1 + o - t, q + v, p + u);
						this.method_17406(iWorld, o + t, q + v, 1 + p - u);
						this.method_17406(iWorld, 1 + o - t, q + v, 1 + p - u);
						if ((t > -2 || u > -1) && (t != -1 || u != -2)) {
							int var29 = 1;
							this.method_17406(iWorld, o + t, q + var29, p + u);
							this.method_17406(iWorld, 1 + o - t, q + var29, p + u);
							this.method_17406(iWorld, o + t, q + var29, 1 + p - u);
							this.method_17406(iWorld, 1 + o - t, q + var29, 1 + p - u);
						}
					}
				}

				if (random.nextBoolean()) {
					this.method_17406(iWorld, o, q + 2, p);
					this.method_17406(iWorld, o + 1, q + 2, p);
					this.method_17406(iWorld, o + 1, q + 2, p + 1);
					this.method_17406(iWorld, o, q + 2, p + 1);
				}

				for (int w = -3; w <= 4; w++) {
					for (int x = -3; x <= 4; x++) {
						if ((w != -3 || x != -3) && (w != -3 || x != 4) && (w != 4 || x != -3) && (w != 4 || x != 4) && (Math.abs(w) < 3 || Math.abs(x) < 3)) {
							this.method_17406(iWorld, o + w, q, p + x);
						}
					}
				}

				for (int y = -1; y <= 2; y++) {
					for (int z = -1; z <= 2; z++) {
						if ((y < 0 || y > 1 || z < 0 || z > 1) && random.nextInt(3) <= 0) {
							int aa = random.nextInt(3) + 2;

							for (int ab = 0; ab < aa; ab++) {
								this.method_17407(set, iWorld, new BlockPos(j + y, q - ab - 1, l + z));
							}

							for (int ac = -1; ac <= 1; ac++) {
								for (int ad = -1; ad <= 1; ad++) {
									this.method_17406(iWorld, o + y + ac, q, p + z + ad);
								}
							}

							for (int ae = -2; ae <= 2; ae++) {
								for (int af = -2; af <= 2; af++) {
									if (Math.abs(ae) != 2 || Math.abs(af) != 2) {
										this.method_17406(iWorld, o + y + ae, q - 1, p + z + af);
									}
								}
							}
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	private boolean method_9219(BlockView blockView, BlockPos blockPos, int i) {
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = 0; m <= i + 1; m++) {
			int n = 1;
			if (m == 0) {
				n = 0;
			}

			if (m >= i - 1) {
				n = 2;
			}

			for (int o = -n; o <= n; o++) {
				for (int p = -n; p <= n; p++) {
					if (!this.isBlockReplaceable(blockView.getBlockState(mutable.setPosition(j + o, k + m, l + p)).getBlock())) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private void method_17407(Set<BlockPos> set, IWorld iWorld, BlockPos blockPos) {
		if (this.isBlockReplaceable(iWorld.getBlockState(blockPos).getBlock())) {
			this.method_17293(set, iWorld, blockPos, LOG);
		}
	}

	private void method_17406(IWorld iWorld, int i, int j, int k) {
		BlockPos blockPos = new BlockPos(i, j, k);
		if (iWorld.getBlockState(blockPos).isAir()) {
			this.method_17344(iWorld, blockPos, LEAVES);
		}
	}
}
