package net.minecraft.structure;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.class_3998;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public abstract class class_2762 extends StructurePiece {
	private static final StructurePlacementData field_13019 = new StructurePlacementData();
	protected Structure field_13016;
	protected StructurePlacementData field_13017 = field_13019.method_11870(true).method_11866(Blocks.AIR);
	protected BlockPos field_13018;

	public class_2762() {
	}

	public class_2762(int i) {
		super(i);
	}

	protected void method_11856(Structure structure, BlockPos blockPos, StructurePlacementData structurePlacementData) {
		this.field_13016 = structure;
		this.method_11853(Direction.NORTH);
		this.field_13018 = blockPos;
		this.field_13017 = structurePlacementData;
		this.method_11858();
	}

	@Override
	protected void serialize(NbtCompound structureNbt) {
		structureNbt.putInt("TPX", this.field_13018.getX());
		structureNbt.putInt("TPY", this.field_13018.getY());
		structureNbt.putInt("TPZ", this.field_13018.getZ());
	}

	@Override
	protected void method_5530(NbtCompound nbtCompound, class_3998 arg) {
		this.field_13018 = new BlockPos(nbtCompound.getInt("TPX"), nbtCompound.getInt("TPY"), nbtCompound.getInt("TPZ"));
	}

	@Override
	public boolean method_58(IWorld iWorld, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		this.field_13017.method_11869(blockBox);
		if (this.field_13016.method_17697(iWorld, this.field_13018, this.field_13017, 2)) {
			Map<BlockPos, String> map = this.field_13016.method_11890(this.field_13018, this.field_13017);

			for (Entry<BlockPos, String> entry : map.entrySet()) {
				String string = (String)entry.getValue();
				this.method_11857(string, (BlockPos)entry.getKey(), iWorld, random, blockBox);
			}
		}

		return true;
	}

	protected abstract void method_11857(String string, BlockPos blockPos, IWorld iWorld, Random random, BlockBox blockBox);

	private void method_11858() {
		BlockRotation blockRotation = this.field_13017.method_11874();
		BlockPos blockPos = this.field_13017.method_17693();
		BlockPos blockPos2 = this.field_13016.method_11885(blockRotation);
		BlockMirror blockMirror = this.field_13017.method_11871();
		int i = blockPos.getX();
		int j = blockPos.getZ();
		int k = blockPos2.getX() - 1;
		int l = blockPos2.getY() - 1;
		int m = blockPos2.getZ() - 1;
		switch (blockRotation) {
			case NONE:
				this.boundingBox = new BlockBox(0, 0, 0, k, l, m);
				break;
			case CLOCKWISE_180:
				this.boundingBox = new BlockBox(i + i - k, 0, j + j - m, i + i, l, j + j);
				break;
			case COUNTERCLOCKWISE_90:
				this.boundingBox = new BlockBox(i - j, 0, i + j - m, i - j + k, l, i + j);
				break;
			case CLOCKWISE_90:
				this.boundingBox = new BlockBox(i + j - k, 0, j - i, i + j, l, j - i + m);
		}

		switch (blockMirror) {
			case NONE:
			default:
				break;
			case FRONT_BACK:
				BlockPos blockPos3 = BlockPos.ORIGIN;
				if (blockRotation == BlockRotation.CLOCKWISE_90 || blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
					blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.WEST), m);
				} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
					blockPos3 = blockPos3.offset(Direction.EAST, k);
				} else {
					blockPos3 = blockPos3.offset(Direction.WEST, k);
				}

				this.boundingBox.move(blockPos3.getX(), 0, blockPos3.getZ());
				break;
			case LEFT_RIGHT:
				BlockPos blockPos4 = BlockPos.ORIGIN;
				if (blockRotation == BlockRotation.CLOCKWISE_90 || blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
					blockPos4 = blockPos4.offset(blockRotation.rotate(Direction.NORTH), k);
				} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
					blockPos4 = blockPos4.offset(Direction.SOUTH, m);
				} else {
					blockPos4 = blockPos4.offset(Direction.NORTH, m);
				}

				this.boundingBox.move(blockPos4.getX(), 0, blockPos4.getZ());
		}

		this.boundingBox.move(this.field_13018.getX(), this.field_13018.getY(), this.field_13018.getZ());
	}

	@Override
	public void translate(int x, int y, int z) {
		super.translate(x, y, z);
		this.field_13018 = this.field_13018.add(x, y, z);
	}
}
