package net.minecraft.structure;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.StoneBrickBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TemplePieces {
	public static void registerPieces() {
		StructurePieceManager.registerPiece(TemplePieces.DesertPyramid.class, "TeDP");
		StructurePieceManager.registerPiece(TemplePieces.JunglePyramid.class, "TeJP");
		StructurePieceManager.registerPiece(TemplePieces.SwampHut.class, "TeSH");
		StructurePieceManager.registerPiece(TemplePieces.class_2761.class, "Iglu");
	}

	abstract static class AbstractPiece extends StructurePiece {
		protected int width;
		protected int height;
		protected int depth;
		protected int hPos = -1;

		public AbstractPiece() {
		}

		protected AbstractPiece(Random random, int i, int j, int k, int l, int m, int n) {
			super(0);
			this.width = l;
			this.height = m;
			this.depth = n;
			this.method_11853(Direction.DirectionType.HORIZONTAL.getRandomDirection(random));
			if (this.method_11854().getAxis() == Direction.Axis.Z) {
				this.boundingBox = new BlockBox(i, j, k, i + l - 1, j + m - 1, k + n - 1);
			} else {
				this.boundingBox = new BlockBox(i, j, k, i + n - 1, j + m - 1, k + l - 1);
			}
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			structureNbt.putInt("Width", this.width);
			structureNbt.putInt("Height", this.height);
			structureNbt.putInt("Depth", this.depth);
			structureNbt.putInt("HPos", this.hPos);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			this.width = structureNbt.getInt("Width");
			this.height = structureNbt.getInt("Height");
			this.depth = structureNbt.getInt("Depth");
			this.hPos = structureNbt.getInt("HPos");
		}

		protected boolean adjustToAverageHeight(World world, BlockBox boundingBox, int deltaY) {
			if (this.hPos >= 0) {
				return true;
			} else {
				int i = 0;
				int j = 0;
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; k++) {
					for (int l = this.boundingBox.minX; l <= this.boundingBox.maxX; l++) {
						mutable.setPosition(l, 64, k);
						if (boundingBox.contains(mutable)) {
							i += Math.max(world.getTopPosition(mutable).getY(), world.dimension.getAverageYLevel());
							j++;
						}
					}
				}

				if (j == 0) {
					return false;
				} else {
					this.hPos = i / j;
					this.boundingBox.move(0, this.hPos - this.boundingBox.minY + deltaY, 0);
					return true;
				}
			}
		}
	}

	public static class DesertPyramid extends TemplePieces.AbstractPiece {
		private final boolean[] chestsPlaced = new boolean[4];

		public DesertPyramid() {
		}

		public DesertPyramid(Random random, int i, int j) {
			super(random, i, 64, j, 21, 15, 21);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("hasPlacedChest0", this.chestsPlaced[0]);
			structureNbt.putBoolean("hasPlacedChest1", this.chestsPlaced[1]);
			structureNbt.putBoolean("hasPlacedChest2", this.chestsPlaced[2]);
			structureNbt.putBoolean("hasPlacedChest3", this.chestsPlaced[3]);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.chestsPlaced[0] = structureNbt.getBoolean("hasPlacedChest0");
			this.chestsPlaced[1] = structureNbt.getBoolean("hasPlacedChest1");
			this.chestsPlaced[2] = structureNbt.getBoolean("hasPlacedChest2");
			this.chestsPlaced[3] = structureNbt.getBoolean("hasPlacedChest3");
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			this.fillWithOutline(
				world, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);

			for (int i = 1; i <= 9; i++) {
				this.fillWithOutline(
					world, boundingBox, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
				);
				this.fillWithOutline(
					world, boundingBox, i + 1, i, i + 1, this.width - 2 - i, i, this.depth - 2 - i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false
				);
			}

			for (int j = 0; j < this.width; j++) {
				for (int k = 0; k < this.depth; k++) {
					int l = -5;
					this.fillAirAndLiquidsDownwards(world, Blocks.SANDSTONE.getDefaultState(), j, -5, k, boundingBox);
				}
			}

			BlockState blockState = Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
			BlockState blockState2 = Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
			BlockState blockState3 = Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
			BlockState blockState4 = Blocks.SANDSTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
			int m = ~DyeColor.ORANGE.getSwappedId() & 15;
			int n = ~DyeColor.BLUE.getSwappedId() & 15;
			this.fillWithOutline(world, boundingBox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.setBlockState(world, blockState, 2, 10, 0, boundingBox);
			this.setBlockState(world, blockState2, 2, 10, 4, boundingBox);
			this.setBlockState(world, blockState3, 0, 10, 2, boundingBox);
			this.setBlockState(world, blockState4, 4, 10, 2, boundingBox);
			this.fillWithOutline(world, boundingBox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.setBlockState(world, blockState, this.width - 3, 10, 0, boundingBox);
			this.setBlockState(world, blockState2, this.width - 3, 10, 4, boundingBox);
			this.setBlockState(world, blockState3, this.width - 5, 10, 2, boundingBox);
			this.setBlockState(world, blockState4, this.width - 1, 10, 2, boundingBox);
			this.fillWithOutline(world, boundingBox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 9, 1, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 9, 2, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 9, 3, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 10, 3, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 11, 3, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 11, 2, 1, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 11, 1, 1, boundingBox);
			this.fillWithOutline(world, boundingBox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.fillWithOutline(world, boundingBox, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				1,
				8,
				8,
				3,
				8,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				12,
				1,
				8,
				12,
				3,
				8,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				1,
				12,
				8,
				3,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				12,
				1,
				12,
				12,
				3,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(world, boundingBox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.fillWithOutline(world, boundingBox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				5,
				5,
				9,
				5,
				7,
				11,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				this.width - 6,
				5,
				9,
				this.width - 6,
				7,
				11,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 5, 5, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 5, 6, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 6, 6, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), this.width - 6, 5, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), this.width - 6, 6, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), this.width - 7, 6, 10, boundingBox);
			this.fillWithOutline(world, boundingBox, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, blockState, 2, 4, 5, boundingBox);
			this.setBlockState(world, blockState, 2, 3, 4, boundingBox);
			this.setBlockState(world, blockState, this.width - 3, 4, 5, boundingBox);
			this.setBlockState(world, blockState, this.width - 3, 3, 4, boundingBox);
			this.fillWithOutline(world, boundingBox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.setBlockState(world, Blocks.SANDSTONE.getDefaultState(), 1, 1, 2, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.getDefaultState(), this.width - 2, 1, 2, boundingBox);
			this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.SANDSTONE.getId()), 1, 2, 2, boundingBox);
			this.setBlockState(world, Blocks.STONE_SLAB.stateFromData(StoneSlabBlock.SlabType.SANDSTONE.getId()), this.width - 2, 2, 2, boundingBox);
			this.setBlockState(world, blockState4, 2, 1, 2, boundingBox);
			this.setBlockState(world, blockState3, this.width - 3, 1, 2, boundingBox);
			this.fillWithOutline(world, boundingBox, 4, 3, 5, 4, 3, 18, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(
				world, boundingBox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.fillWithOutline(world, boundingBox, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

			for (int o = 5; o <= 17; o += 2) {
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 4, 1, o, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 4, 2, o, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), this.width - 5, 1, o, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), this.width - 5, 2, o, boundingBox);
			}

			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 10, 0, 7, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 10, 0, 8, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 9, 0, 9, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 11, 0, 9, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 8, 0, 10, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 12, 0, 10, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 7, 0, 10, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 13, 0, 10, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 9, 0, 11, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 11, 0, 11, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 10, 0, 12, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 10, 0, 13, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(n), 10, 0, 10, boundingBox);

			for (int p = 0; p <= this.width - 1; p += this.width - 1) {
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), p, 2, 1, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 2, 2, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), p, 2, 3, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), p, 3, 1, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 3, 2, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), p, 3, 3, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 4, 1, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), p, 4, 2, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 4, 3, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), p, 5, 1, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 5, 2, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), p, 5, 3, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 6, 1, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), p, 6, 2, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 6, 3, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 7, 1, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 7, 2, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), p, 7, 3, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), p, 8, 1, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), p, 8, 2, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), p, 8, 3, boundingBox);
			}

			for (int q = 2; q <= this.width - 3; q += this.width - 3 - 2) {
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), q - 1, 2, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q, 2, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), q + 1, 2, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), q - 1, 3, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q, 3, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), q + 1, 3, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q - 1, 4, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), q, 4, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q + 1, 4, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), q - 1, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), q + 1, 5, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q - 1, 6, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), q, 6, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q + 1, 6, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q - 1, 7, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q, 7, 0, boundingBox);
				this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), q + 1, 7, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), q - 1, 8, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), q, 8, 0, boundingBox);
				this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), q + 1, 8, 0, boundingBox);
			}

			this.fillWithOutline(
				world,
				boundingBox,
				8,
				4,
				0,
				12,
				6,
				0,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 8, 6, 0, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 12, 6, 0, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 9, 5, 0, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 10, 5, 0, boundingBox);
			this.setBlockState(world, Blocks.STAINED_TERRACOTTA.stateFromData(m), 11, 5, 0, boundingBox);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				-14,
				8,
				12,
				-11,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				-10,
				8,
				12,
				-10,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()),
				false
			);
			this.fillWithOutline(
				world,
				boundingBox,
				8,
				-9,
				8,
				12,
				-9,
				12,
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()),
				false
			);
			this.fillWithOutline(world, boundingBox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
			this.fillWithOutline(world, boundingBox, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, boundingBox);
			this.fillWithOutline(world, boundingBox, 9, -13, 9, 11, -13, 11, Blocks.TNT.getDefaultState(), Blocks.AIR.getDefaultState(), false);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 8, -11, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 8, -10, 10, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 7, -10, 10, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 7, -11, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 12, -11, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 12, -10, 10, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 13, -10, 10, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 13, -11, 10, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, -11, 8, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, -10, 8, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 10, -10, 7, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 10, -11, 7, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, -11, 12, boundingBox);
			this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, -10, 12, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.CHISELED.getId()), 10, -10, 13, boundingBox);
			this.setBlockState(world, Blocks.SANDSTONE.stateFromData(SandstoneBlock.SandstoneType.SMOOTH.getId()), 10, -11, 13, boundingBox);

			for (Direction direction : Direction.DirectionType.HORIZONTAL) {
				if (!this.chestsPlaced[direction.getHorizontal()]) {
					int r = direction.getOffsetX() * 2;
					int s = direction.getOffsetZ() * 2;
					this.chestsPlaced[direction.getHorizontal()] = this.method_11852(world, boundingBox, random, 10 + r, -11, 10 + s, LootTables.DESERT_PYRAMID_CHEST);
				}
			}

			return true;
		}
	}

	public static class JunglePyramid extends TemplePieces.AbstractPiece {
		private boolean mainChest;
		private boolean hiddenChest;
		private boolean placedTrap1;
		private boolean placedTrap2;
		private static final TemplePieces.JunglePyramid.Randomizer randomizer = new TemplePieces.JunglePyramid.Randomizer();

		public JunglePyramid() {
		}

		public JunglePyramid(Random random, int i, int j) {
			super(random, i, 64, j, 12, 10, 15);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("placedMainChest", this.mainChest);
			structureNbt.putBoolean("placedHiddenChest", this.hiddenChest);
			structureNbt.putBoolean("placedTrap1", this.placedTrap1);
			structureNbt.putBoolean("placedTrap2", this.placedTrap2);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.mainChest = structureNbt.getBoolean("placedMainChest");
			this.hiddenChest = structureNbt.getBoolean("placedHiddenChest");
			this.placedTrap1 = structureNbt.getBoolean("placedTrap1");
			this.placedTrap2 = structureNbt.getBoolean("placedTrap2");
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (!this.adjustToAverageHeight(world, boundingBox, 0)) {
				return false;
			} else {
				this.fillRandomized(world, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 1, 2, 9, 2, 2, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 1, 12, 9, 2, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 1, 3, 2, 2, 11, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 9, 1, 3, 9, 2, 11, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 1, 3, 1, 10, 6, 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 1, 3, 13, 10, 6, 13, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 1, 3, 2, 1, 6, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 10, 3, 2, 10, 6, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 3, 2, 9, 3, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 6, 2, 9, 6, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 3, 7, 3, 8, 7, 11, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 4, 8, 4, 7, 8, 10, false, random, randomizer);
				this.setAir(world, boundingBox, 3, 1, 3, 8, 2, 11);
				this.setAir(world, boundingBox, 4, 3, 6, 7, 3, 9);
				this.setAir(world, boundingBox, 2, 4, 2, 9, 5, 12);
				this.setAir(world, boundingBox, 4, 6, 5, 7, 6, 9);
				this.setAir(world, boundingBox, 5, 7, 6, 6, 7, 8);
				this.setAir(world, boundingBox, 5, 1, 2, 6, 2, 2);
				this.setAir(world, boundingBox, 5, 2, 12, 6, 2, 12);
				this.setAir(world, boundingBox, 5, 5, 1, 6, 5, 1);
				this.setAir(world, boundingBox, 5, 5, 13, 6, 5, 13);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 5, 5, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, 5, 5, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 5, 9, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 10, 5, 9, boundingBox);

				for (int i = 0; i <= 14; i += 14) {
					this.fillRandomized(world, boundingBox, 2, 4, i, 2, 5, i, false, random, randomizer);
					this.fillRandomized(world, boundingBox, 4, 4, i, 4, 5, i, false, random, randomizer);
					this.fillRandomized(world, boundingBox, 7, 4, i, 7, 5, i, false, random, randomizer);
					this.fillRandomized(world, boundingBox, 9, 4, i, 9, 5, i, false, random, randomizer);
				}

				this.fillRandomized(world, boundingBox, 5, 6, 0, 6, 6, 0, false, random, randomizer);

				for (int j = 0; j <= 11; j += 11) {
					for (int k = 2; k <= 12; k += 2) {
						this.fillRandomized(world, boundingBox, j, 4, k, j, 5, k, false, random, randomizer);
					}

					this.fillRandomized(world, boundingBox, j, 6, 5, j, 6, 5, false, random, randomizer);
					this.fillRandomized(world, boundingBox, j, 6, 9, j, 6, 9, false, random, randomizer);
				}

				this.fillRandomized(world, boundingBox, 2, 7, 2, 2, 9, 2, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 9, 7, 2, 9, 9, 2, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 2, 7, 12, 2, 9, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 9, 7, 12, 9, 9, 12, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 4, 9, 4, 4, 9, 4, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 7, 9, 4, 7, 9, 4, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 4, 9, 10, 4, 9, 10, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 7, 9, 10, 7, 9, 10, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 5, 9, 7, 6, 9, 7, false, random, randomizer);
				BlockState blockState = Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
				BlockState blockState2 = Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
				BlockState blockState3 = Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
				BlockState blockState4 = Blocks.STONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
				this.setBlockState(world, blockState4, 5, 9, 6, boundingBox);
				this.setBlockState(world, blockState4, 6, 9, 6, boundingBox);
				this.setBlockState(world, blockState3, 5, 9, 8, boundingBox);
				this.setBlockState(world, blockState3, 6, 9, 8, boundingBox);
				this.setBlockState(world, blockState4, 4, 0, 0, boundingBox);
				this.setBlockState(world, blockState4, 5, 0, 0, boundingBox);
				this.setBlockState(world, blockState4, 6, 0, 0, boundingBox);
				this.setBlockState(world, blockState4, 7, 0, 0, boundingBox);
				this.setBlockState(world, blockState4, 4, 1, 8, boundingBox);
				this.setBlockState(world, blockState4, 4, 2, 9, boundingBox);
				this.setBlockState(world, blockState4, 4, 3, 10, boundingBox);
				this.setBlockState(world, blockState4, 7, 1, 8, boundingBox);
				this.setBlockState(world, blockState4, 7, 2, 9, boundingBox);
				this.setBlockState(world, blockState4, 7, 3, 10, boundingBox);
				this.fillRandomized(world, boundingBox, 4, 1, 9, 4, 1, 9, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 7, 1, 9, 7, 1, 9, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 4, 1, 10, 7, 2, 10, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 5, 4, 5, 6, 4, 5, false, random, randomizer);
				this.setBlockState(world, blockState, 4, 4, 5, boundingBox);
				this.setBlockState(world, blockState2, 7, 4, 5, boundingBox);

				for (int l = 0; l < 4; l++) {
					this.setBlockState(world, blockState3, 5, 0 - l, 6 + l, boundingBox);
					this.setBlockState(world, blockState3, 6, 0 - l, 6 + l, boundingBox);
					this.setAir(world, boundingBox, 5, 0 - l, 7 + l, 6, 0 - l, 9 + l);
				}

				this.setAir(world, boundingBox, 1, -3, 12, 10, -1, 13);
				this.setAir(world, boundingBox, 1, -3, 1, 3, -1, 13);
				this.setAir(world, boundingBox, 1, -3, 1, 9, -1, 5);

				for (int m = 1; m <= 13; m += 2) {
					this.fillRandomized(world, boundingBox, 1, -3, m, 1, -2, m, false, random, randomizer);
				}

				for (int n = 2; n <= 12; n += 2) {
					this.fillRandomized(world, boundingBox, 1, -1, n, 3, -1, n, false, random, randomizer);
				}

				this.fillRandomized(world, boundingBox, 2, -2, 1, 5, -2, 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 7, -2, 1, 9, -2, 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 6, -3, 1, 6, -3, 1, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 6, -1, 1, 6, -1, 1, false, random, randomizer);
				this.setBlockState(
					world, Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.EAST).with(TripwireHookBlock.ATTACHED, true), 1, -3, 8, boundingBox
				);
				this.setBlockState(
					world, Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.WEST).with(TripwireHookBlock.ATTACHED, true), 4, -3, 8, boundingBox
				);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 2, -3, 8, boundingBox);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 3, -3, 8, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 7, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 6, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 5, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 4, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 3, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 2, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 5, -3, 1, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 4, -3, 1, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3, -3, 1, boundingBox);
				if (!this.placedTrap1) {
					this.placedTrap1 = this.method_11851(world, boundingBox, random, 3, -2, 1, Direction.NORTH, LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST);
				}

				this.setBlockState(world, Blocks.VINE.getDefaultState().with(VineBlock.SOUTH, true), 3, -2, 2, boundingBox);
				this.setBlockState(
					world,
					Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.NORTH).with(TripwireHookBlock.ATTACHED, true),
					7,
					-3,
					1,
					boundingBox
				);
				this.setBlockState(
					world,
					Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.SOUTH).with(TripwireHookBlock.ATTACHED, true),
					7,
					-3,
					5,
					boundingBox
				);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 7, -3, 2, boundingBox);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 7, -3, 3, boundingBox);
				this.setBlockState(world, Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.ATTACHED, true), 7, -3, 4, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -3, 6, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -3, 6, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -3, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 4, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 9, -2, 4, boundingBox);
				if (!this.placedTrap2) {
					this.placedTrap2 = this.method_11851(world, boundingBox, random, 9, -2, 3, Direction.WEST, LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST);
				}

				this.setBlockState(world, Blocks.VINE.getDefaultState().with(VineBlock.EAST, true), 8, -1, 3, boundingBox);
				this.setBlockState(world, Blocks.VINE.getDefaultState().with(VineBlock.EAST, true), 8, -2, 3, boundingBox);
				if (!this.mainChest) {
					this.mainChest = this.method_11852(world, boundingBox, random, 8, -3, 3, LootTables.JUNGLE_TEMPLE_CHEST);
				}

				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 2, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 1, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 4, -3, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -2, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -1, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 6, -3, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -2, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -1, 5, boundingBox);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 5, boundingBox);
				this.fillRandomized(world, boundingBox, 9, -1, 1, 9, -1, 5, false, random, randomizer);
				this.setAir(world, boundingBox, 8, -3, 8, 10, -1, 10);
				this.setBlockState(world, Blocks.STONE_BRICKS.stateFromData(StoneBrickBlock.CHISELED_ID), 8, -2, 11, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.stateFromData(StoneBrickBlock.CHISELED_ID), 9, -2, 11, boundingBox);
				this.setBlockState(world, Blocks.STONE_BRICKS.stateFromData(StoneBrickBlock.CHISELED_ID), 10, -2, 11, boundingBox);
				BlockState blockState5 = Blocks.LEVER.getDefaultState().with(LeverBlock.FACING, LeverBlock.LeverType.NORTH);
				this.setBlockState(world, blockState5, 8, -2, 12, boundingBox);
				this.setBlockState(world, blockState5, 9, -2, 12, boundingBox);
				this.setBlockState(world, blockState5, 10, -2, 12, boundingBox);
				this.fillRandomized(world, boundingBox, 8, -3, 8, 8, -3, 10, false, random, randomizer);
				this.fillRandomized(world, boundingBox, 10, -3, 8, 10, -3, 10, false, random, randomizer);
				this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -2, 9, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -2, 9, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 8, -2, 10, boundingBox);
				this.setBlockState(world, Blocks.REDSTONE_WIRE.getDefaultState(), 10, -1, 9, boundingBox);
				this.setBlockState(world, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.UP), 9, -2, 8, boundingBox);
				this.setBlockState(world, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -2, 8, boundingBox);
				this.setBlockState(world, Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -1, 8, boundingBox);
				this.setBlockState(world, Blocks.UNPOWERED_REPEATER.getDefaultState().with(RepeaterBlock.DIRECTION, Direction.NORTH), 10, -2, 10, boundingBox);
				if (!this.hiddenChest) {
					this.hiddenChest = this.method_11852(world, boundingBox, random, 9, -3, 10, LootTables.JUNGLE_TEMPLE_CHEST);
				}

				return true;
			}
		}

		static class Randomizer extends StructurePiece.BlockRandomizer {
			private Randomizer() {
			}

			@Override
			public void setBlock(Random random, int x, int y, int z, boolean placeBlock) {
				if (random.nextFloat() < 0.4F) {
					this.block = Blocks.COBBLESTONE.getDefaultState();
				} else {
					this.block = Blocks.MOSSY_COBBLESTONE.getDefaultState();
				}
			}
		}
	}

	public static class SwampHut extends TemplePieces.AbstractPiece {
		private boolean witch;

		public SwampHut() {
		}

		public SwampHut(Random random, int i, int j) {
			super(random, i, 64, j, 7, 7, 9);
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
			super.serialize(structureNbt);
			structureNbt.putBoolean("Witch", this.witch);
		}

		@Override
		protected void deserialize(NbtCompound structureNbt) {
			super.deserialize(structureNbt);
			this.witch = structureNbt.getBoolean("Witch");
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (!this.adjustToAverageHeight(world, boundingBox, 0)) {
				return false;
			} else {
				this.fillWithOutline(
					world,
					boundingBox,
					1,
					1,
					1,
					5,
					1,
					7,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					1,
					4,
					2,
					5,
					4,
					7,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					2,
					1,
					0,
					4,
					1,
					0,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					2,
					2,
					2,
					3,
					3,
					2,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					1,
					2,
					3,
					1,
					3,
					6,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					5,
					2,
					3,
					5,
					3,
					6,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(
					world,
					boundingBox,
					2,
					2,
					7,
					4,
					3,
					7,
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					Blocks.PLANKS.stateFromData(PlanksBlock.WoodType.SPRUCE.getId()),
					false
				);
				this.fillWithOutline(world, boundingBox, 1, 0, 2, 1, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 5, 0, 2, 5, 3, 2, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 1, 0, 7, 1, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.fillWithOutline(world, boundingBox, 5, 0, 7, 5, 3, 7, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 3, 4, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 5, 3, 4, boundingBox);
				this.setBlockState(world, Blocks.AIR.getDefaultState(), 5, 3, 5, boundingBox);
				this.setBlockState(
					world, Blocks.FLOWER_POT.getDefaultState().with(FlowerPotBlock.CONTENTS, FlowerPotBlock.PottablePlantType.RED_MUSHROOM), 1, 3, 5, boundingBox
				);
				this.setBlockState(world, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, boundingBox);
				this.setBlockState(world, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, boundingBox);
				this.setBlockState(world, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, boundingBox);
				BlockState blockState = Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
				BlockState blockState2 = Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
				BlockState blockState3 = Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
				BlockState blockState4 = Blocks.SPRUCE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
				this.fillWithOutline(world, boundingBox, 0, 4, 1, 6, 4, 1, blockState, blockState, false);
				this.fillWithOutline(world, boundingBox, 0, 4, 2, 0, 4, 7, blockState2, blockState2, false);
				this.fillWithOutline(world, boundingBox, 6, 4, 2, 6, 4, 7, blockState3, blockState3, false);
				this.fillWithOutline(world, boundingBox, 0, 4, 8, 6, 4, 8, blockState4, blockState4, false);

				for (int i = 2; i <= 7; i += 5) {
					for (int j = 1; j <= 5; j += 4) {
						this.fillAirAndLiquidsDownwards(world, Blocks.LOG.getDefaultState(), j, -1, i, boundingBox);
					}
				}

				if (!this.witch) {
					int k = this.applyXTransform(2, 5);
					int l = this.applyYTransform(2);
					int m = this.applyZTransform(2, 5);
					if (boundingBox.contains(new BlockPos(k, l, m))) {
						this.witch = true;
						WitchEntity witchEntity = new WitchEntity(world);
						witchEntity.refreshPositionAndAngles((double)k + 0.5, (double)l, (double)m + 0.5, 0.0F, 0.0F);
						witchEntity.initialize(world.getLocalDifficulty(new BlockPos(k, l, m)), null);
						world.spawnEntity(witchEntity);
					}
				}

				return true;
			}
		}
	}

	public static class class_2761 extends TemplePieces.AbstractPiece {
		private static final Identifier field_13009 = new Identifier("igloo/igloo_top");
		private static final Identifier field_13010 = new Identifier("igloo/igloo_middle");
		private static final Identifier field_13011 = new Identifier("igloo/igloo_bottom");

		public class_2761() {
		}

		public class_2761(Random random, int i, int j) {
			super(random, i, 64, j, 7, 5, 8);
		}

		@Override
		public boolean generate(World world, Random random, BlockBox boundingBox) {
			if (!this.adjustToAverageHeight(world, boundingBox, -1)) {
				return false;
			} else {
				BlockBox blockBox = this.getBoundingBox();
				BlockPos blockPos = new BlockPos(blockBox.minX, blockBox.minY, blockBox.minZ);
				BlockRotation[] blockRotations = BlockRotation.values();
				MinecraftServer minecraftServer = world.getServer();
				class_2763 lv = world.getSaveHandler().method_11956();
				StructurePlacementData structurePlacementData = new StructurePlacementData()
					.method_11868(blockRotations[random.nextInt(blockRotations.length)])
					.method_11866(Blocks.STRUCTURE_VOID)
					.method_11869(blockBox);
				Structure structure = lv.method_11861(minecraftServer, field_13009);
				structure.method_11882(world, blockPos, structurePlacementData);
				if (random.nextDouble() < 0.5) {
					Structure structure2 = lv.method_11861(minecraftServer, field_13010);
					Structure structure3 = lv.method_11861(minecraftServer, field_13011);
					int i = random.nextInt(8) + 4;

					for (int j = 0; j < i; j++) {
						BlockPos blockPos2 = structure.method_11887(structurePlacementData, new BlockPos(3, -1 - j * 3, 5), structurePlacementData, new BlockPos(1, 2, 1));
						structure2.method_11882(world, blockPos.add(blockPos2), structurePlacementData);
					}

					BlockPos blockPos3 = blockPos.add(
						structure.method_11887(structurePlacementData, new BlockPos(3, -1 - i * 3, 5), structurePlacementData, new BlockPos(3, 5, 7))
					);
					structure3.method_11882(world, blockPos3, structurePlacementData);
					Map<BlockPos, String> map = structure3.method_11890(blockPos3, structurePlacementData);

					for (Entry<BlockPos, String> entry : map.entrySet()) {
						if ("chest".equals(entry.getValue())) {
							BlockPos blockPos4 = (BlockPos)entry.getKey();
							world.setBlockState(blockPos4, Blocks.AIR.getDefaultState(), 3);
							BlockEntity blockEntity = world.getBlockEntity(blockPos4.down());
							if (blockEntity instanceof ChestBlockEntity) {
								((ChestBlockEntity)blockEntity).method_11660(LootTables.IGLOO_CHEST_CHEST, random.nextLong());
							}
						}
					}
				} else {
					BlockPos blockPos5 = Structure.method_11886(structurePlacementData, new BlockPos(3, 0, 5));
					world.setBlockState(blockPos.add(blockPos5), Blocks.SNOW.getDefaultState(), 3);
				}

				return true;
			}
		}
	}
}
