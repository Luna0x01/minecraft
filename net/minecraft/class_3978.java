package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceManager;
import net.minecraft.structure.class_8;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3978 extends class_8 {
	private boolean field_19356;
	private boolean field_19357;
	private boolean field_19358;
	private boolean field_19359;
	private static final class_3978.class_7 field_19360 = new class_3978.class_7();

	public static void method_17607() {
		StructurePieceManager.registerPiece(class_3978.class, "TeJP");
	}

	public class_3978() {
	}

	public class_3978(Random random, int i, int j) {
		super(random, i, 64, j, 12, 10, 15);
	}

	@Override
	protected void serialize(NbtCompound structureNbt) {
		super.serialize(structureNbt);
		structureNbt.putBoolean("placedMainChest", this.field_19356);
		structureNbt.putBoolean("placedHiddenChest", this.field_19357);
		structureNbt.putBoolean("placedTrap1", this.field_19358);
		structureNbt.putBoolean("placedTrap2", this.field_19359);
	}

	@Override
	protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
		super.method_5530(nbtCompound, arg);
		this.field_19356 = nbtCompound.getBoolean("placedMainChest");
		this.field_19357 = nbtCompound.getBoolean("placedHiddenChest");
		this.field_19358 = nbtCompound.getBoolean("placedTrap1");
		this.field_19359 = nbtCompound.getBoolean("placedTrap2");
	}

	@Override
	public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		if (!this.method_16(iWorld, blockBox, 0)) {
			return false;
		} else {
			this.method_17655(iWorld, blockBox, 0, -4, 0, this.field_14 - 1, 0, this.field_16 - 1, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 2, 1, 2, 9, 2, 2, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 2, 1, 12, 9, 2, 12, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 2, 1, 3, 2, 2, 11, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 9, 1, 3, 9, 2, 11, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 1, 3, 1, 10, 6, 1, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 1, 3, 13, 10, 6, 13, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 1, 3, 2, 1, 6, 12, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 10, 3, 2, 10, 6, 12, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 2, 3, 2, 9, 3, 12, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 2, 6, 2, 9, 6, 12, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 3, 7, 3, 8, 7, 11, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 4, 8, 4, 7, 8, 10, false, random, field_19360);
			this.method_60(iWorld, blockBox, 3, 1, 3, 8, 2, 11);
			this.method_60(iWorld, blockBox, 4, 3, 6, 7, 3, 9);
			this.method_60(iWorld, blockBox, 2, 4, 2, 9, 5, 12);
			this.method_60(iWorld, blockBox, 4, 6, 5, 7, 6, 9);
			this.method_60(iWorld, blockBox, 5, 7, 6, 6, 7, 8);
			this.method_60(iWorld, blockBox, 5, 1, 2, 6, 2, 2);
			this.method_60(iWorld, blockBox, 5, 2, 12, 6, 2, 12);
			this.method_60(iWorld, blockBox, 5, 5, 1, 6, 5, 1);
			this.method_60(iWorld, blockBox, 5, 5, 13, 6, 5, 13);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 1, 5, 5, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 10, 5, 5, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 1, 5, 9, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 10, 5, 9, blockBox);

			for (int i = 0; i <= 14; i += 14) {
				this.method_17655(iWorld, blockBox, 2, 4, i, 2, 5, i, false, random, field_19360);
				this.method_17655(iWorld, blockBox, 4, 4, i, 4, 5, i, false, random, field_19360);
				this.method_17655(iWorld, blockBox, 7, 4, i, 7, 5, i, false, random, field_19360);
				this.method_17655(iWorld, blockBox, 9, 4, i, 9, 5, i, false, random, field_19360);
			}

			this.method_17655(iWorld, blockBox, 5, 6, 0, 6, 6, 0, false, random, field_19360);

			for (int j = 0; j <= 11; j += 11) {
				for (int k = 2; k <= 12; k += 2) {
					this.method_17655(iWorld, blockBox, j, 4, k, j, 5, k, false, random, field_19360);
				}

				this.method_17655(iWorld, blockBox, j, 6, 5, j, 6, 5, false, random, field_19360);
				this.method_17655(iWorld, blockBox, j, 6, 9, j, 6, 9, false, random, field_19360);
			}

			this.method_17655(iWorld, blockBox, 2, 7, 2, 2, 9, 2, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 9, 7, 2, 9, 9, 2, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 2, 7, 12, 2, 9, 12, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 9, 7, 12, 9, 9, 12, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 4, 9, 4, 4, 9, 4, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 7, 9, 4, 7, 9, 4, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 4, 9, 10, 4, 9, 10, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 7, 9, 10, 7, 9, 10, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 5, 9, 7, 6, 9, 7, false, random, field_19360);
			BlockState blockState = Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.EAST);
			BlockState blockState2 = Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.WEST);
			BlockState blockState3 = Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.SOUTH);
			BlockState blockState4 = Blocks.COBBLESTONE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH);
			this.method_56(iWorld, blockState4, 5, 9, 6, blockBox);
			this.method_56(iWorld, blockState4, 6, 9, 6, blockBox);
			this.method_56(iWorld, blockState3, 5, 9, 8, blockBox);
			this.method_56(iWorld, blockState3, 6, 9, 8, blockBox);
			this.method_56(iWorld, blockState4, 4, 0, 0, blockBox);
			this.method_56(iWorld, blockState4, 5, 0, 0, blockBox);
			this.method_56(iWorld, blockState4, 6, 0, 0, blockBox);
			this.method_56(iWorld, blockState4, 7, 0, 0, blockBox);
			this.method_56(iWorld, blockState4, 4, 1, 8, blockBox);
			this.method_56(iWorld, blockState4, 4, 2, 9, blockBox);
			this.method_56(iWorld, blockState4, 4, 3, 10, blockBox);
			this.method_56(iWorld, blockState4, 7, 1, 8, blockBox);
			this.method_56(iWorld, blockState4, 7, 2, 9, blockBox);
			this.method_56(iWorld, blockState4, 7, 3, 10, blockBox);
			this.method_17655(iWorld, blockBox, 4, 1, 9, 4, 1, 9, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 7, 1, 9, 7, 1, 9, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 4, 1, 10, 7, 2, 10, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 5, 4, 5, 6, 4, 5, false, random, field_19360);
			this.method_56(iWorld, blockState, 4, 4, 5, blockBox);
			this.method_56(iWorld, blockState2, 7, 4, 5, blockBox);

			for (int l = 0; l < 4; l++) {
				this.method_56(iWorld, blockState3, 5, 0 - l, 6 + l, blockBox);
				this.method_56(iWorld, blockState3, 6, 0 - l, 6 + l, blockBox);
				this.method_60(iWorld, blockBox, 5, 0 - l, 7 + l, 6, 0 - l, 9 + l);
			}

			this.method_60(iWorld, blockBox, 1, -3, 12, 10, -1, 13);
			this.method_60(iWorld, blockBox, 1, -3, 1, 3, -1, 13);
			this.method_60(iWorld, blockBox, 1, -3, 1, 9, -1, 5);

			for (int m = 1; m <= 13; m += 2) {
				this.method_17655(iWorld, blockBox, 1, -3, m, 1, -2, m, false, random, field_19360);
			}

			for (int n = 2; n <= 12; n += 2) {
				this.method_17655(iWorld, blockBox, 1, -1, n, 3, -1, n, false, random, field_19360);
			}

			this.method_17655(iWorld, blockBox, 2, -2, 1, 5, -2, 1, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 7, -2, 1, 9, -2, 1, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 6, -3, 1, 6, -3, 1, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 6, -1, 1, 6, -1, 1, false, random, field_19360);
			this.method_56(
				iWorld,
				Blocks.TRIPWIRE_HOOK
					.getDefaultState()
					.withProperty(TripwireHookBlock.FACING, Direction.EAST)
					.withProperty(TripwireHookBlock.field_18554, Boolean.valueOf(true)),
				1,
				-3,
				8,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.TRIPWIRE_HOOK
					.getDefaultState()
					.withProperty(TripwireHookBlock.FACING, Direction.WEST)
					.withProperty(TripwireHookBlock.field_18554, Boolean.valueOf(true)),
				4,
				-3,
				8,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.TRIPWIRE
					.getDefaultState()
					.withProperty(TripwireBlock.field_18546, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18548, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18543, Boolean.valueOf(true)),
				2,
				-3,
				8,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.TRIPWIRE
					.getDefaultState()
					.withProperty(TripwireBlock.field_18546, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18548, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18543, Boolean.valueOf(true)),
				3,
				-3,
				8,
				blockBox
			);
			BlockState blockState5 = Blocks.REDSTONE_WIRE
				.getDefaultState()
				.withProperty(RedstoneWireBlock.field_18443, WireConnection.SIDE)
				.withProperty(RedstoneWireBlock.field_18445, WireConnection.SIDE);
			this.method_56(iWorld, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(RedstoneWireBlock.field_18445, WireConnection.SIDE), 5, -3, 7, blockBox);
			this.method_56(iWorld, blockState5, 5, -3, 6, blockBox);
			this.method_56(iWorld, blockState5, 5, -3, 5, blockBox);
			this.method_56(iWorld, blockState5, 5, -3, 4, blockBox);
			this.method_56(iWorld, blockState5, 5, -3, 3, blockBox);
			this.method_56(iWorld, blockState5, 5, -3, 2, blockBox);
			this.method_56(
				iWorld,
				Blocks.REDSTONE_WIRE
					.getDefaultState()
					.withProperty(RedstoneWireBlock.field_18443, WireConnection.SIDE)
					.withProperty(RedstoneWireBlock.field_18446, WireConnection.SIDE),
				5,
				-3,
				1,
				blockBox
			);
			this.method_56(iWorld, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(RedstoneWireBlock.field_18444, WireConnection.SIDE), 4, -3, 1, blockBox);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3, -3, 1, blockBox);
			if (!this.field_19358) {
				this.field_19358 = this.method_11851(iWorld, blockBox, random, 3, -2, 1, Direction.NORTH, LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST);
			}

			this.method_56(iWorld, Blocks.VINE.getDefaultState().withProperty(VineBlock.field_18566, Boolean.valueOf(true)), 3, -2, 2, blockBox);
			this.method_56(
				iWorld,
				Blocks.TRIPWIRE_HOOK
					.getDefaultState()
					.withProperty(TripwireHookBlock.FACING, Direction.NORTH)
					.withProperty(TripwireHookBlock.field_18554, Boolean.valueOf(true)),
				7,
				-3,
				1,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.TRIPWIRE_HOOK
					.getDefaultState()
					.withProperty(TripwireHookBlock.FACING, Direction.SOUTH)
					.withProperty(TripwireHookBlock.field_18554, Boolean.valueOf(true)),
				7,
				-3,
				5,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.TRIPWIRE
					.getDefaultState()
					.withProperty(TripwireBlock.field_18545, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18547, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18543, Boolean.valueOf(true)),
				7,
				-3,
				2,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.TRIPWIRE
					.getDefaultState()
					.withProperty(TripwireBlock.field_18545, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18547, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18543, Boolean.valueOf(true)),
				7,
				-3,
				3,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.TRIPWIRE
					.getDefaultState()
					.withProperty(TripwireBlock.field_18545, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18547, Boolean.valueOf(true))
					.withProperty(TripwireBlock.field_18543, Boolean.valueOf(true)),
				7,
				-3,
				4,
				blockBox
			);
			this.method_56(iWorld, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(RedstoneWireBlock.field_18444, WireConnection.SIDE), 8, -3, 6, blockBox);
			this.method_56(
				iWorld,
				Blocks.REDSTONE_WIRE
					.getDefaultState()
					.withProperty(RedstoneWireBlock.field_18446, WireConnection.SIDE)
					.withProperty(RedstoneWireBlock.field_18445, WireConnection.SIDE),
				9,
				-3,
				6,
				blockBox
			);
			this.method_56(
				iWorld,
				Blocks.REDSTONE_WIRE
					.getDefaultState()
					.withProperty(RedstoneWireBlock.field_18443, WireConnection.SIDE)
					.withProperty(RedstoneWireBlock.field_18445, WireConnection.UP),
				9,
				-3,
				5,
				blockBox
			);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 4, blockBox);
			this.method_56(iWorld, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(RedstoneWireBlock.field_18443, WireConnection.SIDE), 9, -2, 4, blockBox);
			if (!this.field_19359) {
				this.field_19359 = this.method_11851(iWorld, blockBox, random, 9, -2, 3, Direction.WEST, LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST);
			}

			this.method_56(iWorld, Blocks.VINE.getDefaultState().withProperty(VineBlock.field_18565, Boolean.valueOf(true)), 8, -1, 3, blockBox);
			this.method_56(iWorld, Blocks.VINE.getDefaultState().withProperty(VineBlock.field_18565, Boolean.valueOf(true)), 8, -2, 3, blockBox);
			if (!this.field_19356) {
				this.field_19356 = this.method_11852(iWorld, blockBox, random, 8, -3, 3, LootTables.JUNGLE_TEMPLE_CHEST);
			}

			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 2, blockBox);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 1, blockBox);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 4, -3, 5, blockBox);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -2, 5, blockBox);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -1, 5, blockBox);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 6, -3, 5, blockBox);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -2, 5, blockBox);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -1, 5, blockBox);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 5, blockBox);
			this.method_17655(iWorld, blockBox, 9, -1, 1, 9, -1, 5, false, random, field_19360);
			this.method_60(iWorld, blockBox, 8, -3, 8, 10, -1, 10);
			this.method_56(iWorld, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 8, -2, 11, blockBox);
			this.method_56(iWorld, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 9, -2, 11, blockBox);
			this.method_56(iWorld, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 10, -2, 11, blockBox);
			BlockState blockState6 = Blocks.LEVER
				.getDefaultState()
				.withProperty(LeverBlock.FACING, Direction.NORTH)
				.withProperty(LeverBlock.FACE, WallMountLocation.WALL);
			this.method_56(iWorld, blockState6, 8, -2, 12, blockBox);
			this.method_56(iWorld, blockState6, 9, -2, 12, blockBox);
			this.method_56(iWorld, blockState6, 10, -2, 12, blockBox);
			this.method_17655(iWorld, blockBox, 8, -3, 8, 8, -3, 10, false, random, field_19360);
			this.method_17655(iWorld, blockBox, 10, -3, 8, 10, -3, 10, false, random, field_19360);
			this.method_56(iWorld, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -2, 9, blockBox);
			this.method_56(iWorld, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(RedstoneWireBlock.field_18443, WireConnection.SIDE), 8, -2, 9, blockBox);
			this.method_56(iWorld, Blocks.REDSTONE_WIRE.getDefaultState().withProperty(RedstoneWireBlock.field_18445, WireConnection.SIDE), 8, -2, 10, blockBox);
			this.method_56(iWorld, Blocks.REDSTONE_WIRE.getDefaultState(), 10, -1, 9, blockBox);
			this.method_56(iWorld, Blocks.STICKY_PISTON.getDefaultState().withProperty(PistonBlock.FACING, Direction.UP), 9, -2, 8, blockBox);
			this.method_56(iWorld, Blocks.STICKY_PISTON.getDefaultState().withProperty(PistonBlock.FACING, Direction.WEST), 10, -2, 8, blockBox);
			this.method_56(iWorld, Blocks.STICKY_PISTON.getDefaultState().withProperty(PistonBlock.FACING, Direction.WEST), 10, -1, 8, blockBox);
			this.method_56(iWorld, Blocks.REPEATER.getDefaultState().withProperty(RepeaterBlock.FACING, Direction.NORTH), 10, -2, 10, blockBox);
			if (!this.field_19357) {
				this.field_19357 = this.method_11852(iWorld, blockBox, random, 9, -3, 10, LootTables.JUNGLE_TEMPLE_CHEST);
			}

			return true;
		}
	}

	static class class_7 extends StructurePiece.BlockRandomizer {
		private class_7() {
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
