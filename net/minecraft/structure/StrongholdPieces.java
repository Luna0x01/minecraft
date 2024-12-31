package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.StoneBrickBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
	static int TOTAL_WEIGHT;
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
		Direction orientation,
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
		StrongholdPieces.StartPiece start, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
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
		protected void deserialize(NbtCompound structureNbt) {
			this.entryDoor = StrongholdPieces.AbstractPiece.EntranceType.valueOf(structureNbt.getString("EntryDoor"));
		}

		protected void generateEntrance(World world, Random rand, BlockBox box, StrongholdPieces.AbstractPiece.EntranceType type, int x, int y, int z) {
			switch (type) {
				case OPENING:
				default:
					this.fillWithOutline(world, box, x, y, z, x + 3 - 1, y + 3 - 1, z, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
					break;
				case WOOD_DOOR:
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x, y, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 1, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 2, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x + 1, y + 2, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 2, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 1, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y, z, box);
					this.setBlockState(world, Blocks.OAK_DOOR.getDefaultState(), x + 1, y, z, box);
					this.setBlockState(world, Blocks.OAK_DOOR.stateFromData(8), x + 1, y + 1, z, box);
					break;
				case GRATES:
					this.setBlockState(world, Blocks.AIR.getDefaultState(), x + 1, y, z, box);
					this.setBlockState(world, Blocks.AIR.getDefaultState(), x + 1, y + 1, z, box);
					this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), x, y, z, box);
					this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), x, y + 1, z, box);
					this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), x, y + 2, z, box);
					this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), x + 1, y + 2, z, box);
					this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), x + 2, y + 2, z, box);
					this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), x + 2, y + 1, z, box);
					this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), x + 2, y, z, box);
					break;
				case IRON_DOOR:
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x, y, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 1, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x, y + 2, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x + 1, y + 2, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 2, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y + 1, z, box);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), x + 2, y, z, box);
					this.setBlockState(world, Blocks.IRON_DOOR.getDefaultState(), x + 1, y, z, box);
					this.setBlockState(world, Blocks.IRON_DOOR.stateFromData(8), x + 1, y + 1, z, box);
					this.setBlockState(world, Blocks.STONE_BUTTON.stateFromData(this.getData(Blocks.STONE_BUTTON, 4)), x + 2, y + 1, z + 1, box);
					this.setBlockState(world, Blocks.STONE_BUTTON.stateFromData(this.getData(Blocks.STONE_BUTTON, 3)), x + 2, y + 1, z - 1, box);
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

		protected StructurePiece fillForwardOpening(
			StrongholdPieces.StartPiece start, List<StructurePiece> pieces, Random random, int leftRightOffset, int heightOffset
		) {
			if (this.facing != null) {
				switch (this.facing) {
					case NORTH:
						return StrongholdPieces.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ - 1,
							this.facing,
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
							this.facing,
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
							this.facing,
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
							this.facing,
							this.getChainLength()
						);
				}
			}

			return null;
		}

		protected StructurePiece fillNWOpening(StrongholdPieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset) {
			if (this.facing != null) {
				switch (this.facing) {
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

		protected StructurePiece fillSEOpening(StrongholdPieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset) {
			if (this.facing != null) {
				switch (this.facing) {
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
		private static final List<WeightedRandomChestContent> POSSIBLE_CHEST_CONTENT = Lists.newArrayList(
			new WeightedRandomChestContent[]{
				new WeightedRandomChestContent(Items.ENDER_PEARL, 0, 1, 1, 10),
				new WeightedRandomChestContent(Items.DIAMOND, 0, 1, 3, 3),
				new WeightedRandomChestContent(Items.IRON_INGOT, 0, 1, 5, 10),
				new WeightedRandomChestContent(Items.GOLD_INGOT, 0, 1, 3, 5),
				new WeightedRandomChestContent(Items.REDSTONE, 0, 4, 9, 5),
				new WeightedRandomChestContent(Items.BREAD, 0, 1, 3, 15),
				new WeightedRandomChestContent(Items.APPLE, 0, 1, 3, 15),
				new WeightedRandomChestContent(Items.IRON_PICKAXE, 0, 1, 1, 5),
				new WeightedRandomChestContent(Items.IRON_SWORD, 0, 1, 1, 5),
				new WeightedRandomChestContent(Items.IRON_CHESTPLATE, 0, 1, 1, 5),
				new WeightedRandomChestContent(Items.IRON_HELMET, 0, 1, 1, 5),
				new WeightedRandomChestContent(Items.IRON_LEGGINGS, 0, 1, 1, 5),
				new WeightedRandomChestContent(Items.IRON_BOOTS, 0, 1, 1, 5),
				new WeightedRandomChestContent(Items.GOLDEN_APPLE, 0, 1, 1, 1),
				new WeightedRandomChestContent(Items.SADDLE, 0, 1, 1, 1),
				new WeightedRandomChestContent(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1),
				new WeightedRandomChestContent(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1),
				new WeightedRandomChestContent(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
			}
		);
		private boolean chestGenerated;

		public ChestCorridor() {
		}

		public ChestCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Chest", this.chestGenerated);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.chestGenerated = structureNbt.getBoolean("Chest");
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
				this.generateEntrance(world, random, boundingBox, StrongholdPieces.AbstractPiece.EntranceType.OPENING, 1, 1, 6);
				this.fillWithOutline(world, boundingBox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.getDefaultState(), Blocks.STONE_BRICKS.getDefaultState(), false);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE_BRICK.getId()), 3, 1, 1, boundingBox);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE_BRICK.getId()), 3, 1, 5, boundingBox);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE_BRICK.getId()), 3, 2, 2, boundingBox);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE_BRICK.getId()), 3, 2, 4, boundingBox);

				for (int i = 2; i <= 4; i++) {
					this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE_BRICK.getId()), 2, 1, i, boundingBox);
				}

				if (!this.chestGenerated && boundingBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
					this.chestGenerated = true;
					this.placeChest(
						world,
						boundingBox,
						random,
						3,
						2,
						3,
						WeightedRandomChestContent.combineLootTables(POSSIBLE_CHEST_CONTENT, Items.ENCHANTED_BOOK.getLootTable(random)),
						2 + random.nextInt(2)
					);
				}

				return true;
			}
		}
	}

	public static class EndPortalRoom extends StrongholdPieces.AbstractPiece {
		private boolean spawnerPlaced;

		public EndPortalRoom() {
		}

		public EndPortalRoom(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
			this.boundingBox = blockBox;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Mob", this.spawnerPlaced);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.spawnerPlaced = structureNbt.getBoolean("Mob");
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			this.fillRandomized(world, boundingBox, 0, 0, 0, 10, 7, 15, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.generateEntrance(world, random, boundingBox, StrongholdPieces.AbstractPiece.EntranceType.GRATES, 4, 1, 0);
			int i = 6;
			this.fillRandomized(world, boundingBox, 1, i, 1, 1, i, 14, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.fillRandomized(world, boundingBox, 9, i, 1, 9, i, 14, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.fillRandomized(world, boundingBox, 2, i, 1, 8, i, 2, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.fillRandomized(world, boundingBox, 2, i, 14, 8, i, 14, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.fillRandomized(world, boundingBox, 1, 1, 1, 2, 1, 4, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.fillRandomized(world, boundingBox, 8, 1, 1, 9, 1, 4, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.fillWithOutline(world, boundingBox, 1, 1, 1, 1, 1, 3, Blocks.FLOWING_LAVA.getDefaultState(), Blocks.FLOWING_LAVA.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 1, 1, 9, 1, 3, Blocks.FLOWING_LAVA.getDefaultState(), Blocks.FLOWING_LAVA.getDefaultState(), false);
			this.fillRandomized(world, boundingBox, 3, 1, 8, 7, 1, 12, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.fillWithOutline(world, boundingBox, 4, 1, 9, 6, 1, 11, Blocks.FLOWING_LAVA.getDefaultState(), Blocks.FLOWING_LAVA.getDefaultState(), false);

			for (int j = 3; j < 14; j += 2) {
				this.fillWithOutline(world, boundingBox, 0, 3, j, 0, 4, j, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 10, 3, j, 10, 4, j, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
			}

			for (int k = 2; k < 9; k += 2) {
				this.fillWithOutline(world, boundingBox, k, 3, 15, k, 4, 15, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
			}

			int l = this.getData(Blocks.STONE_BRICK_STAIRS, 3);
			this.fillRandomized(world, boundingBox, 4, 1, 5, 6, 1, 7, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.fillRandomized(world, boundingBox, 4, 2, 6, 6, 2, 7, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
			this.fillRandomized(world, boundingBox, 4, 3, 7, 6, 3, 7, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);

			for (int m = 4; m <= 6; m++) {
				this.setBlockState(world, Blocks.STONE_BRICK_STAIRS.stateFromData(l), m, 1, 4, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICK_STAIRS.stateFromData(l), m, 2, 5, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICK_STAIRS.stateFromData(l), m, 3, 6, boundingBox);
			}

			int n = Direction.NORTH.getHorizontal();
			int o = Direction.SOUTH.getHorizontal();
			int p = Direction.EAST.getHorizontal();
			int q = Direction.WEST.getHorizontal();
			if (this.facing != null) {
				switch (this.facing) {
					case SOUTH:
						n = Direction.SOUTH.getHorizontal();
						o = Direction.NORTH.getHorizontal();
						break;
					case WEST:
						n = Direction.WEST.getHorizontal();
						o = Direction.EAST.getHorizontal();
						p = Direction.SOUTH.getHorizontal();
						q = Direction.NORTH.getHorizontal();
						break;
					case EAST:
						n = Direction.EAST.getHorizontal();
						o = Direction.WEST.getHorizontal();
						p = Direction.SOUTH.getHorizontal();
						q = Direction.NORTH.getHorizontal();
				}
			}

			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(n).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 4, 3, 8, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(n).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 5, 3, 8, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(n).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 6, 3, 8, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(o).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 4, 3, 12, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(o).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 5, 3, 12, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(o).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 6, 3, 12, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(p).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 3, 3, 9, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(p).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 3, 3, 10, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(p).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 3, 3, 11, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(q).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 7, 3, 9, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(q).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 7, 3, 10, boundingBox);
			this.setBlockState(world, Blocks.END_PORTAL_FRAME.stateFromData(q).with(EndPortalFrameBlock.EYE, random.nextFloat() > 0.9F), 7, 3, 11, boundingBox);
			if (!this.spawnerPlaced) {
				i = this.applyYTransform(3);
				BlockPos blockPos = new BlockPos(this.applyXTransform(5, 6), i, this.applyZTransform(5, 6));
				if (boundingBox.contains(blockPos)) {
					this.spawnerPlaced = true;
					world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
					BlockEntity blockEntity = world.getBlockEntity(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId("Silverfish");
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
			this.facing = direction;
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
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.lowerLeftExists = structureNbt.getBoolean("leftLow");
			this.upperLeftExists = structureNbt.getBoolean("leftHigh");
			this.lowerRightExists = structureNbt.getBoolean("rightLow");
			this.upperRightExists = structureNbt.getBoolean("rightHigh");
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = 3;
			int j = 5;
			if (this.facing == Direction.WEST || this.facing == Direction.NORTH) {
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, 0, 0, 9, 8, 10, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 4, 3, 0);
				if (this.lowerLeftExists) {
					this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 5, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}

				if (this.lowerRightExists) {
					this.fillWithOutline(world, boundingBox, 9, 3, 1, 9, 5, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}

				if (this.upperLeftExists) {
					this.fillWithOutline(world, boundingBox, 0, 5, 7, 0, 7, 9, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}

				if (this.upperRightExists) {
					this.fillWithOutline(world, boundingBox, 9, 5, 7, 9, 7, 9, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}

				this.fillWithOutline(world, boundingBox, 5, 1, 10, 7, 3, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				this.fillRandomized(world, boundingBox, 1, 2, 1, 8, 2, 6, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillRandomized(world, boundingBox, 4, 1, 5, 4, 4, 9, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillRandomized(world, boundingBox, 8, 1, 5, 8, 4, 9, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillRandomized(world, boundingBox, 1, 4, 7, 3, 4, 9, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillRandomized(world, boundingBox, 1, 3, 5, 3, 3, 6, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillWithOutline(world, boundingBox, 1, 3, 4, 3, 3, 4, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 1, 4, 6, 3, 4, 6, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
				this.fillRandomized(world, boundingBox, 5, 1, 7, 7, 1, 8, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillWithOutline(world, boundingBox, 5, 1, 9, 7, 1, 9, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 5, 2, 7, 7, 2, 7, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 4, 5, 7, 4, 5, 9, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 8, 5, 7, 8, 5, 9, Blocks.STONE_SLAB.getDefaultState(), Blocks.STONE_SLAB.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 5, 5, 7, 7, 5, 9, Blocks.DOUBLE_STONE_SLAB.getDefaultState(), Blocks.DOUBLE_STONE_SLAB.getDefaultState(), false);
				this.setBlockState(world, Blocks.TORCH.getDefaultState(), 6, 5, 6, boundingBox);
				return true;
			}
		}
	}

	public static class FlexibleCorridor extends StrongholdPieces.AbstractPiece {
		private int length;

		public FlexibleCorridor() {
		}

		public FlexibleCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
			this.boundingBox = blockBox;
			this.length = direction != Direction.NORTH && direction != Direction.SOUTH ? blockBox.getBlockCountX() : blockBox.getBlockCountZ();
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putInt("Steps", this.length);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.length = structureNbt.getInt("Steps");
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				for (int i = 0; i < this.length; i++) {
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 0, 0, i, boundingBox);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 0, i, boundingBox);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 0, i, boundingBox);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 0, i, boundingBox);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 4, 0, i, boundingBox);

					for (int j = 1; j <= 3; j++) {
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 0, j, i, boundingBox);
						this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, j, i, boundingBox);
						this.setBlockState(world, Blocks.AIR.getDefaultState(), 2, j, i, boundingBox);
						this.setBlockState(world, Blocks.AIR.getDefaultState(), 3, j, i, boundingBox);
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 4, j, i, boundingBox);
					}

					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 0, 4, i, boundingBox);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, i, boundingBox);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, i, boundingBox);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 4, i, boundingBox);
					this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 4, 4, i, boundingBox);
				}

				return true;
			}
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
					this.block = Blocks.STONE_BRICKS.stateFromData(StoneBrickBlock.CRACKED_ID);
				} else if (f < 0.5F) {
					this.block = Blocks.STONE_BRICKS.stateFromData(StoneBrickBlock.MOSSY_ID);
				} else if (f < 0.55F) {
					this.block = Blocks.MONSTER_EGG.stateFromData(InfestedBlock.Variants.STONE_BRICK.getId());
				} else {
					this.block = Blocks.STONE_BRICKS.getDefaultState();
				}
			} else {
				this.block = Blocks.AIR.getDefaultState();
			}
		}
	}

	public static class LeftTurn extends StrongholdPieces.AbstractPiece {
		public LeftTurn() {
		}

		public LeftTurn(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			if (this.facing != Direction.NORTH && this.facing != Direction.EAST) {
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
				if (this.facing != Direction.NORTH && this.facing != Direction.EAST) {
					this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				} else {
					this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}

				return true;
			}
		}
	}

	public static class Library extends StrongholdPieces.AbstractPiece {
		private static final List<WeightedRandomChestContent> POSSIBLE_CHEST_CONTENT = Lists.newArrayList(
			new WeightedRandomChestContent[]{
				new WeightedRandomChestContent(Items.BOOK, 0, 1, 3, 20),
				new WeightedRandomChestContent(Items.PAPER, 0, 2, 7, 20),
				new WeightedRandomChestContent(Items.MAP, 0, 1, 1, 1),
				new WeightedRandomChestContent(Items.COMPASS, 0, 1, 1, 1)
			}
		);
		private boolean tall;

		public Library() {
		}

		public Library(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
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
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.tall = structureNbt.getBoolean("Tall");
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				int i = 11;
				if (!this.tall) {
					i = 6;
				}

				this.fillRandomized(world, boundingBox, 0, 0, 0, 13, i - 1, 14, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 4, 1, 0);
				this.fillWithOutlineUnderSeaLevel(
					world, boundingBox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.getDefaultState(), Blocks.COBWEB.getDefaultState(), false
				);
				int j = 1;
				int k = 12;

				for (int l = 1; l <= 13; l++) {
					if ((l - 1) % 4 == 0) {
						this.fillWithOutline(world, boundingBox, 1, 1, l, 1, 4, l, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
						this.fillWithOutline(world, boundingBox, 12, 1, l, 12, 4, l, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
						this.setBlockState(world, Blocks.TORCH.getDefaultState(), 2, 3, l, boundingBox);
						this.setBlockState(world, Blocks.TORCH.getDefaultState(), 11, 3, l, boundingBox);
						if (this.tall) {
							this.fillWithOutline(world, boundingBox, 1, 6, l, 1, 9, l, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
							this.fillWithOutline(world, boundingBox, 12, 6, l, 12, 9, l, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
						}
					} else {
						this.fillWithOutline(world, boundingBox, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
						this.fillWithOutline(world, boundingBox, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
						if (this.tall) {
							this.fillWithOutline(world, boundingBox, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
							this.fillWithOutline(world, boundingBox, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
						}
					}
				}

				for (int m = 3; m < 12; m += 2) {
					this.fillWithOutline(world, boundingBox, 3, 1, m, 4, 3, m, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					this.fillWithOutline(world, boundingBox, 6, 1, m, 7, 3, m, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
					this.fillWithOutline(world, boundingBox, 9, 1, m, 10, 3, m, Blocks.BOOKSHELF.getDefaultState(), Blocks.BOOKSHELF.getDefaultState(), false);
				}

				if (this.tall) {
					this.fillWithOutline(world, boundingBox, 1, 5, 1, 3, 5, 13, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
					this.fillWithOutline(world, boundingBox, 10, 5, 1, 12, 5, 13, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
					this.fillWithOutline(world, boundingBox, 4, 5, 1, 9, 5, 2, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
					this.fillWithOutline(world, boundingBox, 4, 5, 12, 9, 5, 13, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
					this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 9, 5, 11, boundingBox);
					this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 8, 5, 11, boundingBox);
					this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 9, 5, 10, boundingBox);
					this.fillWithOutline(world, boundingBox, 3, 6, 2, 3, 6, 12, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
					this.fillWithOutline(world, boundingBox, 10, 6, 2, 10, 6, 10, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
					this.fillWithOutline(world, boundingBox, 4, 6, 2, 9, 6, 2, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
					this.fillWithOutline(world, boundingBox, 4, 6, 12, 8, 6, 12, Blocks.OAK_FENCE.getDefaultState(), Blocks.OAK_FENCE.getDefaultState(), false);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 9, 6, 11, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 8, 6, 11, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 9, 6, 10, boundingBox);
					int n = this.getData(Blocks.LADDER, 3);
					this.setBlockState(world, Blocks.LADDER.stateFromData(n), 10, 1, 13, boundingBox);
					this.setBlockState(world, Blocks.LADDER.stateFromData(n), 10, 2, 13, boundingBox);
					this.setBlockState(world, Blocks.LADDER.stateFromData(n), 10, 3, 13, boundingBox);
					this.setBlockState(world, Blocks.LADDER.stateFromData(n), 10, 4, 13, boundingBox);
					this.setBlockState(world, Blocks.LADDER.stateFromData(n), 10, 5, 13, boundingBox);
					this.setBlockState(world, Blocks.LADDER.stateFromData(n), 10, 6, 13, boundingBox);
					this.setBlockState(world, Blocks.LADDER.stateFromData(n), 10, 7, 13, boundingBox);
					int o = 7;
					int p = 7;
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o - 1, 9, p, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o, 9, p, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o - 1, 8, p, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o, 8, p, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o - 1, 7, p, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o, 7, p, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o - 2, 7, p, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o + 1, 7, p, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o - 1, 7, p - 1, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o - 1, 7, p + 1, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o, 7, p - 1, boundingBox);
					this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), o, 7, p + 1, boundingBox);
					this.setBlockState(world, Blocks.TORCH.getDefaultState(), o - 2, 8, p, boundingBox);
					this.setBlockState(world, Blocks.TORCH.getDefaultState(), o + 1, 8, p, boundingBox);
					this.setBlockState(world, Blocks.TORCH.getDefaultState(), o - 1, 8, p - 1, boundingBox);
					this.setBlockState(world, Blocks.TORCH.getDefaultState(), o - 1, 8, p + 1, boundingBox);
					this.setBlockState(world, Blocks.TORCH.getDefaultState(), o, 8, p - 1, boundingBox);
					this.setBlockState(world, Blocks.TORCH.getDefaultState(), o, 8, p + 1, boundingBox);
				}

				this.placeChest(
					world,
					boundingBox,
					random,
					3,
					3,
					5,
					WeightedRandomChestContent.combineLootTables(POSSIBLE_CHEST_CONTENT, Items.ENCHANTED_BOOK.getLootTable(random, 1, 5, 2)),
					1 + random.nextInt(4)
				);
				if (this.tall) {
					this.setBlockState(world, Blocks.AIR.getDefaultState(), 12, 9, 1, boundingBox);
					this.placeChest(
						world,
						boundingBox,
						random,
						12,
						8,
						1,
						WeightedRandomChestContent.combineLootTables(POSSIBLE_CHEST_CONTENT, Items.ENCHANTED_BOOK.getLootTable(random, 1, 5, 2)),
						1 + random.nextInt(4)
					);
				}

				return true;
			}
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
			this.facing = direction;
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, 0, 0, 8, 4, 10, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
				this.fillWithOutline(world, boundingBox, 1, 1, 10, 3, 3, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				this.fillRandomized(world, boundingBox, 4, 1, 1, 4, 3, 1, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillRandomized(world, boundingBox, 4, 1, 3, 4, 3, 3, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillRandomized(world, boundingBox, 4, 1, 7, 4, 3, 7, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillRandomized(world, boundingBox, 4, 1, 9, 4, 3, 9, false, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.fillWithOutline(world, boundingBox, 4, 1, 4, 4, 3, 6, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 5, 1, 5, 7, 3, 5, Blocks.IRON_BARS.getDefaultState(), Blocks.IRON_BARS.getDefaultState(), false);
				this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 4, 3, 2, boundingBox);
				this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 4, 3, 8, boundingBox);
				this.setBlockState(world, Blocks.IRON_DOOR.stateFromData(this.getData(Blocks.IRON_DOOR, 3)), 4, 1, 2, boundingBox);
				this.setBlockState(world, Blocks.IRON_DOOR.stateFromData(this.getData(Blocks.IRON_DOOR, 3) + 8), 4, 2, 2, boundingBox);
				this.setBlockState(world, Blocks.IRON_DOOR.stateFromData(this.getData(Blocks.IRON_DOOR, 3)), 4, 1, 8, boundingBox);
				this.setBlockState(world, Blocks.IRON_DOOR.stateFromData(this.getData(Blocks.IRON_DOOR, 3) + 8), 4, 2, 8, boundingBox);
				return true;
			}
		}
	}

	public static class RightTurn extends StrongholdPieces.LeftTurn {
		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			if (this.facing != Direction.NORTH && this.facing != Direction.EAST) {
				this.fillNWOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
			} else {
				this.fillSEOpening((StrongholdPieces.StartPiece)start, pieces, random, 1, 1);
			}
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
				if (this.facing != Direction.NORTH && this.facing != Direction.EAST) {
					this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				} else {
					this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 3, 3, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}

				return true;
			}
		}
	}

	public static class SpiralStaircase extends StrongholdPieces.AbstractPiece {
		private boolean isStructureStart;

		public SpiralStaircase() {
		}

		public SpiralStaircase(int i, Random random, int j, int k) {
			super(i);
			this.isStructureStart = true;
			this.facing = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
			this.entryDoor = StrongholdPieces.AbstractPiece.EntranceType.OPENING;
			switch (this.facing) {
				case NORTH:
				case SOUTH:
					this.boundingBox = new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
					break;
				default:
					this.boundingBox = new BlockBox(j, 64, k, j + 5 - 1, 74, k + 5 - 1);
			}
		}

		public SpiralStaircase(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.isStructureStart = false;
			this.facing = direction;
			this.entryDoor = this.getEntranceType(random);
			this.boundingBox = blockBox;
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Source", this.isStructureStart);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.isStructureStart = structureNbt.getBoolean("Source");
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, 0, 0, 4, 10, 4, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 7, 0);
				this.generateEntrance(world, random, boundingBox, StrongholdPieces.AbstractPiece.EntranceType.OPENING, 1, 1, 4);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 6, 1, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 1, boundingBox);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE.getId()), 1, 6, 1, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 5, 2, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 4, 3, boundingBox);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE.getId()), 1, 5, 3, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 4, 3, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 3, boundingBox);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE.getId()), 3, 4, 3, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 3, 2, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 2, 1, boundingBox);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE.getId()), 3, 3, 1, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 2, 1, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 1, boundingBox);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE.getId()), 1, 2, 1, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 1, 2, boundingBox);
				this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.STONE.getId()), 1, 1, 3, boundingBox);
				return true;
			}
		}
	}

	public static class SquareRoom extends StrongholdPieces.AbstractPiece {
		private static final List<WeightedRandomChestContent> POSSIBLE_CHEST_CONTENT = Lists.newArrayList(
			new WeightedRandomChestContent[]{
				new WeightedRandomChestContent(Items.IRON_INGOT, 0, 1, 5, 10),
				new WeightedRandomChestContent(Items.GOLD_INGOT, 0, 1, 3, 5),
				new WeightedRandomChestContent(Items.REDSTONE, 0, 4, 9, 5),
				new WeightedRandomChestContent(Items.COAL, 0, 3, 8, 10),
				new WeightedRandomChestContent(Items.BREAD, 0, 1, 3, 15),
				new WeightedRandomChestContent(Items.APPLE, 0, 1, 3, 15),
				new WeightedRandomChestContent(Items.IRON_PICKAXE, 0, 1, 1, 1)
			}
		);
		protected int roomType;

		public SquareRoom() {
		}

		public SquareRoom(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
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
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.roomType = structureNbt.getInt("Type");
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, 0, 0, 10, 6, 10, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 4, 1, 0);
				this.fillWithOutline(world, boundingBox, 4, 1, 10, 6, 3, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 0, 1, 4, 0, 3, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 10, 1, 4, 10, 3, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				switch (this.roomType) {
					case 0:
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, boundingBox);
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, boundingBox);
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, boundingBox);
						this.setBlockState(world, Blocks.TORCH.getDefaultState(), 4, 3, 5, boundingBox);
						this.setBlockState(world, Blocks.TORCH.getDefaultState(), 6, 3, 5, boundingBox);
						this.setBlockState(world, Blocks.TORCH.getDefaultState(), 5, 3, 4, boundingBox);
						this.setBlockState(world, Blocks.TORCH.getDefaultState(), 5, 3, 6, boundingBox);
						this.setBlockState(world, Blocks.STONE_SLAB.getDefaultState(), 4, 1, 4, boundingBox);
						this.setBlockState(world, Blocks.STONE_SLAB.getDefaultState(), 4, 1, 5, boundingBox);
						this.setBlockState(world, Blocks.STONE_SLAB.getDefaultState(), 4, 1, 6, boundingBox);
						this.setBlockState(world, Blocks.STONE_SLAB.getDefaultState(), 6, 1, 4, boundingBox);
						this.setBlockState(world, Blocks.STONE_SLAB.getDefaultState(), 6, 1, 5, boundingBox);
						this.setBlockState(world, Blocks.STONE_SLAB.getDefaultState(), 6, 1, 6, boundingBox);
						this.setBlockState(world, Blocks.STONE_SLAB.getDefaultState(), 5, 1, 4, boundingBox);
						this.setBlockState(world, Blocks.STONE_SLAB.getDefaultState(), 5, 1, 6, boundingBox);
						break;
					case 1:
						for (int i = 0; i < 5; i++) {
							this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 1, 3 + i, boundingBox);
							this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 7, 1, 3 + i, boundingBox);
							this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 3, boundingBox);
							this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 3 + i, 1, 7, boundingBox);
						}

						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 1, 5, boundingBox);
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 2, 5, boundingBox);
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 5, 3, 5, boundingBox);
						this.setBlockState(world, Blocks.FLOWING_WATER.getDefaultState(), 5, 4, 5, boundingBox);
						break;
					case 2:
						for (int j = 1; j <= 9; j++) {
							this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 1, 3, j, boundingBox);
							this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 9, 3, j, boundingBox);
						}

						for (int k = 1; k <= 9; k++) {
							this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), k, 3, 1, boundingBox);
							this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), k, 3, 9, boundingBox);
						}

						this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 4, boundingBox);
						this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 5, 1, 6, boundingBox);
						this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 4, boundingBox);
						this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 5, 3, 6, boundingBox);
						this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, 1, 5, boundingBox);
						this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 6, 1, 5, boundingBox);
						this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, 3, 5, boundingBox);
						this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 6, 3, 5, boundingBox);

						for (int l = 1; l <= 3; l++) {
							this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, l, 4, boundingBox);
							this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 6, l, 4, boundingBox);
							this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 4, l, 6, boundingBox);
							this.setBlockState(world, Blocks.COBBLESTONE.getDefaultState(), 6, l, 6, boundingBox);
						}

						this.setBlockState(world, Blocks.TORCH.getDefaultState(), 5, 3, 5, boundingBox);

						for (int m = 2; m <= 8; m++) {
							this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 2, 3, m, boundingBox);
							this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 3, 3, m, boundingBox);
							if (m <= 3 || m >= 7) {
								this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 4, 3, m, boundingBox);
								this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 5, 3, m, boundingBox);
								this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 6, 3, m, boundingBox);
							}

							this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 7, 3, m, boundingBox);
							this.setBlockState(world, Blocks.PLANKS.getDefaultState(), 8, 3, m, boundingBox);
						}

						this.setBlockState(world, Blocks.LADDER.stateFromData(this.getData(Blocks.LADDER, Direction.WEST.getId())), 9, 1, 3, boundingBox);
						this.setBlockState(world, Blocks.LADDER.stateFromData(this.getData(Blocks.LADDER, Direction.WEST.getId())), 9, 2, 3, boundingBox);
						this.setBlockState(world, Blocks.LADDER.stateFromData(this.getData(Blocks.LADDER, Direction.WEST.getId())), 9, 3, 3, boundingBox);
						this.placeChest(
							world,
							boundingBox,
							random,
							3,
							4,
							8,
							WeightedRandomChestContent.combineLootTables(POSSIBLE_CHEST_CONTENT, Items.ENCHANTED_BOOK.getLootTable(random)),
							1 + random.nextInt(4)
						);
				}

				return true;
			}
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

		@Override
		public BlockPos getCenterBlockPos() {
			return this.portalRoom != null ? this.portalRoom.getCenterBlockPos() : super.getCenterBlockPos();
		}
	}

	public static class StraightCorridor extends StrongholdPieces.AbstractPiece {
		private boolean leftExitExists;
		private boolean rightExitExists;

		public StraightCorridor() {
		}

		public StraightCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
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
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.leftExitExists = structureNbt.getBoolean("Left");
			this.rightExitExists = structureNbt.getBoolean("Right");
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 1, 0);
				this.generateEntrance(world, random, boundingBox, StrongholdPieces.AbstractPiece.EntranceType.OPENING, 1, 1, 6);
				this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1F, 1, 2, 1, Blocks.TORCH.getDefaultState());
				this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1F, 3, 2, 1, Blocks.TORCH.getDefaultState());
				this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1F, 1, 2, 5, Blocks.TORCH.getDefaultState());
				this.addBlockWithRandomThreshold(world, boundingBox, random, 0.1F, 3, 2, 5, Blocks.TORCH.getDefaultState());
				if (this.leftExitExists) {
					this.fillWithOutline(world, boundingBox, 0, 1, 2, 0, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}

				if (this.rightExitExists) {
					this.fillWithOutline(world, boundingBox, 4, 1, 2, 4, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}

				return true;
			}
		}
	}

	public static class StraightStairs extends StrongholdPieces.AbstractPiece {
		public StraightStairs() {
		}

		public StraightStairs(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.facing = direction;
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
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.isTouchingLiquid(world, boundingBox)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, 0, 0, 4, 10, 7, true, random, StrongholdPieces.INFESTED_STONE_RANDOMIZER);
				this.generateEntrance(world, random, boundingBox, this.entryDoor, 1, 7, 0);
				this.generateEntrance(world, random, boundingBox, StrongholdPieces.AbstractPiece.EntranceType.OPENING, 1, 1, 7);
				int i = this.getData(Blocks.STONE_STAIRS, 2);

				for (int j = 0; j < 6; j++) {
					this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 1, 6 - j, 1 + j, boundingBox);
					this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 2, 6 - j, 1 + j, boundingBox);
					this.setBlockState(world, Blocks.STONE_STAIRS.stateFromData(i), 3, 6 - j, 1 + j, boundingBox);
					if (j < 5) {
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 1, 5 - j, 1 + j, boundingBox);
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 2, 5 - j, 1 + j, boundingBox);
						this.setBlockState(world, Blocks.STONE_BRICKS.getDefaultState(), 3, 5 - j, 1 + j, boundingBox);
					}
				}

				return true;
			}
		}
	}
}
