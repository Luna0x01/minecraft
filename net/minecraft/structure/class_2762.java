package net.minecraft.structure;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
	protected void method_5530(NbtCompound nbtCompound, class_2763 arg) {
		this.field_13018 = new BlockPos(nbtCompound.getInt("TPX"), nbtCompound.getInt("TPY"), nbtCompound.getInt("TPZ"));
	}

	@Override
	public boolean generate(World world, Random random, BlockBox boundingBox) {
		this.field_13017.method_11869(boundingBox);
		this.field_13016.method_13391(world, this.field_13018, this.field_13017, 18);
		Map<BlockPos, String> map = this.field_13016.method_11890(this.field_13018, this.field_13017);

		for (Entry<BlockPos, String> entry : map.entrySet()) {
			String string = (String)entry.getValue();
			this.method_11857(string, (BlockPos)entry.getKey(), world, random, boundingBox);
		}

		return true;
	}

	protected abstract void method_11857(String string, BlockPos blockPos, World world, Random random, BlockBox blockBox);

	private void method_11858() {
		BlockRotation blockRotation = this.field_13017.method_11874();
		BlockPos blockPos = this.field_13016.method_11885(blockRotation);
		BlockMirror blockMirror = this.field_13017.method_11871();
		this.boundingBox = new BlockBox(0, 0, 0, blockPos.getX(), blockPos.getY() - 1, blockPos.getZ());
		switch (blockRotation) {
			case NONE:
			default:
				break;
			case CLOCKWISE_90:
				this.boundingBox.move(-blockPos.getX(), 0, 0);
				break;
			case COUNTERCLOCKWISE_90:
				this.boundingBox.move(0, 0, -blockPos.getZ());
				break;
			case CLOCKWISE_180:
				this.boundingBox.move(-blockPos.getX(), 0, -blockPos.getZ());
		}

		switch (blockMirror) {
			case NONE:
			default:
				break;
			case FRONT_BACK:
				BlockPos blockPos2 = BlockPos.ORIGIN;
				if (blockRotation == BlockRotation.CLOCKWISE_90 || blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
					blockPos2 = blockPos2.offset(blockRotation.rotate(Direction.WEST), blockPos.getZ());
				} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
					blockPos2 = blockPos2.offset(Direction.EAST, blockPos.getX());
				} else {
					blockPos2 = blockPos2.offset(Direction.WEST, blockPos.getX());
				}

				this.boundingBox.move(blockPos2.getX(), 0, blockPos2.getZ());
				break;
			case LEFT_RIGHT:
				BlockPos blockPos3 = BlockPos.ORIGIN;
				if (blockRotation == BlockRotation.CLOCKWISE_90 || blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
					blockPos3 = blockPos3.offset(blockRotation.rotate(Direction.NORTH), blockPos.getX());
				} else if (blockRotation == BlockRotation.CLOCKWISE_180) {
					blockPos3 = blockPos3.offset(Direction.SOUTH, blockPos.getZ());
				} else {
					blockPos3 = blockPos3.offset(Direction.NORTH, blockPos.getZ());
				}

				this.boundingBox.move(blockPos3.getX(), 0, blockPos3.getZ());
		}

		this.boundingBox.move(this.field_13018.getX(), this.field_13018.getY(), this.field_13018.getZ());
	}

	@Override
	public void translate(int x, int y, int z) {
		super.translate(x, y, z);
		this.field_13018 = this.field_13018.add(x, y, z);
	}
}
