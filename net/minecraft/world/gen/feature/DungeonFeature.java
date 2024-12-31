package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.loot.LootTables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonFeature extends Feature {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String[] spawnableMobs = new String[]{"Skeleton", "Zombie", "Zombie", "Spider"};

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		int i = 3;
		int j = random.nextInt(2) + 2;
		int k = -j - 1;
		int l = j + 1;
		int m = -1;
		int n = 4;
		int o = random.nextInt(2) + 2;
		int p = -o - 1;
		int q = o + 1;
		int r = 0;

		for (int s = k; s <= l; s++) {
			for (int t = -1; t <= 4; t++) {
				for (int u = p; u <= q; u++) {
					BlockPos blockPos2 = blockPos.add(s, t, u);
					Material material = world.getBlockState(blockPos2).getMaterial();
					boolean bl = material.isSolid();
					if (t == -1 && !bl) {
						return false;
					}

					if (t == 4 && !bl) {
						return false;
					}

					if ((s == k || s == l || u == p || u == q) && t == 0 && world.isAir(blockPos2) && world.isAir(blockPos2.up())) {
						r++;
					}
				}
			}
		}

		if (r >= 1 && r <= 5) {
			for (int v = k; v <= l; v++) {
				for (int w = 3; w >= -1; w--) {
					for (int x = p; x <= q; x++) {
						BlockPos blockPos3 = blockPos.add(v, w, x);
						if (v != k && w != -1 && x != p && v != l && w != 4 && x != q) {
							if (world.getBlockState(blockPos3).getBlock() != Blocks.CHEST) {
								world.setAir(blockPos3);
							}
						} else if (blockPos3.getY() >= 0 && !world.getBlockState(blockPos3.down()).getMaterial().isSolid()) {
							world.setAir(blockPos3);
						} else if (world.getBlockState(blockPos3).getMaterial().isSolid() && world.getBlockState(blockPos3).getBlock() != Blocks.CHEST) {
							if (w == -1 && random.nextInt(4) != 0) {
								world.setBlockState(blockPos3, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
							} else {
								world.setBlockState(blockPos3, Blocks.COBBLESTONE.getDefaultState(), 2);
							}
						}
					}
				}
			}

			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 3; z++) {
					int aa = blockPos.getX() + random.nextInt(j * 2 + 1) - j;
					int ab = blockPos.getY();
					int ac = blockPos.getZ() + random.nextInt(o * 2 + 1) - o;
					BlockPos blockPos4 = new BlockPos(aa, ab, ac);
					if (world.isAir(blockPos4)) {
						int ad = 0;

						for (Direction direction : Direction.DirectionType.HORIZONTAL) {
							if (world.getBlockState(blockPos4.offset(direction)).getMaterial().isSolid()) {
								ad++;
							}
						}

						if (ad == 1) {
							world.setBlockState(blockPos4, Blocks.CHEST.changeFacing(world, blockPos4, Blocks.CHEST.getDefaultState()), 2);
							BlockEntity blockEntity = world.getBlockEntity(blockPos4);
							if (blockEntity instanceof ChestBlockEntity) {
								((ChestBlockEntity)blockEntity).method_11660(LootTables.SIMPLE_DUNGEON_CHEST, random.nextLong());
							}
							break;
						}
					}
				}
			}

			world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
			BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
			if (blockEntity2 instanceof MobSpawnerBlockEntity) {
				((MobSpawnerBlockEntity)blockEntity2).getLogic().setEntityId(this.getRandomSpawnerMob(random));
			} else {
				LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
			}

			return true;
		} else {
			return false;
		}
	}

	private String getRandomSpawnerMob(Random random) {
		return spawnableMobs[random.nextInt(spawnableMobs.length)];
	}
}
