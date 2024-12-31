package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3735;
import net.minecraft.class_3998;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class StrongholdPieces {
	private static final StrongholdPieces.PieceData[] ALL_PIECES = new StrongholdPieces.PieceData[]{
		new StrongholdPieces.PieceData(StrongholdPieces.StraightCorridor.class, 40, 0),
		new StrongholdPieces.PieceData(StrongholdPieces.PrisonHall.class, 5, 5),
		new StrongholdPieces.PieceData(StrongholdPieces.LeftTurn.class, 20, 0),
		new StrongholdPieces.PieceData(StrongholdPieces.RightTurn.class, 20, 0),
		new StrongholdPieces.PieceData(StrongholdPieces.SquareRoom.class, 10, 6),
		new StrongholdPieces.PieceData(StrongholdPieces.StraightStairs.class, 5, 5),
		new StrongholdPieces.PieceData(StrongholdPieces.SpiralStaircase.class, 5, 5),
		new StrongholdPieces.PieceData(StrongholdPieces.FiveWayCrossing.class, 5, 4),
		new StrongholdPieces.PieceData(StrongholdPieces.ChestCorridor.class, 5, 4),
		new StrongholdPieces.PieceData(StrongholdPieces.Library.class, 10, 2) {
			@Override
			public boolean canGenerate(int chainLength) {
				return super.canGenerate(chainLength) && chainLength > 4;
			}
		},
		new StrongholdPieces.PieceData(StrongholdPieces.EndPortalRoom.class, 20, 1) {
			@Override
			public boolean canGenerate(int chainLength) {
				return super.canGenerate(chainLength) && chainLength > 5;
			}
		}
	};
	private static List<StrongholdPieces.PieceData> POSSIBLE_PIECES;
	private static Class<? extends StrongholdPieces.AbstractPiece> ACTIVE_PIECE_TYPE;
	private static int TOTAL_WEIGHT;
	private static final StrongholdPieces.InfestedStoneRandomizer INFESTED_STONE_RANDOMIZER = new StrongholdPieces.InfestedStoneRandomizer();

	public static void registerPieces() {
		StructurePieceManager.registerPiece(StrongholdPieces.ChestCorridor.class, "SHCC");
		StructurePieceManager.registerPiece(StrongholdPieces.FlexibleCorridor.class, "SHFC");
		StructurePieceManager.registerPiece(StrongholdPieces.FiveWayCrossing.class, "SH5C");
		StructurePieceManager.registerPiece(StrongholdPieces.LeftTurn.class, "SHLT");
		StructurePieceManager.registerPiece(StrongholdPieces.Library.class, "SHLi");
		StructurePieceManager.registerPiece(StrongholdPieces.EndPortalRoom.class, "SHPR");
		StructurePieceManager.registerPiece(StrongholdPieces.PrisonHall.class, "SHPH");
		StructurePieceManager.registerPiece(StrongholdPieces.RightTurn.class, "SHRT");
		StructurePieceManager.registerPiece(StrongholdPieces.SquareRoom.class, "SHRC");
		StructurePieceManager.registerPiece(StrongholdPieces.SpiralStaircase.class, "SHSD");
		StructurePieceManager.registerPiece(StrongholdPieces.StartPiece.class, "SHStart");
		StructurePieceManager.registerPiece(StrongholdPieces.StraightCorridor.class, "SHS");
		StructurePieceManager.registerPiece(StrongholdPieces.StraightStairs.class, "SHSSD");
	}

	public static void init() {
		POSSIBLE_PIECES = Lists.newArrayList();

		for (StrongholdPieces.PieceData pieceData : ALL_PIECES) {
			pieceData.generatedCount = 0;
			POSSIBLE_PIECES.add(pieceData);
		}

		ACTIVE_PIECE_TYPE = null;
	}

	private static boolean checkRemainingPieces() {
		boolean bl = false;
		TOTAL_WEIGHT = 0;

		for (StrongholdPieces.PieceData pieceData : POSSIBLE_PIECES) {
			if (pieceData.limit > 0 && pieceData.generatedCount < pieceData.limit) {
				bl = true;
			}

			TOTAL_WEIGHT = TOTAL_WEIGHT + pieceData.weight;
		}

		return bl;
	}

	private static StrongholdPieces.AbstractPiece createPiece(
		Class<? extends StrongholdPieces.AbstractPiece> pieceType,
		List<StructurePiece> pieces,
		Random random,
		int x,
		int y,
		int z,
		@Nullable Direction orientation,
		int chainLength
	) {
		StrongholdPieces.AbstractPiece abstractPiece = null;
		if (pieceType == StrongholdPieces.StraightCorridor.class) {
			abstractPiece = StrongholdPieces.StraightCorridor.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.PrisonHall.class) {
			abstractPiece = StrongholdPieces.PrisonHall.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.LeftTurn.class) {
			abstractPiece = StrongholdPieces.LeftTurn.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.RightTurn.class) {
			abstractPiece = StrongholdPieces.RightTurn.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.SquareRoom.class) {
			abstractPiece = StrongholdPieces.SquareRoom.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.StraightStairs.class) {
			abstractPiece = StrongholdPieces.StraightStairs.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.SpiralStaircase.class) {
			abstractPiece = StrongholdPieces.SpiralStaircase.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.FiveWayCrossing.class) {
			abstractPiece = StrongholdPieces.FiveWayCrossing.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.ChestCorridor.class) {
			abstractPiece = StrongholdPieces.ChestCorridor.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.Library.class) {
			abstractPiece = StrongholdPieces.Library.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (pieceType == StrongholdPieces.EndPortalRoom.class) {
			abstractPiece = StrongholdPieces.EndPortalRoom.create(pieces, random, x, y, z, orientation, chainLength);
		}

		return abstractPiece;
	}

	private static StrongholdPieces.AbstractPiece pickPiece(
		StrongholdPieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		if (!checkRemainingPieces()) {
			return null;
		} else {
			if (ACTIVE_PIECE_TYPE != null) {
				StrongholdPieces.AbstractPiece abstractPiece = createPiece(ACTIVE_PIECE_TYPE, pieces, random, x, y, z, orientation, chainLength);
				ACTIVE_PIECE_TYPE = null;
				if (abstractPiece != null) {
					return abstractPiece;
				}
			}

			int i = 0;

			while (i < 5) {
				i++;
				int j = random.nextInt(TOTAL_WEIGHT);

				for (StrongholdPieces.PieceData pieceData : POSSIBLE_PIECES) {
					j -= pieceData.weight;
					if (j < 0) {
						if (!pieceData.canGenerate(chainLength) || pieceData == start.lastPiece) {
							break;
						}

						StrongholdPieces.AbstractPiece abstractPiece2 = createPiece(pieceData.pieceType, pieces, random, x, y, z, orientation, chainLength);
						if (abstractPiece2 != null) {
							pieceData.generatedCount++;
							start.lastPiece = pieceData;
							if (!pieceData.canGenerate()) {
								POSSIBLE_PIECES.remove(pieceData);
							}

							return abstractPiece2;
						}
					}
				}
			}

			BlockBox blockBox = StrongholdPieces.FlexibleCorridor.create(pieces, random, x, y, z, orientation);
			return blockBox != null && blockBox.minY > 1 ? new StrongholdPieces.FlexibleCorridor(chainLength, random, blockBox, orientation) : null;
		}
	}

	private static StructurePiece generate(
		StrongholdPieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, @Nullable Direction orientation, int chainLength
	) {
		if (chainLength > 50) {
			return null;
		} else if (Math.abs(x - start.getBoundingBox().minX) <= 112 && Math.abs(z - start.getBoundingBox().minZ) <= 112) {
			StructurePiece structurePiece = pickPiece(start, pieces, random, x, y, z, orientation, chainLength + 1);
			if (structurePiece != null) {
				pieces.add(structurePiece);
				start.pieces.add(structurePiece);
			}

			return structurePiece;
		} else {
			return null;
		}
	}

	abstract static class AbstractPiece extends StructurePiece {
		protected StrongholdPieces.AbstractPiece.EntranceType entryDoor = StrongholdPieces.AbstractPiece.EntranceType.OPENING;

		public AbstractPiece() {
		}

		protected AbstractPiece(int i) {
			super(i);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			structureNbt.putString("EntryDoor", this.entryDoor.name());
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			this.entryDoor = StrongholdPieces.AbstractPiece.EntranceType.valueOf(nbtCompound.getString("EntryDoor"));
		}

		protected void method_41(IWorld iWorld, Random random, BlockBox blockBox, StrongholdPieces.AbstractPiece.EntranceType entranceType, int i, int j, int k) {
			switch (entranceType) {
				case OPENING:
					this.method_17653(iWorld, blockBox, i, j, k, i + 3 - 1, j + 3 - 1, k, field_19406, field_19406, false);
					break;
				case WOOD_DOOR:
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j + 1, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j + 2, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 1, j + 2, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 2, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 1, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j, k, blockBox);
					this.method_56(iWorld, Blocks.OAK_DOOR.getDefaultState(), i + 1, j, k, blockBox);
					this.method_56(iWorld, Blocks.OAK_DOOR.getDefaultState().withProperty(DoorBlock.field_18296, DoubleBlockHalf.UPPER), i + 1, j + 1, k, blockBox);
					break;
				case GRATES:
					this.method_56(iWorld, Blocks.CAVE_AIR.getDefaultState(), i + 1, j, k, blockBox);
					this.method_56(iWorld, Blocks.CAVE_AIR.getDefaultState(), i + 1, j + 1, k, blockBox);
					this.method_56(iWorld, Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18268, Boolean.valueOf(true)), i, j, k, blockBox);
					this.method_56(iWorld, Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18268, Boolean.valueOf(true)), i, j + 1, k, blockBox);
					this.method_56(
						iWorld,
						Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18266, Boolean.valueOf(true)).withProperty(PaneBlock.field_18268, Boolean.valueOf(true)),
						i,
						j + 2,
						k,
						blockBox
					);
					this.method_56(
						iWorld,
						Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18266, Boolean.valueOf(true)).withProperty(PaneBlock.field_18268, Boolean.valueOf(true)),
						i + 1,
						j + 2,
						k,
						blockBox
					);
					this.method_56(
						iWorld,
						Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18266, Boolean.valueOf(true)).withProperty(PaneBlock.field_18268, Boolean.valueOf(true)),
						i + 2,
						j + 2,
						k,
						blockBox
					);
					this.method_56(iWorld, Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18266, Boolean.valueOf(true)), i + 2, j + 1, k, blockBox);
					this.method_56(iWorld, Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18266, Boolean.valueOf(true)), i + 2, j, k, blockBox);
					break;
				case IRON_DOOR:
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j + 1, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i, j + 2, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 1, j + 2, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 2, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j + 1, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), i + 2, j, k, blockBox);
					this.method_56(iWorld, Blocks.IRON_DOOR.getDefaultState(), i + 1, j, k, blockBox);
					this.method_56(iWorld, Blocks.IRON_DOOR.getDefaultState().withProperty(DoorBlock.field_18296, DoubleBlockHalf.UPPER), i + 1, j + 1, k, blockBox);
					this.method_56(iWorld, Blocks.STONE_BUTTON.getDefaultState().withProperty(AbstractButtonBlock.FACING, Direction.NORTH), i + 2, j + 1, k + 1, blockBox);
					this.method_56(iWorld, Blocks.STONE_BUTTON.getDefaultState().withProperty(AbstractButtonBlock.FACING, Direction.SOUTH), i + 2, j + 1, k - 1, blockBox);
			}
		}

		protected StrongholdPieces.AbstractPiece.EntranceType getEntranceType(Random random) {
			int i = random.nextInt(5);
			switch (i) {
				case 0:
				case 1:
				default:
					return StrongholdPieces.AbstractPiece.EntranceType.OPENING;
				case 2:
					return StrongholdPieces.AbstractPiece.EntranceType.WOOD_DOOR;
				case 3:
					return StrongholdPieces.AbstractPiece.EntranceType.GRATES;
				case 4:
					return StrongholdPieces.AbstractPiece.EntranceType.IRON_DOOR;
			}
		}

		@Nullable
		protected StructurePiece fillForwardOpening(
			StrongholdPieces.StartPiece start, List<StructurePiece> pieces, Random random, int leftRightOffset, int heightOffset
		) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ - 1,
							direction,
							this.getChainLength()
						);
					case SOUTH:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.maxZ + 1,
							direction,
							this.getChainLength()
						);
					case WEST:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							direction,
							this.getChainLength()
						);
					case EAST:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							direction,
							this.getChainLength()
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece fillNWOpening(StrongholdPieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.WEST,
							this.getChainLength()
						);
					case SOUTH:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.WEST,
							this.getChainLength()
						);
					case WEST:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ - 1,
							Direction.NORTH,
							this.getChainLength()
						);
					case EAST:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ - 1,
							Direction.NORTH,
							this.getChainLength()
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece fillSEOpening(StrongholdPieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.EAST,
							this.getChainLength()
						);
					case SOUTH:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.EAST,
							this.getChainLength()
						);
					case WEST:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.maxZ + 1,
							Direction.SOUTH,
							this.getChainLength()
						);
					case EAST:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.maxZ + 1,
							Direction.SOUTH,
							this.getChainLength()
						);
				}
			}

			return null;
		}

		protected static boolean isInbounds(BlockBox boundingBox) {
			return boundingBox != null && boundingBox.minY > 10;
		}

		public static enum EntranceType {
			OPENING,
			WOOD_DOOR,
			GRATES,
			IRON_DOOR;
		}
	}

	public static class ChestCorridor extends StrongholdPieces.AbstractPiece {
		private boolean chestGenerated;

		public ChestCorridor() {
		}

		public ChestCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Chest", this.chestGenerated);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.chestGenerated = nbtCompound.getBoolean("Chest");
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
		}

		public static StrongholdPieces.ChestCorridor create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 7, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new StrongholdPieces.ChestCorridor(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 1, 1, 0);
			this.method_41(iWorld, random, blockBox, StrongholdPieces.AbstractPiece.EntranceType.OPENING, 1, 1, 6);
			this.method_17653(iWorld, blockBox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getDefaultState(), Blocks.STONE_BRICKS.getDefaultState(), false);
			this.method_56(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 1, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 1, 5, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 2, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3, 2, 4, blockBox);

			for (int i = 2; i <= 4; i++) {
				this.method_56(iWorld, Blocks.STONE_BRICK_SLAB.getDefaultState(), 2, 1, i, blockBox);
			}

			if (!this.chestGenerated && blockBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
				this.chestGenerated = true;
				this.method_11852(iWorld, blockBox, random, 3, 2, 3, LootTables.STRONGHOLD_CORRIDOR_CHEST);
			}

			return true;
		}
	}

	public static class EndPortalRoom extends StrongholdPieces.AbstractPiece {
		private boolean spawnerPlaced;

		public EndPortalRoom() {
		}

		public EndPortalRoom(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Mob", this.spawnerPlaced);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.spawnerPlaced = nbtCompound.getBoolean("Mob");
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			if (start != null) {
				((StrongholdPieces.StartPiece)start).portalRoom = this;
			}
		}

		public static StrongholdPieces.EndPortalRoom create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 11, 8, 16, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new StrongholdPieces.EndPortalRoom(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 10, 7, 15, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, StrongholdPieces.AbstractPiece.EntranceType.GRATES, 4, 1, 0);
			int i = 6;
			this.method_17655(iWorld, blockBox, 1, i, 1, 1, i, 14, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 9, i, 1, 9, i, 14, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 2, i, 1, 8, i, 2, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 2, i, 14, 8, i, 14, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 1, 1, 1, 2, 1, 4, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 8, 1, 1, 9, 1, 4, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17653(iWorld, blockBox, 1, 1, 1, 1, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 9, 1, 1, 9, 1, 3, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
			this.method_17655(iWorld, blockBox, 3, 1, 8, 7, 1, 12, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17653(iWorld, blockBox, 4, 1, 9, 6, 1, 11, Blocks.LAVA.getDefaultState(), Blocks.LAVA.getDefaultState(), false);
			BlockState blockState = Blocks.IRON_BARS
				.getDefaultState()
				.withProperty(PaneBlock.field_18265, Boolean.valueOf(true))
				.withProperty(PaneBlock.field_18267, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.IRON_BARS
				.getDefaultState()
				.withProperty(PaneBlock.field_18268, Boolean.valueOf(true))
				.withProperty(PaneBlock.field_18266, Boolean.valueOf(true));

			for (int j = 3; j < 14; j += 2) {
				this.method_17653(iWorld, blockBox, 0, 3, j, 0, 4, j, blockState, blockState, false);
				this.method_17653(iWorld, blockBox, 10, 3, j, 10, 4, j, blockState, blockState, false);
			}

			for (int k = 2; k < 9; k += 2) {
				this.method_17653(iWorld, blockBox, k, 3, 15, k, 4, 15, blockState2, blockState2, false);
			}

			BlockState blockState3 = Blocks.STONE_BRICK_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH);
			this.method_17655(iWorld, blockBox, 4, 1, 5, 6, 1, 7, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 4, 2, 6, 6, 2, 7, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 4, 3, 7, 6, 3, 7, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);

			for (int l = 4; l <= 6; l++) {
				this.method_56(iWorld, blockState3, l, 1, 4, blockBox);
				this.method_56(iWorld, blockState3, l, 2, 5, blockBox);
				this.method_56(iWorld, blockState3, l, 3, 6, blockBox);
			}

			BlockState blockState4 = Blocks.END_PORTAL_FRAME.getDefaultState().withProperty(EndPortalFrameBlock.FACING, Direction.NORTH);
			BlockState blockState5 = Blocks.END_PORTAL_FRAME.getDefaultState().withProperty(EndPortalFrameBlock.FACING, Direction.SOUTH);
			BlockState blockState6 = Blocks.END_PORTAL_FRAME.getDefaultState().withProperty(EndPortalFrameBlock.FACING, Direction.EAST);
			BlockState blockState7 = Blocks.END_PORTAL_FRAME.getDefaultState().withProperty(EndPortalFrameBlock.FACING, Direction.WEST);
			boolean bl = true;
			boolean[] bls = new boolean[12];

			for (int m = 0; m < bls.length; m++) {
				bls[m] = random.nextFloat() > 0.9F;
				bl &= bls[m];
			}

			this.method_56(iWorld, blockState4.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[0])), 4, 3, 8, blockBox);
			this.method_56(iWorld, blockState4.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[1])), 5, 3, 8, blockBox);
			this.method_56(iWorld, blockState4.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[2])), 6, 3, 8, blockBox);
			this.method_56(iWorld, blockState5.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[3])), 4, 3, 12, blockBox);
			this.method_56(iWorld, blockState5.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[4])), 5, 3, 12, blockBox);
			this.method_56(iWorld, blockState5.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[5])), 6, 3, 12, blockBox);
			this.method_56(iWorld, blockState6.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[6])), 3, 3, 9, blockBox);
			this.method_56(iWorld, blockState6.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[7])), 3, 3, 10, blockBox);
			this.method_56(iWorld, blockState6.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[8])), 3, 3, 11, blockBox);
			this.method_56(iWorld, blockState7.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[9])), 7, 3, 9, blockBox);
			this.method_56(iWorld, blockState7.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[10])), 7, 3, 10, blockBox);
			this.method_56(iWorld, blockState7.withProperty(EndPortalFrameBlock.field_18306, Boolean.valueOf(bls[11])), 7, 3, 11, blockBox);
			if (bl) {
				BlockState blockState8 = Blocks.END_PORTAL.getDefaultState();
				this.method_56(iWorld, blockState8, 4, 3, 9, blockBox);
				this.method_56(iWorld, blockState8, 5, 3, 9, blockBox);
				this.method_56(iWorld, blockState8, 6, 3, 9, blockBox);
				this.method_56(iWorld, blockState8, 4, 3, 10, blockBox);
				this.method_56(iWorld, blockState8, 5, 3, 10, blockBox);
				this.method_56(iWorld, blockState8, 6, 3, 10, blockBox);
				this.method_56(iWorld, blockState8, 4, 3, 11, blockBox);
				this.method_56(iWorld, blockState8, 5, 3, 11, blockBox);
				this.method_56(iWorld, blockState8, 6, 3, 11, blockBox);
			}

			if (!this.spawnerPlaced) {
				i = this.applyYTransform(3);
				BlockPos blockPos = new BlockPos(this.applyXTransform(5, 6), i, this.applyZTransform(5, 6));
				if (blockBox.contains(blockPos)) {
					this.spawnerPlaced = true;
					iWorld.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
					BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().method_16278(EntityType.SILVERFISH);
					}
				}
			}

			return true;
		}
	}

	public static class FiveWayCrossing extends StrongholdPieces.AbstractPiece {
		private boolean lowerLeftExists;
		private boolean upperLeftExists;
		private boolean lowerRightExists;
		private boolean upperRightExists;

		public FiveWayCrossing() {
		}

		public FiveWayCrossing(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
			this.lowerLeftExists = random.nextBoolean();
			this.upperLeftExists = random.nextBoolean();
			this.lowerRightExists = random.nextBoolean();
			this.upperRightExists = random.nextInt(3) > 0;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("leftLow", this.lowerLeftExists);
			structureNbt.putBoolean("leftHigh", this.upperLeftExists);
			structureNbt.putBoolean("rightLow", this.lowerRightExists);
			structureNbt.putBoolean("rightHigh", this.upperRightExists);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.lowerLeftExists = nbtCompound.getBoolean("leftLow");
			this.upperLeftExists = nbtCompound.getBoolean("leftHigh");
			this.lowerRightExists = nbtCompound.getBoolean("rightLow");
			this.upperRightExists = nbtCompound.getBoolean("rightHigh");
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = 3;
			int j = 5;
			Direction direction = this.method_11854();
			if (direction == Direction.WEST || direction == Direction.NORTH) {
				i = 8 - i;
				j = 8 - j;
			}

			this.fillForwardOpening((StrongholdPieces.StartPiece)start, pieces, random, 5, 1);
			if (this.lowerLeftExists) {
				this.fillNWOpening((StrongholdPieces.StartPiece)start, pieces, random, i, 1);
			}

			if (this.upperLeftExists) {
				this.fillNWOpening((StrongholdPieces.StartPiece)start, pieces, random, j, 7);
			}

			if (this.lowerRightExists) {
				this.fillSEOpening((StrongholdPieces.StartPiece)start, pieces, random, i, 1);
			}

			if (this.upperRightExists) {
				this.fillSEOpening((StrongholdPieces.StartPiece)start, pieces, random, j, 7);
			}
		}

		public static StrongholdPieces.FiveWayCrossing create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -3, 0, 10, 9, 11, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new StrongholdPieces.FiveWayCrossing(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 9, 8, 10, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 4, 3, 0);
			if (this.lowerLeftExists) {
				this.method_17653(iWorld, blockBox, 0, 3, 1, 0, 5, 3, field_19406, field_19406, false);
			}

			if (this.lowerRightExists) {
				this.method_17653(iWorld, blockBox, 9, 3, 1, 9, 5, 3, field_19406, field_19406, false);
			}

			if (this.upperLeftExists) {
				this.method_17653(iWorld, blockBox, 0, 5, 7, 0, 7, 9, field_19406, field_19406, false);
			}

			if (this.upperRightExists) {
				this.method_17653(iWorld, blockBox, 9, 5, 7, 9, 7, 9, field_19406, field_19406, false);
			}

			this.method_17653(iWorld, blockBox, 5, 1, 10, 7, 3, 10, field_19406, field_19406, false);
			this.method_17655(iWorld, blockBox, 1, 2, 1, 8, 2, 6, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 4, 1, 5, 4, 4, 9, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 8, 1, 5, 8, 4, 9, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 1, 4, 7, 3, 4, 9, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 1, 3, 5, 3, 3, 6, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17653(iWorld, blockBox, 1, 3, 4, 3, 3, 4, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 4, 6, 3, 4, 6, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
			this.method_17655(iWorld, blockBox, 5, 1, 7, 7, 1, 8, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17653(iWorld, blockBox, 5, 1, 9, 7, 1, 9, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 2, 7, 7, 2, 7, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 5, 7, 4, 5, 9, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 8, 5, 7, 8, 5, 9, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
			this.method_17653(
				iWorld,
				blockBox,
				5,
				5,
				7,
				7,
				5,
				9,
				Blocks.STONE_SLAB.getDefaultState().withProperty(SlabBlock.field_18486, SlabType.DOUBLE),
				Blocks.STONE_SLAB.getDefaultState().withProperty(SlabBlock.field_18486, SlabType.DOUBLE),
				false
			);
			this.method_56(iWorld, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.SOUTH), 6, 5, 6, blockBox);
			return true;
		}
	}

	public static class FlexibleCorridor extends StrongholdPieces.AbstractPiece {
		private int length;

		public FlexibleCorridor() {
		}

		public FlexibleCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.length = direction != Direction.NORTH && direction != Direction.SOUTH ? blockBox.getBlockCountX() : blockBox.getBlockCountZ();
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putInt("Steps", this.length);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.length = nbtCompound.getInt("Steps");
		}

		public static BlockBox create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation) {
			int i = 3;
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 4, orientation);
			StructurePiece structurePiece = StructurePiece.getOverlappingPiece(pieces, blockBox);
			if (structurePiece == null) {
				return null;
			} else {
				if (structurePiece.getBoundingBox().minY == blockBox.minY) {
					for (int j = 3; j >= 1; j--) {
						blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, j - 1, orientation);
						if (!structurePiece.getBoundingBox().intersects(blockBox)) {
							return BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, j, orientation);
						}
					}
				}

				return null;
			}
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			for (int i = 0; i < this.length; i++) {
				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 0, 0, i, blockBox);
				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 0, i, blockBox);
				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 0, i, blockBox);
				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 0, i, blockBox);
				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 4, 0, i, blockBox);

				for (int j = 1; j <= 3; j++) {
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 0, j, i, blockBox);
					this.method_56(iWorld, Blocks.CAVE_AIR.getDefaultState(), 1, j, i, blockBox);
					this.method_56(iWorld, Blocks.CAVE_AIR.getDefaultState(), 2, j, i, blockBox);
					this.method_56(iWorld, Blocks.CAVE_AIR.getDefaultState(), 3, j, i, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 4, j, i, blockBox);
				}

				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 0, 4, i, blockBox);
				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, i, blockBox);
				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, i, blockBox);
				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 4, i, blockBox);
				this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 4, 4, i, blockBox);
			}

			return true;
		}
	}

	static class InfestedStoneRandomizer extends StructurePiece.BlockRandomizer {
		private InfestedStoneRandomizer() {
		}

		@Override
		public void setBlock(Random random, int x, int y, int z, boolean placeBlock) {
			if (placeBlock) {
				float f = random.nextFloat();
				if (f < 0.2F) {
					this.block = Blocks.CRACKED_STONE_BRICKS.getDefaultState();
				} else if (f < 0.5F) {
					this.block = Blocks.MOSSY_STONE_BRICKS.getDefaultState();
				} else if (f < 0.55F) {
					this.block = Blocks.INFESTED_STONE_BRICKS.getDefaultState();
				} else {
					this.block = Blocks.STONE_BRICKS.getDefaultState();
				}
			} else {
				this.block = Blocks.CAVE_AIR.getDefaultState();
			}
		}
	}

	public static class LeftTurn extends StrongholdPieces.AbstractPiece {
		public LeftTurn() {
		}

		public LeftTurn(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			Direction direction = this.method_11854();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillSEOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
			} else {
				this.fillNWOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
			}
		}

		public static StrongholdPieces.LeftTurn create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 5, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new StrongholdPieces.LeftTurn(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.method_11854();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.method_17653(iWorld, blockBox, 4, 1, 1, 4, 3, 3, field_19406, field_19406, false);
			} else {
				this.method_17653(iWorld, blockBox, 0, 1, 1, 0, 3, 3, field_19406, field_19406, false);
			}

			return true;
		}
	}

	public static class Library extends StrongholdPieces.AbstractPiece {
		private boolean tall;

		public Library() {
		}

		public Library(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
			this.tall = blockBox.getBlockCountY() > 6;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Tall", this.tall);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.tall = nbtCompound.getBoolean("Tall");
		}

		public static StrongholdPieces.Library create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 14, 11, 15, orientation);
			if (!isInbounds(blockBox) || StructurePiece.getOverlappingPiece(pieces, blockBox) != null) {
				blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 14, 6, 15, orientation);
				if (!isInbounds(blockBox) || StructurePiece.getOverlappingPiece(pieces, blockBox) != null) {
					return null;
				}
			}

			return new StrongholdPieces.Library(chainLength, random, blockBox, orientation);
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			int i = 11;
			if (!this.tall) {
				i = 6;
			}

			this.method_17655(iWorld, blockBox, 0, 0, 0, 13, i - 1, 14, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 4, 1, 0);
			this.method_17656(iWorld, blockBox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getDefaultState(), Blocks.COBWEB.getDefaultState(), false, false);
			int j = 1;
			int k = 12;

			for (int l = 1; l <= 13; l++) {
				if ((l - 1) % 4 == 0) {
					this.method_17653(iWorld, blockBox, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
					this.method_17653(iWorld, blockBox, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
					this.method_56(iWorld, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.EAST), 2, 3, l, blockBox);
					this.method_56(iWorld, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.WEST), 11, 3, l, blockBox);
					if (this.tall) {
						this.method_17653(iWorld, blockBox, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
						this.method_17653(iWorld, blockBox, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
					}
				} else {
					this.method_17653(iWorld, blockBox, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					this.method_17653(iWorld, blockBox, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					if (this.tall) {
						this.method_17653(iWorld, blockBox, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
						this.method_17653(iWorld, blockBox, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					}
				}
			}

			for (int m = 3; m < 12; m += 2) {
				this.method_17653(iWorld, blockBox, 3, 1, m, 4, 3, m, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
				this.method_17653(iWorld, blockBox, 6, 1, m, 7, 3, m, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
				this.method_17653(iWorld, blockBox, 9, 1, m, 10, 3, m, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
			}

			if (this.tall) {
				this.method_17653(iWorld, blockBox, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.method_17653(iWorld, blockBox, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.method_17653(iWorld, blockBox, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.method_17653(iWorld, blockBox, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState(), false);
				this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 11, blockBox);
				this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 8, 5, 11, blockBox);
				this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 9, 5, 10, blockBox);
				BlockState blockState = Blocks.OAK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
				BlockState blockState2 = Blocks.OAK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
				this.method_17653(iWorld, blockBox, 3, 6, 3, 3, 6, 11, blockState2, blockState2, false);
				this.method_17653(iWorld, blockBox, 10, 6, 3, 10, 6, 9, blockState2, blockState2, false);
				this.method_17653(iWorld, blockBox, 4, 6, 2, 9, 6, 2, blockState, blockState, false);
				this.method_17653(iWorld, blockBox, 4, 6, 12, 7, 6, 12, blockState, blockState, false);
				this.method_56(
					iWorld,
					Blocks.OAK_FENCE.getDefaultState().withProperty(FenceBlock.field_18265, Boolean.valueOf(true)).withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
					3,
					6,
					2,
					blockBox
				);
				this.method_56(
					iWorld,
					Blocks.OAK_FENCE.getDefaultState().withProperty(FenceBlock.field_18267, Boolean.valueOf(true)).withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
					3,
					6,
					12,
					blockBox
				);
				this.method_56(
					iWorld,
					Blocks.OAK_FENCE.getDefaultState().withProperty(FenceBlock.field_18265, Boolean.valueOf(true)).withProperty(FenceBlock.field_18268, Boolean.valueOf(true)),
					10,
					6,
					2,
					blockBox
				);

				for (int n = 0; n <= 2; n++) {
					this.method_56(
						iWorld,
						Blocks.OAK_FENCE
							.getDefaultState()
							.withProperty(FenceBlock.field_18267, Boolean.valueOf(true))
							.withProperty(FenceBlock.field_18268, Boolean.valueOf(true)),
						8 + n,
						6,
						12 - n,
						blockBox
					);
					if (n != 2) {
						this.method_56(
							iWorld,
							Blocks.OAK_FENCE
								.getDefaultState()
								.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
								.withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
							8 + n,
							6,
							11 - n,
							blockBox
						);
					}
				}

				BlockState blockState3 = Blocks.LADDER.getDefaultState().withProperty(LadderBlock.FACING, Direction.SOUTH);
				this.method_56(iWorld, blockState3, 10, 1, 13, blockBox);
				this.method_56(iWorld, blockState3, 10, 2, 13, blockBox);
				this.method_56(iWorld, blockState3, 10, 3, 13, blockBox);
				this.method_56(iWorld, blockState3, 10, 4, 13, blockBox);
				this.method_56(iWorld, blockState3, 10, 5, 13, blockBox);
				this.method_56(iWorld, blockState3, 10, 6, 13, blockBox);
				this.method_56(iWorld, blockState3, 10, 7, 13, blockBox);
				int o = 7;
				int p = 7;
				BlockState blockState4 = Blocks.OAK_FENCE.getDefaultState().withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
				this.method_56(iWorld, blockState4, 6, 9, 7, blockBox);
				BlockState blockState5 = Blocks.OAK_FENCE.getDefaultState().withProperty(FenceBlock.field_18268, Boolean.valueOf(true));
				this.method_56(iWorld, blockState5, 7, 9, 7, blockBox);
				this.method_56(iWorld, blockState4, 6, 8, 7, blockBox);
				this.method_56(iWorld, blockState5, 7, 8, 7, blockBox);
				BlockState blockState6 = blockState2.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
				this.method_56(iWorld, blockState6, 6, 7, 7, blockBox);
				this.method_56(iWorld, blockState6, 7, 7, 7, blockBox);
				this.method_56(iWorld, blockState4, 5, 7, 7, blockBox);
				this.method_56(iWorld, blockState5, 8, 7, 7, blockBox);
				this.method_56(iWorld, blockState4.withProperty(FenceBlock.field_18265, Boolean.valueOf(true)), 6, 7, 6, blockBox);
				this.method_56(iWorld, blockState4.withProperty(FenceBlock.field_18267, Boolean.valueOf(true)), 6, 7, 8, blockBox);
				this.method_56(iWorld, blockState5.withProperty(FenceBlock.field_18265, Boolean.valueOf(true)), 7, 7, 6, blockBox);
				this.method_56(iWorld, blockState5.withProperty(FenceBlock.field_18267, Boolean.valueOf(true)), 7, 7, 8, blockBox);
				BlockState blockState7 = Blocks.TORCH.getDefaultState();
				this.method_56(iWorld, blockState7, 5, 8, 7, blockBox);
				this.method_56(iWorld, blockState7, 8, 8, 7, blockBox);
				this.method_56(iWorld, blockState7, 6, 8, 6, blockBox);
				this.method_56(iWorld, blockState7, 6, 8, 8, blockBox);
				this.method_56(iWorld, blockState7, 7, 8, 6, blockBox);
				this.method_56(iWorld, blockState7, 7, 8, 8, blockBox);
			}

			this.method_11852(iWorld, blockBox, random, 3, 3, 5, LootTables.STRONGHOLD_LIBRARY_CHEST);
			if (this.tall) {
				this.method_56(iWorld, field_19406, 12, 9, 1, blockBox);
				this.method_11852(iWorld, blockBox, random, 12, 8, 1, LootTables.STRONGHOLD_LIBRARY_CHEST);
			}

			return true;
		}
	}

	static class PieceData {
		public Class<? extends StrongholdPieces.AbstractPiece> pieceType;
		public final int weight;
		public int generatedCount;
		public int limit;

		public PieceData(Class<? extends StrongholdPieces.AbstractPiece> class_, int i, int j) {
			this.pieceType = class_;
			this.weight = i;
			this.limit = j;
		}

		public boolean canGenerate(int chainLength) {
			return this.limit == 0 || this.generatedCount < this.limit;
		}

		public boolean canGenerate() {
			return this.limit == 0 || this.generatedCount < this.limit;
		}
	}

	public static class PrisonHall extends StrongholdPieces.AbstractPiece {
		public PrisonHall() {
		}

		public PrisonHall(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
		}

		public static StrongholdPieces.PrisonHall create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 9, 5, 11, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new StrongholdPieces.PrisonHall(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 8, 4, 10, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 1, 1, 0);
			this.method_17653(iWorld, blockBox, 1, 1, 10, 3, 3, 10, field_19406, field_19406, false);
			this.method_17655(iWorld, blockBox, 4, 1, 1, 4, 3, 1, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 4, 1, 3, 4, 3, 3, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 4, 1, 7, 4, 3, 7, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_17655(iWorld, blockBox, 4, 1, 9, 4, 3, 9, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);

			for (int i = 1; i <= 3; i++) {
				this.method_56(
					iWorld,
					Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18265, Boolean.valueOf(true)).withProperty(PaneBlock.field_18267, Boolean.valueOf(true)),
					4,
					i,
					4,
					blockBox
				);
				this.method_56(
					iWorld,
					Blocks.IRON_BARS
						.getDefaultState()
						.withProperty(PaneBlock.field_18265, Boolean.valueOf(true))
						.withProperty(PaneBlock.field_18267, Boolean.valueOf(true))
						.withProperty(PaneBlock.field_18266, Boolean.valueOf(true)),
					4,
					i,
					5,
					blockBox
				);
				this.method_56(
					iWorld,
					Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18265, Boolean.valueOf(true)).withProperty(PaneBlock.field_18267, Boolean.valueOf(true)),
					4,
					i,
					6,
					blockBox
				);
				this.method_56(
					iWorld,
					Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18268, Boolean.valueOf(true)).withProperty(PaneBlock.field_18266, Boolean.valueOf(true)),
					5,
					i,
					5,
					blockBox
				);
				this.method_56(
					iWorld,
					Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18268, Boolean.valueOf(true)).withProperty(PaneBlock.field_18266, Boolean.valueOf(true)),
					6,
					i,
					5,
					blockBox
				);
				this.method_56(
					iWorld,
					Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18268, Boolean.valueOf(true)).withProperty(PaneBlock.field_18266, Boolean.valueOf(true)),
					7,
					i,
					5,
					blockBox
				);
			}

			this.method_56(
				iWorld,
				Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18265, Boolean.valueOf(true)).withProperty(PaneBlock.field_18267, Boolean.valueOf(true)),
				4,
				3,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.IRON_BARS.getDefaultState().withProperty(PaneBlock.field_18265, Boolean.valueOf(true)).withProperty(PaneBlock.field_18267, Boolean.valueOf(true)),
				4,
				3,
				8,
				blockBox
			);
			BlockState blockState = Blocks.IRON_DOOR.getDefaultState().withProperty(DoorBlock.FACING, Direction.WEST);
			BlockState blockState2 = Blocks.IRON_DOOR
				.getDefaultState()
				.withProperty(DoorBlock.FACING, Direction.WEST)
				.withProperty(DoorBlock.field_18296, DoubleBlockHalf.UPPER);
			this.method_56(iWorld, blockState, 4, 1, 2, blockBox);
			this.method_56(iWorld, blockState2, 4, 2, 2, blockBox);
			this.method_56(iWorld, blockState, 4, 1, 8, blockBox);
			this.method_56(iWorld, blockState2, 4, 2, 8, blockBox);
			return true;
		}
	}

	public static class RightTurn extends StrongholdPieces.LeftTurn {
		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			Direction direction = this.method_11854();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.fillNWOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
			} else {
				this.fillSEOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
			}
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 1, 1, 0);
			Direction direction = this.method_11854();
			if (direction != Direction.NORTH && direction != Direction.EAST) {
				this.method_17653(iWorld, blockBox, 0, 1, 1, 0, 3, 3, field_19406, field_19406, false);
			} else {
				this.method_17653(iWorld, blockBox, 4, 1, 1, 4, 3, 3, field_19406, field_19406, false);
			}

			return true;
		}
	}

	public static class SpiralStaircase extends StrongholdPieces.AbstractPiece {
		private boolean isStructureStart;

		public SpiralStaircase() {
		}

		public SpiralStaircase(int i, Random random, int j, int k) {
			super(i);
			this.isStructureStart = true;
			this.method_11853(Direction.DirectionType.HORIZONTAL.getRandomDirection(random));
			this.entryDoor = StrongholdPieces.AbstractPiece.EntranceType.OPENING;
			if (this.method_11854().getAxis() == Direction.Axis.Z) {
				this.boundingBox = new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			} else {
				this.boundingBox = new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			}
		}

		public SpiralStaircase(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.isStructureStart = false;
			this.method_11853(direction);
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Source", this.isStructureStart);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.isStructureStart = nbtCompound.getBoolean("Source");
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			if (this.isStructureStart) {
				StrongholdPieces.ACTIVE_PIECE_TYPE = StrongholdPieces.FiveWayCrossing.class;
			}

			this.fillForwardOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
		}

		public static StrongholdPieces.SpiralStaircase create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 11, 5, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new StrongholdPieces.SpiralStaircase(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 4, 10, 4, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 1, 7, 0);
			this.method_41(iWorld, random, blockBox, StrongholdPieces.AbstractPiece.EntranceType.OPENING, 1, 1, 4);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 6, 1, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 1, blockBox);
			this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 1, 6, 1, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 2, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, 3, blockBox);
			this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 1, 5, 3, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, 3, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 3, blockBox);
			this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 3, 4, 3, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 2, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 2, 1, blockBox);
			this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 3, 3, 1, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 2, 1, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 1, blockBox);
			this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 1, 2, 1, blockBox);
			this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 2, blockBox);
			this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 1, 1, 3, blockBox);
			return true;
		}
	}

	public static class SquareRoom extends StrongholdPieces.AbstractPiece {
		protected int roomType;

		public SquareRoom() {
		}

		public SquareRoom(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
			this.roomType = random.nextInt(5);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putInt("Type", this.roomType);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.roomType = nbtCompound.getInt("Type");
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((StrongholdPieces.StartPiece)start, pieces, random, 4, 1);
			this.fillNWOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 4);
			this.fillSEOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 4);
		}

		public static StrongholdPieces.SquareRoom create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -4, -1, 0, 11, 7, 11, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new StrongholdPieces.SquareRoom(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 10, 6, 10, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 4, 1, 0);
			this.method_17653(iWorld, blockBox, 4, 1, 10, 6, 3, 10, field_19406, field_19406, false);
			this.method_17653(iWorld, blockBox, 0, 1, 4, 0, 3, 6, field_19406, field_19406, false);
			this.method_17653(iWorld, blockBox, 10, 1, 4, 10, 3, 6, field_19406, field_19406, false);
			switch (this.roomType) {
				case 0:
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, blockBox);
					this.method_56(iWorld, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.WEST), 4, 3, 5, blockBox);
					this.method_56(iWorld, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.EAST), 6, 3, 5, blockBox);
					this.method_56(iWorld, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.SOUTH), 5, 3, 4, blockBox);
					this.method_56(iWorld, Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.NORTH), 5, 3, 6, blockBox);
					this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 4, 1, 4, blockBox);
					this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 4, 1, 5, blockBox);
					this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 4, 1, 6, blockBox);
					this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 6, 1, 4, blockBox);
					this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 6, 1, 5, blockBox);
					this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 6, 1, 6, blockBox);
					this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 5, 1, 4, blockBox);
					this.method_56(iWorld, Blocks.STONE_SLAB.getDefaultState(), 5, 1, 6, blockBox);
					break;
				case 1:
					for (int i = 0; i < 5; i++) {
						this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 1, 3 + i, blockBox);
						this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 7, 1, 3 + i, blockBox);
						this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 3, blockBox);
						this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 7, blockBox);
					}

					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, blockBox);
					this.method_56(iWorld, Blocks.WATER.getDefaultState(), 5, 4, 5, blockBox);
					break;
				case 2:
					for (int j = 1; j <= 9; j++) {
						this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 1, 3, j, blockBox);
						this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 9, 3, j, blockBox);
					}

					for (int k = 1; k <= 9; k++) {
						this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), k, 3, 1, blockBox);
						this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), k, 3, 9, blockBox);
					}

					this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, blockBox);
					this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, blockBox);
					this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, blockBox);
					this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, blockBox);
					this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, blockBox);
					this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, blockBox);
					this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, blockBox);
					this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, blockBox);

					for (int l = 1; l <= 3; l++) {
						this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 4, l, 4, blockBox);
						this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 6, l, 4, blockBox);
						this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 4, l, 6, blockBox);
						this.method_56(iWorld, Blocks.COBBLESTONE.getDefaultState(), 6, l, 6, blockBox);
					}

					this.method_56(iWorld, Blocks.TORCH.getDefaultState(), 5, 3, 5, blockBox);

					for (int m = 2; m <= 8; m++) {
						this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 2, 3, m, blockBox);
						this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 3, 3, m, blockBox);
						if (m <= 3 || m >= 7) {
							this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 4, 3, m, blockBox);
							this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 5, 3, m, blockBox);
							this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 6, 3, m, blockBox);
						}

						this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 7, 3, m, blockBox);
						this.method_56(iWorld, Blocks.OAK_PLANKS.getDefaultState(), 8, 3, m, blockBox);
					}

					BlockState blockState = Blocks.LADDER.getDefaultState().withProperty(LadderBlock.FACING, Direction.WEST);
					this.method_56(iWorld, blockState, 9, 1, 3, blockBox);
					this.method_56(iWorld, blockState, 9, 2, 3, blockBox);
					this.method_56(iWorld, blockState, 9, 3, 3, blockBox);
					this.method_11852(iWorld, blockBox, random, 3, 4, 8, LootTables.STRONGHOLD_CROSSING_CHEST);
			}

			return true;
		}
	}

	public static class StartPiece extends StrongholdPieces.SpiralStaircase {
		public StrongholdPieces.PieceData lastPiece;
		public StrongholdPieces.EndPortalRoom portalRoom;
		public List<StructurePiece> pieces = Lists.newArrayList();

		public StartPiece() {
		}

		public StartPiece(int i, Random random, int j, int k) {
			super(0, random, j, k);
		}
	}

	public static class StraightCorridor extends StrongholdPieces.AbstractPiece {
		private boolean leftExitExists;
		private boolean rightExitExists;

		public StraightCorridor() {
		}

		public StraightCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
			this.leftExitExists = random.nextInt(2) == 0;
			this.rightExitExists = random.nextInt(2) == 0;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Left", this.leftExitExists);
			structureNbt.putBoolean("Right", this.rightExitExists);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.leftExitExists = nbtCompound.getBoolean("Left");
			this.rightExitExists = nbtCompound.getBoolean("Right");
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
			if (this.leftExitExists) {
				this.fillNWOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 2);
			}

			if (this.rightExitExists) {
				this.fillSEOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 2);
			}
		}

		public static StrongholdPieces.StraightCorridor create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -1, 0, 5, 5, 7, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new StrongholdPieces.StraightCorridor(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 1, 1, 0);
			this.method_41(iWorld, random, blockBox, StrongholdPieces.AbstractPiece.EntranceType.OPENING, 1, 1, 6);
			BlockState blockState = Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.EAST);
			BlockState blockState2 = Blocks.WALL_TORCH.getDefaultState().withProperty(class_3735.field_18582, Direction.WEST);
			this.method_65(iWorld, blockBox, random, 0.1F, 1, 2, 1, blockState);
			this.method_65(iWorld, blockBox, random, 0.1F, 3, 2, 1, blockState2);
			this.method_65(iWorld, blockBox, random, 0.1F, 1, 2, 5, blockState);
			this.method_65(iWorld, blockBox, random, 0.1F, 3, 2, 5, blockState2);
			if (this.leftExitExists) {
				this.method_17653(iWorld, blockBox, 0, 1, 2, 0, 3, 4, field_19406, field_19406, false);
			}

			if (this.rightExitExists) {
				this.method_17653(iWorld, blockBox, 4, 1, 2, 4, 3, 4, field_19406, field_19406, false);
			}

			return true;
		}
	}

	public static class StraightStairs extends StrongholdPieces.AbstractPiece {
		public StraightStairs() {
		}

		public StraightStairs(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
		}

		public static StrongholdPieces.StraightStairs create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 11, 8, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new StrongholdPieces.StraightStairs(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17655(iWorld, blockBox, 0, 0, 0, 4, 10, 7, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.method_41(iWorld, random, blockBox, this.entryDoor, 1, 7, 0);
			this.method_41(iWorld, random, blockBox, StrongholdPieces.AbstractPiece.EntranceType.OPENING, 1, 1, 7);
			BlockState blockState = Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.SOUTH);

			for (int i = 0; i < 6; i++) {
				this.method_56(iWorld, blockState, 1, 6 - i, 1 + i, blockBox);
				this.method_56(iWorld, blockState, 2, 6 - i, 1 + i, blockBox);
				this.method_56(iWorld, blockState, 3, 6 - i, 1 + i, blockBox);
				if (i < 5) {
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 1, 5 - i, 1 + i, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 2, 5 - i, 1 + i, blockBox);
					this.method_56(iWorld, Blocks.STONE_BRICKS.getDefaultState(), 3, 5 - i, 1 + i, blockBox);
				}
			}

			return true;
		}
	}
}
