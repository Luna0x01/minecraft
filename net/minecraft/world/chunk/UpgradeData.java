package net.minecraft.world.chunk;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.EightWayDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final UpgradeData NO_UPGRADE_DATA = new UpgradeData();
	private static final EightWayDirection[] EIGHT_WAYS = EightWayDirection.values();
	private final EnumSet<EightWayDirection> sidesToUpgrade = EnumSet.noneOf(EightWayDirection.class);
	private final int[][] centerIndicesToUpgrade = new int[16][];
	private static final Map<Block, UpgradeData.Logic> BLOCK_TO_LOGIC = new IdentityHashMap();
	private static final Set<UpgradeData.Logic> CALLBACK_LOGICS = Sets.newHashSet();

	private UpgradeData() {
	}

	public UpgradeData(CompoundTag compoundTag) {
		this();
		if (compoundTag.contains("Indices", 10)) {
			CompoundTag compoundTag2 = compoundTag.getCompound("Indices");

			for (int i = 0; i < this.centerIndicesToUpgrade.length; i++) {
				String string = String.valueOf(i);
				if (compoundTag2.contains(string, 11)) {
					this.centerIndicesToUpgrade[i] = compoundTag2.getIntArray(string);
				}
			}
		}

		int j = compoundTag.getInt("Sides");

		for (EightWayDirection eightWayDirection : EightWayDirection.values()) {
			if ((j & 1 << eightWayDirection.ordinal()) != 0) {
				this.sidesToUpgrade.add(eightWayDirection);
			}
		}
	}

	public void upgrade(WorldChunk worldChunk) {
		this.upgradeCenter(worldChunk);

		for (EightWayDirection eightWayDirection : EIGHT_WAYS) {
			upgradeSide(worldChunk, eightWayDirection);
		}

		World world = worldChunk.getWorld();
		CALLBACK_LOGICS.forEach(logic -> logic.postUpdate(world));
	}

	private static void upgradeSide(WorldChunk worldChunk, EightWayDirection eightWayDirection) {
		World world = worldChunk.getWorld();
		if (worldChunk.getUpgradeData().sidesToUpgrade.remove(eightWayDirection)) {
			Set<Direction> set = eightWayDirection.getDirections();
			int i = 0;
			int j = 15;
			boolean bl = set.contains(Direction.field_11034);
			boolean bl2 = set.contains(Direction.field_11039);
			boolean bl3 = set.contains(Direction.field_11035);
			boolean bl4 = set.contains(Direction.field_11043);
			boolean bl5 = set.size() == 1;
			ChunkPos chunkPos = worldChunk.getPos();
			int k = chunkPos.getStartX() + (!bl5 || !bl4 && !bl3 ? (bl2 ? 0 : 15) : 1);
			int l = chunkPos.getStartX() + (!bl5 || !bl4 && !bl3 ? (bl2 ? 0 : 15) : 14);
			int m = chunkPos.getStartZ() + (!bl5 || !bl && !bl2 ? (bl4 ? 0 : 15) : 1);
			int n = chunkPos.getStartZ() + (!bl5 || !bl && !bl2 ? (bl4 ? 0 : 15) : 14);
			Direction[] directions = Direction.values();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (BlockPos blockPos : BlockPos.iterate(k, 0, m, l, world.getHeight() - 1, n)) {
				BlockState blockState = world.getBlockState(blockPos);
				BlockState blockState2 = blockState;

				for (Direction direction : directions) {
					mutable.set(blockPos).setOffset(direction);
					blockState2 = applyAdjacentBlock(blockState2, direction, world, blockPos, mutable);
				}

				Block.replaceBlock(blockState, blockState2, world, blockPos, 18);
			}
		}
	}

	private static BlockState applyAdjacentBlock(BlockState blockState, Direction direction, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return ((UpgradeData.Logic)BLOCK_TO_LOGIC.getOrDefault(blockState.getBlock(), UpgradeData.BulitinLogic.field_12962))
			.getUpdatedState(blockState, direction, iWorld.getBlockState(blockPos2), iWorld, blockPos, blockPos2);
	}

	private void upgradeCenter(WorldChunk worldChunk) {
		try (
			BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();
			BlockPos.PooledMutable pooledMutable2 = BlockPos.PooledMutable.get();
		) {
			ChunkPos chunkPos = worldChunk.getPos();
			IWorld iWorld = worldChunk.getWorld();

			for (int i = 0; i < 16; i++) {
				ChunkSection chunkSection = worldChunk.getSectionArray()[i];
				int[] is = this.centerIndicesToUpgrade[i];
				this.centerIndicesToUpgrade[i] = null;
				if (chunkSection != null && is != null && is.length > 0) {
					Direction[] directions = Direction.values();
					PalettedContainer<BlockState> palettedContainer = chunkSection.getContainer();

					for (int j : is) {
						int k = j & 15;
						int l = j >> 8 & 15;
						int m = j >> 4 & 15;
						pooledMutable.set(chunkPos.getStartX() + k, (i << 4) + l, chunkPos.getStartZ() + m);
						BlockState blockState = palettedContainer.get(j);
						BlockState blockState2 = blockState;

						for (Direction direction : directions) {
							pooledMutable2.set(pooledMutable).setOffset(direction);
							if (pooledMutable.getX() >> 4 == chunkPos.x && pooledMutable.getZ() >> 4 == chunkPos.z) {
								blockState2 = applyAdjacentBlock(blockState2, direction, iWorld, pooledMutable, pooledMutable2);
							}
						}

						Block.replaceBlock(blockState, blockState2, iWorld, pooledMutable, 18);
					}
				}
			}

			for (int n = 0; n < this.centerIndicesToUpgrade.length; n++) {
				if (this.centerIndicesToUpgrade[n] != null) {
					LOGGER.warn("Discarding update data for section {} for chunk ({} {})", n, chunkPos.x, chunkPos.z);
				}

				this.centerIndicesToUpgrade[n] = null;
			}
		}
	}

	public boolean isDone() {
		for (int[] is : this.centerIndicesToUpgrade) {
			if (is != null) {
				return false;
			}
		}

		return this.sidesToUpgrade.isEmpty();
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();

		for (int i = 0; i < this.centerIndicesToUpgrade.length; i++) {
			String string = String.valueOf(i);
			if (this.centerIndicesToUpgrade[i] != null && this.centerIndicesToUpgrade[i].length != 0) {
				compoundTag2.putIntArray(string, this.centerIndicesToUpgrade[i]);
			}
		}

		if (!compoundTag2.isEmpty()) {
			compoundTag.put("Indices", compoundTag2);
		}

		int j = 0;

		for (EightWayDirection eightWayDirection : this.sidesToUpgrade) {
			j |= 1 << eightWayDirection.ordinal();
		}

		compoundTag.putByte("Sides", (byte)j);
		return compoundTag;
	}

	static enum BulitinLogic implements UpgradeData.Logic {
		field_12957(
			Blocks.field_10282,
			Blocks.field_10316,
			Blocks.field_10197,
			Blocks.field_10022,
			Blocks.field_10300,
			Blocks.field_10321,
			Blocks.field_10145,
			Blocks.field_10133,
			Blocks.field_10522,
			Blocks.field_10353,
			Blocks.field_10628,
			Blocks.field_10233,
			Blocks.field_10404,
			Blocks.field_10456,
			Blocks.field_10023,
			Blocks.field_10529,
			Blocks.field_10287,
			Blocks.field_10506,
			Blocks.field_10535,
			Blocks.field_10105,
			Blocks.field_10414,
			Blocks.field_10081,
			Blocks.field_10255,
			Blocks.field_10102,
			Blocks.field_10534,
			Blocks.field_10121,
			Blocks.field_10411,
			Blocks.field_10231,
			Blocks.field_10284,
			Blocks.field_10544,
			Blocks.field_10330,
			Blocks.field_10187,
			Blocks.field_10088,
			Blocks.field_10391,
			Blocks.field_10401,
			Blocks.field_10587,
			Blocks.field_10265
		) {
			@Override
			public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				return blockState;
			}
		},
		field_12962 {
			@Override
			public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				return blockState.getStateForNeighborUpdate(direction, iWorld.getBlockState(blockPos2), iWorld, blockPos, blockPos2);
			}
		},
		field_12960(Blocks.field_10034, Blocks.field_10380) {
			@Override
			public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				if (blockState2.getBlock() == blockState.getBlock()
					&& direction.getAxis().isHorizontal()
					&& blockState.get(ChestBlock.CHEST_TYPE) == ChestType.field_12569
					&& blockState2.get(ChestBlock.CHEST_TYPE) == ChestType.field_12569) {
					Direction direction2 = blockState.get(ChestBlock.FACING);
					if (direction.getAxis() != direction2.getAxis() && direction2 == blockState2.get(ChestBlock.FACING)) {
						ChestType chestType = direction == direction2.rotateYClockwise() ? ChestType.field_12574 : ChestType.field_12571;
						iWorld.setBlockState(blockPos2, blockState2.with(ChestBlock.CHEST_TYPE, chestType.getOpposite()), 18);
						if (direction2 == Direction.field_11043 || direction2 == Direction.field_11034) {
							BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
							BlockEntity blockEntity2 = iWorld.getBlockEntity(blockPos2);
							if (blockEntity instanceof ChestBlockEntity && blockEntity2 instanceof ChestBlockEntity) {
								ChestBlockEntity.copyInventory((ChestBlockEntity)blockEntity, (ChestBlockEntity)blockEntity2);
							}
						}

						return blockState.with(ChestBlock.CHEST_TYPE, chestType);
					}
				}

				return blockState;
			}
		},
		field_12963(true, Blocks.field_10098, Blocks.field_10539, Blocks.field_10035, Blocks.field_10335, Blocks.field_10503, Blocks.field_9988) {
			private final ThreadLocal<List<ObjectSet<BlockPos>>> distanceToPositions = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity(7));

			@Override
			public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				BlockState blockState3 = blockState.getStateForNeighborUpdate(direction, iWorld.getBlockState(blockPos2), iWorld, blockPos, blockPos2);
				if (blockState != blockState3) {
					int i = (Integer)blockState3.get(Properties.DISTANCE_1_7);
					List<ObjectSet<BlockPos>> list = (List<ObjectSet<BlockPos>>)this.distanceToPositions.get();
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
			public void postUpdate(IWorld iWorld) {
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				List<ObjectSet<BlockPos>> list = (List<ObjectSet<BlockPos>>)this.distanceToPositions.get();

				for (int i = 2; i < list.size(); i++) {
					int j = i - 1;
					ObjectSet<BlockPos> objectSet = (ObjectSet<BlockPos>)list.get(j);
					ObjectSet<BlockPos> objectSet2 = (ObjectSet<BlockPos>)list.get(i);
					ObjectIterator var8 = objectSet.iterator();

					while (var8.hasNext()) {
						BlockPos blockPos = (BlockPos)var8.next();
						BlockState blockState = iWorld.getBlockState(blockPos);
						if ((Integer)blockState.get(Properties.DISTANCE_1_7) >= j) {
							iWorld.setBlockState(blockPos, blockState.with(Properties.DISTANCE_1_7, Integer.valueOf(j)), 18);
							if (i != 7) {
								for (Direction direction : DIRECTIONS) {
									mutable.set(blockPos).setOffset(direction);
									BlockState blockState2 = iWorld.getBlockState(mutable);
									if (blockState2.contains(Properties.DISTANCE_1_7) && (Integer)blockState.get(Properties.DISTANCE_1_7) > i) {
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
		field_12958(Blocks.field_10168, Blocks.field_9984) {
			@Override
			public BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				if ((Integer)blockState.get(StemBlock.AGE) == 7) {
					GourdBlock gourdBlock = ((StemBlock)blockState.getBlock()).getGourdBlock();
					if (blockState2.getBlock() == gourdBlock) {
						return gourdBlock.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction);
					}
				}

				return blockState;
			}
		};

		public static final Direction[] DIRECTIONS = Direction.values();

		private BulitinLogic(Block... blocks) {
			this(false, blocks);
		}

		private BulitinLogic(boolean bl, Block... blocks) {
			for (Block block : blocks) {
				UpgradeData.BLOCK_TO_LOGIC.put(block, this);
			}

			if (bl) {
				UpgradeData.CALLBACK_LOGICS.add(this);
			}
		}
	}

	public interface Logic {
		BlockState getUpdatedState(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2);

		default void postUpdate(IWorld iWorld) {
		}
	}
}
