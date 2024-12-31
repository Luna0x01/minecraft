package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.class_3804;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;

public class FilledMapItem extends NetworkSyncedItem {
	public FilledMapItem(Item.Settings settings) {
		super(settings);
	}

	public static ItemStack method_16113(World world, int i, int j, byte b, boolean bl, boolean bl2) {
		ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
		method_16112(itemStack, world, i, j, b, bl, bl2, world.dimension.method_11789());
		return itemStack;
	}

	@Nullable
	public static MapState method_16111(ItemStack itemStack, World world) {
		MapState mapState = method_16115(world, "map_" + method_16117(itemStack));
		if (mapState == null && !world.isClient) {
			mapState = method_16112(itemStack, world, world.method_3588().getSpawnX(), world.method_3588().getSpawnZ(), 3, false, false, world.dimension.method_11789());
		}

		return mapState;
	}

	public static int method_16117(ItemStack itemStack) {
		NbtCompound nbtCompound = itemStack.getNbt();
		return nbtCompound != null && nbtCompound.contains("map", 99) ? nbtCompound.getInt("map") : 0;
	}

	private static MapState method_16112(ItemStack itemStack, World world, int i, int j, int k, boolean bl, boolean bl2, DimensionType dimensionType) {
		int l = world.method_16396(DimensionType.OVERWORLD, "map");
		MapState mapState = new MapState("map_" + l);
		mapState.method_17931(i, j, k, bl, bl2, dimensionType);
		world.method_16397(DimensionType.OVERWORLD, mapState.method_17914(), mapState);
		itemStack.getOrCreateNbt().putInt("map", l);
		return mapState;
	}

	@Nullable
	public static MapState method_16115(IWorld iWorld, String string) {
		return iWorld.method_16398(DimensionType.OVERWORLD, MapState::new, string);
	}

	public void updateColors(World world, Entity entity, MapState state) {
		if (world.dimension.method_11789() == state.field_19747 && entity instanceof PlayerEntity) {
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
							Multiset<MaterialColor> multiset = LinkedHashMultiset.create();
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
										multiset.add(Blocks.DIRT.getDefaultState().getMaterialColor(world, BlockPos.ORIGIN), 10);
									} else {
										multiset.add(Blocks.STONE.getDefaultState().getMaterialColor(world, BlockPos.ORIGIN), 100);
									}

									e = 100.0;
								} else {
									BlockPos.Mutable mutable = new BlockPos.Mutable();

									for (int y = 0; y < i; y++) {
										for (int z = 0; z < i; z++) {
											int aa = chunk.method_16992(class_3804.class_3805.WORLD_SURFACE, y + u, z + v) + 1;
											BlockState blockState3;
											if (aa <= 1) {
												blockState3 = Blocks.BEDROCK.getDefaultState();
											} else {
												do {
													blockState3 = chunk.getBlockState(y + u, --aa, z + v);
													mutable.setPosition((chunk.chunkX << 4) + y + u, aa, (chunk.chunkZ << 4) + z + v);
												} while (blockState3.getMaterialColor(world, mutable) == MaterialColor.AIR && aa > 0);

												if (aa > 0 && !blockState3.getFluidState().isEmpty()) {
													int ab = aa - 1;

													BlockState blockState2;
													do {
														blockState2 = chunk.getBlockState(y + u, ab--, z + v);
														w++;
													} while (ab > 0 && !blockState2.getFluidState().isEmpty());

													blockState3 = this.method_16114(world, blockState3, mutable);
												}
											}

											state.method_17933(world, (chunk.chunkX << 4) + y + u, (chunk.chunkZ << 4) + z + v);
											e += (double)aa / (double)(i * i);
											multiset.add(blockState3.getMaterialColor(world, mutable));
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

	private BlockState method_16114(World world, BlockState blockState, BlockPos blockPos) {
		FluidState fluidState = blockState.getFluidState();
		return !fluidState.isEmpty() && !Block.isFaceFullSquare(blockState.getCollisionShape(world, blockPos), Direction.UP) ? fluidState.method_17813() : blockState;
	}

	private static boolean method_16116(Biome[] biomes, int i, int j, int k) {
		return biomes[j * i + k * i * 128 * i].getDepth() >= 0.0F;
	}

	public static void method_13664(World world, ItemStack itemStack) {
		MapState mapState = method_16111(itemStack, world);
		if (mapState != null) {
			if (world.dimension.method_11789() == mapState.field_19747) {
				int i = 1 << mapState.scale;
				int j = mapState.xCenter;
				int k = mapState.zCenter;
				Biome[] biomes = world.method_3586().method_17046().method_17020().method_16477((j / i - 64) * i, (k / i - 64) * i, 128 * i, 128 * i, false);

				for (int l = 0; l < 128; l++) {
					for (int m = 0; m < 128; m++) {
						if (l > 0 && m > 0 && l < 127 && m < 127) {
							Biome biome = biomes[l * i + m * i * 128 * i];
							int n = 8;
							if (method_16116(biomes, i, l - 1, m - 1)) {
								n--;
							}

							if (method_16116(biomes, i, l - 1, m + 1)) {
								n--;
							}

							if (method_16116(biomes, i, l - 1, m)) {
								n--;
							}

							if (method_16116(biomes, i, l + 1, m - 1)) {
								n--;
							}

							if (method_16116(biomes, i, l + 1, m + 1)) {
								n--;
							}

							if (method_16116(biomes, i, l + 1, m)) {
								n--;
							}

							if (method_16116(biomes, i, l, m - 1)) {
								n--;
							}

							if (method_16116(biomes, i, l, m + 1)) {
								n--;
							}

							int o = 3;
							MaterialColor materialColor = MaterialColor.AIR;
							if (biome.getDepth() < 0.0F) {
								materialColor = MaterialColor.ORANGE;
								if (n > 7 && m % 2 == 0) {
									o = (l + (int)(MathHelper.sin((float)m + 0.0F) * 7.0F)) / 8 % 5;
									if (o == 3) {
										o = 1;
									} else if (o == 4) {
										o = 0;
									}
								} else if (n > 7) {
									materialColor = MaterialColor.AIR;
								} else if (n > 5) {
									o = 1;
								} else if (n > 3) {
									o = 0;
								} else if (n > 1) {
									o = 0;
								}
							} else if (n > 0) {
								materialColor = MaterialColor.BROWN;
								if (n > 3) {
									o = 1;
								} else {
									o = 3;
								}
							}

							if (materialColor != MaterialColor.AIR) {
								mapState.colors[l + m * 128] = (byte)(materialColor.id * 4 + o);
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
			MapState mapState = method_16111(stack, world);
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
		return method_16111(stack, world).method_17932(stack, world, player);
	}

	@Override
	public void onCraft(ItemStack stack, World world, PlayerEntity player) {
		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("map_scale_direction", 99)) {
			method_11399(stack, world, nbtCompound.getInt("map_scale_direction"));
			nbtCompound.remove("map_scale_direction");
		}
	}

	protected static void method_11399(ItemStack itemStack, World world, int i) {
		MapState mapState = method_16111(itemStack, world);
		if (mapState != null) {
			method_16112(
				itemStack,
				world,
				mapState.xCenter,
				mapState.zCenter,
				MathHelper.clamp(mapState.scale + i, 0, 4),
				mapState.trackingPosition,
				mapState.field_15238,
				mapState.field_19747
			);
		}
	}

	@Override
	public void appendTooltips(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext tooltipContext) {
		if (tooltipContext.isAdvanced()) {
			MapState mapState = world == null ? null : method_16111(stack, world);
			if (mapState != null) {
				tooltip.add(new TranslatableText("filled_map.id", method_16117(stack)).formatted(Formatting.GRAY));
				tooltip.add(new TranslatableText("filled_map.scale", 1 << mapState.scale).formatted(Formatting.GRAY));
				tooltip.add(new TranslatableText("filled_map.level", mapState.scale, 4).formatted(Formatting.GRAY));
			} else {
				tooltip.add(new TranslatableText("filled_map.unknown").formatted(Formatting.GRAY));
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

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		BlockState blockState = itemUsageContext.getWorld().getBlockState(itemUsageContext.getBlockPos());
		if (blockState.isIn(BlockTags.BANNERS)) {
			if (!itemUsageContext.field_17405.isClient) {
				MapState mapState = method_16111(itemUsageContext.getItemStack(), itemUsageContext.getWorld());
				mapState.method_17934(itemUsageContext.getWorld(), itemUsageContext.getBlockPos());
			}

			return ActionResult.SUCCESS;
		} else {
			return super.useOnBlock(itemUsageContext);
		}
	}
}
