package net.minecraft.structure;

import java.util.Random;
import net.minecraft.class_3804;
import net.minecraft.class_3998;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class TemplePieces {
	public static void registerPieces() {
		StructurePieceManager.registerPiece(TemplePieces.class_3970.class, "BTP");
	}

	public static class class_3970 extends StructurePiece {
		public class_3970() {
		}

		public class_3970(BlockPos blockPos) {
			super(0);
			this.boundingBox = new BlockBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
		}

		@Override
		protected void serialize(NbtCompound structureNbt) {
		}

		@Override
		protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
		}

		@Override
		public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
			int i = iWorld.method_16372(class_3804.class_3805.OCEAN_FLOOR_WG, this.boundingBox.minX, this.boundingBox.minZ);
			BlockPos.Mutable mutable = new BlockPos.Mutable(this.boundingBox.minX, i, this.boundingBox.minZ);

			while (mutable.getY() > 0) {
				BlockState blockState = iWorld.getBlockState(mutable);
				BlockState blockState2 = iWorld.getBlockState(mutable.down());
				if (blockState2 == Blocks.SANDSTONE.getDefaultState()
					|| blockState2 == Blocks.STONE.getDefaultState()
					|| blockState2 == Blocks.ANDESITE.getDefaultState()
					|| blockState2 == Blocks.GRANITE.getDefaultState()
					|| blockState2 == Blocks.DIORITE.getDefaultState()) {
					BlockState blockState3 = !blockState.isAir() && !this.method_17590(blockState) ? blockState : Blocks.SAND.getDefaultState();

					for (Direction direction : Direction.values()) {
						BlockPos blockPos = mutable.offset(direction);
						BlockState blockState4 = iWorld.getBlockState(blockPos);
						if (blockState4.isAir() || this.method_17590(blockState4)) {
							BlockPos blockPos2 = blockPos.down();
							BlockState blockState5 = iWorld.getBlockState(blockPos2);
							if ((blockState5.isAir() || this.method_17590(blockState5)) && direction != Direction.UP) {
								iWorld.setBlockState(blockPos, blockState2, 3);
							} else {
								iWorld.setBlockState(blockPos, blockState3, 3);
							}
						}
					}

					return this.method_13775(
						iWorld, blockBox, random, new BlockPos(this.boundingBox.minX, mutable.getY(), this.boundingBox.minZ), LootTables.BURIED_TREASURE_CHEST, null
					);
				}

				mutable.method_19934(0, -1, 0);
			}

			return false;
		}

		private boolean method_17590(BlockState blockState) {
			return blockState == Blocks.WATER.getDefaultState() || blockState == Blocks.LAVA.getDefaultState();
		}
	}
}
