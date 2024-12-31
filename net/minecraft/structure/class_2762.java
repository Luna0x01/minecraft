package net.minecraft.structure;

import java.util.Map;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
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
	protected void deserialize(NbtCompound structureNbt) {
		this.field_13018 = new BlockPos(structureNbt.getInt("TPX"), structureNbt.getInt("TPY"), structureNbt.getInt("TPZ"));
	}

	@Override
	public boolean generate(World world, Random random, BlockBox boundingBox) {
		this.field_13017.method_11869(boundingBox);
		this.field_13016.method_11896(world, this.field_13018, this.field_13017);
		Map<BlockPos, String> map = this.field_13016.method_11890(this.field_13018, this.field_13017);

		for (BlockPos blockPos : map.keySet()) {
			String string = (String)map.get(blockPos);
			this.method_11857(string, blockPos, world, random, boundingBox);
		}

		return true;
	}

	protected abstract void method_11857(String string, BlockPos blockPos, World world, Random random, BlockBox blockBox);

	private void method_11858() {
		BlockRotation blockRotation = this.field_13017.method_11874();
		BlockPos blockPos = this.field_13016.method_11885(blockRotation);
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

		this.boundingBox.move(this.field_13018.getX(), this.field_13018.getY(), this.field_13018.getZ());
	}

	@Override
	public void translate(int x, int y, int z) {
		super.translate(x, y, z);
		this.field_13018 = this.field_13018.add(x, y, z);
	}
}
