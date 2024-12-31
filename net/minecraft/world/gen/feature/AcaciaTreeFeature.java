package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Leaves2Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Log2Block;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AcaciaTreeFeature extends FoliageFeature {
	private static final BlockState LOG = Blocks.LOG2.getDefaultState().with(Log2Block.VARIANT, PlanksBlock.WoodType.ACACIA);
	private static final BlockState LEAVES = Blocks.LEAVES2
		.getDefaultState()
		.with(Leaves2Block.VARIANT, PlanksBlock.WoodType.ACACIA)
		.with(LeavesBlock.CHECK_DECAY, false);

	public AcaciaTreeFeature(boolean bl) {
		super(bl);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
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
				if ((block == Blocks.GRASS || block == Blocks.DIRT) && blockPos.getY() < 256 - i - 1) {
					this.setDirt(world, blockPos.down());
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
						Material material = world.getBlockState(blockPos2).getMaterial();
						if (material == Material.AIR || material == Material.FOLIAGE) {
							this.setAcaciaLog(world, blockPos2);
							r = t;
						}
					}

					BlockPos blockPos3 = new BlockPos(p, r, q);

					for (int u = -3; u <= 3; u++) {
						for (int v = -3; v <= 3; v++) {
							if (Math.abs(u) != 3 || Math.abs(v) != 3) {
								this.setAcaciaLeaves(world, blockPos3.add(u, 0, v));
							}
						}
					}

					blockPos3 = blockPos3.up();

					for (int w = -1; w <= 1; w++) {
						for (int x = -1; x <= 1; x++) {
							this.setAcaciaLeaves(world, blockPos3.add(w, 0, x));
						}
					}

					this.setAcaciaLeaves(world, blockPos3.east(2));
					this.setAcaciaLeaves(world, blockPos3.west(2));
					this.setAcaciaLeaves(world, blockPos3.south(2));
					this.setAcaciaLeaves(world, blockPos3.north(2));
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
								Material material2 = world.getBlockState(blockPos4).getMaterial();
								if (material2 == Material.AIR || material2 == Material.FOLIAGE) {
									this.setAcaciaLog(world, blockPos4);
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
										this.setAcaciaLeaves(world, blockPos5.add(ac, 0, ad));
									}
								}
							}

							blockPos5 = blockPos5.up();

							for (int ae = -1; ae <= 1; ae++) {
								for (int af = -1; af <= 1; af++) {
									this.setAcaciaLeaves(world, blockPos5.add(ae, 0, af));
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

	private void setAcaciaLog(World world, BlockPos blockPos) {
		this.setBlockStateWithoutUpdatingNeighbors(world, blockPos, LOG);
	}

	private void setAcaciaLeaves(World world, BlockPos blockPos) {
		Material material = world.getBlockState(blockPos).getMaterial();
		if (material == Material.AIR || material == Material.FOLIAGE) {
			this.setBlockStateWithoutUpdatingNeighbors(world, blockPos, LEAVES);
		}
	}
}
