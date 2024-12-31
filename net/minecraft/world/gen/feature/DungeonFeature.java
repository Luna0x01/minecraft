package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.class_2737;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonFeature extends class_3844<class_3871> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final EntityType<?>[] field_19218 = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
	private static final BlockState field_19219 = Blocks.CAVE_AIR.getDefaultState();

	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
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
					Material material = iWorld.getBlockState(blockPos2).getMaterial();
					boolean bl = material.isSolid();
					if (t == -1 && !bl) {
						return false;
					}

					if (t == 4 && !bl) {
						return false;
					}

					if ((s == k || s == l || u == p || u == q) && t == 0 && iWorld.method_8579(blockPos2) && iWorld.method_8579(blockPos2.up())) {
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
							if (iWorld.getBlockState(blockPos3).getBlock() != Blocks.CHEST) {
								iWorld.setBlockState(blockPos3, field_19219, 2);
							}
						} else if (blockPos3.getY() >= 0 && !iWorld.getBlockState(blockPos3.down()).getMaterial().isSolid()) {
							iWorld.setBlockState(blockPos3, field_19219, 2);
						} else if (iWorld.getBlockState(blockPos3).getMaterial().isSolid() && iWorld.getBlockState(blockPos3).getBlock() != Blocks.CHEST) {
							if (w == -1 && random.nextInt(4) != 0) {
								iWorld.setBlockState(blockPos3, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
							} else {
								iWorld.setBlockState(blockPos3, Blocks.COBBLESTONE.getDefaultState(), 2);
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
					if (iWorld.method_8579(blockPos4)) {
						int ad = 0;

						for (Direction direction : Direction.DirectionType.HORIZONTAL) {
							if (iWorld.getBlockState(blockPos4.offset(direction)).getMaterial().isSolid()) {
								ad++;
							}
						}

						if (ad == 1) {
							iWorld.setBlockState(blockPos4, StructurePiece.method_17652(iWorld, blockPos4, Blocks.CHEST.getDefaultState()), 2);
							class_2737.method_16833(iWorld, random, blockPos4, LootTables.SIMPLE_DUNGEON_CHEST);
							break;
						}
					}
				}
			}

			iWorld.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
			BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
			if (blockEntity instanceof MobSpawnerBlockEntity) {
				((MobSpawnerBlockEntity)blockEntity).getLogic().method_16278(this.method_17389(random));
			} else {
				LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ());
			}

			return true;
		} else {
			return false;
		}
	}

	private EntityType<?> method_17389(Random random) {
		return field_19218[random.nextInt(field_19218.length)];
	}
}
