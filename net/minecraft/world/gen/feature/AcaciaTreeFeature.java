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
import net.minecraft.world.IWorld;

public class AcaciaTreeFeature extends FoliageFeature<class_3871> {
	private static final BlockState LOG = Blocks.ACACIA_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.ACACIA_LEAVES.getDefaultState();

	public AcaciaTreeFeature(boolean bl) {
		super(bl);
	}

	@Override
	public boolean method_17294(Set<BlockPos> set, IWorld iWorld, Random random, BlockPos blockPos) {
		int i = random.nextInt(3) + random.nextInt(3) + 5;
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
				if ((block == Blocks.GRASS_BLOCK || Block.method_16588(block)) && blockPos.getY() < 256 - i - 1) {
					this.method_17292(iWorld, blockPos.down());
					Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
					int n = i - random.nextInt(4) - 1;
					int o = 3 - random.nextInt(3);
					int p = blockPos.getX();
					int q = blockPos.getZ();
					int r = 0;

					for (int s = 0; s < i; s++) {
						int t = blockPos.getY() + s;
						if (s >= n && o > 0) {
							p += direction.getOffsetX();
							q += direction.getOffsetZ();
							o--;
						}

						BlockPos blockPos2 = new BlockPos(p, t, q);
						BlockState blockState = iWorld.getBlockState(blockPos2);
						if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
							this.method_17408(set, iWorld, blockPos2);
							r = t;
						}
					}

					BlockPos blockPos3 = new BlockPos(p, r, q);

					for (int u = -3; u <= 3; u++) {
						for (int v = -3; v <= 3; v++) {
							if (Math.abs(u) != 3 || Math.abs(v) != 3) {
								this.method_17409(iWorld, blockPos3.add(u, 0, v));
							}
						}
					}

					blockPos3 = blockPos3.up();

					for (int w = -1; w <= 1; w++) {
						for (int x = -1; x <= 1; x++) {
							this.method_17409(iWorld, blockPos3.add(w, 0, x));
						}
					}

					this.method_17409(iWorld, blockPos3.east(2));
					this.method_17409(iWorld, blockPos3.west(2));
					this.method_17409(iWorld, blockPos3.south(2));
					this.method_17409(iWorld, blockPos3.north(2));
					p = blockPos.getX();
					q = blockPos.getZ();
					Direction direction2 = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
					if (direction2 != direction) {
						int y = n - random.nextInt(2) - 1;
						int z = 1 + random.nextInt(3);
						r = 0;

						for (int aa = y; aa < i && z > 0; z--) {
							if (aa >= 1) {
								int ab = blockPos.getY() + aa;
								p += direction2.getOffsetX();
								q += direction2.getOffsetZ();
								BlockPos blockPos4 = new BlockPos(p, ab, q);
								BlockState blockState2 = iWorld.getBlockState(blockPos4);
								if (blockState2.isAir() || blockState2.isIn(BlockTags.LEAVES)) {
									this.method_17408(set, iWorld, blockPos4);
									r = ab;
								}
							}

							aa++;
						}

						if (r > 0) {
							BlockPos blockPos5 = new BlockPos(p, r, q);

							for (int ac = -2; ac <= 2; ac++) {
								for (int ad = -2; ad <= 2; ad++) {
									if (Math.abs(ac) != 2 || Math.abs(ad) != 2) {
										this.method_17409(iWorld, blockPos5.add(ac, 0, ad));
									}
								}
							}

							blockPos5 = blockPos5.up();

							for (int ae = -1; ae <= 1; ae++) {
								for (int af = -1; af <= 1; af++) {
									this.method_17409(iWorld, blockPos5.add(ae, 0, af));
								}
							}
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

	private void method_17408(Set<BlockPos> set, IWorld iWorld, BlockPos blockPos) {
		this.method_17293(set, iWorld, blockPos, LOG);
	}

	private void method_17409(IWorld iWorld, BlockPos blockPos) {
		BlockState blockState = iWorld.getBlockState(blockPos);
		if (blockState.isAir() || blockState.isIn(BlockTags.LEAVES)) {
			this.method_17344(iWorld, blockPos, LEAVES);
		}
	}
}
