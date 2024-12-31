package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.ChunkPlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public final class MobSpawnerHelper {
	private static final int field_9221 = (int)Math.pow(17.0, 2.0);
	private final Set<ChunkPos> field_9222 = Sets.newHashSet();

	public int tickSpawners(ServerWorld world, boolean spawnAnimals, boolean spawnMonsters, boolean bl) {
		if (!spawnAnimals && !spawnMonsters) {
			return 0;
		} else {
			this.field_9222.clear();
			int i = 0;

			for (PlayerEntity playerEntity : world.playerEntities) {
				if (!playerEntity.isSpectator()) {
					int j = MathHelper.floor(playerEntity.x / 16.0);
					int k = MathHelper.floor(playerEntity.z / 16.0);
					int l = 8;

					for (int m = -8; m <= 8; m++) {
						for (int n = -8; n <= 8; n++) {
							boolean bl2 = m == -8 || m == 8 || n == -8 || n == 8;
							ChunkPos chunkPos = new ChunkPos(m + j, n + k);
							if (!this.field_9222.contains(chunkPos)) {
								i++;
								if (!bl2 && world.getWorldBorder().contains(chunkPos)) {
									ChunkPlayerManager chunkPlayerManager = world.getPlayerWorldManager().method_12811(chunkPos.x, chunkPos.z);
									if (chunkPlayerManager != null && chunkPlayerManager.method_12805()) {
										this.field_9222.add(chunkPos);
									}
								}
							}
						}
					}
				}
			}

			int o = 0;
			BlockPos blockPos = world.getSpawnPos();

			for (EntityCategory entityCategory : EntityCategory.values()) {
				if ((!entityCategory.isHostile() || spawnMonsters) && (entityCategory.isHostile() || spawnAnimals) && (!entityCategory.isBreedable() || bl)) {
					int p = world.getPersistentEntityCount(entityCategory.getCategoryClass());
					int q = entityCategory.getSpawnCap() * i / field_9221;
					if (p <= q) {
						BlockPos.Mutable mutable = new BlockPos.Mutable();

						label134:
						for (ChunkPos chunkPos2 : this.field_9222) {
							BlockPos blockPos2 = findSpawnLocation(world, chunkPos2.x, chunkPos2.z);
							int r = blockPos2.getX();
							int s = blockPos2.getY();
							int t = blockPos2.getZ();
							BlockState blockState = world.getBlockState(blockPos2);
							if (!blockState.method_11734()) {
								int u = 0;

								for (int v = 0; v < 3; v++) {
									int w = r;
									int x = s;
									int y = t;
									int z = 6;
									Biome.SpawnEntry spawnEntry = null;
									EntityData entityData = null;
									int aa = MathHelper.ceil(Math.random() * 4.0);

									for (int ab = 0; ab < aa; ab++) {
										w += world.random.nextInt(6) - world.random.nextInt(6);
										x += world.random.nextInt(1) - world.random.nextInt(1);
										y += world.random.nextInt(6) - world.random.nextInt(6);
										mutable.setPosition(w, x, y);
										float f = (float)w + 0.5F;
										float g = (float)y + 0.5F;
										if (!world.isPlayerInRange((double)f, (double)x, (double)g, 24.0) && !(blockPos.squaredDistanceTo((double)f, (double)x, (double)g) < 576.0)) {
											if (spawnEntry == null) {
												spawnEntry = world.method_10754(entityCategory, mutable);
												if (spawnEntry == null) {
													break;
												}
											}

											if (world.method_10753(entityCategory, spawnEntry, mutable) && isSpawnable(EntityLocations.getLocation(spawnEntry.entity), world, mutable)) {
												MobEntity mobEntity;
												try {
													mobEntity = (MobEntity)spawnEntry.entity.getConstructor(World.class).newInstance(world);
												} catch (Exception var36) {
													var36.printStackTrace();
													return o;
												}

												mobEntity.refreshPositionAndAngles((double)f, (double)x, (double)g, world.random.nextFloat() * 360.0F, 0.0F);
												if (mobEntity.canSpawn() && mobEntity.hasNoSpawnCollisions()) {
													entityData = mobEntity.initialize(world.getLocalDifficulty(new BlockPos(mobEntity)), entityData);
													if (mobEntity.hasNoSpawnCollisions()) {
														u++;
														world.spawnEntity(mobEntity);
													} else {
														mobEntity.remove();
													}

													if (u >= mobEntity.getLimitPerChunk()) {
														continue label134;
													}
												}

												o += u;
											}
										}
									}
								}
							}
						}
					}
				}
			}

			return o;
		}
	}

	private static BlockPos findSpawnLocation(World world, int chunkX, int chunkZ) {
		Chunk chunk = world.getChunk(chunkX, chunkZ);
		int i = chunkX * 16 + world.random.nextInt(16);
		int j = chunkZ * 16 + world.random.nextInt(16);
		int k = MathHelper.roundUp(chunk.getHighestBlockY(new BlockPos(i, 0, j)) + 1, 16);
		int l = world.random.nextInt(k > 0 ? k : chunk.getHighestNonEmptySectionYOffset() + 16 - 1);
		return new BlockPos(i, l, j);
	}

	public static boolean method_11496(BlockState blockState) {
		if (blockState.method_11733()) {
			return false;
		} else if (blockState.emitsRedstonePower()) {
			return false;
		} else {
			return blockState.getMaterial().isFluid() ? false : !AbstractRailBlock.isRail(blockState);
		}
	}

	public static boolean isSpawnable(MobEntity.Location location, World world, BlockPos pos) {
		if (!world.getWorldBorder().contains(pos)) {
			return false;
		} else {
			BlockState blockState = world.getBlockState(pos);
			if (location == MobEntity.Location.IN_WATER) {
				return blockState.getMaterial() == Material.WATER
					&& world.getBlockState(pos.down()).getMaterial() == Material.WATER
					&& !world.getBlockState(pos.up()).method_11734();
			} else {
				BlockPos blockPos = pos.down();
				if (!world.getBlockState(blockPos).method_11739()) {
					return false;
				} else {
					Block block = world.getBlockState(blockPos).getBlock();
					boolean bl = block != Blocks.BEDROCK && block != Blocks.BARRIER;
					return bl && method_11496(blockState) && method_11496(world.getBlockState(pos.up()));
				}
			}
		}
	}

	public static void spawnMobs(World world, Biome biome, int x, int z, int randomX, int randomZ, Random random) {
		List<Biome.SpawnEntry> list = biome.getSpawnEntries(EntityCategory.PASSIVE);
		if (!list.isEmpty()) {
			while (random.nextFloat() < biome.getMaxSpawnLimit()) {
				Biome.SpawnEntry spawnEntry = Weighting.getRandom(world.random, list);
				int i = spawnEntry.minGroupSize + random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
				EntityData entityData = null;
				int j = x + random.nextInt(randomX);
				int k = z + random.nextInt(randomZ);
				int l = j;
				int m = k;

				for (int n = 0; n < i; n++) {
					boolean bl = false;

					for (int o = 0; !bl && o < 4; o++) {
						BlockPos blockPos = world.getTopPosition(new BlockPos(j, 0, k));
						if (isSpawnable(MobEntity.Location.ON_GROUND, world, blockPos)) {
							MobEntity mobEntity;
							try {
								mobEntity = (MobEntity)spawnEntry.entity.getConstructor(World.class).newInstance(world);
							} catch (Exception var21) {
								var21.printStackTrace();
								continue;
							}

							mobEntity.refreshPositionAndAngles((double)((float)j + 0.5F), (double)blockPos.getY(), (double)((float)k + 0.5F), random.nextFloat() * 360.0F, 0.0F);
							world.spawnEntity(mobEntity);
							entityData = mobEntity.initialize(world.getLocalDifficulty(new BlockPos(mobEntity)), entityData);
							bl = true;
						}

						j += random.nextInt(5) - random.nextInt(5);

						for (k += random.nextInt(5) - random.nextInt(5); j < x || j >= x + randomX || k < z || k >= z + randomX; k = m + random.nextInt(5) - random.nextInt(5)) {
							j = l + random.nextInt(5) - random.nextInt(5);
						}
					}
				}
			}
		}
	}
}
