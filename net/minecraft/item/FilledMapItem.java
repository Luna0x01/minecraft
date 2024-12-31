package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class FilledMapItem extends NetworkSyncedItem {
	protected FilledMapItem() {
		this.setUnbreakable(true);
	}

	public static MapState getMapState(int i, World world) {
		String string = "map_" + i;
		MapState mapState = (MapState)world.getOrCreateState(MapState.class, string);
		if (mapState == null) {
			mapState = new MapState(string);
			world.replaceState(string, mapState);
		}

		return mapState;
	}

	public MapState getMapState(ItemStack itemStack, World world) {
		String string = "map_" + itemStack.getData();
		MapState mapState = (MapState)world.getOrCreateState(MapState.class, string);
		if (mapState == null && !world.isClient) {
			itemStack.setDamage(world.getIntState("map"));
			string = "map_" + itemStack.getData();
			mapState = new MapState(string);
			mapState.scale = 3;
			mapState.method_9308((double)world.getLevelProperties().getSpawnX(), (double)world.getLevelProperties().getSpawnZ(), mapState.scale);
			mapState.dimensionId = (byte)world.dimension.getType();
			mapState.markDirty();
			world.replaceState(string, mapState);
		}

		return mapState;
	}

	public void updateColors(World world, Entity entity, MapState state) {
		if (world.dimension.getType() == state.dimensionId && entity instanceof PlayerEntity) {
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
										multiset.add(Blocks.DIRT.getMaterialColor(Blocks.DIRT.getDefaultState().with(DirtBlock.VARIANT, DirtBlock.DirtType.DIRT)), 10);
									} else {
										multiset.add(Blocks.STONE.getMaterialColor(Blocks.STONE.getDefaultState().with(StoneBlock.VARIANT, StoneBlock.StoneType.STONE)), 100);
									}

									e = 100.0;
								} else {
									BlockPos.Mutable mutable = new BlockPos.Mutable();

									for (int y = 0; y < i; y++) {
										for (int z = 0; z < i; z++) {
											int aa = chunk.getHighestBlockY(y + u, z + v) + 1;
											BlockState blockState = Blocks.AIR.getDefaultState();
											if (aa > 1) {
												do {
													blockState = chunk.method_9154(mutable.setPosition(y + u, --aa, z + v));
												} while (blockState.getBlock().getMaterialColor(blockState) == MaterialColor.AIR && aa > 0);

												if (aa > 0 && blockState.getBlock().getMaterial().isFluid()) {
													int ab = aa - 1;

													Block block;
													do {
														block = chunk.method_9131(y + u, ab--, z + v);
														w++;
													} while (ab > 0 && block.getMaterial().isFluid());
												}
											}

											e += (double)aa / (double)(i * i);
											multiset.add(blockState.getBlock().getMaterialColor(blockState));
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

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			MapState mapState = this.getMapState(stack, world);
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				mapState.update(playerEntity, stack);
			}

			if (selected) {
				this.updateColors(world, entity, mapState);
			}
		}
	}

	@Override
	public Packet createSyncPacket(ItemStack stack, World world, PlayerEntity player) {
		return this.getMapState(stack, world).createMapSyncPacket(stack, world, player);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		if (stack.hasNbt() && stack.getNbt().getBoolean("map_is_scaling")) {
			MapState mapState = Items.FILLED_MAP.getMapState(stack, world);
			stack.setDamage(world.getIntState("map"));
			MapState mapState2 = new MapState("map_" + stack.getData());
			mapState2.scale = (byte)(mapState.scale + 1);
			if (mapState2.scale > 4) {
				mapState2.scale = 4;
			}

			mapState2.method_9308((double)mapState.xCenter, (double)mapState.zCenter, mapState2.scale);
			mapState2.dimensionId = mapState.dimensionId;
			mapState2.markDirty();
			world.replaceState("map_" + stack.getData(), mapState2);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, PlayerEntity player, List<String> lines, boolean advanced) {
		MapState mapState = this.getMapState(stack, player.world);
		if (advanced) {
			if (mapState == null) {
				lines.add("Unknown map");
			} else {
				lines.add("Scaling at 1:" + (1 << mapState.scale));
				lines.add("(Level " + mapState.scale + "/" + 4 + ")");
			}
		}
	}
}
