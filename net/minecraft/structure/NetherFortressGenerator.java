package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherFortressGenerator {
	private static final int field_31557 = 30;
	private static final int field_31558 = 10;
	static final NetherFortressGenerator.PieceData[] ALL_BRIDGE_PIECES = new NetherFortressGenerator.PieceData[]{
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.Bridge.class, 30, 0, true),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgeCrossing.class, 10, 4),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgeSmallCrossing.class, 10, 4),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgeStairs.class, 10, 3),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.BridgePlatform.class, 5, 2),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorExit.class, 5, 1)
	};
	static final NetherFortressGenerator.PieceData[] ALL_CORRIDOR_PIECES = new NetherFortressGenerator.PieceData[]{
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.SmallCorridor.class, 25, 0, true),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorCrossing.class, 15, 5),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorRightTurn.class, 5, 10),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorLeftTurn.class, 5, 10),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorStairs.class, 10, 3, true),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorBalcony.class, 7, 2),
		new NetherFortressGenerator.PieceData(NetherFortressGenerator.CorridorNetherWartsRoom.class, 5, 2)
	};

	static NetherFortressGenerator.Piece createPiece(
		NetherFortressGenerator.PieceData pieceData,
		StructurePiecesHolder structurePiecesHolder,
		Random random,
		int x,
		int y,
		int z,
		Direction orientation,
		int chainLength
	) {
		Class<? extends NetherFortressGenerator.Piece> class_ = pieceData.pieceType;
		NetherFortressGenerator.Piece piece = null;
		if (class_ == NetherFortressGenerator.Bridge.class) {
			piece = NetherFortressGenerator.Bridge.create(structurePiecesHolder, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.BridgeCrossing.class) {
			piece = NetherFortressGenerator.BridgeCrossing.create(structurePiecesHolder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.BridgeSmallCrossing.class) {
			piece = NetherFortressGenerator.BridgeSmallCrossing.create(structurePiecesHolder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.BridgeStairs.class) {
			piece = NetherFortressGenerator.BridgeStairs.create(structurePiecesHolder, x, y, z, chainLength, orientation);
		} else if (class_ == NetherFortressGenerator.BridgePlatform.class) {
			piece = NetherFortressGenerator.BridgePlatform.create(structurePiecesHolder, x, y, z, chainLength, orientation);
		} else if (class_ == NetherFortressGenerator.CorridorExit.class) {
			piece = NetherFortressGenerator.CorridorExit.create(structurePiecesHolder, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.SmallCorridor.class) {
			piece = NetherFortressGenerator.SmallCorridor.create(structurePiecesHolder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorRightTurn.class) {
			piece = NetherFortressGenerator.CorridorRightTurn.create(structurePiecesHolder, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorLeftTurn.class) {
			piece = NetherFortressGenerator.CorridorLeftTurn.create(structurePiecesHolder, random, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorStairs.class) {
			piece = NetherFortressGenerator.CorridorStairs.create(structurePiecesHolder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorBalcony.class) {
			piece = NetherFortressGenerator.CorridorBalcony.create(structurePiecesHolder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorCrossing.class) {
			piece = NetherFortressGenerator.CorridorCrossing.create(structurePiecesHolder, x, y, z, orientation, chainLength);
		} else if (class_ == NetherFortressGenerator.CorridorNetherWartsRoom.class) {
			piece = NetherFortressGenerator.CorridorNetherWartsRoom.create(structurePiecesHolder, x, y, z, orientation, chainLength);
		}

		return piece;
	}

	public static class Bridge extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 10;
		private static final int SIZE_Z = 19;

		public Bridge(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public Bridge(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 1, 3, false);
		}

		public static NetherFortressGenerator.Bridge create(
			StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -3, 0, 5, 10, 19, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.Bridge(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 0, 3, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 2; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, boundingBox);
				}
			}

			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = blockState.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState3 = blockState.with(FenceBlock.WEST, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 4, 0, 4, 4, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 14, 0, 4, 14, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 17, 0, 4, 17, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 4, 1, 1, 4, 4, 1, blockState3, blockState3, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 4, 4, 4, 4, blockState3, blockState3, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 14, 4, 4, 14, blockState3, blockState3, false);
			this.fillWithOutline(world, boundingBox, 4, 1, 17, 4, 4, 17, blockState3, blockState3, false);
			return true;
		}
	}

	public static class BridgeCrossing extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 19;
		private static final int SIZE_Y = 10;
		private static final int SIZE_Z = 19;

		public BridgeCrossing(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		protected BridgeCrossing(int x, int z, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0, StructurePiece.createBox(x, 64, z, orientation, 19, 10, 19));
			this.setOrientation(orientation);
		}

		protected BridgeCrossing(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
			super(structurePieceType, nbtCompound);
		}

		public BridgeCrossing(ServerWorld world, NbtCompound nbt) {
			this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 8, 3, false);
			this.fillNWOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 3, 8, false);
			this.fillSEOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 3, 8, false);
		}

		public static NetherFortressGenerator.BridgeCrossing create(
			StructurePiecesHolder structurePiecesHolder, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -8, -3, 0, 19, 10, 19, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgeCrossing(chainLength, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 0, 10, 7, 18, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 8, 18, 7, 10, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 7; i <= 11; i++) {
				for (int j = 0; j <= 2; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, 18 - j, boundingBox);
				}
			}

			this.fillWithOutline(world, boundingBox, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int k = 0; k <= 2; k++) {
				for (int l = 7; l <= 11; l++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), k, -1, l, boundingBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), 18 - k, -1, l, boundingBox);
				}
			}

			return true;
		}
	}

	public static class BridgeEnd extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 10;
		private static final int SIZE_Z = 8;
		private final int seed;

		public BridgeEnd(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.seed = random.nextInt();
		}

		public BridgeEnd(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END, nbt);
			this.seed = nbt.getInt("Seed");
		}

		public static NetherFortressGenerator.BridgeEnd create(
			StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -3, 0, 5, 10, 8, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgeEnd(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putInt("Seed", this.seed);
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			Random random2 = new Random((long)this.seed);

			for (int i = 0; i <= 4; i++) {
				for (int j = 3; j <= 4; j++) {
					int k = random2.nextInt(8);
					this.fillWithOutline(world, boundingBox, i, j, 0, i, j, k, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}
			}

			int l = random2.nextInt(8);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 0, 5, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			l = random2.nextInt(8);
			this.fillWithOutline(world, boundingBox, 4, 5, 0, 4, 5, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int n = 0; n <= 4; n++) {
				int o = random2.nextInt(5);
				this.fillWithOutline(world, boundingBox, n, 2, 0, n, 2, o, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			}

			for (int p = 0; p <= 4; p++) {
				for (int q = 0; q <= 1; q++) {
					int r = random2.nextInt(3);
					this.fillWithOutline(world, boundingBox, p, q, 0, p, q, r, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}
			}

			return true;
		}
	}

	public static class BridgePlatform extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 7;
		private static final int SIZE_Y = 8;
		private static final int SIZE_Z = 9;
		private boolean hasBlazeSpawner;

		public BridgePlatform(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public BridgePlatform(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_PLATFORM, nbt);
			this.hasBlazeSpawner = nbt.getBoolean("Mob");
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putBoolean("Mob", this.hasBlazeSpawner);
		}

		public static NetherFortressGenerator.BridgePlatform create(
			StructurePiecesHolder structurePiecesHolder, int x, int y, int z, int chainLength, Direction orientation
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 8, 9, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgePlatform(chainLength, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 6, 7, 7, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 1, 6, 3, boundingBox);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 5, 6, 3, boundingBox);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.NORTH, Boolean.valueOf(true)),
				0,
				6,
				3,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.NORTH, Boolean.valueOf(true)),
				6,
				6,
				3,
				boundingBox
			);
			this.fillWithOutline(world, boundingBox, 0, 6, 4, 0, 6, 7, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 6, 6, 4, 6, 6, 7, blockState2, blockState2, false);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				0,
				6,
				8,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				6,
				6,
				8,
				boundingBox
			);
			this.fillWithOutline(world, boundingBox, 1, 6, 8, 5, 6, 8, blockState, blockState, false);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 1, 7, 8, boundingBox);
			this.fillWithOutline(world, boundingBox, 2, 7, 8, 4, 7, 8, blockState, blockState, false);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 5, 7, 8, boundingBox);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)), 2, 8, 8, boundingBox);
			this.addBlock(world, blockState, 3, 8, 8, boundingBox);
			this.addBlock(world, Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)), 4, 8, 8, boundingBox);
			if (!this.hasBlazeSpawner) {
				BlockPos blockPos = this.offsetPos(3, 5, 5);
				if (boundingBox.contains(blockPos)) {
					this.hasBlazeSpawner = true;
					world.setBlockState(blockPos, Blocks.SPAWNER.getDefaultState(), 2);
					BlockEntity blockEntity = world.getBlockEntity(blockPos);
					if (blockEntity instanceof MobSpawnerBlockEntity) {
						((MobSpawnerBlockEntity)blockEntity).getLogic().setEntityId(EntityType.BLAZE);
					}
				}
			}

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class BridgeSmallCrossing extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 7;
		private static final int SIZE_Y = 9;
		private static final int SIZE_Z = 7;

		public BridgeSmallCrossing(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public BridgeSmallCrossing(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_SMALL_CROSSING, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 2, 0, false);
			this.fillNWOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 0, 2, false);
			this.fillSEOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 0, 2, false);
		}

		public static NetherFortressGenerator.BridgeSmallCrossing create(
			StructurePiecesHolder structurePiecesHolder, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 9, 7, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgeSmallCrossing(chainLength, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 6, 7, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 6, 4, 5, 6, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 2, 0, 5, 4, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 5, 2, 6, 5, 4, blockState2, blockState2, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class BridgeStairs extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 7;
		private static final int SIZE_Y = 11;
		private static final int SIZE_Z = 7;

		public BridgeStairs(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public BridgeStairs(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STAIRS, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillSEOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 6, 2, false);
		}

		public static NetherFortressGenerator.BridgeStairs create(
			StructurePiecesHolder structurePiecesHolder, int x, int y, int z, int chainlength, Direction orientation
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -2, 0, 0, 7, 11, 7, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.BridgeStairs(chainlength, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 6, 10, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 3, 2, 0, 5, 4, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 6, 3, 2, 6, 5, 2, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 6, 3, 4, 6, 5, 4, blockState2, blockState2, false);
			this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 5, 2, 5, boundingBox);
			this.fillWithOutline(world, boundingBox, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 8, 2, 6, 8, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 5, 0, blockState, blockState, false);

			for (int i = 0; i <= 6; i++) {
				for (int j = 0; j <= 6; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorBalcony extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 9;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 9;

		public CorridorBalcony(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorBalcony(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_BALCONY, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			int i = 1;
			Direction direction = this.getFacing();
			if (direction == Direction.WEST || direction == Direction.NORTH) {
				i = 5;
			}

			this.fillNWOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 0, i, random.nextInt(8) > 0);
			this.fillSEOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 0, i, random.nextInt(8) > 0);
		}

		public static NetherFortressGenerator.CorridorBalcony create(
			StructurePiecesHolder structurePiecesHolder, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -3, 0, 0, 9, 7, 9, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorBalcony(chainLength, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 8, 5, 8, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 0, 1, 4, 0, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 4, 0, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 1, 4, 2, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 1, 4, 7, 2, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 8, 7, 3, 8, blockState2, blockState2, false);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.EAST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				0,
				3,
				8,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.WEST, Boolean.valueOf(true)).with(FenceBlock.SOUTH, Boolean.valueOf(true)),
				8,
				3,
				8,
				boundingBox
			);
			this.fillWithOutline(world, boundingBox, 0, 3, 6, 0, 3, 7, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 8, 3, 6, 8, 3, 7, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 4, 5, 1, 5, 5, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 7, 4, 5, 7, 5, 5, blockState2, blockState2, false);

			for (int i = 0; i <= 5; i++) {
				for (int j = 0; j <= 8; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), j, -1, i, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorCrossing extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 5;

		public CorridorCrossing(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorCrossing(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_CROSSING, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 1, 0, true);
			this.fillNWOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 0, 1, true);
			this.fillSEOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorCrossing create(
			StructurePiecesHolder structurePiecesHolder, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorCrossing(chainLength, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorExit extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 13;
		private static final int SIZE_Y = 14;
		private static final int SIZE_Z = 13;

		public CorridorExit(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorExit(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_EXIT, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 5, 3, true);
		}

		public static NetherFortressGenerator.CorridorExit create(
			StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -5, -3, 0, 13, 14, 13, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorExit(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.getDefaultState(), Blocks.NETHER_BRICK_FENCE.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.fillWithOutline(world, boundingBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.fillWithOutline(world, boundingBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, boundingBox);
				if (i != 11) {
					this.addBlock(world, blockState, i + 1, 13, 0, boundingBox);
					this.addBlock(world, blockState, i + 1, 13, 12, boundingBox);
					this.addBlock(world, blockState2, 0, 13, i + 1, boundingBox);
					this.addBlock(world, blockState2, 12, 13, i + 1, boundingBox);
				}
			}

			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				0,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				12,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				12,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				0,
				boundingBox
			);

			for (int j = 3; j <= 9; j += 2) {
				this.fillWithOutline(
					world,
					boundingBox,
					1,
					7,
					j,
					1,
					8,
					j,
					blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)),
					blockState2.with(FenceBlock.WEST, Boolean.valueOf(true)),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					11,
					7,
					j,
					11,
					8,
					j,
					blockState2.with(FenceBlock.EAST, Boolean.valueOf(true)),
					blockState2.with(FenceBlock.EAST, Boolean.valueOf(true)),
					false
				);
			}

			this.fillWithOutline(world, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int k = 4; k <= 8; k++) {
				for (int l = 0; l <= 2; l++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), k, -1, l, boundingBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), k, -1, 12 - l, boundingBox);
				}
			}

			for (int m = 0; m <= 2; m++) {
				for (int n = 4; n <= 8; n++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), m, -1, n, boundingBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), 12 - m, -1, n, boundingBox);
				}
			}

			this.fillWithOutline(world, boundingBox, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 6, 1, 6, 6, 4, 6, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 6, 0, 6, boundingBox);
			this.addBlock(world, Blocks.LAVA.getDefaultState(), 6, 5, 6, boundingBox);
			BlockPos blockPos = this.offsetPos(6, 5, 6);
			if (boundingBox.contains(blockPos)) {
				world.getFluidTickScheduler().schedule(blockPos, Fluids.LAVA, 0);
			}

			return true;
		}
	}

	public static class CorridorLeftTurn extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 5;
		private boolean containsChest;

		public CorridorLeftTurn(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.containsChest = random.nextInt(3) == 0;
		}

		public CorridorLeftTurn(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_LEFT_TURN, nbt);
			this.containsChest = nbt.getBoolean("Chest");
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillNWOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorLeftTurn create(
			StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorLeftTurn(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 3, 1, 4, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 3, 4, 4, 3, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && boundingBox.contains(this.offsetPos(3, 2, 3))) {
				this.containsChest = false;
				this.addChest(world, boundingBox, random, 3, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
			}

			this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorNetherWartsRoom extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 13;
		private static final int SIZE_Y = 14;
		private static final int SIZE_Z = 13;

		public CorridorNetherWartsRoom(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorNetherWartsRoom(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 5, 3, true);
			this.fillForwardOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 5, 11, true);
		}

		public static NetherFortressGenerator.CorridorNetherWartsRoom create(
			StructurePiecesHolder structurePiecesHolder, int x, int y, int z, Direction orientation, int chainlength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -5, -3, 0, 13, 14, 13, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorNetherWartsRoom(chainlength, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			BlockState blockState3 = blockState2.with(FenceBlock.WEST, Boolean.valueOf(true));
			BlockState blockState4 = blockState2.with(FenceBlock.EAST, Boolean.valueOf(true));

			for (int i = 1; i <= 11; i += 2) {
				this.fillWithOutline(world, boundingBox, i, 10, 0, i, 11, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, i, 10, 12, i, 11, 12, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 0, 10, i, 0, 11, i, blockState2, blockState2, false);
				this.fillWithOutline(world, boundingBox, 12, 10, i, 12, 11, i, blockState2, blockState2, false);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 0, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), i, 13, 12, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 0, 13, i, boundingBox);
				this.addBlock(world, Blocks.NETHER_BRICKS.getDefaultState(), 12, 13, i, boundingBox);
				if (i != 11) {
					this.addBlock(world, blockState, i + 1, 13, 0, boundingBox);
					this.addBlock(world, blockState, i + 1, 13, 12, boundingBox);
					this.addBlock(world, blockState2, 0, 13, i + 1, boundingBox);
					this.addBlock(world, blockState2, 12, 13, i + 1, boundingBox);
				}
			}

			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				0,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.EAST, Boolean.valueOf(true)),
				0,
				13,
				12,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.SOUTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				12,
				boundingBox
			);
			this.addBlock(
				world,
				Blocks.NETHER_BRICK_FENCE.getDefaultState().with(FenceBlock.NORTH, Boolean.valueOf(true)).with(FenceBlock.WEST, Boolean.valueOf(true)),
				12,
				13,
				0,
				boundingBox
			);

			for (int j = 3; j <= 9; j += 2) {
				this.fillWithOutline(world, boundingBox, 1, 7, j, 1, 8, j, blockState3, blockState3, false);
				this.fillWithOutline(world, boundingBox, 11, 7, j, 11, 8, j, blockState4, blockState4, false);
			}

			BlockState blockState5 = Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);

			for (int k = 0; k <= 6; k++) {
				int l = k + 4;

				for (int m = 5; m <= 7; m++) {
					this.addBlock(world, blockState5, m, 5 + k, l, boundingBox);
				}

				if (l >= 5 && l <= 8) {
					this.fillWithOutline(world, boundingBox, 5, 5, l, 7, k + 4, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				} else if (l >= 9 && l <= 10) {
					this.fillWithOutline(world, boundingBox, 5, 8, l, 7, k + 4, l, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				}

				if (k >= 1) {
					this.fillWithOutline(world, boundingBox, 5, 6 + k, l, 7, 9 + k, l, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				}
			}

			for (int n = 5; n <= 7; n++) {
				this.addBlock(world, blockState5, n, 12, 11, boundingBox);
			}

			this.fillWithOutline(world, boundingBox, 5, 6, 7, 5, 7, 7, blockState4, blockState4, false);
			this.fillWithOutline(world, boundingBox, 7, 6, 7, 7, 7, 7, blockState3, blockState3, false);
			this.fillWithOutline(world, boundingBox, 5, 13, 12, 7, 13, 12, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			BlockState blockState6 = blockState5.with(StairsBlock.FACING, Direction.EAST);
			BlockState blockState7 = blockState5.with(StairsBlock.FACING, Direction.WEST);
			this.addBlock(world, blockState7, 4, 5, 2, boundingBox);
			this.addBlock(world, blockState7, 4, 5, 3, boundingBox);
			this.addBlock(world, blockState7, 4, 5, 9, boundingBox);
			this.addBlock(world, blockState7, 4, 5, 10, boundingBox);
			this.addBlock(world, blockState6, 8, 5, 2, boundingBox);
			this.addBlock(world, blockState6, 8, 5, 3, boundingBox);
			this.addBlock(world, blockState6, 8, 5, 9, boundingBox);
			this.addBlock(world, blockState6, 8, 5, 10, boundingBox);
			this.fillWithOutline(world, boundingBox, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.getDefaultState(), Blocks.NETHER_WART.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int o = 4; o <= 8; o++) {
				for (int p = 0; p <= 2; p++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), o, -1, p, boundingBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), o, -1, 12 - p, boundingBox);
				}
			}

			for (int q = 0; q <= 2; q++) {
				for (int r = 4; r <= 8; r++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), q, -1, r, boundingBox);
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), 12 - q, -1, r, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorRightTurn extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 5;
		private boolean containsChest;

		public CorridorRightTurn(int chainLength, Random random, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, chainLength, boundingBox);
			this.setOrientation(orientation);
			this.containsChest = random.nextInt(3) == 0;
		}

		public CorridorRightTurn(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_RIGHT_TURN, nbt);
			this.containsChest = nbt.getBoolean("Chest");
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putBoolean("Chest", this.containsChest);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillSEOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 0, 1, true);
		}

		public static NetherFortressGenerator.CorridorRightTurn create(
			StructurePiecesHolder structurePiecesHolder, Random random, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorRightTurn(chainLength, random, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.WEST, Boolean.valueOf(true))
				.with(FenceBlock.EAST, Boolean.valueOf(true));
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 4, 1, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 3, 0, 4, 3, blockState2, blockState2, false);
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 3, 4, 1, 4, 4, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 3, 3, 4, 3, 4, 4, blockState, blockState, false);
			if (this.containsChest && boundingBox.contains(this.offsetPos(1, 2, 3))) {
				this.containsChest = false;
				this.addChest(world, boundingBox, random, 1, 2, 3, LootTables.NETHER_BRIDGE_CHEST);
			}

			this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class CorridorStairs extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 14;
		private static final int SIZE_Z = 10;

		public CorridorStairs(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public CorridorStairs(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_CORRIDOR_STAIRS, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 1, 0, true);
		}

		public static NetherFortressGenerator.CorridorStairs create(
			StructurePiecesHolder structurePiecesHolder, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, -7, 0, 5, 14, 10, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.CorridorStairs(chainLength, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			BlockState blockState = Blocks.NETHER_BRICK_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			BlockState blockState2 = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));

			for (int i = 0; i <= 9; i++) {
				int j = Math.max(1, 7 - i);
				int k = Math.min(Math.max(j + 5, 14 - i), 13);
				int l = i;
				this.fillWithOutline(world, boundingBox, 0, 0, i, 4, j, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 1, j + 1, i, 3, k - 1, i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
				if (i <= 6) {
					this.addBlock(world, blockState, 1, j + 1, i, boundingBox);
					this.addBlock(world, blockState, 2, j + 1, i, boundingBox);
					this.addBlock(world, blockState, 3, j + 1, i, boundingBox);
				}

				this.fillWithOutline(world, boundingBox, 0, k, i, 4, k, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 0, j + 1, i, 0, k - 1, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 4, j + 1, i, 4, k - 1, i, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
				if ((i & 1) == 0) {
					this.fillWithOutline(world, boundingBox, 0, j + 2, i, 0, j + 3, i, blockState2, blockState2, false);
					this.fillWithOutline(world, boundingBox, 4, j + 2, i, 4, j + 3, i, blockState2, blockState2, false);
				}

				for (int m = 0; m <= 4; m++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), m, -1, l, boundingBox);
				}
			}

			return true;
		}
	}

	abstract static class Piece extends StructurePiece {
		protected Piece(StructurePieceType structurePieceType, int i, BlockBox blockBox) {
			super(structurePieceType, i, blockBox);
		}

		public Piece(StructurePieceType structurePieceType, NbtCompound nbtCompound) {
			super(structurePieceType, nbtCompound);
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
		}

		private int checkRemainingPieces(List<NetherFortressGenerator.PieceData> possiblePieces) {
			boolean bl = false;
			int i = 0;

			for (NetherFortressGenerator.PieceData pieceData : possiblePieces) {
				if (pieceData.limit > 0 && pieceData.generatedCount < pieceData.limit) {
					bl = true;
				}

				i += pieceData.weight;
			}

			return bl ? i : -1;
		}

		private NetherFortressGenerator.Piece pickPiece(
			NetherFortressGenerator.Start start,
			List<NetherFortressGenerator.PieceData> possiblePieces,
			StructurePiecesHolder structurePiecesHolder,
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

				for (NetherFortressGenerator.PieceData pieceData : possiblePieces) {
					k -= pieceData.weight;
					if (k < 0) {
						if (!pieceData.canGenerate(chainLength) || pieceData == start.lastPiece && !pieceData.repeatable) {
							break;
						}

						NetherFortressGenerator.Piece piece = NetherFortressGenerator.createPiece(pieceData, structurePiecesHolder, random, x, y, z, orientation, chainLength);
						if (piece != null) {
							pieceData.generatedCount++;
							start.lastPiece = pieceData;
							if (!pieceData.canGenerate()) {
								possiblePieces.remove(pieceData);
							}

							return piece;
						}
					}
				}
			}

			return NetherFortressGenerator.BridgeEnd.create(structurePiecesHolder, random, x, y, z, orientation, chainLength);
		}

		private StructurePiece pieceGenerator(
			NetherFortressGenerator.Start start,
			StructurePiecesHolder structurePiecesHolder,
			Random random,
			int x,
			int y,
			int z,
			@Nullable Direction orientation,
			int chainLength,
			boolean inside
		) {
			if (Math.abs(x - start.getBoundingBox().getMinX()) <= 112 && Math.abs(z - start.getBoundingBox().getMinZ()) <= 112) {
				List<NetherFortressGenerator.PieceData> list = start.bridgePieces;
				if (inside) {
					list = start.corridorPieces;
				}

				StructurePiece structurePiece = this.pickPiece(start, list, structurePiecesHolder, random, x, y, z, orientation, chainLength + 1);
				if (structurePiece != null) {
					structurePiecesHolder.addPiece(structurePiece);
					start.pieces.add(structurePiece);
				}

				return structurePiece;
			} else {
				return NetherFortressGenerator.BridgeEnd.create(structurePiecesHolder, random, x, y, z, orientation, chainLength);
			}
		}

		@Nullable
		protected StructurePiece fillForwardOpening(
			NetherFortressGenerator.Start start, StructurePiecesHolder structurePiecesHolder, Random random, int leftRightOffset, int heightOffset, boolean inside
		) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() - 1,
							direction,
							this.getChainLength(),
							inside
						);
					case SOUTH:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMaxZ() + 1,
							direction,
							this.getChainLength(),
							inside
						);
					case WEST:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMinX() - 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							direction,
							this.getChainLength(),
							inside
						);
					case EAST:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMaxX() + 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
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
			NetherFortressGenerator.Start start, StructurePiecesHolder structurePiecesHolder, Random random, int heightOffset, int leftRightOffset, boolean inside
		) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMinX() - 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.WEST,
							this.getChainLength(),
							inside
						);
					case SOUTH:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMinX() - 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.WEST,
							this.getChainLength(),
							inside
						);
					case WEST:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() - 1,
							Direction.NORTH,
							this.getChainLength(),
							inside
						);
					case EAST:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() - 1,
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
			NetherFortressGenerator.Start start, StructurePiecesHolder structurePiecesHolder, Random random, int heightOffset, int leftRightOffset, boolean inside
		) {
			Direction direction = this.getFacing();
			if (direction != null) {
				switch (direction) {
					case NORTH:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMaxX() + 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.EAST,
							this.getChainLength(),
							inside
						);
					case SOUTH:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMaxX() + 1,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMinZ() + leftRightOffset,
							Direction.EAST,
							this.getChainLength(),
							inside
						);
					case WEST:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMaxZ() + 1,
							Direction.SOUTH,
							this.getChainLength(),
							inside
						);
					case EAST:
						return this.pieceGenerator(
							start,
							structurePiecesHolder,
							random,
							this.boundingBox.getMinX() + leftRightOffset,
							this.boundingBox.getMinY() + heightOffset,
							this.boundingBox.getMaxZ() + 1,
							Direction.SOUTH,
							this.getChainLength(),
							inside
						);
				}
			}

			return null;
		}

		protected static boolean isInBounds(BlockBox boundingBox) {
			return boundingBox != null && boundingBox.getMinY() > 10;
		}
	}

	static class PieceData {
		public final Class<? extends NetherFortressGenerator.Piece> pieceType;
		public final int weight;
		public int generatedCount;
		public final int limit;
		public final boolean repeatable;

		public PieceData(Class<? extends NetherFortressGenerator.Piece> pieceType, int weight, int limit, boolean repeatable) {
			this.pieceType = pieceType;
			this.weight = weight;
			this.limit = limit;
			this.repeatable = repeatable;
		}

		public PieceData(Class<? extends NetherFortressGenerator.Piece> pieceType, int weight, int limit) {
			this(pieceType, weight, limit, false);
		}

		public boolean canGenerate(int chainLength) {
			return this.limit == 0 || this.generatedCount < this.limit;
		}

		public boolean canGenerate() {
			return this.limit == 0 || this.generatedCount < this.limit;
		}
	}

	public static class SmallCorridor extends NetherFortressGenerator.Piece {
		private static final int SIZE_X = 5;
		private static final int SIZE_Y = 7;
		private static final int SIZE_Z = 5;

		public SmallCorridor(int chainLength, BlockBox boundingBox, Direction orientation) {
			super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, chainLength, boundingBox);
			this.setOrientation(orientation);
		}

		public SmallCorridor(ServerWorld world, NbtCompound nbt) {
			super(StructurePieceType.NETHER_FORTRESS_SMALL_CORRIDOR, nbt);
		}

		@Override
		public void fillOpenings(StructurePiece start, StructurePiecesHolder structurePiecesHolder, Random random) {
			this.fillForwardOpening((NetherFortressGenerator.Start)start, structurePiecesHolder, random, 1, 0, true);
		}

		public static NetherFortressGenerator.SmallCorridor create(
			StructurePiecesHolder structurePiecesHolder, int x, int y, int z, Direction orientation, int chainLength
		) {
			BlockBox blockBox = BlockBox.rotated(x, y, z, -1, 0, 0, 5, 7, 5, orientation);
			return isInBounds(blockBox) && structurePiecesHolder.getIntersecting(blockBox) == null
				? new NetherFortressGenerator.SmallCorridor(chainLength, blockBox, orientation)
				: null;
		}

		@Override
		public boolean generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			BlockState blockState = Blocks.NETHER_BRICK_FENCE
				.getDefaultState()
				.with(FenceBlock.NORTH, Boolean.valueOf(true))
				.with(FenceBlock.SOUTH, Boolean.valueOf(true));
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 4, 1, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 3, 0, 4, 3, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 1, 4, 4, 1, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 4, 3, 3, 4, 4, 3, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.getDefaultState(), Blocks.NETHER_BRICKS.getDefaultState(), false);

			for (int i = 0; i <= 4; i++) {
				for (int j = 0; j <= 4; j++) {
					this.fillDownwards(world, Blocks.NETHER_BRICKS.getDefaultState(), i, -1, j, boundingBox);
				}
			}

			return true;
		}
	}

	public static class Start extends NetherFortressGenerator.BridgeCrossing {
		public NetherFortressGenerator.PieceData lastPiece;
		public List<NetherFortressGenerator.PieceData> bridgePieces;
		public List<NetherFortressGenerator.PieceData> corridorPieces;
		public final List<StructurePiece> pieces = Lists.newArrayList();

		public Start(Random random, int x, int z) {
			super(x, z, getRandomHorizontalDirection(random));
			this.bridgePieces = Lists.newArrayList();

			for (NetherFortressGenerator.PieceData pieceData : NetherFortressGenerator.ALL_BRIDGE_PIECES) {
				pieceData.generatedCount = 0;
				this.bridgePieces.add(pieceData);
			}

			this.corridorPieces = Lists.newArrayList();

			for (NetherFortressGenerator.PieceData pieceData2 : NetherFortressGenerator.ALL_CORRIDOR_PIECES) {
				pieceData2.generatedCount = 0;
				this.corridorPieces.add(pieceData2);
			}
		}

		public Start(ServerWorld serverWorld, NbtCompound nbtCompound) {
			super(StructurePieceType.NETHER_FORTRESS_START, nbtCompound);
		}
	}
}
