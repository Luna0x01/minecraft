package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructurePieceManager;
import net.minecraft.structure.class_8;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3993 extends class_8 {
	private boolean field_19413;

	public static void method_17671() {
		StructurePieceManager.registerPiece(class_3993.class, "TeSH");
	}

	public class_3993() {
	}

	public class_3993(Random random, int i, int j) {
		super(random, i, 64, j, 7, 7, 9);
	}

	@Override
	protected void serialize(NbtCompound structureNbt) {
		super.serialize(structureNbt);
		structureNbt.putBoolean("Witch", this.field_19413);
	}

	@Override
	protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
		super.method_5530(nbtCompound, arg);
		this.field_19413 = nbtCompound.getBoolean("Witch");
	}

	@Override
	public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		if (!this.method_16(iWorld, blockBox, 0)) {
			return false;
		} else {
			this.method_17653(iWorld, blockBox, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.getDefaultState(), Blocks.SPRUCE_PLANKS.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
			this.method_17653(iWorld, blockBox, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LOG.getDefaultState(), false);
			this.method_56(iWorld, Blocks.OAK_FENCE.getDefaultState(), 2, 3, 2, blockBox);
			this.method_56(iWorld, Blocks.OAK_FENCE.getDefaultState(), 3, 3, 7, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 1, 3, 4, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 5, 3, 4, blockBox);
			this.method_56(iWorld, Blocks.AIR.getDefaultState(), 5, 3, 5, blockBox);
			this.method_56(iWorld, Blocks.POTTED_RED_MUSHROOM.getDefaultState(), 1, 3, 5, blockBox);
			this.method_56(iWorld, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 2, 6, blockBox);
			this.method_56(iWorld, Blocks.CAULDRON.getDefaultState(), 4, 2, 6, blockBox);
			this.method_56(iWorld, Blocks.OAK_FENCE.getDefaultState(), 1, 2, 1, blockBox);
			this.method_56(iWorld, Blocks.OAK_FENCE.getDefaultState(), 5, 2, 1, blockBox);
			BlockState blockState = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.NORTH);
			BlockState blockState2 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.EAST);
			BlockState blockState3 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.WEST);
			BlockState blockState4 = Blocks.SPRUCE_STAIRS.getDefaultState().withProperty(StairsBlock.FACING, Direction.SOUTH);
			this.method_17653(iWorld, blockBox, 0, 4, 1, 6, 4, 1, blockState, blockState, false);
			this.method_17653(iWorld, blockBox, 0, 4, 2, 0, 4, 7, blockState2, blockState2, false);
			this.method_17653(iWorld, blockBox, 6, 4, 2, 6, 4, 7, blockState3, blockState3, false);
			this.method_17653(iWorld, blockBox, 0, 4, 8, 6, 4, 8, blockState4, blockState4, false);
			this.method_56(iWorld, blockState.withProperty(StairsBlock.field_18504, StairShape.OUTER_RIGHT), 0, 4, 1, blockBox);
			this.method_56(iWorld, blockState.withProperty(StairsBlock.field_18504, StairShape.OUTER_LEFT), 6, 4, 1, blockBox);
			this.method_56(iWorld, blockState4.withProperty(StairsBlock.field_18504, StairShape.OUTER_LEFT), 0, 4, 8, blockBox);
			this.method_56(iWorld, blockState4.withProperty(StairsBlock.field_18504, StairShape.OUTER_RIGHT), 6, 4, 8, blockBox);

			for (int i = 2; i <= 7; i += 5) {
				for (int j = 1; j <= 5; j += 4) {
					this.method_72(iWorld, Blocks.OAK_LOG.getDefaultState(), j, -1, i, blockBox);
				}
			}

			if (!this.field_19413) {
				int k = this.applyXTransform(2, 5);
				int l = this.applyYTransform(2);
				int m = this.applyZTransform(2, 5);
				if (blockBox.contains(new BlockPos(k, l, m))) {
					this.field_19413 = true;
					WitchEntity witchEntity = new WitchEntity(iWorld.method_16348());
					witchEntity.setPersistent();
					witchEntity.refreshPositionAndAngles((double)k + 0.5, (double)l, (double)m + 0.5, 0.0F, 0.0F);
					witchEntity.initialize(iWorld.method_8482(new BlockPos(k, l, m)), null, null);
					iWorld.method_3686(witchEntity);
				}
			}

			return true;
		}
	}
}
