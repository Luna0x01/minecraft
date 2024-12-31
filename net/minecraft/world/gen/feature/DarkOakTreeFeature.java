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

public class DarkOakTreeFeature extends FoliageFeature {
	private static final BlockState LOG = Blocks.LOG2.getDefaultState().with(Log2Block.VARIANT, PlanksBlock.WoodType.DARK_OAK);
	private static final BlockState LEAVES = Blocks.LEAVES2
		.getDefaultState()
		.with(Leaves2Block.VARIANT, PlanksBlock.WoodType.DARK_OAK)
		.with(LeavesBlock.CHECK_DECAY, false);

	public DarkOakTreeFeature(boolean bl) {
		super(bl);
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int i = random.nextInt(3) + random.nextInt(2) + 6;
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();
		if (k >= 1 && k + i + 1 < 256) {
			BlockPos blockPos2 = blockPos.down();
			Block block = world.getBlockState(blockPos2).getBlock();
			if (block != Blocks.GRASS && block != Blocks.DIRT) {
				return false;
			} else if (!this.isAreaAboveClear(world, blockPos, i)) {
				return false;
			} else {
				this.setDirt(world, blockPos2);
				this.setDirt(world, blockPos2.east());
				this.setDirt(world, blockPos2.south());
				this.setDirt(world, blockPos2.south().east());
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
					Material material = world.getBlockState(blockPos3).getBlock().getMaterial();
					if (material == Material.AIR || material == Material.FOLIAGE) {
						this.setDarkOakLog(world, blockPos3);
						this.setDarkOakLog(world, blockPos3.east());
						this.setDarkOakLog(world, blockPos3.south());
						this.setDarkOakLog(world, blockPos3.east().south());
					}
				}

				for (int t = -2; t <= 0; t++) {
					for (int u = -2; u <= 0; u++) {
						int v = -1;
						this.setDarkOakLeaves(world, o + t, q + v, p + u);
						this.setDarkOakLeaves(world, 1 + o - t, q + v, p + u);
						this.setDarkOakLeaves(world, o + t, q + v, 1 + p - u);
						this.setDarkOakLeaves(world, 1 + o - t, q + v, 1 + p - u);
						if ((t > -2 || u > -1) && (t != -1 || u != -2)) {
							int var28 = 1;
							this.setDarkOakLeaves(world, o + t, q + var28, p + u);
							this.setDarkOakLeaves(world, 1 + o - t, q + var28, p + u);
							this.setDarkOakLeaves(world, o + t, q + var28, 1 + p - u);
							this.setDarkOakLeaves(world, 1 + o - t, q + var28, 1 + p - u);
						}
					}
				}

				if (random.nextBoolean()) {
					this.setDarkOakLeaves(world, o, q + 2, p);
					this.setDarkOakLeaves(world, o + 1, q + 2, p);
					this.setDarkOakLeaves(world, o + 1, q + 2, p + 1);
					this.setDarkOakLeaves(world, o, q + 2, p + 1);
				}

				for (int w = -3; w <= 4; w++) {
					for (int x = -3; x <= 4; x++) {
						if ((w != -3 || x != -3) && (w != -3 || x != 4) && (w != 4 || x != -3) && (w != 4 || x != 4) && (Math.abs(w) < 3 || Math.abs(x) < 3)) {
							this.setDarkOakLeaves(world, o + w, q, p + x);
						}
					}
				}

				for (int y = -1; y <= 2; y++) {
					for (int z = -1; z <= 2; z++) {
						if ((y < 0 || y > 1 || z < 0 || z > 1) && random.nextInt(3) <= 0) {
							int aa = random.nextInt(3) + 2;

							for (int ab = 0; ab < aa; ab++) {
								this.setDarkOakLog(world, new BlockPos(j + y, q - ab - 1, l + z));
							}

							for (int ac = -1; ac <= 1; ac++) {
								for (int ad = -1; ad <= 1; ad++) {
									this.setDarkOakLeaves(world, o + y + ac, q, p + z + ad);
								}
							}

							for (int ae = -2; ae <= 2; ae++) {
								for (int af = -2; af <= 2; af++) {
									if (Math.abs(ae) != 2 || Math.abs(af) != 2) {
										this.setDarkOakLeaves(world, o + y + ae, q - 1, p + z + af);
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

	private boolean isAreaAboveClear(World world, BlockPos blockPos, int maxHeight) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = 0; l <= maxHeight + 1; l++) {
			int m = 1;
			if (l == 0) {
				m = 0;
			}

			if (l >= maxHeight - 1) {
				m = 2;
			}

			for (int n = -m; n <= m; n++) {
				for (int o = -m; o <= m; o++) {
					if (!this.isBlockReplaceable(world.getBlockState(mutable.setPosition(i + n, j + l, k + o)).getBlock())) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private void setDarkOakLog(World world, BlockPos blockPos) {
		if (this.isBlockReplaceable(world.getBlockState(blockPos).getBlock())) {
			this.setBlockStateWithoutUpdatingNeighbors(world, blockPos, LOG);
		}
	}

	private void setDarkOakLeaves(World world, int posX, int posY, int posZ) {
		BlockPos blockPos = new BlockPos(posX, posY, posZ);
		Block block = world.getBlockState(blockPos).getBlock();
		if (block.getMaterial() == Material.AIR) {
			this.setBlockStateWithoutUpdatingNeighbors(world, blockPos, LEAVES);
		}
	}
}
