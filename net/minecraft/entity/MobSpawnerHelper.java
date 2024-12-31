package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_3804;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.ChunkPlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MobSpawnerHelper {
	private static final Logger field_17508 = LogManager.getLogger();
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
								if (!bl2 && world.method_8524().contains(chunkPos)) {
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
			BlockPos blockPos = world.method_3585();

			for (EntityCategory entityCategory : EntityCategory.values()) {
				if ((!entityCategory.isHostile() || spawnMonsters) && (entityCategory.isHostile() || spawnAnimals) && (!entityCategory.isBreedable() || bl)) {
					int p = entityCategory.getSpawnCap() * i / field_9221;
					int q = world.method_16324(entityCategory.getCategoryClass(), p);
					if (q <= p) {
						BlockPos.Mutable mutable = new BlockPos.Mutable();

						label152:
						for (ChunkPos chunkPos2 : this.field_9222) {
							BlockPos blockPos2 = findSpawnLocation(world, chunkPos2.x, chunkPos2.z);
							int r = blockPos2.getX();
							int s = blockPos2.getY();
							int t = blockPos2.getZ();
							BlockState blockState = world.getBlockState(blockPos2);
							if (!blockState.method_16907()) {
								int u = 0;

								for (int v = 0; v < 3; v++) {
									int w = r;
									int x = s;
									int y = t;
									int z = 6;
									Biome.SpawnEntry spawnEntry = null;
									EntityData entityData = null;
									int aa = MathHelper.ceil(Math.random() * 4.0);
									int ab = 0;

									for (int ac = 0; ac < aa; ac++) {
										w += world.random.nextInt(6) - world.random.nextInt(6);
										x += world.random.nextInt(1) - world.random.nextInt(1);
										y += world.random.nextInt(6) - world.random.nextInt(6);
										mutable.setPosition(w, x, y);
										float f = (float)w + 0.5F;
										float g = (float)y + 0.5F;
										PlayerEntity playerEntity2 = world.method_16318((double)f, (double)g, -1.0);
										if (playerEntity2 != null) {
											double d = playerEntity2.squaredDistanceTo((double)f, (double)x, (double)g);
											if (!(d <= 576.0) && !(blockPos.squaredDistanceTo((double)f, (double)x, (double)g) < 576.0)) {
												if (spawnEntry == null) {
													spawnEntry = world.method_10754(entityCategory, mutable);
													if (spawnEntry == null) {
														break;
													}

													aa = spawnEntry.minGroupSize + world.random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
												}

												if (world.method_10753(entityCategory, spawnEntry, mutable)) {
													EntityLocations.class_3464 lv = EntityLocations.method_15658(spawnEntry.field_17657);
													if (lv != null && method_16404(lv, world, mutable, spawnEntry.field_17657)) {
														MobEntity mobEntity;
														try {
															mobEntity = spawnEntry.field_17657.spawn(world);
														} catch (Exception var41) {
															field_17508.warn("Failed to create mob", var41);
															return o;
														}

														mobEntity.refreshPositionAndAngles((double)f, (double)x, (double)g, world.random.nextFloat() * 360.0F, 0.0F);
														if ((d <= 16384.0 || !mobEntity.canImmediatelyDespawn()) && mobEntity.method_15652(world, false) && mobEntity.method_15653(world)) {
															entityData = mobEntity.initialize(world.method_8482(new BlockPos(mobEntity)), entityData, null);
															if (mobEntity.method_15653(world)) {
																u++;
																ab++;
																world.method_3686(mobEntity);
															} else {
																mobEntity.remove();
															}

															if (u >= mobEntity.getLimitPerChunk()) {
																continue label152;
															}

															if (mobEntity.method_15654(ab)) {
																break;
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
				}
			}

			return o;
		}
	}

	private static BlockPos findSpawnLocation(World world, int chunkX, int chunkZ) {
		Chunk chunk = world.method_16347(chunkX, chunkZ);
		int i = chunkX * 16 + world.random.nextInt(16);
		int j = chunkZ * 16 + world.random.nextInt(16);
		int k = chunk.method_16992(class_3804.class_3805.LIGHT_BLOCKING, i, j) + 1;
		int l = world.random.nextInt(k + 1);
		return new BlockPos(i, l, j);
	}

	public static boolean method_16407(BlockState blockState, FluidState fluidState) {
		if (blockState.method_16905()) {
			return false;
		} else if (blockState.emitsRedstonePower()) {
			return false;
		} else {
			return !fluidState.isEmpty() ? false : !blockState.isIn(BlockTags.RAILS);
		}
	}

	public static boolean method_16404(
		EntityLocations.class_3464 arg, RenderBlockView renderBlockView, BlockPos blockPos, @Nullable EntityType<? extends MobEntity> entityType
	) {
		if (entityType != null && renderBlockView.method_8524().contains(blockPos)) {
			BlockState blockState = renderBlockView.getBlockState(blockPos);
			FluidState fluidState = renderBlockView.getFluidState(blockPos);
			switch (arg) {
				case IN_WATER:
					return fluidState.matches(FluidTags.WATER)
						&& renderBlockView.getFluidState(blockPos.down()).matches(FluidTags.WATER)
						&& !renderBlockView.getBlockState(blockPos.up()).method_16907();
				case ON_GROUND:
				default:
					BlockState blockState2 = renderBlockView.getBlockState(blockPos.down());
					if (blockState2.method_16913() || entityType != null && EntityLocations.method_15661(entityType, blockState2)) {
						Block block = blockState2.getBlock();
						boolean bl = block != Blocks.BEDROCK && block != Blocks.BARRIER;
						return bl
							&& method_16407(blockState, fluidState)
							&& method_16407(renderBlockView.getBlockState(blockPos.up()), renderBlockView.getFluidState(blockPos.up()));
					} else {
						return false;
					}
			}
		} else {
			return false;
		}
	}

	public static void method_16406(IWorld iWorld, Biome biome, int i, int j, Random random) {
		List<Biome.SpawnEntry> list = biome.getSpawnEntries(EntityCategory.PASSIVE);
		if (!list.isEmpty()) {
			int k = i << 4;
			int l = j << 4;

			while (random.nextFloat() < biome.getMaxSpawnLimit()) {
				Biome.SpawnEntry spawnEntry = Weighting.getRandom(random, list);
				int m = spawnEntry.minGroupSize + random.nextInt(1 + spawnEntry.maxGroupSize - spawnEntry.minGroupSize);
				EntityData entityData = null;
				int n = k + random.nextInt(16);
				int o = l + random.nextInt(16);
				int p = n;
				int q = o;

				for (int r = 0; r < m; r++) {
					boolean bl = false;

					for (int s = 0; !bl && s < 4; s++) {
						BlockPos blockPos = method_16405(iWorld, spawnEntry.field_17657, n, o);
						if (method_16404(EntityLocations.class_3464.ON_GROUND, iWorld, blockPos, spawnEntry.field_17657)) {
							MobEntity mobEntity;
							try {
								mobEntity = spawnEntry.field_17657.spawn(iWorld.method_16348());
							} catch (Exception var24) {
								field_17508.warn("Failed to create mob", var24);
								continue;
							}

							double d = MathHelper.clamp((double)n, (double)k + (double)mobEntity.width, (double)k + 16.0 - (double)mobEntity.width);
							double e = MathHelper.clamp((double)o, (double)l + (double)mobEntity.width, (double)l + 16.0 - (double)mobEntity.width);
							mobEntity.refreshPositionAndAngles(d, (double)blockPos.getY(), e, random.nextFloat() * 360.0F, 0.0F);
							if (mobEntity.method_15652(iWorld, false) && mobEntity.method_15653(iWorld)) {
								entityData = mobEntity.initialize(iWorld.method_8482(new BlockPos(mobEntity)), entityData, null);
								iWorld.method_3686(mobEntity);
								bl = true;
							}
						}

						n += random.nextInt(5) - random.nextInt(5);

						for (o += random.nextInt(5) - random.nextInt(5); n < k || n >= k + 16 || o < l || o >= l + 16; o = q + random.nextInt(5) - random.nextInt(5)) {
							n = p + random.nextInt(5) - random.nextInt(5);
						}
					}
				}
			}
		}
	}

	private static BlockPos method_16405(IWorld iWorld, @Nullable EntityType<? extends MobEntity> entityType, int i, int j) {
		BlockPos blockPos = new BlockPos(i, iWorld.method_16372(EntityLocations.method_15662(entityType), i, j), j);
		BlockPos blockPos2 = blockPos.down();
		return iWorld.getBlockState(blockPos2).canPlaceAtSide(iWorld, blockPos2, BlockPlacementEnvironment.LAND) ? blockPos2 : blockPos;
	}
}
