package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.palette.PaletteContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3790 {
	private static final Logger field_18936 = LogManager.getLogger();
	public static final class_3790 field_18935 = new class_3790();
	private static final class_4335[] field_18937 = class_4335.values();
	private final EnumSet<class_4335> field_18938 = EnumSet.noneOf(class_4335.class);
	private final int[][] field_18939 = new int[16][];
	private static final Map<Block, class_3790.class_3791> field_18940 = new IdentityHashMap();
	private static final Set<class_3790.class_3791> field_18941 = Sets.newHashSet();

	private class_3790() {
	}

	public class_3790(NbtCompound nbtCompound) {
		this();
		if (nbtCompound.contains("Indices", 10)) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("Indices");

			for (int i = 0; i < this.field_18939.length; i++) {
				String string = String.valueOf(i);
				if (nbtCompound2.contains(string, 11)) {
					this.field_18939[i] = nbtCompound2.getIntArray(string);
				}
			}
		}

		int j = nbtCompound.getInt("Sides");

		for (class_4335 lv : class_4335.values()) {
			if ((j & 1 << lv.ordinal()) != 0) {
				this.field_18938.add(lv);
			}
		}
	}

	public void method_17154(Chunk chunk) {
		this.method_17157(chunk);

		for (class_4335 lv : field_18937) {
			method_17155(chunk, lv);
		}

		World world = chunk.getWorld();
		field_18941.forEach(arg -> arg.method_17160(world));
	}

	private static void method_17155(Chunk chunk, class_4335 arg) {
		World world = chunk.getWorld();
		if (chunk.method_17065().field_18938.remove(arg)) {
			Set<Direction> set = arg.method_19951();
			int i = 0;
			int j = 15;
			boolean bl = set.contains(Direction.EAST);
			boolean bl2 = set.contains(Direction.WEST);
			boolean bl3 = set.contains(Direction.SOUTH);
			boolean bl4 = set.contains(Direction.NORTH);
			boolean bl5 = set.size() == 1;
			int k = (chunk.chunkX << 4) + (!bl5 || !bl4 && !bl3 ? (bl2 ? 0 : 15) : 1);
			int l = (chunk.chunkX << 4) + (!bl5 || !bl4 && !bl3 ? (bl2 ? 0 : 15) : 14);
			int m = (chunk.chunkZ << 4) + (!bl5 || !bl && !bl2 ? (bl4 ? 0 : 15) : 1);
			int n = (chunk.chunkZ << 4) + (!bl5 || !bl && !bl2 ? (bl4 ? 0 : 15) : 14);
			Direction[] directions = Direction.values();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (BlockPos.Mutable mutable2 : BlockPos.mutableIterate(k, 0, m, l, world.getMaxBuildHeight() - 1, n)) {
				BlockState blockState = world.getBlockState(mutable2);
				BlockState blockState2 = blockState;

				for (Direction direction : directions) {
					mutable.set(mutable2).move(direction);
					blockState2 = method_17153(blockState2, direction, world, mutable2, mutable);
				}

				Block.method_16572(blockState, blockState2, world, mutable2, 18);
			}
		}
	}

	private static BlockState method_17153(BlockState blockState, Direction direction, IWorld iWorld, BlockPos.Mutable mutable, BlockPos.Mutable mutable2) {
		return ((class_3790.class_3791)field_18940.getOrDefault(blockState.getBlock(), class_3790.class_3792.DEFAULT))
			.method_17161(blockState, direction, iWorld.getBlockState(mutable2), iWorld, mutable, mutable2);
	}

	private void method_17157(Chunk chunk) {
		try (
			BlockPos.Pooled pooled = BlockPos.Pooled.get();
			BlockPos.Pooled pooled2 = BlockPos.Pooled.get();
		) {
			IWorld iWorld = chunk.getWorld();

			for (int i = 0; i < 16; i++) {
				ChunkSection chunkSection = chunk.method_17003()[i];
				int[] is = this.field_18939[i];
				this.field_18939[i] = null;
				if (chunkSection != null && is != null && is.length > 0) {
					Direction[] directions = Direction.values();
					PaletteContainer<BlockState> paletteContainer = chunkSection.getBlockData();

					for (int j : is) {
						int k = j & 15;
						int l = j >> 8 & 15;
						int m = j >> 4 & 15;
						pooled.setPosition(k + (chunk.chunkX << 4), l + (i << 4), m + (chunk.chunkZ << 4));
						BlockState blockState = paletteContainer.method_17099(j);
						BlockState blockState2 = blockState;

						for (Direction direction : directions) {
							pooled2.set(pooled).move(direction);
							if (pooled.getX() >> 4 == chunk.chunkX && pooled.getZ() >> 4 == chunk.chunkZ) {
								blockState2 = method_17153(blockState2, direction, iWorld, pooled, pooled2);
							}
						}

						Block.method_16572(blockState, blockState2, iWorld, pooled, 18);
					}
				}
			}

			for (int n = 0; n < this.field_18939.length; n++) {
				if (this.field_18939[n] != null) {
					field_18936.warn("Discarding update data for section {} for chunk ({} {})", n, chunk.chunkX, chunk.chunkZ);
				}

				this.field_18939[n] = null;
			}
		}
	}

	public boolean method_17151() {
		for (int[] is : this.field_18939) {
			if (is != null) {
				return false;
			}
		}

		return this.field_18938.isEmpty();
	}

	public NbtCompound method_17156() {
		NbtCompound nbtCompound = new NbtCompound();
		NbtCompound nbtCompound2 = new NbtCompound();

		for (int i = 0; i < this.field_18939.length; i++) {
			String string = String.valueOf(i);
			if (this.field_18939[i] != null && this.field_18939[i].length != 0) {
				nbtCompound2.putIntArray(string, this.field_18939[i]);
			}
		}

		if (!nbtCompound2.isEmpty()) {
			nbtCompound.put("Indices", nbtCompound2);
		}

		int j = 0;

		for (class_4335 lv : this.field_18938) {
			j |= 1 << lv.ordinal();
		}

		nbtCompound.putByte("Sides", (byte)j);
		return nbtCompound;
	}

	public interface class_3791 {
		BlockState method_17161(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2);

		default void method_17160(IWorld iWorld) {
		}
	}

	static enum class_3792 implements class_3790.class_3791 {
		BLACKLIST(
			Blocks.OBSERVER,
			Blocks.NETHER_PORTAL,
			Blocks.WHITE_CONCRETE_POWDER,
			Blocks.ORANGE_CONCRETE_POWDER,
			Blocks.MAGENTA_CONCRETE_POWDER,
			Blocks.LIGHT_BLUE_CONCRETE_POWDER,
			Blocks.YELLOW_CONCRETE_POWDER,
			Blocks.LIME_CONCRETE_POWDER,
			Blocks.PINK_CONCRETE_POWDER,
			Blocks.GRAY_CONCRETE_POWDER,
			Blocks.LIGHT_GRAY_CONCRETE_POWDER,
			Blocks.CYAN_CONCRETE_POWDER,
			Blocks.PURPLE_CONCRETE_POWDER,
			Blocks.BLUE_CONCRETE_POWDER,
			Blocks.BROWN_CONCRETE_POWDER,
			Blocks.GREEN_CONCRETE_POWDER,
			Blocks.RED_CONCRETE_POWDER,
			Blocks.BLACK_CONCRETE_POWDER,
			Blocks.ANVIL,
			Blocks.CHIPPED_ANVIL,
			Blocks.DAMAGED_ANVIL,
			Blocks.DRAGON_EGG,
			Blocks.GRAVEL,
			Blocks.SAND,
			Blocks.RED_SAND,
			Blocks.SIGN,
			Blocks.WALL_SIGN
		) {
			@Override
			public BlockState method_17161(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				return blockState;
			}
		},
		DEFAULT {
			@Override
			public BlockState method_17161(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				return blockState.getStateForNeighborUpdate(direction, iWorld.getBlockState(blockPos2), iWorld, blockPos, blockPos2);
			}
		},
		CHEST(Blocks.CHEST, Blocks.TRAPPED_CHEST) {
			@Override
			public BlockState method_17161(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				if (blockState2.getBlock() == blockState.getBlock()
					&& direction.getAxis().isHorizontal()
					&& blockState.getProperty(ChestBlock.CHEST_TYPE) == ChestType.SINGLE
					&& blockState2.getProperty(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) {
					Direction direction2 = blockState.getProperty(ChestBlock.FACING);
					if (direction.getAxis() != direction2.getAxis() && direction2 == blockState2.getProperty(ChestBlock.FACING)) {
						ChestType chestType = direction == direction2.rotateYClockwise() ? ChestType.LEFT : ChestType.RIGHT;
						iWorld.setBlockState(blockPos2, blockState2.withProperty(ChestBlock.CHEST_TYPE, chestType.getOpposite()), 18);
						if (direction2 == Direction.NORTH || direction2 == Direction.EAST) {
							BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
							BlockEntity blockEntity2 = iWorld.getBlockEntity(blockPos2);
							if (blockEntity instanceof ChestBlockEntity && blockEntity2 instanceof ChestBlockEntity) {
								ChestBlockEntity.method_16793((ChestBlockEntity)blockEntity, (ChestBlockEntity)blockEntity2);
							}
						}

						return blockState.withProperty(ChestBlock.CHEST_TYPE, chestType);
					}
				}

				return blockState;
			}
		},
		LEAVES(true, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES) {
			private final ThreadLocal<List<ObjectSet<BlockPos>>> field_18949 = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity(7));

			@Override
			public BlockState method_17161(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				BlockState blockState3 = blockState.getStateForNeighborUpdate(direction, iWorld.getBlockState(blockPos2), iWorld, blockPos, blockPos2);
				if (blockState != blockState3) {
					int i = (Integer)blockState3.getProperty(Properties.DISTANCE_1_7);
					List<ObjectSet<BlockPos>> list = (List<ObjectSet<BlockPos>>)this.field_18949.get();
					if (list.isEmpty()) {
						for (int j = 0; j < 7; j++) {
							list.add(new ObjectOpenHashSet());
						}
					}

					((ObjectSet)list.get(i)).add(blockPos.toImmutable());
				}

				return blockState;
			}

			@Override
			public void method_17160(IWorld iWorld) {
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				List<ObjectSet<BlockPos>> list = (List<ObjectSet<BlockPos>>)this.field_18949.get();

				for (int i = 2; i < list.size(); i++) {
					int j = i - 1;
					ObjectSet<BlockPos> objectSet = (ObjectSet<BlockPos>)list.get(j);
					ObjectSet<BlockPos> objectSet2 = (ObjectSet<BlockPos>)list.get(i);
					ObjectIterator var8 = objectSet.iterator();

					while (var8.hasNext()) {
						BlockPos blockPos = (BlockPos)var8.next();
						BlockState blockState = iWorld.getBlockState(blockPos);
						if ((Integer)blockState.getProperty(Properties.DISTANCE_1_7) >= j) {
							iWorld.setBlockState(blockPos, blockState.withProperty(Properties.DISTANCE_1_7, Integer.valueOf(j)), 18);
							if (i != 7) {
								for (Direction direction : field_18947) {
									mutable.set(blockPos).move(direction);
									BlockState blockState2 = iWorld.getBlockState(mutable);
									if (blockState2.method_16933(Properties.DISTANCE_1_7) && (Integer)blockState.getProperty(Properties.DISTANCE_1_7) > i) {
										objectSet2.add(mutable.toImmutable());
									}
								}
							}
						}
					}
				}

				list.clear();
			}
		},
		STEM_BLOCK(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM) {
			@Override
			public BlockState method_17161(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				if ((Integer)blockState.getProperty(StemBlock.AGE) == 7) {
					GourdBlock gourdBlock = ((StemBlock)blockState.getBlock()).getGourdBlock();
					if (blockState2.getBlock() == gourdBlock) {
						return gourdBlock.getAttachedStem().getDefaultState().withProperty(HorizontalFacingBlock.FACING, direction);
					}
				}

				return blockState;
			}
		};

		public static final Direction[] field_18947 = Direction.values();

		private class_3792(Block... blocks) {
			this(false, blocks);
		}

		private class_3792(boolean bl, Block... blocks) {
			for (Block block : blocks) {
				class_3790.field_18940.put(block, this);
			}

			if (bl) {
				class_3790.field_18941.add(this);
			}
		}
	}
}
