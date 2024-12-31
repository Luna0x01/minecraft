package net.minecraft.util.math;

import net.minecraft.entity.Entity;

public class ChunkPos {
	public final int x;
	public final int z;

	public ChunkPos(int i, int j) {
		this.x = i;
		this.z = j;
	}

	public ChunkPos(BlockPos blockPos) {
		this.x = blockPos.getX() >> 4;
		this.z = blockPos.getZ() >> 4;
	}

	public static long getIdFromCoords(int x, int z) {
		return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
	}

	public int hashCode() {
		int i = 1664525 * this.x + 1013904223;
		int j = 1664525 * (this.z ^ -559038737) + 1013904223;
		return i ^ j;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChunkPos)) {
			return false;
		} else {
			ChunkPos chunkPos = (ChunkPos)obj;
			return this.x == chunkPos.x && this.z == chunkPos.z;
		}
	}

	public double squaredDistanceToCenter(Entity entity) {
		double d = (double)(this.x * 16 + 8);
		double e = (double)(this.z * 16 + 8);
		double f = d - entity.x;
		double g = e - entity.z;
		return f * f + g * g;
	}

	public int getCenterX() {
		return (this.x << 4) + 8;
	}

	public int getCenterZ() {
		return (this.z << 4) + 8;
	}

	public int getActualX() {
		return this.x << 4;
	}

	public int getActualZ() {
		return this.z << 4;
	}

	public int getOppositeX() {
		return (this.x << 4) + 15;
	}

	public int getOppositeZ() {
		return (this.z << 4) + 15;
	}

	public BlockPos toBlockPos(int offsetX, int y, int offsetZ) {
		return new BlockPos((this.x << 4) + offsetX, y, (this.z << 4) + offsetZ);
	}

	public BlockPos toBlockPos(int y) {
		return new BlockPos(this.getCenterX(), y, this.getCenterZ());
	}

	public String toString() {
		return "[" + this.x + ", " + this.z + "]";
	}
}
