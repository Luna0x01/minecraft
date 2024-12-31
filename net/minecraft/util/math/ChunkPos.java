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

	public ChunkPos(long l) {
		this.x = (int)l;
		this.z = (int)(l >> 32);
	}

	public long method_16281() {
		return getIdFromCoords(this.x, this.z);
	}

	public static long getIdFromCoords(int x, int z) {
		return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
	}

	public static int method_16282(long l) {
		return (int)(l & 4294967295L);
	}

	public static int method_16283(long l) {
		return (int)(l >>> 32 & 4294967295L);
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

	public String toString() {
		return "[" + this.x + ", " + this.z + "]";
	}

	public BlockPos method_16284() {
		return new BlockPos(this.x << 4, 0, this.z << 4);
	}
}
