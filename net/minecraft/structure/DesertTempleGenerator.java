package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DesertTempleGenerator extends StructurePieceWithDimensions {
	private final boolean[] hasPlacedChest = new boolean[4];

	public DesertTempleGenerator(Random random, int i, int j) {
		super(StructurePieceType.DESERT_TEMPLE, random, i, 64, j, 21, 15, 21);
	}

	public DesertTempleGenerator(StructureManager structureManager, CompoundTag compoundTag) {
		super(StructurePieceType.DESERT_TEMPLE, compoundTag);
		this.hasPlacedChest[0] = compoundTag.getBoolean("hasPlacedChest0");
		this.hasPlacedChest[1] = compoundTag.getBoolean("hasPlacedChest1");
		this.hasPlacedChest[2] = compoundTag.getBoolean("hasPlacedChest2");
		this.hasPlacedChest[3] = compoundTag.getBoolean("hasPlacedChest3");
	}

	@Override
	protected void toNbt(CompoundTag compoundTag) {
		super.toNbt(compoundTag);
		compoundTag.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
		compoundTag.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
		compoundTag.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
		compoundTag.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
	}

	@Override
	public boolean generate(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		this.fillWithOutline(
			iWorld, blockBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);

		for (int i = 1; i <= 9; i++) {
			this.fillWithOutline(
				iWorld, blockBox, i, i, i, this.width - 1 - i, i, this.depth - 1 - i, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
			);
			this.fillWithOutline(
				iWorld,
				blockBox,
				i + 1,
				i,
				i + 1,
				this.width - 2 - i,
				i,
				this.depth - 2 - i,
				Blocks.field_10124.getDefaultState(),
				Blocks.field_10124.getDefaultState(),
				false
			);
		}

		for (int j = 0; j < this.width; j++) {
			for (int k = 0; k < this.depth; k++) {
				int l = -5;
				this.method_14936(iWorld, Blocks.field_9979.getDefaultState(), j, -5, k, blockBox);
			}
		}

		BlockState blockState = Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, Direction.field_11043);
		BlockState blockState2 = Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, Direction.field_11035);
		BlockState blockState3 = Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, Direction.field_11034);
		BlockState blockState4 = Blocks.field_10142.getDefaultState().with(StairsBlock.FACING, Direction.field_11039);
		this.fillWithOutline(iWorld, blockBox, 0, 0, 0, 4, 9, 4, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 1, 10, 1, 3, 10, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.addBlock(iWorld, blockState, 2, 10, 0, blockBox);
		this.addBlock(iWorld, blockState2, 2, 10, 4, blockBox);
		this.addBlock(iWorld, blockState3, 0, 10, 2, blockBox);
		this.addBlock(iWorld, blockState4, 4, 10, 2, blockBox);
		this.fillWithOutline(
			iWorld, blockBox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false
		);
		this.fillWithOutline(
			iWorld, blockBox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.addBlock(iWorld, blockState, this.width - 3, 10, 0, blockBox);
		this.addBlock(iWorld, blockState2, this.width - 3, 10, 4, blockBox);
		this.addBlock(iWorld, blockState3, this.width - 5, 10, 2, blockBox);
		this.addBlock(iWorld, blockState4, this.width - 1, 10, 2, blockBox);
		this.fillWithOutline(iWorld, blockBox, 8, 0, 0, 12, 4, 4, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 9, 1, 0, 11, 3, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 9, 1, 1, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 9, 2, 1, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 9, 3, 1, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 10, 3, 1, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 11, 3, 1, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 11, 2, 1, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 11, 1, 1, blockBox);
		this.fillWithOutline(iWorld, blockBox, 4, 1, 1, 8, 3, 3, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 4, 1, 2, 8, 2, 2, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 12, 1, 1, 16, 3, 3, Blocks.field_9979.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 12, 1, 2, 16, 2, 2, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, blockBox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.fillWithOutline(iWorld, blockBox, 9, 4, 9, 11, 4, 11, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 8, 1, 8, 8, 3, 8, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 12, 1, 8, 12, 3, 8, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 8, 1, 12, 8, 3, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 12, 1, 12, 12, 3, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 1, 1, 5, 4, 4, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, blockBox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.fillWithOutline(iWorld, blockBox, 6, 7, 9, 6, 7, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, blockBox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.fillWithOutline(iWorld, blockBox, 5, 5, 9, 5, 7, 11, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, blockBox, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false
		);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 5, 5, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 5, 6, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 6, 6, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), this.width - 6, 5, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), this.width - 6, 6, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), this.width - 7, 6, 10, blockBox);
		this.fillWithOutline(iWorld, blockBox, 2, 4, 4, 2, 6, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, blockBox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false
		);
		this.addBlock(iWorld, blockState, 2, 4, 5, blockBox);
		this.addBlock(iWorld, blockState, 2, 3, 4, blockBox);
		this.addBlock(iWorld, blockState, this.width - 3, 4, 5, blockBox);
		this.addBlock(iWorld, blockState, this.width - 3, 3, 4, blockBox);
		this.fillWithOutline(iWorld, blockBox, 1, 1, 3, 2, 2, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, blockBox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.addBlock(iWorld, Blocks.field_9979.getDefaultState(), 1, 1, 2, blockBox);
		this.addBlock(iWorld, Blocks.field_9979.getDefaultState(), this.width - 2, 1, 2, blockBox);
		this.addBlock(iWorld, Blocks.field_10007.getDefaultState(), 1, 2, 2, blockBox);
		this.addBlock(iWorld, Blocks.field_10007.getDefaultState(), this.width - 2, 2, 2, blockBox);
		this.addBlock(iWorld, blockState4, 2, 1, 2, blockBox);
		this.addBlock(iWorld, blockState3, this.width - 3, 1, 2, blockBox);
		this.fillWithOutline(iWorld, blockBox, 4, 3, 5, 4, 3, 17, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, blockBox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false
		);
		this.fillWithOutline(iWorld, blockBox, 3, 1, 5, 4, 2, 16, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.fillWithOutline(
			iWorld, blockBox, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false
		);

		for (int m = 5; m <= 17; m += 2) {
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 4, 1, m, blockBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 4, 2, m, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), this.width - 5, 1, m, blockBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), this.width - 5, 2, m, blockBox);
		}

		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 10, 0, 7, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 10, 0, 8, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 9, 0, 9, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 11, 0, 9, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 8, 0, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 12, 0, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 7, 0, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 13, 0, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 9, 0, 11, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 11, 0, 11, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 10, 0, 12, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 10, 0, 13, blockBox);
		this.addBlock(iWorld, Blocks.field_10409.getDefaultState(), 10, 0, 10, blockBox);

		for (int n = 0; n <= this.width - 1; n += this.width - 1) {
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), n, 2, 1, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 2, 2, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), n, 2, 3, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), n, 3, 1, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 3, 2, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), n, 3, 3, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 4, 1, blockBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), n, 4, 2, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 4, 3, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), n, 5, 1, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 5, 2, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), n, 5, 3, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 6, 1, blockBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), n, 6, 2, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 6, 3, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 7, 1, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 7, 2, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), n, 7, 3, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), n, 8, 1, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), n, 8, 2, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), n, 8, 3, blockBox);
		}

		for (int o = 2; o <= this.width - 3; o += this.width - 3 - 2) {
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), o - 1, 2, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o, 2, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), o + 1, 2, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), o - 1, 3, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o, 3, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), o + 1, 3, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o - 1, 4, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), o, 4, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o + 1, 4, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), o - 1, 5, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o, 5, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), o + 1, 5, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o - 1, 6, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), o, 6, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o + 1, 6, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o - 1, 7, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o, 7, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), o + 1, 7, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), o - 1, 8, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), o, 8, 0, blockBox);
			this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), o + 1, 8, 0, blockBox);
		}

		this.fillWithOutline(iWorld, blockBox, 8, 4, 0, 12, 6, 0, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 8, 6, 0, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 12, 6, 0, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 9, 5, 0, blockBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 10, 5, 0, blockBox);
		this.addBlock(iWorld, Blocks.field_10184.getDefaultState(), 11, 5, 0, blockBox);
		this.fillWithOutline(iWorld, blockBox, 8, -14, 8, 12, -11, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 8, -10, 8, 12, -10, 12, Blocks.field_10292.getDefaultState(), Blocks.field_10292.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 8, -9, 8, 12, -9, 12, Blocks.field_10361.getDefaultState(), Blocks.field_10361.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 8, -8, 8, 12, -1, 12, Blocks.field_9979.getDefaultState(), Blocks.field_9979.getDefaultState(), false);
		this.fillWithOutline(iWorld, blockBox, 9, -11, 9, 11, -1, 11, Blocks.field_10124.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.addBlock(iWorld, Blocks.field_10158.getDefaultState(), 10, -11, 10, blockBox);
		this.fillWithOutline(iWorld, blockBox, 9, -13, 9, 11, -13, 11, Blocks.field_10375.getDefaultState(), Blocks.field_10124.getDefaultState(), false);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 8, -11, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 8, -10, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 7, -10, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 7, -11, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 12, -11, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 12, -10, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 13, -10, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 13, -11, 10, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, -11, 8, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, -10, 8, blockBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 10, -10, 7, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 10, -11, 7, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, -11, 12, blockBox);
		this.addBlock(iWorld, Blocks.field_10124.getDefaultState(), 10, -10, 12, blockBox);
		this.addBlock(iWorld, Blocks.field_10292.getDefaultState(), 10, -10, 13, blockBox);
		this.addBlock(iWorld, Blocks.field_10361.getDefaultState(), 10, -11, 13, blockBox);

		for (Direction direction : Direction.Type.field_11062) {
			if (!this.hasPlacedChest[direction.getHorizontal()]) {
				int p = direction.getOffsetX() * 2;
				int q = direction.getOffsetZ() * 2;
				this.hasPlacedChest[direction.getHorizontal()] = this.addChest(iWorld, blockBox, random, 10 + p, -11, 10 + q, LootTables.field_885);
			}
		}

		return true;
	}
}
