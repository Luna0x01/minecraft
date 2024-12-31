package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_3998;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class NetherFortressPieces {
	private static final NetherFortressPieces.PieceData[] ALL_BRIDGE_PIECES = new NetherFortressPieces.PieceData[]{
		new NetherFortressPieces.PieceData(NetherFortressPieces.BridgePiece.class, 30, 0, true),
		new NetherFortressPieces.PieceData(NetherFortressPieces.BridgeCrossing.class, 10, 4),
		new NetherFortressPieces.PieceData(NetherFortressPieces.BridgeSmallCrossing.class, 10, 4),
		new NetherFortressPieces.PieceData(NetherFortressPieces.BridgeStairs.class, 10, 3),
		new NetherFortressPieces.PieceData(NetherFortressPieces.BridgePlatform.class, 5, 2),
		new NetherFortressPieces.PieceData(NetherFortressPieces.CorridorExit.class, 5, 1)
	};
	private static final NetherFortressPieces.PieceData[] ALL_CORRIDOR_PIECES = new NetherFortressPieces.PieceData[]{
		new NetherFortressPieces.PieceData(NetherFortressPieces.SmallCorridor.class, 25, 0, true),
		new NetherFortressPieces.PieceData(NetherFortressPieces.CorridorCrossing.class, 15, 5),
		new NetherFortressPieces.PieceData(NetherFortressPieces.CorridorRightTurn.class, 5, 10),
		new NetherFortressPieces.PieceData(NetherFortressPieces.CorridorLeftTurn.class, 5, 10),
		new NetherFortressPieces.PieceData(NetherFortressPieces.CorridorStairs.class, 10, 3, true),
		new NetherFortressPieces.PieceData(NetherFortressPieces.CorridorBalcony.class, 7, 2),
		new NetherFortressPieces.PieceData(NetherFortressPieces.CorridorNetherWartsRoom.class, 5, 2)
	};

	public static void registerPieces() {
		StructurePieceManager.registerPiece(NetherFortressPieces.BridgeCrossing.class, "NeBCr");
		StructurePieceManager.registerPiece(NetherFortressPieces.BridgeEnd.class, "NeBEF");
		StructurePieceManager.registerPiece(NetherFortressPieces.BridgePiece.class, "NeBS");
		StructurePieceManager.registerPiece(NetherFortressPieces.CorridorStairs.class, "NeCCS");
		StructurePieceManager.registerPiece(NetherFortressPieces.CorridorBalcony.class, "NeCTB");
		StructurePieceManager.registerPiece(NetherFortressPieces.CorridorExit.class, "NeCE");
		StructurePieceManager.registerPiece(NetherFortressPieces.CorridorCrossing.class, "NeSCSC");
		StructurePieceManager.registerPiece(NetherFortressPieces.CorridorLeftTurn.class, "NeSCLT");
		StructurePieceManager.registerPiece(NetherFortressPieces.SmallCorridor.class, "NeSC");
		StructurePieceManager.registerPiece(NetherFortressPieces.CorridorRightTurn.class, "NeSCRT");
		StructurePieceManager.registerPiece(NetherFortressPieces.CorridorNetherWartsRoom.class, "NeCSR");
		StructurePieceManager.registerPiece(NetherFortressPieces.BridgePlatform.class, "NeMT");
		StructurePieceManager.registerPiece(NetherFortressPieces.BridgeSmallCrossing.class, "NeRC");
		StructurePieceManager.registerPiece(NetherFortressPieces.BridgeStairs.class, "NeSR");
		StructurePieceManager.registerPiece(NetherFortressPieces.StartPiece.class, "NeStart");
	}

	private static NetherFortressPieces.AbstractPiece createPiece(
		NetherFortressPieces.PieceData pieceData, List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
	) {
		Class<? extends NetherFortressPieces.AbstractPiece> class_ = pieceData.pieceType;
		NetherFortressPieces.AbstractPiece abstractPiece = null;
		if (class_ == NetherFortressPieces.BridgePiece.class) {
			abstractPiece = NetherFortressPieces.BridgePiece.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.BridgeCrossing.class) {
			abstractPiece = NetherFortressPieces.BridgeCrossing.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.BridgeSmallCrossing.class) {
			abstractPiece = NetherFortressPieces.BridgeSmallCrossing.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.BridgeStairs.class) {
			abstractPiece = NetherFortressPieces.BridgeStairs.create(pieces, random, x, y, z, chainLength, orientation);
		} else if (class_ == NetherFortressPieces.BridgePlatform.class) {
			abstractPiece = NetherFortressPieces.BridgePlatform.create(pieces, random, x, y, z, chainLength, orientation);
		} else if (class_ == NetherFortressPieces.CorridorExit.class) {
			abstractPiece = NetherFortressPieces.CorridorExit.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.SmallCorridor.class) {
			abstractPiece = NetherFortressPieces.SmallCorridor.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.CorridorRightTurn.class) {
			abstractPiece = NetherFortressPieces.CorridorRightTurn.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.CorridorLeftTurn.class) {
			abstractPiece = NetherFortressPieces.CorridorLeftTurn.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.CorridorStairs.class) {
			abstractPiece = NetherFortressPieces.CorridorStairs.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.CorridorBalcony.class) {
			abstractPiece = NetherFortressPieces.CorridorBalcony.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.CorridorCrossing.class) {
			abstractPiece = NetherFortressPieces.CorridorCrossing.create(pieces, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressPieces.CorridorNetherWartsRoom.class) {
			abstractPiece = NetherFortressPieces.CorridorNetherWartsRoom.create(pieces, random, x, y, z, orientation, chainLength);
		}

		return abstractPiece;
	}

	abstract static class AbstractPiece extends StructurePiece {
		public AbstractPiece() {
		}

		protected AbstractPiece(int i) {
			super(i);
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
		}

		private int checkRemainingPieces(List<NetherFortressPieces.PieceData> possiblePieces) {
			boolean bl = false;
			int i = 0;

			for (NetherFortressPieces.PieceData pieceData : possiblePieces) {
				if (pieceData.limit > 0 && pieceData.generatedCount < pieceData.limit) {
					bl = true;
				}

				i += pieceData.weight;
			}

			return bl ? i : -1;
		}

		private NetherFortressPieces.AbstractPiece pickPiece(
			NetherFortressPieces.StartPiece start,
			List<NetherFortressPieces.PieceData> possiblePieces,
			List<StructurePiece> pieces,
			Random random,
			int x,
			int y,
			int z,
			Direction orientation,
			int chainLength
		) {
			int i = this.checkRemainingPieces(possiblePieces);
			boolean bl = i > 0 && chainLength <= 30;
			int j = 0;

			while (j < 5 && bl) {
				j++;
				int k = random.nextInt(i);

				for (NetherFortressPieces.PieceData pieceData : possiblePieces) {
					k -= pieceData.weight;
					if (k < 0) {
						if (!pieceData.canGenerate(chainLength) || pieceData == start.lastPiece && !pieceData.repeatable) {
							break;
						}

						NetherFortressPieces.AbstractPiece abstractPiece = NetherFortressPieces.createPiece(pieceData, pieces, random, x, y, z, orientation, chainLength);
						if (abstractPiece != null) {
							pieceData.generatedCount++;
							start.lastPiece = pieceData;
							if (!pieceData.canGenerate()) {
								possiblePieces.remove(pieceData);
							}

							return abstractPiece;
						}
					}
				}
			}

			return NetherFortressPieces.BridgeEnd.create(pieces, random, x, y, z, orientation, chainLength);
		}

		private StructurePiece generate(
			NetherFortressPieces.StartPiece start,
			List<StructurePiece> pieces,
			Random random,
			int x,
			int y,
			int z,
			@Nullable Direction orientation,
			int chainLength,
			boolean inside
		) {
			if (Math.abs(x - start.getBoundingBox().minX) <= 112 && Math.abs(z - start.getBoundingBox().minZ) <= 112) {
				List<NetherFortressPieces.PieceData> list = start.bridgePieces;
				if (inside) {
					list = start.corridorPieces;
				}

				StructurePiece structurePiece = this.pickPiece(start, list, pieces, random, x, y, z, orientation, chainLength + 1);
				if (structurePiece != null) {
					pieces.add(structurePiece);
					start.pieces.add(structurePiece);
				}

				return structurePiece;
			} else {
				return NetherFortressPieces.BridgeEnd.create(pieces, random, x, y, z, orientation, chainLength);
			}
		}

		@Nullable
		protected StructurePiece fillForwardOpening(
			NetherFortressPieces.StartPiece start, List<StructurePiece> pieces, Random random, int leftRightOffset, int heightOffset, boolean inside
		) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ - 1,
							direction,
							this.getChainLength(),
							inside
						);
					case SOUTH:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.maxZ + 1,
							direction,
							this.getChainLength(),
							inside
						);
					case WEST:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							direction,
							this.getChainLength(),
							inside
						);
					case EAST:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							direction,
							this.getChainLength(),
							inside
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece fillNWOpening(
			NetherFortressPieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset, boolean inside
		) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.WEST,
							this.getChainLength(),
							inside
						);
					case SOUTH:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX - 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.WEST,
							this.getChainLength(),
							inside
						);
					case WEST:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ - 1,
							Direction.NORTH,
							this.getChainLength(),
							inside
						);
					case EAST:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ - 1,
							Direction.NORTH,
							this.getChainLength(),
							inside
						);
				}
			}

			return null;
		}

		@Nullable
		protected StructurePiece fillSEOpening(
			NetherFortressPieces.StartPiece start, List<StructurePiece> pieces, Random random, int heightOffset, int leftRightOffset, boolean inside
		) {
			Direction direction = this.method_11854();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.EAST,
							this.getChainLength(),
							inside
						);
					case SOUTH:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.maxX + 1,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.minZ + leftRightOffset,
							Direction.EAST,
							this.getChainLength(),
							inside
						);
					case WEST:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.maxZ + 1,
							Direction.SOUTH,
							this.getChainLength(),
							inside
						);
					case EAST:
						return this.generate(
							start,
							pieces,
							random,
							this.boundingBox.minX + leftRightOffset,
							this.boundingBox.minY + heightOffset,
							this.boundingBox.maxZ + 1,
							Direction.SOUTH,
							this.getChainLength(),
							inside
						);
				}
			}

			return null;
		}

		protected static boolean isInbounds(BlockBox box) {
			return box != null && box.minY > 10;
		}
	}

	public static class BridgeCrossing extends NetherFortressPieces.AbstractPiece {
		public BridgeCrossing() {
		}

		public BridgeCrossing(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		protected BridgeCrossing(Random random, int i, int j) {
			super(0);
			this.method_11853(Direction.DirectionType.HORIZONTAL.getRandomDirection(random));
			if (this.method_11854().getAxis() == Direction.Axis.Z) {
				this.boundingBox = new BlockBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
			} else {
				this.boundingBox = new BlockBox(i, 64, j, i + 19 - 1, 73, j + 19 - 1);
			}
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((NetherFortressPieces.StartPiece)start, pieces, random, 8, 3, false);
			this.fillNWOpening((NetherFortressPieces.StartPiece)start, pieces, random, 3, 8, false);
			this.fillSEOpening((NetherFortressPieces.StartPiece)start, pieces, random, 3, 8, false);
		}

		public static NetherFortressPieces.BridgeCrossing create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction boundingBox, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -8, -3, 0, 19, 10, 19, boundingBox);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.BridgeCrossing(chainLength, random, blockBox, boundingBox)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 8, 5, 0, 10, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 8, 18, 7, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 7; i <= 11; i++) {
				for (int j = 0; j <= 2; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, blockBox);
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, blockBox);
				}
			}

			this.method_17653(iWorld, blockBox, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int k = 0; k <= 2; k++) {
				for (int l = 7; l <= 11; l++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), k, -1, l, blockBox);
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), 18 - k, -1, l, blockBox);
				}
			}

			return true;
		}
	}

	public static class BridgeEnd extends NetherFortressPieces.AbstractPiece {
		private int seed;

		public BridgeEnd() {
		}

		public BridgeEnd(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.seed = random.nextInt();
		}

		public static NetherFortressPieces.BridgeEnd create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -3, 0, 5, 10, 8, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.BridgeEnd(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.seed = nbtCompound.getInt("Seed");
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putInt("Seed", this.seed);
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			Random random2 = new Random((long)this.seed);

			for (int i = 0; i <= 4; i++) {
				for (int j = 3; j <= 4; j++) {
					int k = random2.nextInt(8);
					this.method_17653(iWorld, blockBox, i, j, 0, i, j, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}
			}

			int l = random2.nextInt(8);
			this.method_17653(iWorld, blockBox, 0, 5, 0, 0, 5, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			l = random2.nextInt(8);
			this.method_17653(iWorld, blockBox, 4, 5, 0, 4, 5, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int n = 0; n <= 4; n++) {
				int o = random2.nextInt(5);
				this.method_17653(iWorld, blockBox, n, 2, 0, n, 2, o, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			}

			for (int p = 0; p <= 4; p++) {
				for (int q = 0; q <= 1; q++) {
					int r = random2.nextInt(3);
					this.method_17653(iWorld, blockBox, p, q, 0, p, q, r, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}
			}

			return true;
		}
	}

	public static class BridgePiece extends NetherFortressPieces.AbstractPiece {
		public BridgePiece() {
		}

		public BridgePiece(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((NetherFortressPieces.StartPiece)start, pieces, random, 1, 3, false);
		}

		public static NetherFortressPieces.BridgePiece create(List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -3, 0, 5, 10, 19, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.BridgePiece(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 5, 0, 3, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 2; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, blockBox);
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, blockBox);
				}
			}

			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			BlockState blockState2 = blockState.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			BlockState blockState3 = blockState.withProperty(FenceBlock.field_18268, Boolean.valueOf(true));
			this.method_17653(iWorld, blockBox, 0, 1, 1, 0, 4, 1, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 0, 3, 4, 0, 4, 4, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 0, 3, 14, 0, 4, 14, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 0, 1, 17, 0, 4, 17, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 4, 1, 1, 4, 4, 1, blockState3, blockState3, false);
			this.method_17653(iWorld, blockBox, 4, 3, 4, 4, 4, 4, blockState3, blockState3, false);
			this.method_17653(iWorld, blockBox, 4, 3, 14, 4, 4, 14, blockState3, blockState3, false);
			this.method_17653(iWorld, blockBox, 4, 1, 17, 4, 4, 17, blockState3, blockState3, false);
			return true;
		}
	}

	public static class BridgePlatform extends NetherFortressPieces.AbstractPiece {
		private boolean hasBlazeSpawner;

		public BridgePlatform() {
		}

		public BridgePlatform(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.hasBlazeSpawner = nbtCompound.getBoolean("Mob");
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Mob", this.hasBlazeSpawner);
		}

		public static NetherFortressPieces.BridgePlatform create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, int chainLength, Direction orientation
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 8, 9, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.BridgePlatform(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 2, 0, 6, 7, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			this.method_56(iWorld, Blocks.NETHER_BRICK_FENCE.getDefaultState().withProperty(FenceBlock.field_18268, Boolean.valueOf(true)), 1, 6, 3, blockBox);
			this.method_56(iWorld, Blocks.NETHER_BRICK_FENCE.getDefaultState().withProperty(FenceBlock.field_18266, Boolean.valueOf(true)), 5, 6, 3, blockBox);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18266, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18265, Boolean.valueOf(true)),
				0,
				6,
				3,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18265, Boolean.valueOf(true)),
				6,
				6,
				3,
				blockBox
			);
			this.method_17653(iWorld, blockBox, 0, 6, 4, 0, 6, 7, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 6, 6, 4, 6, 6, 7, blockState2, blockState2, false);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18266, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18267, Boolean.valueOf(true)),
				0,
				6,
				8,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18267, Boolean.valueOf(true)),
				6,
				6,
				8,
				blockBox
			);
			this.method_17653(iWorld, blockBox, 1, 6, 8, 5, 6, 8, blockState, blockState, false);
			this.method_56(iWorld, Blocks.NETHER_BRICK_FENCE.getDefaultState().withProperty(FenceBlock.field_18266, Boolean.valueOf(true)), 1, 7, 8, blockBox);
			this.method_17653(iWorld, blockBox, 2, 7, 8, 4, 7, 8, blockState, blockState, false);
			this.method_56(iWorld, Blocks.NETHER_BRICK_FENCE.getDefaultState().withProperty(FenceBlock.field_18268, Boolean.valueOf(true)), 5, 7, 8, blockBox);
			this.method_56(iWorld, Blocks.NETHER_BRICK_FENCE.getDefaultState().withProperty(FenceBlock.field_18266, Boolean.valueOf(true)), 2, 8, 8, blockBox);
			this.method_56(iWorld, blockState, 3, 8, 8, blockBox);
			this.method_56(iWorld, Blocks.NETHER_BRICK_FENCE.getDefaultState().withProperty(FenceBlock.field_18268, Boolean.valueOf(true)), 4, 8, 8, blockBox);
			if (!this.hasBlazeSpawner) {
				BlockPos blockPos = new BlockPos(this.applyXTransform(3, 5), this.applyYTransform(5), this.applyZTransform(3, 5));
				if (blockBox.contains(blockPos)) {
					this.hasBlazeSpawner = true;
					iWorld.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
					BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().method_16278(EntityType.BLAZE);
					}
				}
			}

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, blockBox);
				}
			}

			return true;
		}
	}

	public static class BridgeSmallCrossing extends NetherFortressPieces.AbstractPiece {
		public BridgeSmallCrossing() {
		}

		public BridgeSmallCrossing(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((NetherFortressPieces.StartPiece)start, pieces, random, 2, 0, false);
			this.fillNWOpening((NetherFortressPieces.StartPiece)start, pieces, random, 0, 2, false);
			this.fillSEOpening((NetherFortressPieces.StartPiece)start, pieces, random, 0, 2, false);
		}

		public static NetherFortressPieces.BridgeSmallCrossing create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 9, 7, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.BridgeSmallCrossing(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 6, 7, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			this.method_17653(iWorld, blockBox, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 6, 4, 5, 6, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 2, 0, 5, 4, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 5, 2, 6, 5, 4, blockState2, blockState2, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, blockBox);
				}
			}

			return true;
		}
	}

	public static class BridgeStairs extends NetherFortressPieces.AbstractPiece {
		public BridgeStairs() {
		}

		public BridgeStairs(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillSEOpening((NetherFortressPieces.StartPiece)start, pieces, random, 6, 2, false);
		}

		public static NetherFortressPieces.BridgeStairs create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, int chainLength, Direction orientation
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 11, 7, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.BridgeStairs(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 6, 10, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			this.method_17653(iWorld, blockBox, 0, 3, 2, 0, 5, 4, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 6, 3, 2, 6, 5, 2, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 6, 3, 4, 6, 5, 4, blockState2, blockState2, false);
			this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), 5, 2, 5, blockBox);
			this.method_17653(iWorld, blockBox, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 8, 2, 6, 8, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, blockBox);
				}
			}

			return true;
		}
	}

	public static class CorridorBalcony extends NetherFortressPieces.AbstractPiece {
		public CorridorBalcony() {
		}

		public CorridorBalcony(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			int i = 1;
			Direction direction = this.method_11854();
			if (direction == Direction.WEST || direction == Direction.NORTH) {
				i = 5;
			}

			this.fillNWOpening((NetherFortressPieces.StartPiece)start, pieces, random, 0, i, random.nextInt(8) > 0);
			this.fillSEOpening((NetherFortressPieces.StartPiece)start, pieces, random, 0, i, random.nextInt(8) > 0);
		}

		public static NetherFortressPieces.CorridorBalcony create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -3, 0, 0, 9, 7, 9, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.CorridorBalcony(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			this.method_17653(iWorld, blockBox, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 8, 5, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 3, 0, 1, 4, 0, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 7, 3, 0, 7, 4, 0, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 1, 4, 2, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 1, 4, 7, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 3, 8, 7, 3, 8, blockState2, blockState2, false);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18266, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18267, Boolean.valueOf(true)),
				0,
				3,
				8,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18267, Boolean.valueOf(true)),
				8,
				3,
				8,
				blockBox
			);
			this.method_17653(iWorld, blockBox, 0, 3, 6, 0, 3, 7, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 8, 3, 6, 8, 3, 7, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 4, 5, 1, 5, 5, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 7, 4, 5, 7, 5, 5, blockState2, blockState2, false);

			for (int i = 0; i <= 5; i++) {
				for (int j = 0; j <= 8; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), j, -1, i, blockBox);
				}
			}

			return true;
		}
	}

	public static class CorridorCrossing extends NetherFortressPieces.AbstractPiece {
		public CorridorCrossing() {
		}

		public CorridorCrossing(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((NetherFortressPieces.StartPiece)start, pieces, random, 1, 0, true);
			this.fillNWOpening((NetherFortressPieces.StartPiece)start, pieces, random, 0, 1, true);
			this.fillSEOpening((NetherFortressPieces.StartPiece)start, pieces, random, 0, 1, true);
		}

		public static NetherFortressPieces.CorridorCrossing create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.CorridorCrossing(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, blockBox);
				}
			}

			return true;
		}
	}

	public static class CorridorExit extends NetherFortressPieces.AbstractPiece {
		public CorridorExit() {
		}

		public CorridorExit(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((NetherFortressPieces.StartPiece)start, pieces, random, 5, 3, true);
		}

		public static NetherFortressPieces.CorridorExit create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -5, -3, 0, 13, 14, 13, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.CorridorExit(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.getDefaultState(), Blocks.NETHER_BRICK_FENCE.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.method_17653(iWorld, blockBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.method_17653(iWorld, blockBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.method_17653(iWorld, blockBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.method_17653(iWorld, blockBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, blockBox);
				this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, blockBox);
				this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, blockBox);
				this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, blockBox);
				if (i != 11) {
					this.method_56(iWorld, blockState, i + 1, 13, 0, blockBox);
					this.method_56(iWorld, blockState, i + 1, 13, 12, blockBox);
					this.method_56(iWorld, blockState2, 0, 13, i + 1, blockBox);
					this.method_56(iWorld, blockState2, 12, 13, i + 1, blockBox);
				}
			}

			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
				0,
				13,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18267, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
				0,
				13,
				12,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18267, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18268, Boolean.valueOf(true)),
				12,
				13,
				12,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18268, Boolean.valueOf(true)),
				12,
				13,
				0,
				blockBox
			);

			for (int j = 3; j <= 9; j += 2) {
				this.method_17653(
					iWorld,
					blockBox,
					1,
					7,
					j,
					1,
					8,
					j,
					blockState2.withProperty(FenceBlock.field_18268, Boolean.valueOf(true)),
					blockState2.withProperty(FenceBlock.field_18268, Boolean.valueOf(true)),
					false
				);
				this.method_17653(
					iWorld,
					blockBox,
					11,
					7,
					j,
					11,
					8,
					j,
					blockState2.withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
					blockState2.withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
					false
				);
			}

			this.method_17653(iWorld, blockBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int k = 4; k <= 8; k++) {
				for (int l = 0; l <= 2; l++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), k, -1, l, blockBox);
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), k, -1, 12 - l, blockBox);
				}
			}

			for (int m = 0; m <= 2; m++) {
				for (int n = 4; n <= 8; n++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), m, -1, n, blockBox);
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), 12 - m, -1, n, blockBox);
				}
			}

			this.method_17653(iWorld, blockBox, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 6, 1, 6, 6, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), 6, 0, 6, blockBox);
			this.method_56(iWorld, Blocks.LAVA.getDefaultState(), 6, 5, 6, blockBox);
			BlockPos blockPos = new BlockPos(this.applyXTransform(6, 6), this.applyYTransform(5), this.applyZTransform(6, 6));
			if (blockBox.contains(blockPos)) {
				iWorld.method_16340().schedule(blockPos, Fluids.LAVA, 0);
			}

			return true;
		}
	}

	public static class CorridorLeftTurn extends NetherFortressPieces.AbstractPiece {
		private boolean containsChest;

		public CorridorLeftTurn() {
		}

		public CorridorLeftTurn(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.containsChest = random.nextInt(3) == 0;
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.containsChest = nbtCompound.getBoolean("Chest");
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillNWOpening((NetherFortressPieces.StartPiece)start, pieces, random, 0, 1, true);
		}

		public static NetherFortressPieces.CorridorLeftTurn create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.CorridorLeftTurn(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			this.method_17653(iWorld, blockBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 3, 1, 4, 4, 1, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 4, 3, 3, 4, 4, 3, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && blockBox.contains(new BlockPos(this.applyXTransform(3, 3), this.applyYTransform(2), this.applyZTransform(3, 3)))) {
				this.containsChest = false;
				this.method_11852(iWorld, blockBox, random, 3, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
			}

			this.method_17653(iWorld, blockBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, blockBox);
				}
			}

			return true;
		}
	}

	public static class CorridorNetherWartsRoom extends NetherFortressPieces.AbstractPiece {
		public CorridorNetherWartsRoom() {
		}

		public CorridorNetherWartsRoom(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((NetherFortressPieces.StartPiece)start, pieces, random, 5, 3, true);
			this.fillForwardOpening((NetherFortressPieces.StartPiece)start, pieces, random, 5, 11, true);
		}

		public static NetherFortressPieces.CorridorNetherWartsRoom create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -5, -3, 0, 13, 14, 13, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.CorridorNetherWartsRoom(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			BlockState blockState3 = blockState2.withProperty(FenceBlock.field_18268, Boolean.valueOf(true));
			BlockState blockState4 = blockState2.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.method_17653(iWorld, blockBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.method_17653(iWorld, blockBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.method_17653(iWorld, blockBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.method_17653(iWorld, blockBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, blockBox);
				this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, blockBox);
				this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, blockBox);
				this.method_56(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, blockBox);
				if (i != 11) {
					this.method_56(iWorld, blockState, i + 1, 13, 0, blockBox);
					this.method_56(iWorld, blockState, i + 1, 13, 12, blockBox);
					this.method_56(iWorld, blockState2, 0, 13, i + 1, blockBox);
					this.method_56(iWorld, blockState2, 12, 13, i + 1, blockBox);
				}
			}

			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
				0,
				13,
				0,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18267, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18266, Boolean.valueOf(true)),
				0,
				13,
				12,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18267, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18268, Boolean.valueOf(true)),
				12,
				13,
				12,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.NETHER_BRICK_FENCE
					.getDefaultState()
					.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
					.withProperty(FenceBlock.field_18268, Boolean.valueOf(true)),
				12,
				13,
				0,
				blockBox
			);

			for (int j = 3; j <= 9; j += 2) {
				this.method_17653(iWorld, blockBox, 1, 7, j, 1, 8, j, blockState3, blockState3, false);
				this.method_17653(iWorld, blockBox, 11, 7, j, 11, 8, j, blockState4, blockState4, false);
			}

			BlockState blockState5 = Blocks.NETHER_BRICK_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH);

			for (int k = 0; k <= 6; k++) {
				int l = k + 4;

				for (int m = 5; m <= 7; m++) {
					this.method_56(iWorld, blockState5, m, 5 + k, l, blockBox);
				}

				if (l >= 5 && l <= 8) {
					this.method_17653(iWorld, blockBox, 5, 5, l, 7, k + 4, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				} else if (l >= 9 && l <= 10) {
					this.method_17653(iWorld, blockBox, 5, 8, l, 7, k + 4, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}

				if (k >= 1) {
					this.method_17653(iWorld, blockBox, 5, 6 + k, l, 7, 9 + k, l, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}
			}

			for (int n = 5; n <= 7; n++) {
				this.method_56(iWorld, blockState5, n, 12, 11, blockBox);
			}

			this.method_17653(iWorld, blockBox, 5, 6, 7, 5, 7, 7, blockState4, blockState4, false);
			this.method_17653(iWorld, blockBox, 7, 6, 7, 7, 7, 7, blockState3, blockState3, false);
			this.method_17653(iWorld, blockBox, 5, 13, 12, 7, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState6 = blockState5.withProperty(StairsBlock.FACING, Direction.EAST);
			BlockState blockState7 = blockState5.withProperty(StairsBlock.FACING, Direction.WEST);
			this.method_56(iWorld, blockState7, 4, 5, 2, blockBox);
			this.method_56(iWorld, blockState7, 4, 5, 3, blockBox);
			this.method_56(iWorld, blockState7, 4, 5, 9, blockBox);
			this.method_56(iWorld, blockState7, 4, 5, 10, blockBox);
			this.method_56(iWorld, blockState6, 8, 5, 2, blockBox);
			this.method_56(iWorld, blockState6, 8, 5, 3, blockBox);
			this.method_56(iWorld, blockState6, 8, 5, 9, blockBox);
			this.method_56(iWorld, blockState6, 8, 5, 10, blockBox);
			this.method_17653(iWorld, blockBox, 3, 4, 4, 4, 4, 8, Blocks.SOULSAND.getDefaultState(), Blocks.SOULSAND.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 8, 4, 4, 9, 4, 8, Blocks.SOULSAND.getDefaultState(), Blocks.SOULSAND.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int o = 4; o <= 8; o++) {
				for (int p = 0; p <= 2; p++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), o, -1, p, blockBox);
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), o, -1, 12 - p, blockBox);
				}
			}

			for (int q = 0; q <= 2; q++) {
				for (int r = 4; r <= 8; r++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), q, -1, r, blockBox);
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), 12 - q, -1, r, blockBox);
				}
			}

			return true;
		}
	}

	public static class CorridorRightTurn extends NetherFortressPieces.AbstractPiece {
		private boolean containsChest;

		public CorridorRightTurn() {
		}

		public CorridorRightTurn(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
			this.containsChest = random.nextInt(3) == 0;
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
			super.method_5530(nbtCompound, arg);
			this.containsChest = nbtCompound.getBoolean("Chest");
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillSEOpening((NetherFortressPieces.StartPiece)start, pieces, random, 0, 1, true);
		}

		public static NetherFortressPieces.CorridorRightTurn create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.CorridorRightTurn(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18268, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18266, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			this.method_17653(iWorld, blockBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 3, 1, 0, 4, 1, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 0, 3, 3, 0, 4, 3, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && blockBox.contains(new BlockPos(this.applyXTransform(1, 3), this.applyYTransform(2), this.applyZTransform(1, 3)))) {
				this.containsChest = false;
				this.method_11852(iWorld, blockBox, random, 1, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
			}

			this.method_17653(iWorld, blockBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, blockBox);
				}
			}

			return true;
		}
	}

	public static class CorridorStairs extends NetherFortressPieces.AbstractPiece {
		public CorridorStairs() {
		}

		public CorridorStairs(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((NetherFortressPieces.StartPiece)start, pieces, random, 1, 0, true);
		}

		public static NetherFortressPieces.CorridorStairs create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 14, 10, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.CorridorStairs(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			BlockState blockState = Blocks.NETHER_BRICK_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.SOUTH);
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));

			for (int i = 0; i <= 9; i++) {
				int j = Math.max(1, 7 - i);
				int k = Math.min(Math.max(j + 5, 14 - i), 13);
				int l = i;
				this.method_17653(iWorld, blockBox, 0, 0, i, 4, j, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.method_17653(iWorld, blockBox, 1, j + 1, i, 3, k - 1, i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				if (i <= 6) {
					this.method_56(iWorld, blockState, 1, j + 1, i, blockBox);
					this.method_56(iWorld, blockState, 2, j + 1, i, blockBox);
					this.method_56(iWorld, blockState, 3, j + 1, i, blockBox);
				}

				this.method_17653(iWorld, blockBox, 0, k, i, 4, k, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.method_17653(iWorld, blockBox, 0, j + 1, i, 0, k - 1, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.method_17653(iWorld, blockBox, 4, j + 1, i, 4, k - 1, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				if ((i & 1) == 0) {
					this.method_17653(iWorld, blockBox, 0, j + 2, i, 0, j + 3, i, blockState2, blockState2, false);
					this.method_17653(iWorld, blockBox, 4, j + 2, i, 4, j + 3, i, blockState2, blockState2, false);
				}

				for (int m = 0; m <= 4; m++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), m, -1, l, blockBox);
				}
			}

			return true;
		}
	}

	static class PieceData {
		public Class<? extends NetherFortressPieces.AbstractPiece> pieceType;
		public final int weight;
		public int generatedCount;
		public int limit;
		public boolean repeatable;

		public PieceData(Class<? extends NetherFortressPieces.AbstractPiece> class_, int i, int j, boolean bl) {
			this.pieceType = class_;
			this.weight = i;
			this.limit = j;
			this.repeatable = bl;
		}

		public PieceData(Class<? extends NetherFortressPieces.AbstractPiece> class_, int i, int j) {
			this(class_, i, j, false);
		}

		public boolean canGenerate(int chainLength) {
			return this.limit == 0 || this.generatedCount < this.limit;
		}

		public boolean canGenerate() {
			return this.limit == 0 || this.generatedCount < this.limit;
		}
	}

	public static class SmallCorridor extends NetherFortressPieces.AbstractPiece {
		public SmallCorridor() {
		}

		public SmallCorridor(int i, Random random, BlockBox blockBox, Direction direction) {
			super(i);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		@Override
		public void fillOpenings(StructurePiece start, List<StructurePiece> pieces, Random random) {
			this.fillForwardOpening((NetherFortressPieces.StartPiece)start, pieces, random, 1, 0, true);
		}

		public static NetherFortressPieces.SmallCorridor create(
			List<StructurePiece> pieces, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInbounds(blockBox) && StructurePiece.getOverlappingPiece(pieces, blockBox) == null
				? new NetherFortressPieces.SmallCorridor(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			this.method_17653(iWorld, blockBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.withProperty(FenceBlock.field_18265, Boolean.valueOf(true))
				.withProperty(FenceBlock.field_18267, Boolean.valueOf(true));
			this.method_17653(iWorld, blockBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 0, 3, 1, 0, 4, 1, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 3, 3, 0, 4, 3, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 4, 3, 1, 4, 4, 1, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 4, 3, 3, 4, 4, 3, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.method_72(iWorld, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, blockBox);
				}
			}

			return true;
		}
	}

	public static class StartPiece extends NetherFortressPieces.BridgeCrossing {
		public NetherFortressPieces.PieceData lastPiece;
		public List<NetherFortressPieces.PieceData> bridgePieces;
		public List<NetherFortressPieces.PieceData> corridorPieces;
		public List<StructurePiece> pieces = Lists.newArrayList();

		public StartPiece() {
		}

		public StartPiece(Random random, int i, int j) {
			super(random, i, j);
			this.bridgePieces = Lists.newArrayList();

			for (NetherFortressPieces.PieceData pieceData : NetherFortressPieces.ALL_BRIDGE_PIECES) {
				pieceData.generatedCount = 0;
				this.bridgePieces.add(pieceData);
			}

			this.corridorPieces = Lists.newArrayList();

			for (NetherFortressPieces.PieceData pieceData2 : NetherFortressPieces.ALL_CORRIDOR_PIECES) {
				pieceData2.generatedCount = 0;
				this.corridorPieces.add(pieceData2);
			}
		}
	}
}
