package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructurePieceManager;
import net.minecraft.structure.class_8;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3974 extends class_8 {
	private final boolean[] field_19348 = new boolean[4];

	public static void method_17600() {
		StructurePieceManager.registerPiece(class_3974.class, "TeDP");
	}

	public class_3974() {
	}

	public class_3974(Random random, int i, int j) {
		super(random, i, 64, j, 21, 15, 21);
	}

	@Override
	protected void serialize(NbtCompound structureNbt) {
		super.serialize(structureNbt);
		structureNbt.putBoolean("hasPlacedChest0", this.field_19348[0]);
		structureNbt.putBoolean("hasPlacedChest1", this.field_19348[1]);
		structureNbt.putBoolean("hasPlacedChest2", this.field_19348[2]);
		structureNbt.putBoolean("hasPlacedChest3", this.field_19348[3]);
	}

	@Override
	protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
		super.method_5530(nbtCompound, arg);
		this.field_19348[0] = nbtCompound.getBoolean("hasPlacedChest0");
		this.field_19348[1] = nbtCompound.getBoolean("hasPlacedChest1");
		this.field_19348[2] = nbtCompound.getBoolean("hasPlacedChest2");
		this.field_19348[3] = nbtCompound.getBoolean("hasPlacedChest3");
	}

	@Override
	public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		this.method_17653(
			iWorld, blockBox, 0, -4, 0, this.field_14 - 1, 0, this.field_16 - 1, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);

		for (int i = 1; i <= 9; i++) {
			this.method_17653(
				iWorld, blockBox, i, i, i, this.field_14 - 1 - i, i, this.field_16 - 1 - i, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
			);
			this.method_17653(
				iWorld, blockBox, i + 1, i, i + 1, this.field_14 - 2 - i, i, this.field_16 - 2 - i, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false
			);
		}

		for (int j = 0; j < this.field_14; j++) {
			for (int k = 0; k < this.field_16; k++) {
				int l = -5;
				this.method_72(iWorld, Blocks.SANDSTONE.getDefaultState(), j, -5, k, blockBox);
			}
		}

		BlockState blockState = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH);
		BlockState blockState2 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.SOUTH);
		BlockState blockState3 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.EAST);
		BlockState blockState4 = Blocks.SANDSTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.WEST);
		this.method_17653(iWorld, blockBox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.method_56(iWorld, blockState, 2, 10, 0, blockBox);
		this.method_56(iWorld, blockState2, 2, 10, 4, blockBox);
		this.method_56(iWorld, blockState3, 0, 10, 2, blockBox);
		this.method_56(iWorld, blockState4, 4, 10, 2, blockBox);
		this.method_17653(iWorld, blockBox, this.field_14 - 5, 0, 0, this.field_14 - 1, 9, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(
			iWorld, blockBox, this.field_14 - 4, 10, 1, this.field_14 - 2, 10, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.method_56(iWorld, blockState, this.field_14 - 3, 10, 0, blockBox);
		this.method_56(iWorld, blockState2, this.field_14 - 3, 10, 4, blockBox);
		this.method_56(iWorld, blockState3, this.field_14 - 5, 10, 2, blockBox);
		this.method_56(iWorld, blockState4, this.field_14 - 1, 10, 2, blockBox);
		this.method_17653(iWorld, blockBox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 9, 1, 0, 11, 3, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 1, 1, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 2, 1, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 9, 3, 1, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 10, 3, 1, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 3, 1, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 2, 1, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 11, 1, 1, blockBox);
		this.method_17653(iWorld, blockBox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 4, 1, 2, 8, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 12, 1, 2, 16, 2, 2, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(
			iWorld, blockBox, 5, 4, 5, this.field_14 - 6, 4, this.field_16 - 6, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.method_17653(iWorld, blockBox, 9, 4, 9, 11, 4, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.method_17653(
			iWorld, blockBox, this.field_14 - 5, 1, 5, this.field_14 - 2, 4, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.method_17653(iWorld, blockBox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.method_17653(
			iWorld, blockBox, this.field_14 - 7, 7, 9, this.field_14 - 7, 7, 11, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.method_17653(iWorld, blockBox, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.method_17653(
			iWorld, blockBox, this.field_14 - 6, 5, 9, this.field_14 - 6, 7, 11, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false
		);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 5, 5, 10, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 5, 6, 10, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 6, 6, 10, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), this.field_14 - 6, 5, 10, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), this.field_14 - 6, 6, 10, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), this.field_14 - 7, 6, 10, blockBox);
		this.method_17653(iWorld, blockBox, 2, 4, 4, 2, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, this.field_14 - 3, 4, 4, this.field_14 - 3, 6, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_56(iWorld, blockState, 2, 4, 5, blockBox);
		this.method_56(iWorld, blockState, 2, 3, 4, blockBox);
		this.method_56(iWorld, blockState, this.field_14 - 3, 4, 5, blockBox);
		this.method_56(iWorld, blockState, this.field_14 - 3, 3, 4, blockBox);
		this.method_17653(iWorld, blockBox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.method_17653(
			iWorld, blockBox, this.field_14 - 3, 1, 3, this.field_14 - 2, 2, 3, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.method_56(iWorld, Blocks.SANDSTONE.getDefaultState(), 1, 1, 2, blockBox);
		this.method_56(iWorld, Blocks.SANDSTONE.getDefaultState(), this.field_14 - 2, 1, 2, blockBox);
		this.method_56(iWorld, Blocks.SANDSTONE_SLAB.getDefaultState(), 1, 2, 2, blockBox);
		this.method_56(iWorld, Blocks.SANDSTONE_SLAB.getDefaultState(), this.field_14 - 2, 2, 2, blockBox);
		this.method_56(iWorld, blockState4, 2, 1, 2, blockBox);
		this.method_56(iWorld, blockState3, this.field_14 - 3, 1, 2, blockBox);
		this.method_17653(iWorld, blockBox, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.method_17653(
			iWorld, blockBox, this.field_14 - 5, 3, 5, this.field_14 - 5, 3, 17, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false
		);
		this.method_17653(iWorld, blockBox, 3, 1, 5, 4, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, this.field_14 - 6, 1, 5, this.field_14 - 5, 2, 16, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

		for (int m = 5; m <= 17; m += 2) {
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 4, 1, m, blockBox);
			this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), 4, 2, m, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), this.field_14 - 5, 1, m, blockBox);
			this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), this.field_14 - 5, 2, m, blockBox);
		}

		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 7, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 8, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 9, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 9, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 8, 0, 10, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 12, 0, 10, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 7, 0, 10, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 13, 0, 10, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 0, 11, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 0, 11, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 12, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 10, 0, 13, blockBox);
		this.method_56(iWorld, Blocks.BLUE_TERRACOTTA.getDefaultState(), 10, 0, 10, blockBox);

		for (int n = 0; n <= this.field_14 - 1; n += this.field_14 - 1) {
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), n, 2, 1, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 2, 2, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), n, 2, 3, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), n, 3, 1, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 3, 2, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), n, 3, 3, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 4, 1, blockBox);
			this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), n, 4, 2, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 4, 3, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), n, 5, 1, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 5, 2, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), n, 5, 3, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 6, 1, blockBox);
			this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), n, 6, 2, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 6, 3, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 7, 1, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 7, 2, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), n, 7, 3, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), n, 8, 1, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), n, 8, 2, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), n, 8, 3, blockBox);
		}

		for (int o = 2; o <= this.field_14 - 3; o += this.field_14 - 3 - 2) {
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), o - 1, 2, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o, 2, 0, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), o + 1, 2, 0, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), o - 1, 3, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o, 3, 0, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), o + 1, 3, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o - 1, 4, 0, blockBox);
			this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), o, 4, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o + 1, 4, 0, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), o - 1, 5, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o, 5, 0, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), o + 1, 5, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o - 1, 6, 0, blockBox);
			this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), o, 6, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o + 1, 6, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o - 1, 7, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o, 7, 0, blockBox);
			this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), o + 1, 7, 0, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), o - 1, 8, 0, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), o, 8, 0, blockBox);
			this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), o + 1, 8, 0, blockBox);
		}

		this.method_17653(iWorld, blockBox, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 8, 6, 0, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 12, 6, 0, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 9, 5, 0, blockBox);
		this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, 5, 0, blockBox);
		this.method_56(iWorld, Blocks.ORANGE_TERRACOTTA.getDefaultState(), 11, 5, 0, blockBox);
		this.method_17653(iWorld, blockBox, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.getDefaultState(), Blocks.CHISELED_SANDSTONE.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.getDefaultState(), Blocks.CUT_SANDSTONE.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), false);
		this.method_17653(iWorld, blockBox, 9, -11, 9, 11, -1, 11, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_56(iWorld, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 10, -11, 10, blockBox);
		this.method_17653(iWorld, blockBox, 9, -13, 9, 11, -13, 11, Blocks.TNT.getDefaultState(), Blocks.AIR.getDefaultState(), false);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 8, -11, 10, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 8, -10, 10, blockBox);
		this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), 7, -10, 10, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 7, -11, 10, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 12, -11, 10, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 12, -10, 10, blockBox);
		this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), 13, -10, 10, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 13, -11, 10, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 10, -11, 8, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 10, -10, 8, blockBox);
		this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 7, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 7, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 10, -11, 12, blockBox);
		this.method_56(iWorld, Blocks.AIR.getDefaultState(), 10, -10, 12, blockBox);
		this.method_56(iWorld, Blocks.CHISELED_SANDSTONE.getDefaultState(), 10, -10, 13, blockBox);
		this.method_56(iWorld, Blocks.CUT_SANDSTONE.getDefaultState(), 10, -11, 13, blockBox);

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (!this.field_19348[direction.getHorizontal()]) {
				int p = direction.getOffsetX() * 2;
				int q = direction.getOffsetZ() * 2;
				this.field_19348[direction.getHorizontal()] = this.method_11852(iWorld, blockBox, random, 10 + p, -11, 10 + q, LootTables.DESERT_PYRAMID_CHEST);
			}
		}

		return true;
	}
}
