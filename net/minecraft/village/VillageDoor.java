package net.minecraft.village;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class VillageDoor {
	private final BlockPos pos1;
	private final BlockPos pos2;
	private final Direction facing;
	private int field_3666;
	private boolean field_3667;
	private int field_3668;

	public VillageDoor(BlockPos blockPos, int i, int j, int k) {
		this(blockPos, method_11042(i, j), k);
	}

	private static Direction method_11042(int i, int j) {
		if (i < 0) {
			return Direction.WEST;
		} else if (i > 0) {
			return Direction.EAST;
		} else {
			return j < 0 ? Direction.NORTH : Direction.SOUTH;
		}
	}

	public VillageDoor(BlockPos blockPos, Direction direction, int i) {
		this.pos1 = blockPos;
		this.facing = direction;
		this.pos2 = blockPos.offset(direction, 2);
		this.field_3666 = i;
	}

	public int method_2806(int x, int y, int z) {
		return (int)this.pos1.squaredDistanceTo((double)x, (double)y, (double)z);
	}

	public int method_11043(BlockPos pos) {
		return (int)pos.getSquaredDistance(this.getPos1());
	}

	public int method_11045(BlockPos pos) {
		return (int)this.pos2.getSquaredDistance(pos);
	}

	public boolean method_11046(BlockPos pos) {
		int i = pos.getX() - this.pos1.getX();
		int j = pos.getZ() - this.pos1.getY();
		return i * this.facing.getOffsetX() + j * this.facing.getOffsetZ() >= 0;
	}

	public void method_2809() {
		this.field_3668 = 0;
	}

	public void method_2810() {
		this.field_3668++;
	}

	public int method_2811() {
		return this.field_3668;
	}

	public BlockPos getPos1() {
		return this.pos1;
	}

	public BlockPos getPos2() {
		return this.pos2;
	}

	public int getOffsetX2() {
		return this.facing.getOffsetX() * 2;
	}

	public int getOffsetZ2() {
		return this.facing.getOffsetZ() * 2;
	}

	public int method_2805() {
		return this.field_3666;
	}

	public void method_11041(int i) {
		this.field_3666 = i;
	}

	public boolean method_11051() {
		return this.field_3667;
	}

	public void method_11044(boolean bl) {
		this.field_3667 = bl;
	}
}
