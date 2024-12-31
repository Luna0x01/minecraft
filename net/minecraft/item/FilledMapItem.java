package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class FilledMapItem extends NetworkSyncedItem {
	protected FilledMapItem() {
		this.setUnbreakable(true);
	}

	public static ItemStack method_13663(World world, double d, double e, byte b, boolean bl, boolean bl2) {
		ItemStack itemStack = new ItemStack(Items.FILLED_MAP, 1, world.getIntState("map"));
		String string = "map_" + itemStack.getData();
		MapState mapState = new MapState(string);
		world.replaceState(string, mapState);
		mapState.scale = b;
		mapState.method_9308(d, e, mapState.scale);
		mapState.dimensionId = (byte)world.dimension.getDimensionType().getId();
		mapState.trackingPosition = bl;
		mapState.field_15238 = bl2;
		mapState.markDirty();
		return itemStack;
	}

	@Nullable
	public static MapState getMapState(int i, World world) {
		String string = "map_" + i;
		return (MapState)world.getOrCreateState(MapState.class, string);
	}

	@Nullable
	public MapState getMapState(ItemStack itemStack, World world) {
		String string = "map_" + itemStack.getData();
		MapState mapState = (MapState)world.getOrCreateState(MapState.class, string);
		if (mapState == null && !world.isClient) {
			itemStack.setDamage(world.getIntState("map"));
			string = "map_" + itemStack.getData();
			mapState = new MapState(string);
			mapState.scale = 3;
			mapState.method_9308((double)world.getLevelProperties().getSpawnX(), (double)world.getLevelProperties().getSpawnZ(), mapState.scale);
			mapState.dimensionId = (byte)world.dimension.getDimensionType().getId();
			mapState.markDirty();
			world.replaceState(string, mapState);
		}

		return mapState;
	}

	public void updateColors(World world, Entity entity, MapState state) {
		if (world.dimension.getDimensionType().getId() == state.dimensionId && entity instanceof PlayerEntity) {
			int i = 1 << state.scale;
			int j = state.xCenter;
			int k = state.zCenter;
			int l = MathHelper.floor(entity.x - (double)j) / i + 64;
			int m = MathHelper.floor(entity.z - (double)k) / i + 64;
			int n = 128 / i;
			if (world.dimension.hasNoSkylight()) {
				n /= 2;
			}

			MapState.PlayerUpdateTracker playerUpdateTracker = state.getPlayerSyncData((PlayerEntity)entity);
			playerUpdateTracker.field_4983++;
			boolean bl = false;

			for (int o = l - n + 1; o < l + n; o++) {
				if ((o & 15) == (playerUpdateTracker.field_4983 & 15) || bl) {
					bl = false;
					double d = 0.0;

					for (int p = m - n - 1; p < m + n; p++) {
						if (o >= 0 && p >= -1 && o < 128 && p < 128) {
							int q = o - l;
							int r = p - m;
							boolean bl2 = q * q + r * r > (n - 2) * (n - 2);
							int s = (j / i + o - 64) * i;
							int t = (k / i + p - 64) * i;
							Multiset<MaterialColor> multiset = HashMultiset.create();
							Chunk chunk = world.getChunk(new BlockPos(s, 0, t));
							if (!chunk.isEmpty()) {
								int u = s & 15;
								int v = t & 15;
								int w = 0;
								double e = 0.0;
								if (world.dimension.hasNoSkylight()) {
									int x = s + t * 231871;
									x = x * x * 31287121 + x * 11;
									if ((x >> 20 & 1) == 0) {
										multiset.add(Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT).getMaterialColor(world, BlockPos.ORIGIN), 10);
									} else {
										multiset.add(Blocks.STONE.getDefaultState().with(StoneBlock.VARIANT, StoneBlock.StoneType.STONE).getMaterialColor(world, BlockPos.ORIGIN), 100);
									}

									e = 100.0;
								} else {
									BlockPos.Mutable mutable = new BlockPos.Mutable();

									for (int y = 0; y < i; y++) {
										for (int z = 0; z < i; z++) {
											int aa = chunk.getHighestBlockY(y + u, z + v) + 1;
											BlockState blockState = Blocks.AIR.getDefaultState();
											if (aa <= 1) {
												blockState = Blocks.BEDROCK.getDefaultState();
											} else {
												do {
													blockState = chunk.getBlockState(y + u, --aa, z + v);
													mutable.setPosition((chunk.chunkX << 4) + y + u, aa, (chunk.chunkZ << 4) + z + v);
												} while (blockState.getMaterialColor(world, mutable) == MaterialColor.AIR && aa > 0);

												if (aa > 0 && blockState.getMaterial().isFluid()) {
													int ab = aa - 1;

													BlockState blockState2;
													do {
														blockState2 = chunk.getBlockState(y + u, ab--, z + v);
														w++;
													} while (ab > 0 && blockState2.getMaterial().isFluid());
												}
											}

											e += (double)aa / (double)(i * i);
											multiset.add(blockState.getMaterialColor(world, mutable));
										}
									}
								}

								w /= i * i;
								double f = (e - d) * 4.0 / (double)(i + 4) + ((double)(o + p & 1) - 0.5) * 0.4;
								int ac = 1;
								if (f > 0.6) {
									ac = 2;
								}

								if (f < -0.6) {
									ac = 0;
								}

								MaterialColor materialColor = (MaterialColor)Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialColor.AIR);
								if (materialColor == MaterialColor.WATER) {
									f = (double)w * 0.1 + (double)(o + p & 1) * 0.2;
									ac = 1;
									if (f < 0.5) {
										ac = 2;
									}

									if (f > 0.9) {
										ac = 0;
									}
								}

								d = e;
								if (p >= 0 && q * q + r * r < n * n && (!bl2 || (o + p & 1) != 0)) {
									byte b = state.colors[o + p * 128];
									byte c = (byte)(materialColor.id * 4 + ac);
									if (b != c) {
										state.colors[o + p * 128] = c;
										state.markDirty(o, p);
										bl = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void method_13664(World world, ItemStack itemStack) {
		if (itemStack.getItem() == Items.FILLED_MAP) {
			MapState mapState = Items.FILLED_MAP.getMapState(itemStack, world);
			if (mapState != null) {
				if (world.dimension.getDimensionType().getId() == mapState.dimensionId) {
					int i = 1 << mapState.scale;
					int j = mapState.xCenter;
					int k = mapState.zCenter;
					Biome[] biomes = world.method_3726().method_11538(null, (j / i - 64) * i, (k / i - 64) * i, 128 * i, 128 * i, false);

					for (int l = 0; l < 128; l++) {
						for (int m = 0; m < 128; m++) {
							int n = l * i;
							int o = m * i;
							Biome biome = biomes[n + o * 128 * i];
							MaterialColor materialColor = MaterialColor.AIR;
							int p = 3;
							int q = 8;
							if (l > 0 && m > 0 && l < 127 && m < 127) {
								if (biomes[(l - 1) * i + (m - 1) * i * 128 * i].getDepth() >= 0.0F) {
									q--;
								}

								if (biomes[(l - 1) * i + (m + 1) * i * 128 * i].getDepth() >= 0.0F) {
									q--;
								}

								if (biomes[(l - 1) * i + m * i * 128 * i].getDepth() >= 0.0F) {
									q--;
								}

								if (biomes[(l + 1) * i + (m - 1) * i * 128 * i].getDepth() >= 0.0F) {
									q--;
								}

								if (biomes[(l + 1) * i + (m + 1) * i * 128 * i].getDepth() >= 0.0F) {
									q--;
								}

								if (biomes[(l + 1) * i + m * i * 128 * i].getDepth() >= 0.0F) {
									q--;
								}

								if (biomes[l * i + (m - 1) * i * 128 * i].getDepth() >= 0.0F) {
									q--;
								}

								if (biomes[l * i + (m + 1) * i * 128 * i].getDepth() >= 0.0F) {
									q--;
								}

								if (biome.getDepth() < 0.0F) {
									materialColor = MaterialColor.ORANGE;
									if (q > 7 && m % 2 == 0) {
										p = (l + (int)(MathHelper.sin((float)m + 0.0F) * 7.0F)) / 8 % 5;
										if (p == 3) {
											p = 1;
										} else if (p == 4) {
											p = 0;
										}
									} else if (q > 7) {
										materialColor = MaterialColor.AIR;
									} else if (q > 5) {
										p = 1;
									} else if (q > 3) {
										p = 0;
									} else if (q > 1) {
										p = 0;
									}
								} else if (q > 0) {
									materialColor = MaterialColor.BROWN;
									if (q > 3) {
										p = 1;
									} else {
										p = 3;
									}
								}
							}

							if (materialColor != MaterialColor.AIR) {
								mapState.colors[l + m * 128] = (byte)(materialColor.id * 4 + p);
								mapState.markDirty(l, m);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			MapState mapState = this.getMapState(stack, world);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				mapState.update(playerEntity, stack);
			}

			if (selected || entity instanceof PlayerEntity && ((PlayerEntity)entity).getOffHandStack() == stack) {
				this.updateColors(world, entity, mapState);
			}
		}
	}

	@Nullable
	@Override
	public Packet<?> createSyncPacket(ItemStack stack, World world, PlayerEntity player) {
		return this.getMapState(stack, world).createMapSyncPacket(stack, world, player);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null) {
			if (nbtCompound.contains("map_scale_direction", 99)) {
				method_11399(stack, world, nbtCompound.getInt("map_scale_direction"));
				nbtCompound.remove("map_scale_direction");
			} else if (nbtCompound.getBoolean("map_tracking_position")) {
				method_11400(stack, world);
				nbtCompound.remove("map_tracking_position");
			}
		}
	}

	protected static void method_11399(ItemStack itemStack, World world, int i) {
		MapState mapState = Items.FILLED_MAP.getMapState(itemStack, world);
		itemStack.setDamage(world.getIntState("map"));
		MapState mapState2 = new MapState("map_" + itemStack.getData());
		if (mapState != null) {
			mapState2.scale = (byte)MathHelper.clamp(mapState.scale + i, 0, 4);
			mapState2.trackingPosition = mapState.trackingPosition;
			mapState2.method_9308((double)mapState.xCenter, (double)mapState.zCenter, mapState2.scale);
			mapState2.dimensionId = mapState.dimensionId;
			mapState2.markDirty();
			world.replaceState("map_" + itemStack.getData(), mapState2);
		}
	}

	protected static void method_11400(ItemStack itemStack, World world) {
		MapState mapState = Items.FILLED_MAP.getMapState(itemStack, world);
		itemStack.setDamage(world.getIntState("map"));
		MapState mapState2 = new MapState("map_" + itemStack.getData());
		mapState2.trackingPosition = true;
		if (mapState != null) {
			mapState2.xCenter = mapState.xCenter;
			mapState2.zCenter = mapState.zCenter;
			mapState2.scale = mapState.scale;
			mapState2.dimensionId = mapState.dimensionId;
			mapState2.markDirty();
			world.replaceState("map_" + itemStack.getData(), mapState2);
		}
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<String> tooltip, TooltipContext tooltipContext) {
		if (tooltipContext.isAdvanced()) {
			MapState mapState = world == null ? null : this.getMapState(stack, world);
			if (mapState != null) {
				tooltip.add(CommonI18n.translate("filled_map.scale", 1 << mapState.scale));
				tooltip.add(CommonI18n.translate("filled_map.level", mapState.scale, 4));
			} else {
				tooltip.add(CommonI18n.translate("filled_map.unknown"));
			}
		}
	}

	public static int method_13665(ItemStack itemStack) {
		NbtCompound nbtCompound = itemStack.getNbtCompound("display");
		if (nbtCompound != null && nbtCompound.contains("MapColor", 99)) {
			int i = nbtCompound.getInt("MapColor");
			return 0xFF000000 | i & 16777215;
		} else {
			return -12173266;
		}
	}
}
