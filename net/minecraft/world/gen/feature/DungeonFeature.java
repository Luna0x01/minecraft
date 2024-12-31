package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.loot.LootTables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonFeature extends Feature<DefaultFeatureConfig> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final EntityType<?>[] MOB_SPAWNER_ENTITIES = new EntityType[]{
		EntityType.field_6137, EntityType.field_6051, EntityType.field_6051, EntityType.field_6079
	};
	private static final BlockState AIR = Blocks.field_10543.getDefaultState();

	public DungeonFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_13548(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
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

					if ((s == k || s == l || u == p || u == q) && t == 0 && iWorld.isAir(blockPos2) && iWorld.isAir(blockPos2.up())) {
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
							if (iWorld.getBlockState(blockPos3).getBlock() != Blocks.field_10034) {
								iWorld.setBlockState(blockPos3, AIR, 2);
							}
						} else if (blockPos3.getY() >= 0 && !iWorld.getBlockState(blockPos3.down()).getMaterial().isSolid()) {
							iWorld.setBlockState(blockPos3, AIR, 2);
						} else if (iWorld.getBlockState(blockPos3).getMaterial().isSolid() && iWorld.getBlockState(blockPos3).getBlock() != Blocks.field_10034) {
							if (w == -1 && random.nextInt(4) != 0) {
								iWorld.setBlockState(blockPos3, Blocks.field_9989.getDefaultState(), 2);
							} else {
								iWorld.setBlockState(blockPos3, Blocks.field_10445.getDefaultState(), 2);
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
					if (iWorld.isAir(blockPos4)) {
						int ad = 0;

						for (Direction direction : Direction.Type.field_11062) {
							if (iWorld.getBlockState(blockPos4.offset(direction)).getMaterial().isSolid()) {
								ad++;
							}
						}

						if (ad == 1) {
							iWorld.setBlockState(blockPos4, StructurePiece.method_14916(iWorld, blockPos4, Blocks.field_10034.getDefaultState()), 2);
							LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos4, LootTables.field_356);
							break;
						}
					}
				}
			}

			iWorld.setBlockState(blockPos, Blocks.field_10260.getDefaultState(), 2);
			BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
			if (blockEntity instanceof MobSpawnerBlockEntity) {
				((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(this.getMobSpawnerEntity(random));
			} else {
				LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", blockPos.getX(), blockPos.getY(), blockPos.getZ());
			}

			return true;
		} else {
			return false;
		}
	}

	private EntityType<?> getMobSpawnerEntity(Random random) {
		return MOB_SPAWNER_ENTITIES[random.nextInt(MOB_SPAWNER_ENTITIES.length)];
	}
}
