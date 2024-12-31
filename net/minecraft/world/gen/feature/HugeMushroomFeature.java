package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HugeMushroomFeature extends Feature {
	private final Block block;

	public HugeMushroomFeature(Block block) {
		super(true);
		this.block = block;
	}

	public HugeMushroomFeature() {
		super(false);
		this.block = null;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		Block block = this.block;
		if (block == null) {
			block = random.nextBoolean() ? Blocks.BROWN_MUSHROOM_BLOCK : Blocks.RED_MUSHROOM_BLOCK;
		}

		int i = random.nextInt(3) + 4;
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 < 256) {
			for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; j++) {
				int k = 3;
				if (j <= blockPos.getY() + 3) {
					k = 0;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; l++) {
					for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; m++) {
						if (j >= 0 && j < 256) {
							Material material = world.getBlockState(mutable.setPosition(l, j, m)).getMaterial();
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
				Block block2 = world.getBlockState(blockPos.down()).getBlock();
				if (block2 != Blocks.DIRT && block2 != Blocks.GRASS && block2 != Blocks.MYCELIUM) {
					return false;
				} else {
					int n = blockPos.getY() + i;
					if (block == Blocks.RED_MUSHROOM_BLOCK) {
						n = blockPos.getY() + i - 3;
					}

					for (int o = n; o <= blockPos.getY() + i; o++) {
						int p = 1;
						if (o < blockPos.getY() + i) {
							p++;
						}

						if (block == Blocks.BROWN_MUSHROOM_BLOCK) {
							p = 3;
						}

						int q = blockPos.getX() - p;
						int r = blockPos.getX() + p;
						int s = blockPos.getZ() - p;
						int t = blockPos.getZ() + p;

						for (int u = q; u <= r; u++) {
							for (int v = s; v <= t; v++) {
								int w = 5;
								if (u == q) {
									w--;
								} else if (u == r) {
									w++;
								}

								if (v == s) {
									w -= 3;
								} else if (v == t) {
									w += 3;
								}

								MushroomBlock.MushroomType mushroomType = MushroomBlock.MushroomType.getById(w);
								if (block == Blocks.BROWN_MUSHROOM_BLOCK || o < blockPos.getY() + i) {
									if ((u == q || u == r) && (v == s || v == t)) {
										continue;
									}

									if (u == blockPos.getX() - (p - 1) && v == s) {
										mushroomType = MushroomBlock.MushroomType.NORTH_WEST;
									}

									if (u == q && v == blockPos.getZ() - (p - 1)) {
										mushroomType = MushroomBlock.MushroomType.NORTH_WEST;
									}

									if (u == blockPos.getX() + (p - 1) && v == s) {
										mushroomType = MushroomBlock.MushroomType.NORTH_EAST;
									}

									if (u == r && v == blockPos.getZ() - (p - 1)) {
										mushroomType = MushroomBlock.MushroomType.NORTH_EAST;
									}

									if (u == blockPos.getX() - (p - 1) && v == t) {
										mushroomType = MushroomBlock.MushroomType.SOUTH_WEST;
									}

									if (u == q && v == blockPos.getZ() + (p - 1)) {
										mushroomType = MushroomBlock.MushroomType.SOUTH_WEST;
									}

									if (u == blockPos.getX() + (p - 1) && v == t) {
										mushroomType = MushroomBlock.MushroomType.SOUTH_EAST;
									}

									if (u == r && v == blockPos.getZ() + (p - 1)) {
										mushroomType = MushroomBlock.MushroomType.SOUTH_EAST;
									}
								}

								if (mushroomType == MushroomBlock.MushroomType.CENTER && o < blockPos.getY() + i) {
									mushroomType = MushroomBlock.MushroomType.ALL_INSIDE;
								}

								if (blockPos.getY() >= blockPos.getY() + i - 1 || mushroomType != MushroomBlock.MushroomType.ALL_INSIDE) {
									BlockPos blockPos2 = new BlockPos(u, o, v);
									if (!world.getBlockState(blockPos2).isFullBlock()) {
										this.setBlockStateWithoutUpdatingNeighbors(world, blockPos2, block.getDefaultState().with(MushroomBlock.VARIANT, mushroomType));
									}
								}
							}
						}
					}

					for (int x = 0; x < i; x++) {
						BlockState blockState = world.getBlockState(blockPos.up(x));
						if (!blockState.isFullBlock()) {
							this.setBlockStateWithoutUpdatingNeighbors(world, blockPos.up(x), block.getDefaultState().with(MushroomBlock.VARIANT, MushroomBlock.MushroomType.STEM));
						}
					}

					return true;
				}
			}
		} else {
			return false;
		}
	}
}
