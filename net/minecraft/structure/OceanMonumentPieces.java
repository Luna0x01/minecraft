package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OceanMonumentPieces {
	public static void registerPieces() {
		StructurePieceManager.registerPiece(OceanMonumentPieces.MainBuilding.class, "OMB");
		StructurePieceManager.registerPiece(OceanMonumentPieces.CoreRoom.class, "OMCR");
		StructurePieceManager.registerPiece(OceanMonumentPieces.DoubleXRoom.class, "OMDXR");
		StructurePieceManager.registerPiece(OceanMonumentPieces.DoubleXYRoom.class, "OMDXYR");
		StructurePieceManager.registerPiece(OceanMonumentPieces.DoubleYRoom.class, "OMDYR");
		StructurePieceManager.registerPiece(OceanMonumentPieces.DoubleYZRoom.class, "OMDYZR");
		StructurePieceManager.registerPiece(OceanMonumentPieces.DoubleZRoom.class, "OMDZR");
		StructurePieceManager.registerPiece(OceanMonumentPieces.Entrance.class, "OMEntry");
		StructurePieceManager.registerPiece(OceanMonumentPieces.Penthouse.class, "OMPenthouse");
		StructurePieceManager.registerPiece(OceanMonumentPieces.SimpleRoom.class, "OMSimple");
		StructurePieceManager.registerPiece(OceanMonumentPieces.SimpleTopRoom.class, "OMSimpleT");
	}

	public abstract static class AbstractPiece extends StructurePiece {
		protected static final BlockState PRISMARINE = Blocks.PRISMARINE.stateFromData(PrismarineBlock.ROUGH_ID);
		protected static final BlockState PRISMARINE_BRICKS = Blocks.PRISMARINE.stateFromData(PrismarineBlock.BRICKS_ID);
		protected static final BlockState DARK_PRISMARINE = Blocks.PRISMARINE.stateFromData(PrismarineBlock.DARK_ID);
		protected static final BlockState PRISMARINE2 = PRISMARINE_BRICKS;
		protected static final BlockState SEA_LANTERN = Blocks.SEA_LANTERN.getDefaultState();
		protected static final BlockState WATER = Blocks.WATER.getDefaultState();
		protected static final int TWO_ZERO_ZERO_INDEX = getIndex(2, 0, 0);
		protected static final int TWO_TWO_ZERO_INDEX = getIndex(2, 2, 0);
		protected static final int ZERO_ONE_ZERO_INDEX = getIndex(0, 1, 0);
		protected static final int FOUR_ONE_ZERO_INDEX = getIndex(4, 1, 0);
		protected OceanMonumentPieces.PieceSetting setting;

		protected static final int getIndex(int x, int y, int z) {
			return y * 25 + z * 5 + x;
		}

		public AbstractPiece() {
			super(0);
		}

		public AbstractPiece(int i) {
			super(i);
		}

		public AbstractPiece(Direction direction, BlockBox blockBox) {
			super(1);
			this.method_11853(direction);
			this.boundingBox = blockBox;
		}

		protected AbstractPiece(int i, Direction direction, OceanMonumentPieces.PieceSetting pieceSetting, int j, int k, int l) {
			super(i);
			this.method_11853(direction);
			this.setting = pieceSetting;
			int m = pieceSetting.roomIndex;
			int n = m % 5;
			int o = m / 5 % 5;
			int p = m / 25;
			if (direction != Direction.NORTH && direction != Direction.SOUTH) {
				this.boundingBox = new BlockBox(0, 0, 0, l * 8 - 1, k * 4 - 1, j * 8 - 1);
			} else {
				this.boundingBox = new BlockBox(0, 0, 0, j * 8 - 1, k * 4 - 1, l * 8 - 1);
			}

			switch (direction) {
				case NORTH:
					this.boundingBox.move(n * 8, p * 4, -(o + l) * 8 + 1);
					break;
				case SOUTH:
					this.boundingBox.move(n * 8, p * 4, o * 8);
					break;
				case WEST:
					this.boundingBox.move(-(o + l) * 8 + 1, p * 4, n * 8);
					break;
				default:
					this.boundingBox.move(o * 8, p * 4, n * 8);
			}
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
		}

		protected void setAirAndWater(World world, BlockBox box, int x, int y, int z, int width, int height, int depth, boolean bl) {
			for (int i = y; i <= height; i++) {
				for (int j = x; j <= width; j++) {
					for (int k = z; k <= depth; k++) {
						if (!bl || this.getBlockAt(world, j, i, k, box).getMaterial() != Material.AIR) {
							if (this.applyYTransform(i) >= world.getSeaLevel()) {
								this.setBlockState(world, Blocks.AIR.getDefaultState(), j, i, k, box);
							} else {
								this.setBlockState(world, WATER, j, i, k, box);
							}
						}
					}
				}
			}
		}

		protected void method_9256(World world, BlockBox box, int x, int z, boolean bl) {
			if (bl) {
				this.fillWithOutline(world, box, x + 0, 0, z + 0, x + 2, 0, z + 8 - 1, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, box, x + 5, 0, z + 0, x + 8 - 1, 0, z + 8 - 1, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, box, x + 3, 0, z + 0, x + 4, 0, z + 2, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, box, x + 3, 0, z + 5, x + 4, 0, z + 8 - 1, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, box, x + 3, 0, z + 2, x + 4, 0, z + 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, box, x + 3, 0, z + 5, x + 4, 0, z + 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, box, x + 2, 0, z + 3, x + 2, 0, z + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, box, x + 5, 0, z + 3, x + 5, 0, z + 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			} else {
				this.fillWithOutline(world, box, x + 0, 0, z + 0, x + 8 - 1, 0, z + 8 - 1, PRISMARINE, PRISMARINE, false);
			}
		}

		protected void method_9254(World world, BlockBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState block) {
			for (int i = minY; i <= maxY; i++) {
				for (int j = minX; j <= maxX; j++) {
					for (int k = minZ; k <= maxZ; k++) {
						if (this.getBlockAt(world, j, i, k, box) == WATER) {
							this.setBlockState(world, block, j, i, k, box);
						}
					}
				}
			}
		}

		protected boolean method_9257(BlockBox box, int i, int j, int k, int l) {
			int m = this.applyXTransform(i, j);
			int n = this.applyZTransform(i, j);
			int o = this.applyXTransform(k, l);
			int p = this.applyZTransform(k, l);
			return box.intersectsXZ(Math.min(m, o), Math.min(n, p), Math.max(m, o), Math.max(n, p));
		}

		protected boolean method_9253(World world, BlockBox box, int x, int y, int z) {
			int i = this.applyXTransform(x, z);
			int j = this.applyYTransform(y);
			int k = this.applyZTransform(x, z);
			if (box.contains(new BlockPos(i, j, k))) {
				GuardianEntity guardianEntity = new GuardianEntity(world);
				guardianEntity.setElder(true);
				guardianEntity.heal(guardianEntity.getMaxHealth());
				guardianEntity.refreshPositionAndAngles((double)i + 0.5, (double)j, (double)k + 0.5, 0.0F, 0.0F);
				guardianEntity.initialize(world.getLocalDifficulty(new BlockPos(guardianEntity)), null);
				world.spawnEntity(guardianEntity);
				return true;
			} else {
				return false;
			}
		}
	}

	public static class CoreRoom extends OceanMonumentPieces.AbstractPiece {
		public CoreRoom() {
		}

		public CoreRoom(Direction direction, OceanMonumentPieces.PieceSetting pieceSetting, Random random) {
			super(1, direction, pieceSetting, 2, 2, 2);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			this.method_9254(world, boundingBox, 1, 8, 0, 14, 8, 14, PRISMARINE);
			int i = 7;
			BlockState blockState = PRISMARINE_BRICKS;
			this.fillWithOutline(world, boundingBox, 0, 7, 0, 0, 7, 15, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 15, 7, 0, 15, 7, 15, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 1, 7, 0, 15, 7, 0, blockState, blockState, false);
			this.fillWithOutline(world, boundingBox, 1, 7, 15, 14, 7, 15, blockState, blockState, false);

			for (int j = 1; j <= 6; j++) {
				blockState = PRISMARINE_BRICKS;
				if (j == 2 || j == 6) {
					blockState = PRISMARINE;
				}

				for (int k = 0; k <= 15; k += 15) {
					this.fillWithOutline(world, boundingBox, k, j, 0, k, j, 1, blockState, blockState, false);
					this.fillWithOutline(world, boundingBox, k, j, 6, k, j, 9, blockState, blockState, false);
					this.fillWithOutline(world, boundingBox, k, j, 14, k, j, 15, blockState, blockState, false);
				}

				this.fillWithOutline(world, boundingBox, 1, j, 0, 1, j, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 6, j, 0, 9, j, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 14, j, 0, 14, j, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, j, 15, 14, j, 15, blockState, blockState, false);
			}

			this.fillWithOutline(world, boundingBox, 6, 3, 6, 9, 6, 9, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.getDefaultState(), Blocks.GOLD_BLOCK.getDefaultState(), false);

			for (int l = 3; l <= 6; l += 3) {
				for (int m = 6; m <= 9; m += 3) {
					this.setBlockState(world, SEA_LANTERN, m, l, 6, boundingBox);
					this.setBlockState(world, SEA_LANTERN, m, l, 9, boundingBox);
				}
			}

			this.fillWithOutline(world, boundingBox, 5, 1, 6, 5, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 9, 5, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 1, 6, 10, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 1, 9, 10, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 1, 5, 6, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 1, 5, 9, 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 1, 10, 6, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 1, 10, 9, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 2, 5, 5, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 2, 10, 5, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 2, 5, 10, 6, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 2, 10, 10, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 7, 1, 5, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 7, 1, 10, 7, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 7, 9, 5, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 7, 9, 10, 7, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 7, 5, 6, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 7, 10, 6, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 7, 5, 14, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 7, 10, 14, 7, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 1, 2, 2, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 2, 3, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 13, 1, 2, 13, 1, 3, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 12, 1, 2, 12, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 1, 12, 2, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 13, 3, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 13, 1, 12, 13, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 12, 1, 13, 12, 1, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			return true;
		}
	}

	public static class DoubleXRoom extends OceanMonumentPieces.AbstractPiece {
		public DoubleXRoom() {
		}

		public DoubleXRoom(Direction direction, OceanMonumentPieces.PieceSetting pieceSetting, Random random) {
			super(1, direction, pieceSetting, 2, 1, 1);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			OceanMonumentPieces.PieceSetting pieceSetting = this.setting.neighbors[Direction.EAST.getId()];
			OceanMonumentPieces.PieceSetting pieceSetting2 = this.setting;
			if (this.setting.roomIndex / 25 > 0) {
				this.method_9256(world, boundingBox, 8, 0, pieceSetting.neighborPresences[Direction.DOWN.getId()]);
				this.method_9256(world, boundingBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getId()]);
			}

			if (pieceSetting2.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 1, 4, 1, 7, 4, 6, PRISMARINE);
			}

			if (pieceSetting.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 8, 4, 1, 14, 4, 6, PRISMARINE);
			}

			this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 15, 3, 0, 15, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 0, 15, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 7, 14, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 7, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 15, 2, 0, 15, 2, 7, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 15, 2, 0, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 7, 14, 2, 7, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 15, 1, 0, 15, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 15, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 0, 10, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 2, 0, 9, 2, 3, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 5, 3, 0, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.setBlockState(world, SEA_LANTERN, 6, 2, 3, boundingBox);
			this.setBlockState(world, SEA_LANTERN, 9, 2, 3, boundingBox);
			if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0, false);
			}

			if (pieceSetting2.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 7, 4, 2, 7, false);
			}

			if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4, false);
			}

			if (pieceSetting.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 1, 0, 12, 2, 0, false);
			}

			if (pieceSetting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 1, 7, 12, 2, 7, false);
			}

			if (pieceSetting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 15, 1, 3, 15, 2, 4, false);
			}

			return true;
		}
	}

	static class DoubleXRoomFactory implements OceanMonumentPieces.Factory {
		private DoubleXRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentPieces.PieceSetting settings) {
			return settings.neighborPresences[Direction.EAST.getId()] && !settings.neighbors[Direction.EAST.getId()].used;
		}

		@Override
		public OceanMonumentPieces.AbstractPiece generate(Direction orientation, OceanMonumentPieces.PieceSetting settings, Random random) {
			settings.used = true;
			settings.neighbors[Direction.EAST.getId()].used = true;
			return new OceanMonumentPieces.DoubleXRoom(orientation, settings, random);
		}
	}

	public static class DoubleXYRoom extends OceanMonumentPieces.AbstractPiece {
		public DoubleXYRoom() {
		}

		public DoubleXYRoom(Direction direction, OceanMonumentPieces.PieceSetting pieceSetting, Random random) {
			super(1, direction, pieceSetting, 2, 2, 1);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			OceanMonumentPieces.PieceSetting pieceSetting = this.setting.neighbors[Direction.EAST.getId()];
			OceanMonumentPieces.PieceSetting pieceSetting2 = this.setting;
			OceanMonumentPieces.PieceSetting pieceSetting3 = pieceSetting2.neighbors[Direction.UP.getId()];
			OceanMonumentPieces.PieceSetting pieceSetting4 = pieceSetting.neighbors[Direction.UP.getId()];
			if (this.setting.roomIndex / 25 > 0) {
				this.method_9256(world, boundingBox, 8, 0, pieceSetting.neighborPresences[Direction.DOWN.getId()]);
				this.method_9256(world, boundingBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getId()]);
			}

			if (pieceSetting3.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 1, 8, 1, 7, 8, 6, PRISMARINE);
			}

			if (pieceSetting4.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 8, 8, 1, 14, 8, 6, PRISMARINE);
			}

			for (int i = 1; i <= 7; i++) {
				BlockState blockState = PRISMARINE_BRICKS;
				if (i == 2 || i == 6) {
					blockState = PRISMARINE;
				}

				this.fillWithOutline(world, boundingBox, 0, i, 0, 0, i, 7, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 15, i, 0, 15, i, 7, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, i, 0, 15, i, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, i, 7, 14, i, 7, blockState, blockState, false);
			}

			this.fillWithOutline(world, boundingBox, 2, 1, 3, 2, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 2, 4, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 5, 4, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 13, 1, 3, 13, 7, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 11, 1, 2, 12, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 11, 1, 5, 12, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 3, 5, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 1, 3, 10, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 7, 2, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 5, 2, 5, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 5, 2, 10, 7, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 5, 5, 5, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 10, 5, 5, 10, 7, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.setBlockState(world, PRISMARINE_BRICKS, 6, 6, 2, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 9, 6, 2, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 6, 6, 5, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 9, 6, 5, boundingBox);
			this.fillWithOutline(world, boundingBox, 5, 4, 3, 6, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 4, 3, 10, 4, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.setBlockState(world, SEA_LANTERN, 5, 4, 2, boundingBox);
			this.setBlockState(world, SEA_LANTERN, 5, 4, 5, boundingBox);
			this.setBlockState(world, SEA_LANTERN, 10, 4, 2, boundingBox);
			this.setBlockState(world, SEA_LANTERN, 10, 4, 5, boundingBox);
			if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0, false);
			}

			if (pieceSetting2.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 7, 4, 2, 7, false);
			}

			if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4, false);
			}

			if (pieceSetting.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 1, 0, 12, 2, 0, false);
			}

			if (pieceSetting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 1, 7, 12, 2, 7, false);
			}

			if (pieceSetting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 15, 1, 3, 15, 2, 4, false);
			}

			if (pieceSetting3.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 5, 0, 4, 6, 0, false);
			}

			if (pieceSetting3.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 5, 7, 4, 6, 7, false);
			}

			if (pieceSetting3.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 5, 3, 0, 6, 4, false);
			}

			if (pieceSetting4.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 5, 0, 12, 6, 0, false);
			}

			if (pieceSetting4.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 11, 5, 7, 12, 6, 7, false);
			}

			if (pieceSetting4.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 15, 5, 3, 15, 6, 4, false);
			}

			return true;
		}
	}

	static class DoubleXYRoomFactory implements OceanMonumentPieces.Factory {
		private DoubleXYRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentPieces.PieceSetting settings) {
			if (settings.neighborPresences[Direction.EAST.getId()]
				&& !settings.neighbors[Direction.EAST.getId()].used
				&& settings.neighborPresences[Direction.UP.getId()]
				&& !settings.neighbors[Direction.UP.getId()].used) {
				OceanMonumentPieces.PieceSetting pieceSetting = settings.neighbors[Direction.EAST.getId()];
				return pieceSetting.neighborPresences[Direction.UP.getId()] && !pieceSetting.neighbors[Direction.UP.getId()].used;
			} else {
				return false;
			}
		}

		@Override
		public OceanMonumentPieces.AbstractPiece generate(Direction orientation, OceanMonumentPieces.PieceSetting settings, Random random) {
			settings.used = true;
			settings.neighbors[Direction.EAST.getId()].used = true;
			settings.neighbors[Direction.UP.getId()].used = true;
			settings.neighbors[Direction.EAST.getId()].neighbors[Direction.UP.getId()].used = true;
			return new OceanMonumentPieces.DoubleXYRoom(orientation, settings, random);
		}
	}

	public static class DoubleYRoom extends OceanMonumentPieces.AbstractPiece {
		public DoubleYRoom() {
		}

		public DoubleYRoom(Direction direction, OceanMonumentPieces.PieceSetting pieceSetting, Random random) {
			super(1, direction, pieceSetting, 1, 2, 1);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.setting.roomIndex / 25 > 0) {
				this.method_9256(world, boundingBox, 0, 0, this.setting.neighborPresences[Direction.DOWN.getId()]);
			}

			OceanMonumentPieces.PieceSetting pieceSetting = this.setting.neighbors[Direction.UP.getId()];
			if (pieceSetting.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 1, 8, 1, 6, 8, 6, PRISMARINE);
			}

			this.fillWithOutline(world, boundingBox, 0, 4, 0, 0, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 4, 0, 7, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 4, 0, 6, 4, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 4, 7, 6, 4, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 4, 1, 2, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 4, 2, 1, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 4, 1, 5, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 4, 2, 6, 4, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 4, 5, 2, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 4, 5, 1, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 4, 5, 5, 4, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 4, 5, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			OceanMonumentPieces.PieceSetting pieceSetting2 = this.setting;

			for (int i = 1; i <= 5; i += 4) {
				int j = 0;
				if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 2, i, j, 2, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 5, i, j, 5, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 3, i + 2, j, 4, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 0, i, j, 7, i + 2, j, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 0, i + 1, j, 7, i + 1, j, PRISMARINE, PRISMARINE, false);
				}

				int var9 = 7;
				if (pieceSetting2.neighborPresences[Direction.NORTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 2, i, var9, 2, i + 2, var9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 5, i, var9, 5, i + 2, var9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 3, i + 2, var9, 4, i + 2, var9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 0, i, var9, 7, i + 2, var9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 0, i + 1, var9, 7, i + 1, var9, PRISMARINE, PRISMARINE, false);
				}

				int k = 0;
				if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
					this.fillWithOutline(world, boundingBox, k, i, 2, k, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, k, i, 5, k, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, k, i + 2, 3, k, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, k, i, 0, k, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, k, i + 1, 0, k, i + 1, 7, PRISMARINE, PRISMARINE, false);
				}

				int var10 = 7;
				if (pieceSetting2.neighborPresences[Direction.EAST.getId()]) {
					this.fillWithOutline(world, boundingBox, var10, i, 2, var10, i + 2, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var10, i, 5, var10, i + 2, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var10, i + 2, 3, var10, i + 2, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, var10, i, 0, var10, i + 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var10, i + 1, 0, var10, i + 1, 7, PRISMARINE, PRISMARINE, false);
				}

				pieceSetting2 = pieceSetting;
			}

			return true;
		}
	}

	static class DoubleYRoomFactory implements OceanMonumentPieces.Factory {
		private DoubleYRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentPieces.PieceSetting settings) {
			return settings.neighborPresences[Direction.UP.getId()] && !settings.neighbors[Direction.UP.getId()].used;
		}

		@Override
		public OceanMonumentPieces.AbstractPiece generate(Direction orientation, OceanMonumentPieces.PieceSetting settings, Random random) {
			settings.used = true;
			settings.neighbors[Direction.UP.getId()].used = true;
			return new OceanMonumentPieces.DoubleYRoom(orientation, settings, random);
		}
	}

	public static class DoubleYZRoom extends OceanMonumentPieces.AbstractPiece {
		public DoubleYZRoom() {
		}

		public DoubleYZRoom(Direction direction, OceanMonumentPieces.PieceSetting pieceSetting, Random random) {
			super(1, direction, pieceSetting, 1, 2, 2);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			OceanMonumentPieces.PieceSetting pieceSetting = this.setting.neighbors[Direction.NORTH.getId()];
			OceanMonumentPieces.PieceSetting pieceSetting2 = this.setting;
			OceanMonumentPieces.PieceSetting pieceSetting3 = pieceSetting.neighbors[Direction.UP.getId()];
			OceanMonumentPieces.PieceSetting pieceSetting4 = pieceSetting2.neighbors[Direction.UP.getId()];
			if (this.setting.roomIndex / 25 > 0) {
				this.method_9256(world, boundingBox, 0, 8, pieceSetting.neighborPresences[Direction.DOWN.getId()]);
				this.method_9256(world, boundingBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getId()]);
			}

			if (pieceSetting4.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 1, 8, 1, 6, 8, 7, PRISMARINE);
			}

			if (pieceSetting3.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 1, 8, 8, 6, 8, 14, PRISMARINE);
			}

			for (int i = 1; i <= 7; i++) {
				BlockState blockState = PRISMARINE_BRICKS;
				if (i == 2 || i == 6) {
					blockState = PRISMARINE;
				}

				this.fillWithOutline(world, boundingBox, 0, i, 0, 0, i, 15, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 7, i, 0, 7, i, 15, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, i, 0, 6, i, 0, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 1, i, 15, 6, i, 15, blockState, blockState, false);
			}

			for (int j = 1; j <= 7; j++) {
				BlockState blockState2 = DARK_PRISMARINE;
				if (j == 2 || j == 6) {
					blockState2 = SEA_LANTERN;
				}

				this.fillWithOutline(world, boundingBox, 3, j, 7, 4, j, 8, blockState2, blockState2, false);
			}

			if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0, false);
			}

			if (pieceSetting2.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 1, 3, 7, 2, 4, false);
			}

			if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4, false);
			}

			if (pieceSetting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 15, 4, 2, 15, false);
			}

			if (pieceSetting.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 11, 0, 2, 12, false);
			}

			if (pieceSetting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 1, 11, 7, 2, 12, false);
			}

			if (pieceSetting4.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 5, 0, 4, 6, 0, false);
			}

			if (pieceSetting4.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 5, 3, 7, 6, 4, false);
				this.fillWithOutline(world, boundingBox, 5, 4, 2, 6, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 2, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 5, 6, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			if (pieceSetting4.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 5, 3, 0, 6, 4, false);
				this.fillWithOutline(world, boundingBox, 1, 4, 2, 2, 4, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 2, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 5, 1, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			if (pieceSetting3.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 5, 15, 4, 6, 15, false);
			}

			if (pieceSetting3.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 5, 11, 0, 6, 12, false);
				this.fillWithOutline(world, boundingBox, 1, 4, 10, 2, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 10, 1, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 13, 1, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			if (pieceSetting3.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 5, 11, 7, 6, 12, false);
				this.fillWithOutline(world, boundingBox, 5, 4, 10, 6, 4, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 10, 6, 3, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 13, 6, 3, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			return true;
		}
	}

	static class DoubleYZRoomFactory implements OceanMonumentPieces.Factory {
		private DoubleYZRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentPieces.PieceSetting settings) {
			if (settings.neighborPresences[Direction.NORTH.getId()]
				&& !settings.neighbors[Direction.NORTH.getId()].used
				&& settings.neighborPresences[Direction.UP.getId()]
				&& !settings.neighbors[Direction.UP.getId()].used) {
				OceanMonumentPieces.PieceSetting pieceSetting = settings.neighbors[Direction.NORTH.getId()];
				return pieceSetting.neighborPresences[Direction.UP.getId()] && !pieceSetting.neighbors[Direction.UP.getId()].used;
			} else {
				return false;
			}
		}

		@Override
		public OceanMonumentPieces.AbstractPiece generate(Direction orientation, OceanMonumentPieces.PieceSetting settings, Random random) {
			settings.used = true;
			settings.neighbors[Direction.NORTH.getId()].used = true;
			settings.neighbors[Direction.UP.getId()].used = true;
			settings.neighbors[Direction.NORTH.getId()].neighbors[Direction.UP.getId()].used = true;
			return new OceanMonumentPieces.DoubleYZRoom(orientation, settings, random);
		}
	}

	public static class DoubleZRoom extends OceanMonumentPieces.AbstractPiece {
		public DoubleZRoom() {
		}

		public DoubleZRoom(Direction direction, OceanMonumentPieces.PieceSetting pieceSetting, Random random) {
			super(1, direction, pieceSetting, 1, 1, 2);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			OceanMonumentPieces.PieceSetting pieceSetting = this.setting.neighbors[Direction.NORTH.getId()];
			OceanMonumentPieces.PieceSetting pieceSetting2 = this.setting;
			if (this.setting.roomIndex / 25 > 0) {
				this.method_9256(world, boundingBox, 0, 8, pieceSetting.neighborPresences[Direction.DOWN.getId()]);
				this.method_9256(world, boundingBox, 0, 0, pieceSetting2.neighborPresences[Direction.DOWN.getId()]);
			}

			if (pieceSetting2.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 1, 4, 1, 6, 4, 7, PRISMARINE);
			}

			if (pieceSetting.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 1, 4, 8, 6, 4, 14, PRISMARINE);
			}

			this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 15, 6, 3, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 15, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 15, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 7, 2, 0, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 15, 6, 2, 15, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 7, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 15, 6, 1, 15, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 1, 1, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 1, 1, 6, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 1, 1, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 3, 1, 6, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 13, 1, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 1, 13, 6, 1, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 13, 1, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 3, 13, 6, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 1, 6, 2, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 6, 5, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 1, 9, 2, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 9, 5, 3, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 2, 6, 4, 2, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 3, 2, 9, 4, 2, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 2, 2, 7, 2, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 2, 7, 5, 2, 8, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.setBlockState(world, SEA_LANTERN, 2, 2, 5, boundingBox);
			this.setBlockState(world, SEA_LANTERN, 5, 2, 5, boundingBox);
			this.setBlockState(world, SEA_LANTERN, 2, 2, 10, boundingBox);
			this.setBlockState(world, SEA_LANTERN, 5, 2, 10, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 2, 3, 5, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 5, 3, 5, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 2, 3, 10, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 5, 3, 10, boundingBox);
			if (pieceSetting2.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0, false);
			}

			if (pieceSetting2.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 1, 3, 7, 2, 4, false);
			}

			if (pieceSetting2.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4, false);
			}

			if (pieceSetting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 15, 4, 2, 15, false);
			}

			if (pieceSetting.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 11, 0, 2, 12, false);
			}

			if (pieceSetting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 7, 1, 11, 7, 2, 12, false);
			}

			return true;
		}
	}

	static class DoubleZRoomFactory implements OceanMonumentPieces.Factory {
		private DoubleZRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentPieces.PieceSetting settings) {
			return settings.neighborPresences[Direction.NORTH.getId()] && !settings.neighbors[Direction.NORTH.getId()].used;
		}

		@Override
		public OceanMonumentPieces.AbstractPiece generate(Direction orientation, OceanMonumentPieces.PieceSetting settings, Random random) {
			OceanMonumentPieces.PieceSetting pieceSetting = settings;
			if (!settings.neighborPresences[Direction.NORTH.getId()] || settings.neighbors[Direction.NORTH.getId()].used) {
				pieceSetting = settings.neighbors[Direction.SOUTH.getId()];
			}

			pieceSetting.used = true;
			pieceSetting.neighbors[Direction.NORTH.getId()].used = true;
			return new OceanMonumentPieces.DoubleZRoom(orientation, pieceSetting, random);
		}
	}

	public static class Entrance extends OceanMonumentPieces.AbstractPiece {
		public Entrance() {
		}

		public Entrance(Direction direction, OceanMonumentPieces.PieceSetting pieceSetting) {
			super(1, direction, pieceSetting, 1, 1, 1);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 1, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, 2, 0, 7, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 2, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 5, 1, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			if (this.setting.neighborPresences[Direction.NORTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 7, 4, 2, 7, false);
			}

			if (this.setting.neighborPresences[Direction.WEST.getId()]) {
				this.setAirAndWater(world, boundingBox, 0, 1, 3, 1, 2, 4, false);
			}

			if (this.setting.neighborPresences[Direction.EAST.getId()]) {
				this.setAirAndWater(world, boundingBox, 6, 1, 3, 7, 2, 4, false);
			}

			return true;
		}
	}

	interface Factory {
		boolean canGenerate(OceanMonumentPieces.PieceSetting settings);

		OceanMonumentPieces.AbstractPiece generate(Direction orientation, OceanMonumentPieces.PieceSetting settings, Random random);
	}

	public static class MainBuilding extends OceanMonumentPieces.AbstractPiece {
		private OceanMonumentPieces.PieceSetting field_10195;
		private OceanMonumentPieces.PieceSetting field_10196;
		private final List<OceanMonumentPieces.AbstractPiece> field_10197 = Lists.newArrayList();

		public MainBuilding() {
		}

		public MainBuilding(Random random, int i, int j, Direction direction) {
			super(0);
			this.method_11853(direction);
			Direction direction2 = this.method_11854();
			if (direction2.getAxis() == Direction.Axis.Z) {
				this.boundingBox = new BlockBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
			} else {
				this.boundingBox = new BlockBox(i, 39, j, i + 58 - 1, 61, j + 58 - 1);
			}

			List<OceanMonumentPieces.PieceSetting> list = this.method_9243(random);
			this.field_10195.used = true;
			this.field_10197.add(new OceanMonumentPieces.Entrance(direction2, this.field_10195));
			this.field_10197.add(new OceanMonumentPieces.CoreRoom(direction2, this.field_10196, random));
			List<OceanMonumentPieces.Factory> list2 = Lists.newArrayList();
			list2.add(new OceanMonumentPieces.DoubleXYRoomFactory());
			list2.add(new OceanMonumentPieces.DoubleYZRoomFactory());
			list2.add(new OceanMonumentPieces.DoubleZRoomFactory());
			list2.add(new OceanMonumentPieces.DoubleXRoomFactory());
			list2.add(new OceanMonumentPieces.DoubleYRoomFactory());
			list2.add(new OceanMonumentPieces.SimpleTopRoomFactory());
			list2.add(new OceanMonumentPieces.SimpleRoomFactory());

			for (OceanMonumentPieces.PieceSetting pieceSetting : list) {
				if (!pieceSetting.used && !pieceSetting.isAboveLevelThree()) {
					for (OceanMonumentPieces.Factory factory : list2) {
						if (factory.canGenerate(pieceSetting)) {
							this.field_10197.add(factory.generate(direction2, pieceSetting, random));
							break;
						}
					}
				}
			}

			int k = this.boundingBox.minY;
			int l = this.applyXTransform(9, 22);
			int m = this.applyZTransform(9, 22);

			for (OceanMonumentPieces.AbstractPiece abstractPiece : this.field_10197) {
				abstractPiece.getBoundingBox().move(l, k, m);
			}

			BlockBox blockBox = BlockBox.create(
				this.applyXTransform(1, 1),
				this.applyYTransform(1),
				this.applyZTransform(1, 1),
				this.applyXTransform(23, 21),
				this.applyYTransform(8),
				this.applyZTransform(23, 21)
			);
			BlockBox blockBox2 = BlockBox.create(
				this.applyXTransform(34, 1),
				this.applyYTransform(1),
				this.applyZTransform(34, 1),
				this.applyXTransform(56, 21),
				this.applyYTransform(8),
				this.applyZTransform(56, 21)
			);
			BlockBox blockBox3 = BlockBox.create(
				this.applyXTransform(22, 22),
				this.applyYTransform(13),
				this.applyZTransform(22, 22),
				this.applyXTransform(35, 35),
				this.applyYTransform(17),
				this.applyZTransform(35, 35)
			);
			int n = random.nextInt();
			this.field_10197.add(new OceanMonumentPieces.WingRoom(direction2, blockBox, n++));
			this.field_10197.add(new OceanMonumentPieces.WingRoom(direction2, blockBox2, n++));
			this.field_10197.add(new OceanMonumentPieces.Penthouse(direction2, blockBox3));
		}

		private List<OceanMonumentPieces.PieceSetting> method_9243(Random random) {
			OceanMonumentPieces.PieceSetting[] pieceSettings = new OceanMonumentPieces.PieceSetting[75];

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 4; j++) {
					int k = 0;
					int l = getIndex(i, 0, j);
					pieceSettings[l] = new OceanMonumentPieces.PieceSetting(l);
				}
			}

			for (int m = 0; m < 5; m++) {
				for (int n = 0; n < 4; n++) {
					int o = 1;
					int p = getIndex(m, 1, n);
					pieceSettings[p] = new OceanMonumentPieces.PieceSetting(p);
				}
			}

			for (int q = 1; q < 4; q++) {
				for (int r = 0; r < 2; r++) {
					int s = 2;
					int t = getIndex(q, 2, r);
					pieceSettings[t] = new OceanMonumentPieces.PieceSetting(t);
				}
			}

			this.field_10195 = pieceSettings[TWO_ZERO_ZERO_INDEX];

			for (int u = 0; u < 5; u++) {
				for (int v = 0; v < 5; v++) {
					for (int w = 0; w < 3; w++) {
						int x = getIndex(u, w, v);
						if (pieceSettings[x] != null) {
							for (Direction direction : Direction.values()) {
								int aa = u + direction.getOffsetX();
								int ab = w + direction.getOffsetY();
								int ac = v + direction.getOffsetZ();
								if (aa >= 0 && aa < 5 && ac >= 0 && ac < 5 && ab >= 0 && ab < 3) {
									int ad = getIndex(aa, ab, ac);
									if (pieceSettings[ad] != null) {
										if (ac == v) {
											pieceSettings[x].setNeighbor(direction, pieceSettings[ad]);
										} else {
											pieceSettings[x].setNeighbor(direction.getOpposite(), pieceSettings[ad]);
										}
									}
								}
							}
						}
					}
				}
			}

			OceanMonumentPieces.PieceSetting pieceSetting = new OceanMonumentPieces.PieceSetting(1003);
			OceanMonumentPieces.PieceSetting pieceSetting2 = new OceanMonumentPieces.PieceSetting(1001);
			OceanMonumentPieces.PieceSetting pieceSetting3 = new OceanMonumentPieces.PieceSetting(1002);
			pieceSettings[TWO_TWO_ZERO_INDEX].setNeighbor(Direction.UP, pieceSetting);
			pieceSettings[ZERO_ONE_ZERO_INDEX].setNeighbor(Direction.SOUTH, pieceSetting2);
			pieceSettings[FOUR_ONE_ZERO_INDEX].setNeighbor(Direction.SOUTH, pieceSetting3);
			pieceSetting.used = true;
			pieceSetting2.used = true;
			pieceSetting3.used = true;
			this.field_10195.field_10215 = true;
			this.field_10196 = pieceSettings[getIndex(random.nextInt(4), 0, 2)];
			this.field_10196.used = true;
			this.field_10196.neighbors[Direction.EAST.getId()].used = true;
			this.field_10196.neighbors[Direction.NORTH.getId()].used = true;
			this.field_10196.neighbors[Direction.EAST.getId()].neighbors[Direction.NORTH.getId()].used = true;
			this.field_10196.neighbors[Direction.UP.getId()].used = true;
			this.field_10196.neighbors[Direction.EAST.getId()].neighbors[Direction.UP.getId()].used = true;
			this.field_10196.neighbors[Direction.NORTH.getId()].neighbors[Direction.UP.getId()].used = true;
			this.field_10196.neighbors[Direction.EAST.getId()].neighbors[Direction.NORTH.getId()].neighbors[Direction.UP.getId()].used = true;
			List<OceanMonumentPieces.PieceSetting> list = Lists.newArrayList();

			for (OceanMonumentPieces.PieceSetting pieceSetting4 : pieceSettings) {
				if (pieceSetting4 != null) {
					pieceSetting4.checkNeighborStates();
					list.add(pieceSetting4);
				}
			}

			pieceSetting.checkNeighborStates();
			Collections.shuffle(list, random);
			int ag = 1;

			for (OceanMonumentPieces.PieceSetting pieceSetting5 : list) {
				int ah = 0;
				int ai = 0;

				while (ah < 2 && ai < 5) {
					ai++;
					int aj = random.nextInt(6);
					if (pieceSetting5.neighborPresences[aj]) {
						int ak = Direction.getById(aj).getOpposite().getId();
						pieceSetting5.neighborPresences[aj] = false;
						pieceSetting5.neighbors[aj].neighborPresences[ak] = false;
						if (pieceSetting5.method_9260(ag++) && pieceSetting5.neighbors[aj].method_9260(ag++)) {
							ah++;
						} else {
							pieceSetting5.neighborPresences[aj] = true;
							pieceSetting5.neighbors[aj].neighborPresences[ak] = true;
						}
					}
				}
			}

			list.add(pieceSetting);
			list.add(pieceSetting2);
			list.add(pieceSetting3);
			return list;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			int i = Math.max(world.getSeaLevel(), 64) - this.boundingBox.minY;
			this.setAirAndWater(world, boundingBox, 0, 0, 0, 58, i, 58, false);
			this.method_9244(false, 0, world, random, boundingBox);
			this.method_9244(true, 33, world, random, boundingBox);
			this.method_9245(world, random, boundingBox);
			this.method_9246(world, random, boundingBox);
			this.method_9247(world, random, boundingBox);
			this.method_9248(world, random, boundingBox);
			this.method_9249(world, random, boundingBox);
			this.method_9250(world, random, boundingBox);

			for (int j = 0; j < 7; j++) {
				int k = 0;

				while (k < 7) {
					if (k == 0 && j == 3) {
						k = 6;
					}

					int l = j * 9;
					int m = k * 9;

					for (int n = 0; n < 4; n++) {
						for (int o = 0; o < 4; o++) {
							this.setBlockState(world, PRISMARINE_BRICKS, l + n, 0, m + o, boundingBox);
							this.fillAirAndLiquidsDownwards(world, PRISMARINE_BRICKS, l + n, -1, m + o, boundingBox);
						}
					}

					if (j != 0 && j != 6) {
						k += 6;
					} else {
						k++;
					}
				}
			}

			for (int p = 0; p < 5; p++) {
				this.setAirAndWater(world, boundingBox, -1 - p, 0 + p * 2, -1 - p, -1 - p, 23, 58 + p, false);
				this.setAirAndWater(world, boundingBox, 58 + p, 0 + p * 2, -1 - p, 58 + p, 23, 58 + p, false);
				this.setAirAndWater(world, boundingBox, 0 - p, 0 + p * 2, -1 - p, 57 + p, 23, -1 - p, false);
				this.setAirAndWater(world, boundingBox, 0 - p, 0 + p * 2, 58 + p, 57 + p, 23, 58 + p, false);
			}

			for (OceanMonumentPieces.AbstractPiece abstractPiece : this.field_10197) {
				if (abstractPiece.getBoundingBox().intersects(boundingBox)) {
					abstractPiece.generate(world, random, boundingBox);
				}
			}

			return true;
		}

		private void method_9244(boolean bl, int i, World world, Random random, BlockBox boundingBox) {
			int j = 24;
			if (this.method_9257(boundingBox, i, 0, i + 23, 20)) {
				this.fillWithOutline(world, boundingBox, i + 0, 0, 0, i + 24, 0, 20, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, i + 0, 1, 0, i + 24, 10, 20, false);

				for (int k = 0; k < 4; k++) {
					this.fillWithOutline(world, boundingBox, i + k, k + 1, k, i + k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, i + k + 7, k + 5, k + 7, i + k + 7, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, i + 17 - k, k + 5, k + 7, i + 17 - k, k + 5, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, i + 24 - k, k + 1, k, i + 24 - k, k + 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, i + k + 1, k + 1, k, i + 23 - k, k + 1, k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, i + k + 8, k + 5, k + 7, i + 16 - k, k + 5, k + 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(world, boundingBox, i + 4, 4, 4, i + 6, 4, 20, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, i + 7, 4, 4, i + 17, 4, 6, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, i + 18, 4, 4, i + 20, 4, 20, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, i + 11, 8, 11, i + 13, 8, 20, PRISMARINE, PRISMARINE, false);
				this.setBlockState(world, PRISMARINE2, i + 12, 9, 12, boundingBox);
				this.setBlockState(world, PRISMARINE2, i + 12, 9, 15, boundingBox);
				this.setBlockState(world, PRISMARINE2, i + 12, 9, 18, boundingBox);
				int l = i + (bl ? 19 : 5);
				int m = i + (bl ? 5 : 19);

				for (int n = 20; n >= 5; n -= 3) {
					this.setBlockState(world, PRISMARINE2, l, 5, n, boundingBox);
				}

				for (int o = 19; o >= 7; o -= 3) {
					this.setBlockState(world, PRISMARINE2, m, 5, o, boundingBox);
				}

				for (int p = 0; p < 4; p++) {
					int q = bl ? i + (24 - (17 - p * 3)) : i + 17 - p * 3;
					this.setBlockState(world, PRISMARINE2, q, 5, 5, boundingBox);
				}

				this.setBlockState(world, PRISMARINE2, m, 5, 5, boundingBox);
				this.fillWithOutline(world, boundingBox, i + 11, 1, 12, i + 13, 7, 12, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, i + 12, 1, 11, i + 12, 7, 13, PRISMARINE, PRISMARINE, false);
			}
		}

		private void method_9245(World world, Random random, BlockBox boundingBox) {
			if (this.method_9257(boundingBox, 22, 5, 35, 17)) {
				this.setAirAndWater(world, boundingBox, 25, 0, 0, 32, 8, 20, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(world, boundingBox, 24, 2, 5 + i * 4, 24, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 22, 4, 5 + i * 4, 23, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.setBlockState(world, PRISMARINE_BRICKS, 25, 5, 5 + i * 4, boundingBox);
					this.setBlockState(world, PRISMARINE_BRICKS, 26, 6, 5 + i * 4, boundingBox);
					this.setBlockState(world, SEA_LANTERN, 26, 5, 5 + i * 4, boundingBox);
					this.fillWithOutline(world, boundingBox, 33, 2, 5 + i * 4, 33, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 34, 4, 5 + i * 4, 35, 4, 5 + i * 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.setBlockState(world, PRISMARINE_BRICKS, 32, 5, 5 + i * 4, boundingBox);
					this.setBlockState(world, PRISMARINE_BRICKS, 31, 6, 5 + i * 4, boundingBox);
					this.setBlockState(world, SEA_LANTERN, 31, 5, 5 + i * 4, boundingBox);
					this.fillWithOutline(world, boundingBox, 27, 6, 5 + i * 4, 30, 6, 5 + i * 4, PRISMARINE, PRISMARINE, false);
				}
			}
		}

		private void method_9246(World world, Random random, BlockBox boundingBox) {
			if (this.method_9257(boundingBox, 15, 20, 42, 21)) {
				this.fillWithOutline(world, boundingBox, 15, 0, 21, 42, 0, 21, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 26, 1, 21, 31, 3, 21, false);
				this.fillWithOutline(world, boundingBox, 21, 12, 21, 36, 12, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 17, 11, 21, 40, 11, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 16, 10, 21, 41, 10, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 15, 7, 21, 42, 9, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 16, 6, 21, 41, 6, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 17, 5, 21, 40, 5, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 21, 4, 21, 36, 4, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 22, 3, 21, 26, 3, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 31, 3, 21, 35, 3, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 23, 2, 21, 25, 2, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 32, 2, 21, 34, 2, 21, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 28, 4, 20, 29, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.setBlockState(world, PRISMARINE_BRICKS, 27, 3, 21, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 30, 3, 21, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 26, 2, 21, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 31, 2, 21, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 25, 1, 21, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 32, 1, 21, boundingBox);

				for (int i = 0; i < 7; i++) {
					this.setBlockState(world, DARK_PRISMARINE, 28 - i, 6 + i, 21, boundingBox);
					this.setBlockState(world, DARK_PRISMARINE, 29 + i, 6 + i, 21, boundingBox);
				}

				for (int j = 0; j < 4; j++) {
					this.setBlockState(world, DARK_PRISMARINE, 28 - j, 9 + j, 21, boundingBox);
					this.setBlockState(world, DARK_PRISMARINE, 29 + j, 9 + j, 21, boundingBox);
				}

				this.setBlockState(world, DARK_PRISMARINE, 28, 12, 21, boundingBox);
				this.setBlockState(world, DARK_PRISMARINE, 29, 12, 21, boundingBox);

				for (int k = 0; k < 3; k++) {
					this.setBlockState(world, DARK_PRISMARINE, 22 - k * 2, 8, 21, boundingBox);
					this.setBlockState(world, DARK_PRISMARINE, 22 - k * 2, 9, 21, boundingBox);
					this.setBlockState(world, DARK_PRISMARINE, 35 + k * 2, 8, 21, boundingBox);
					this.setBlockState(world, DARK_PRISMARINE, 35 + k * 2, 9, 21, boundingBox);
				}

				this.setAirAndWater(world, boundingBox, 15, 13, 21, 42, 15, 21, false);
				this.setAirAndWater(world, boundingBox, 15, 1, 21, 15, 6, 21, false);
				this.setAirAndWater(world, boundingBox, 16, 1, 21, 16, 5, 21, false);
				this.setAirAndWater(world, boundingBox, 17, 1, 21, 20, 4, 21, false);
				this.setAirAndWater(world, boundingBox, 21, 1, 21, 21, 3, 21, false);
				this.setAirAndWater(world, boundingBox, 22, 1, 21, 22, 2, 21, false);
				this.setAirAndWater(world, boundingBox, 23, 1, 21, 24, 1, 21, false);
				this.setAirAndWater(world, boundingBox, 42, 1, 21, 42, 6, 21, false);
				this.setAirAndWater(world, boundingBox, 41, 1, 21, 41, 5, 21, false);
				this.setAirAndWater(world, boundingBox, 37, 1, 21, 40, 4, 21, false);
				this.setAirAndWater(world, boundingBox, 36, 1, 21, 36, 3, 21, false);
				this.setAirAndWater(world, boundingBox, 33, 1, 21, 34, 1, 21, false);
				this.setAirAndWater(world, boundingBox, 35, 1, 21, 35, 2, 21, false);
			}
		}

		private void method_9247(World world, Random random, BlockBox boundingBox) {
			if (this.method_9257(boundingBox, 21, 21, 36, 36)) {
				this.fillWithOutline(world, boundingBox, 21, 0, 22, 36, 0, 36, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 21, 1, 22, 36, 23, 36, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(world, boundingBox, 21 + i, 13 + i, 21 + i, 36 - i, 13 + i, 21 + i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 21 + i, 13 + i, 36 - i, 36 - i, 13 + i, 36 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 21 + i, 13 + i, 22 + i, 21 + i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 36 - i, 13 + i, 22 + i, 36 - i, 13 + i, 35 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(world, boundingBox, 25, 16, 25, 32, 16, 32, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 25, 17, 25, 25, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 32, 17, 25, 32, 19, 25, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 25, 17, 32, 25, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 32, 17, 32, 32, 19, 32, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.setBlockState(world, PRISMARINE_BRICKS, 26, 20, 26, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 27, 21, 27, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 27, 20, 27, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 26, 20, 31, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 27, 21, 30, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 27, 20, 30, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 31, 20, 31, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 30, 21, 30, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 30, 20, 30, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 31, 20, 26, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 30, 21, 27, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 30, 20, 27, boundingBox);
				this.fillWithOutline(world, boundingBox, 28, 21, 27, 29, 21, 27, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 27, 21, 28, 27, 21, 29, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 28, 21, 30, 29, 21, 30, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 30, 21, 28, 30, 21, 29, PRISMARINE, PRISMARINE, false);
			}
		}

		private void method_9248(World world, Random random, BlockBox boundingBox) {
			if (this.method_9257(boundingBox, 0, 21, 6, 58)) {
				this.fillWithOutline(world, boundingBox, 0, 0, 21, 6, 0, 57, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 0, 1, 21, 6, 7, 57, false);
				this.fillWithOutline(world, boundingBox, 4, 4, 21, 6, 4, 53, PRISMARINE, PRISMARINE, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(world, boundingBox, i, i + 1, 21, i, i + 1, 57 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int j = 23; j < 53; j += 3) {
					this.setBlockState(world, PRISMARINE2, 5, 5, j, boundingBox);
				}

				this.setBlockState(world, PRISMARINE2, 5, 5, 52, boundingBox);

				for (int k = 0; k < 4; k++) {
					this.fillWithOutline(world, boundingBox, k, k + 1, 21, k, k + 1, 57 - k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(world, boundingBox, 4, 1, 52, 6, 3, 52, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 5, 1, 51, 5, 3, 53, PRISMARINE, PRISMARINE, false);
			}

			if (this.method_9257(boundingBox, 51, 21, 58, 58)) {
				this.fillWithOutline(world, boundingBox, 51, 0, 21, 57, 0, 57, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 51, 1, 21, 57, 7, 57, false);
				this.fillWithOutline(world, boundingBox, 51, 4, 21, 53, 4, 53, PRISMARINE, PRISMARINE, false);

				for (int l = 0; l < 4; l++) {
					this.fillWithOutline(world, boundingBox, 57 - l, l + 1, 21, 57 - l, l + 1, 57 - l, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int m = 23; m < 53; m += 3) {
					this.setBlockState(world, PRISMARINE2, 52, 5, m, boundingBox);
				}

				this.setBlockState(world, PRISMARINE2, 52, 5, 52, boundingBox);
				this.fillWithOutline(world, boundingBox, 51, 1, 52, 53, 3, 52, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 52, 1, 51, 52, 3, 53, PRISMARINE, PRISMARINE, false);
			}

			if (this.method_9257(boundingBox, 0, 51, 57, 57)) {
				this.fillWithOutline(world, boundingBox, 7, 0, 51, 50, 0, 57, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 7, 1, 51, 50, 10, 57, false);

				for (int n = 0; n < 4; n++) {
					this.fillWithOutline(world, boundingBox, n + 1, n + 1, 57 - n, 56 - n, n + 1, 57 - n, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}
			}
		}

		private void method_9249(World world, Random random, BlockBox boundingBox) {
			if (this.method_9257(boundingBox, 7, 21, 13, 50)) {
				this.fillWithOutline(world, boundingBox, 7, 0, 21, 13, 0, 50, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 7, 1, 21, 13, 10, 50, false);
				this.fillWithOutline(world, boundingBox, 11, 8, 21, 13, 8, 53, PRISMARINE, PRISMARINE, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(world, boundingBox, i + 7, i + 5, 21, i + 7, i + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int j = 21; j <= 45; j += 3) {
					this.setBlockState(world, PRISMARINE2, 12, 9, j, boundingBox);
				}
			}

			if (this.method_9257(boundingBox, 44, 21, 50, 54)) {
				this.fillWithOutline(world, boundingBox, 44, 0, 21, 50, 0, 50, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 44, 1, 21, 50, 10, 50, false);
				this.fillWithOutline(world, boundingBox, 44, 8, 21, 46, 8, 53, PRISMARINE, PRISMARINE, false);

				for (int k = 0; k < 4; k++) {
					this.fillWithOutline(world, boundingBox, 50 - k, k + 5, 21, 50 - k, k + 5, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int l = 21; l <= 45; l += 3) {
					this.setBlockState(world, PRISMARINE2, 45, 9, l, boundingBox);
				}
			}

			if (this.method_9257(boundingBox, 8, 44, 49, 54)) {
				this.fillWithOutline(world, boundingBox, 14, 0, 44, 43, 0, 50, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 14, 1, 44, 43, 10, 50, false);

				for (int m = 12; m <= 45; m += 3) {
					this.setBlockState(world, PRISMARINE2, m, 9, 45, boundingBox);
					this.setBlockState(world, PRISMARINE2, m, 9, 52, boundingBox);
					if (m == 12 || m == 18 || m == 24 || m == 33 || m == 39 || m == 45) {
						this.setBlockState(world, PRISMARINE2, m, 9, 47, boundingBox);
						this.setBlockState(world, PRISMARINE2, m, 9, 50, boundingBox);
						this.setBlockState(world, PRISMARINE2, m, 10, 45, boundingBox);
						this.setBlockState(world, PRISMARINE2, m, 10, 46, boundingBox);
						this.setBlockState(world, PRISMARINE2, m, 10, 51, boundingBox);
						this.setBlockState(world, PRISMARINE2, m, 10, 52, boundingBox);
						this.setBlockState(world, PRISMARINE2, m, 11, 47, boundingBox);
						this.setBlockState(world, PRISMARINE2, m, 11, 50, boundingBox);
						this.setBlockState(world, PRISMARINE2, m, 12, 48, boundingBox);
						this.setBlockState(world, PRISMARINE2, m, 12, 49, boundingBox);
					}
				}

				for (int n = 0; n < 3; n++) {
					this.fillWithOutline(world, boundingBox, 8 + n, 5 + n, 54, 49 - n, 5 + n, 54, PRISMARINE, PRISMARINE, false);
				}

				this.fillWithOutline(world, boundingBox, 11, 8, 54, 46, 8, 54, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 14, 8, 44, 43, 8, 53, PRISMARINE, PRISMARINE, false);
			}
		}

		private void method_9250(World world, Random random, BlockBox boundingBox) {
			if (this.method_9257(boundingBox, 14, 21, 20, 43)) {
				this.fillWithOutline(world, boundingBox, 14, 0, 21, 20, 0, 43, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 14, 1, 22, 20, 14, 43, false);
				this.fillWithOutline(world, boundingBox, 18, 12, 22, 20, 12, 39, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 18, 12, 21, 20, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(world, boundingBox, i + 14, i + 9, 21, i + 14, i + 9, 43 - i, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int j = 23; j <= 39; j += 3) {
					this.setBlockState(world, PRISMARINE2, 19, 13, j, boundingBox);
				}
			}

			if (this.method_9257(boundingBox, 37, 21, 43, 43)) {
				this.fillWithOutline(world, boundingBox, 37, 0, 21, 43, 0, 43, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 37, 1, 22, 43, 14, 43, false);
				this.fillWithOutline(world, boundingBox, 37, 12, 22, 39, 12, 39, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 37, 12, 21, 39, 12, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

				for (int k = 0; k < 4; k++) {
					this.fillWithOutline(world, boundingBox, 43 - k, k + 9, 21, 43 - k, k + 9, 43 - k, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int l = 23; l <= 39; l += 3) {
					this.setBlockState(world, PRISMARINE2, 38, 13, l, boundingBox);
				}
			}

			if (this.method_9257(boundingBox, 15, 37, 42, 43)) {
				this.fillWithOutline(world, boundingBox, 21, 0, 37, 36, 0, 43, PRISMARINE, PRISMARINE, false);
				this.setAirAndWater(world, boundingBox, 21, 1, 37, 36, 14, 43, false);
				this.fillWithOutline(world, boundingBox, 21, 12, 37, 36, 12, 39, PRISMARINE, PRISMARINE, false);

				for (int m = 0; m < 4; m++) {
					this.fillWithOutline(world, boundingBox, 15 + m, m + 9, 43 - m, 42 - m, m + 9, 43 - m, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				for (int n = 21; n <= 36; n += 3) {
					this.setBlockState(world, PRISMARINE2, n, 13, 38, boundingBox);
				}
			}
		}
	}

	public static class Penthouse extends OceanMonumentPieces.AbstractPiece {
		public Penthouse() {
		}

		public Penthouse(Direction direction, BlockBox blockBox) {
			super(direction, blockBox);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			this.fillWithOutline(world, boundingBox, 2, -1, 2, 11, -1, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, -1, 0, 1, -1, 11, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 12, -1, 0, 13, -1, 11, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 2, -1, 0, 11, -1, 1, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 2, -1, 12, 11, -1, 13, PRISMARINE, PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 0, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 13, 0, 0, 13, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 0, 0, 12, 0, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 0, 13, 12, 0, 13, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);

			for (int i = 2; i <= 11; i += 3) {
				this.setBlockState(world, SEA_LANTERN, 0, 0, i, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 13, 0, i, boundingBox);
				this.setBlockState(world, SEA_LANTERN, i, 0, 0, boundingBox);
			}

			this.fillWithOutline(world, boundingBox, 2, 0, 3, 4, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 9, 0, 3, 11, 0, 9, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 4, 0, 9, 9, 0, 11, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.setBlockState(world, PRISMARINE_BRICKS, 5, 0, 8, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 8, 0, 8, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 10, 0, 10, boundingBox);
			this.setBlockState(world, PRISMARINE_BRICKS, 3, 0, 10, boundingBox);
			this.fillWithOutline(world, boundingBox, 3, 0, 3, 3, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 10, 0, 3, 10, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 6, 0, 10, 7, 0, 10, DARK_PRISMARINE, DARK_PRISMARINE, false);
			int j = 3;

			for (int k = 0; k < 2; k++) {
				for (int l = 2; l <= 8; l += 3) {
					this.fillWithOutline(world, boundingBox, j, 0, l, j, 2, l, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				j = 10;
			}

			this.fillWithOutline(world, boundingBox, 5, 0, 10, 5, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 8, 0, 10, 8, 2, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 6, -1, 7, 7, -1, 8, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.setAirAndWater(world, boundingBox, 6, -1, 3, 7, -1, 4, false);
			this.method_9253(world, boundingBox, 6, 1, 6);
			return true;
		}
	}

	static class PieceSetting {
		int roomIndex;
		OceanMonumentPieces.PieceSetting[] neighbors = new OceanMonumentPieces.PieceSetting[6];
		boolean[] neighborPresences = new boolean[6];
		boolean used;
		boolean field_10215;
		int field_10216;

		public PieceSetting(int i) {
			this.roomIndex = i;
		}

		public void setNeighbor(Direction orientation, OceanMonumentPieces.PieceSetting settings) {
			this.neighbors[orientation.getId()] = settings;
			settings.neighbors[orientation.getOpposite().getId()] = this;
		}

		public void checkNeighborStates() {
			for (int i = 0; i < 6; i++) {
				this.neighborPresences[i] = this.neighbors[i] != null;
			}
		}

		public boolean method_9260(int i) {
			if (this.field_10215) {
				return true;
			} else {
				this.field_10216 = i;

				for (int j = 0; j < 6; j++) {
					if (this.neighbors[j] != null && this.neighborPresences[j] && this.neighbors[j].field_10216 != i && this.neighbors[j].method_9260(i)) {
						return true;
					}
				}

				return false;
			}
		}

		public boolean isAboveLevelThree() {
			return this.roomIndex >= 75;
		}

		public int countNeighbors() {
			int i = 0;

			for (int j = 0; j < 6; j++) {
				if (this.neighborPresences[j]) {
					i++;
				}
			}

			return i;
		}
	}

	public static class SimpleRoom extends OceanMonumentPieces.AbstractPiece {
		private int roomType;

		public SimpleRoom() {
		}

		public SimpleRoom(Direction direction, OceanMonumentPieces.PieceSetting pieceSetting, Random random) {
			super(1, direction, pieceSetting, 1, 1, 1);
			this.roomType = random.nextInt(3);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.setting.roomIndex / 25 > 0) {
				this.method_9256(world, boundingBox, 0, 0, this.setting.neighborPresences[Direction.DOWN.getId()]);
			}

			if (this.setting.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 1, 4, 1, 6, 4, 6, PRISMARINE);
			}

			boolean bl = this.roomType != 0
				&& random.nextBoolean()
				&& !this.setting.neighborPresences[Direction.DOWN.getId()]
				&& !this.setting.neighborPresences[Direction.UP.getId()]
				&& this.setting.countNeighbors() > 1;
			if (this.roomType == 0) {
				this.fillWithOutline(world, boundingBox, 0, 1, 0, 2, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 3, 0, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 2, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 1, 2, 0, 2, 2, 0, PRISMARINE, PRISMARINE, false);
				this.setBlockState(world, SEA_LANTERN, 1, 2, 1, boundingBox);
				this.fillWithOutline(world, boundingBox, 5, 1, 0, 7, 1, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 5, 3, 0, 7, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 2, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 5, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
				this.setBlockState(world, SEA_LANTERN, 6, 2, 1, boundingBox);
				this.fillWithOutline(world, boundingBox, 0, 1, 5, 2, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 3, 5, 2, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 2, 5, 0, 2, 7, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 1, 2, 7, 2, 2, 7, PRISMARINE, PRISMARINE, false);
				this.setBlockState(world, SEA_LANTERN, 1, 2, 6, boundingBox);
				this.fillWithOutline(world, boundingBox, 5, 1, 5, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 5, 3, 5, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 2, 5, 7, 2, 7, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 5, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
				this.setBlockState(world, SEA_LANTERN, 6, 2, 6, boundingBox);
				if (this.setting.neighborPresences[Direction.SOUTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 3, 3, 0, 4, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 3, 3, 0, 4, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 3, 2, 0, 4, 2, 0, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 3, 1, 0, 4, 1, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (this.setting.neighborPresences[Direction.NORTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 3, 3, 7, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 3, 3, 6, 4, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 3, 2, 7, 4, 2, 7, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 3, 1, 6, 4, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (this.setting.neighborPresences[Direction.WEST.getId()]) {
					this.fillWithOutline(world, boundingBox, 0, 3, 3, 0, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 0, 3, 3, 1, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 0, 2, 3, 0, 2, 4, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 0, 1, 3, 1, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (this.setting.neighborPresences[Direction.EAST.getId()]) {
					this.fillWithOutline(world, boundingBox, 7, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				} else {
					this.fillWithOutline(world, boundingBox, 6, 3, 3, 7, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 7, 2, 3, 7, 2, 4, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 6, 1, 3, 7, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}
			} else if (this.roomType == 1) {
				this.fillWithOutline(world, boundingBox, 2, 1, 2, 2, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 2, 1, 5, 2, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 5, 1, 5, 5, 3, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 5, 1, 2, 5, 3, 2, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.setBlockState(world, SEA_LANTERN, 2, 2, 2, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 2, 2, 5, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 5, 2, 5, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 5, 2, 2, boundingBox);
				this.fillWithOutline(world, boundingBox, 0, 1, 0, 1, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 1, 7, 1, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 1, 6, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 7, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 6, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 1, 0, 7, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 1, 7, 3, 1, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.setBlockState(world, PRISMARINE, 1, 2, 0, boundingBox);
				this.setBlockState(world, PRISMARINE, 0, 2, 1, boundingBox);
				this.setBlockState(world, PRISMARINE, 1, 2, 7, boundingBox);
				this.setBlockState(world, PRISMARINE, 0, 2, 6, boundingBox);
				this.setBlockState(world, PRISMARINE, 6, 2, 7, boundingBox);
				this.setBlockState(world, PRISMARINE, 7, 2, 6, boundingBox);
				this.setBlockState(world, PRISMARINE, 6, 2, 0, boundingBox);
				this.setBlockState(world, PRISMARINE, 7, 2, 1, boundingBox);
				if (!this.setting.neighborPresences[Direction.SOUTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 1, 2, 0, 6, 2, 0, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (!this.setting.neighborPresences[Direction.NORTH.getId()]) {
					this.fillWithOutline(world, boundingBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 1, 2, 7, 6, 2, 7, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (!this.setting.neighborPresences[Direction.WEST.getId()]) {
					this.fillWithOutline(world, boundingBox, 0, 3, 1, 0, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 0, 2, 1, 0, 2, 6, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 0, 1, 1, 0, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				if (!this.setting.neighborPresences[Direction.EAST.getId()]) {
					this.fillWithOutline(world, boundingBox, 7, 3, 1, 7, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, 7, 2, 1, 7, 2, 6, PRISMARINE, PRISMARINE, false);
					this.fillWithOutline(world, boundingBox, 7, 1, 1, 7, 1, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}
			} else if (this.roomType == 2) {
				this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				if (this.setting.neighborPresences[Direction.SOUTH.getId()]) {
					this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0, false);
				}

				if (this.setting.neighborPresences[Direction.NORTH.getId()]) {
					this.setAirAndWater(world, boundingBox, 3, 1, 7, 4, 2, 7, false);
				}

				if (this.setting.neighborPresences[Direction.WEST.getId()]) {
					this.setAirAndWater(world, boundingBox, 0, 1, 3, 0, 2, 4, false);
				}

				if (this.setting.neighborPresences[Direction.EAST.getId()]) {
					this.setAirAndWater(world, boundingBox, 7, 1, 3, 7, 2, 4, false);
				}
			}

			if (bl) {
				this.fillWithOutline(world, boundingBox, 3, 1, 3, 4, 1, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 3, 2, 3, 4, 2, 4, PRISMARINE, PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 3, 3, 3, 4, 3, 4, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			}

			return true;
		}
	}

	static class SimpleRoomFactory implements OceanMonumentPieces.Factory {
		private SimpleRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentPieces.PieceSetting settings) {
			return true;
		}

		@Override
		public OceanMonumentPieces.AbstractPiece generate(Direction orientation, OceanMonumentPieces.PieceSetting settings, Random random) {
			settings.used = true;
			return new OceanMonumentPieces.SimpleRoom(orientation, settings, random);
		}
	}

	public static class SimpleTopRoom extends OceanMonumentPieces.AbstractPiece {
		public SimpleTopRoom() {
		}

		public SimpleTopRoom(Direction direction, OceanMonumentPieces.PieceSetting pieceSetting, Random random) {
			super(1, direction, pieceSetting, 1, 1, 1);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.setting.roomIndex / 25 > 0) {
				this.method_9256(world, boundingBox, 0, 0, this.setting.neighborPresences[Direction.DOWN.getId()]);
			}

			if (this.setting.neighbors[Direction.UP.getId()] == null) {
				this.method_9254(world, boundingBox, 1, 4, 1, 6, 4, 6, PRISMARINE);
			}

			for (int i = 1; i <= 6; i++) {
				for (int j = 1; j <= 6; j++) {
					if (random.nextInt(3) != 0) {
						int k = 2 + (random.nextInt(4) == 0 ? 0 : 1);
						this.fillWithOutline(world, boundingBox, i, k, j, i, 3, j, Blocks.SPONGE.stateFromData(1), Blocks.SPONGE.stateFromData(1), false);
					}
				}
			}

			this.fillWithOutline(world, boundingBox, 0, 1, 0, 0, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 1, 0, 7, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 0, 6, 1, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 1, 7, 6, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 2, 0, 0, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 7, 2, 0, 7, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 0, 6, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 1, 2, 7, 6, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 0, 3, 0, 0, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 7, 3, 0, 7, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 0, 6, 3, 0, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 1, 3, 7, 6, 3, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
			this.fillWithOutline(world, boundingBox, 0, 1, 3, 0, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 7, 1, 3, 7, 2, 4, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 0, 4, 2, 0, DARK_PRISMARINE, DARK_PRISMARINE, false);
			this.fillWithOutline(world, boundingBox, 3, 1, 7, 4, 2, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
			if (this.setting.neighborPresences[Direction.SOUTH.getId()]) {
				this.setAirAndWater(world, boundingBox, 3, 1, 0, 4, 2, 0, false);
			}

			return true;
		}
	}

	static class SimpleTopRoomFactory implements OceanMonumentPieces.Factory {
		private SimpleTopRoomFactory() {
		}

		@Override
		public boolean canGenerate(OceanMonumentPieces.PieceSetting settings) {
			return !settings.neighborPresences[Direction.WEST.getId()]
				&& !settings.neighborPresences[Direction.EAST.getId()]
				&& !settings.neighborPresences[Direction.NORTH.getId()]
				&& !settings.neighborPresences[Direction.SOUTH.getId()]
				&& !settings.neighborPresences[Direction.UP.getId()];
		}

		@Override
		public OceanMonumentPieces.AbstractPiece generate(Direction orientation, OceanMonumentPieces.PieceSetting settings, Random random) {
			settings.used = true;
			return new OceanMonumentPieces.SimpleTopRoom(orientation, settings, random);
		}
	}

	public static class WingRoom extends OceanMonumentPieces.AbstractPiece {
		private int field_10210;

		public WingRoom() {
		}

		public WingRoom(Direction direction, BlockBox blockBox, int i) {
			super(direction, blockBox);
			this.field_10210 = i & 1;
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (this.field_10210 == 0) {
				for (int i = 0; i < 4; i++) {
					this.fillWithOutline(world, boundingBox, 10 - i, 3 - i, 20 - i, 12 + i, 3 - i, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				}

				this.fillWithOutline(world, boundingBox, 7, 0, 6, 15, 0, 16, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 6, 0, 6, 6, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 16, 0, 6, 16, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 7, 7, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 15, 1, 7, 15, 1, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 7, 1, 6, 9, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 13, 1, 6, 15, 3, 6, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 8, 1, 7, 9, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 13, 1, 7, 14, 1, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 9, 0, 5, 13, 0, 5, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 10, 0, 7, 12, 0, 7, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 8, 0, 10, 8, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 14, 0, 10, 14, 0, 12, DARK_PRISMARINE, DARK_PRISMARINE, false);

				for (int j = 18; j >= 7; j -= 3) {
					this.setBlockState(world, SEA_LANTERN, 6, 3, j, boundingBox);
					this.setBlockState(world, SEA_LANTERN, 16, 3, j, boundingBox);
				}

				this.setBlockState(world, SEA_LANTERN, 10, 0, 10, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 12, 0, 10, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 10, 0, 12, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 12, 0, 12, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 8, 3, 6, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 14, 3, 6, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 4, 2, 4, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 4, 1, 4, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 4, 0, 4, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 18, 2, 4, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 18, 1, 4, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 18, 0, 4, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 4, 2, 18, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 4, 1, 18, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 4, 0, 18, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 18, 2, 18, boundingBox);
				this.setBlockState(world, SEA_LANTERN, 18, 1, 18, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 18, 0, 18, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 9, 7, 20, boundingBox);
				this.setBlockState(world, PRISMARINE_BRICKS, 13, 7, 20, boundingBox);
				this.fillWithOutline(world, boundingBox, 6, 0, 21, 7, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 15, 0, 21, 16, 4, 21, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.method_9253(world, boundingBox, 11, 2, 16);
			} else if (this.field_10210 == 1) {
				this.fillWithOutline(world, boundingBox, 9, 3, 18, 13, 3, 20, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 9, 0, 18, 9, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				this.fillWithOutline(world, boundingBox, 13, 0, 18, 13, 2, 18, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				int k = 9;
				int l = 20;
				int m = 5;

				for (int n = 0; n < 2; n++) {
					this.setBlockState(world, PRISMARINE_BRICKS, k, 6, 20, boundingBox);
					this.setBlockState(world, SEA_LANTERN, k, 5, 20, boundingBox);
					this.setBlockState(world, PRISMARINE_BRICKS, k, 4, 20, boundingBox);
					k = 13;
				}

				this.fillWithOutline(world, boundingBox, 7, 3, 7, 15, 3, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
				int var10 = 10;

				for (int o = 0; o < 2; o++) {
					this.fillWithOutline(world, boundingBox, var10, 0, 10, var10, 6, 10, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var10, 0, 12, var10, 6, 12, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.setBlockState(world, SEA_LANTERN, var10, 0, 10, boundingBox);
					this.setBlockState(world, SEA_LANTERN, var10, 0, 12, boundingBox);
					this.setBlockState(world, SEA_LANTERN, var10, 4, 10, boundingBox);
					this.setBlockState(world, SEA_LANTERN, var10, 4, 12, boundingBox);
					var10 = 12;
				}

				var10 = 8;

				for (int p = 0; p < 2; p++) {
					this.fillWithOutline(world, boundingBox, var10, 0, 7, var10, 2, 7, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					this.fillWithOutline(world, boundingBox, var10, 0, 14, var10, 2, 14, PRISMARINE_BRICKS, PRISMARINE_BRICKS, false);
					var10 = 14;
				}

				this.fillWithOutline(world, boundingBox, 8, 3, 8, 8, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.fillWithOutline(world, boundingBox, 14, 3, 8, 14, 3, 13, DARK_PRISMARINE, DARK_PRISMARINE, false);
				this.method_9253(world, boundingBox, 11, 5, 13);
			}

			return true;
		}
	}
}
