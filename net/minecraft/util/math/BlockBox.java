package net.minecraft.util.math;

import com.google.common.base.Objects;
import net.minecraft.nbt.NbtIntArray;

public class BlockBox {
	public int minX;
	public int minY;
	public int minZ;
	public int maxX;
	public int maxY;
	public int maxZ;

	public BlockBox() {
	}

	public BlockBox(int[] is) {
		if (is.length == 6) {
			this.minX = is[0];
			this.minY = is[1];
			this.minZ = is[2];
			this.maxX = is[3];
			this.maxY = is[4];
			this.maxZ = is[5];
		}
	}

	public static BlockBox empty() {
		return new BlockBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	public static BlockBox rotated(int x, int y, int z, int offsetX, int offsetY, int offsetZ, int sizeX, int sizeY, int sizeZ, Direction facing) {
		switch (facing) {
			case NORTH:
				return new BlockBox(x + offsetX, y + offsetY, z - sizeZ + 1 + offsetZ, x + sizeX - 1 + offsetX, y + sizeY - 1 + offsetY, z + offsetZ);
			case SOUTH:
				return new BlockBox(x + offsetX, y + offsetY, z + offsetZ, x + sizeX - 1 + offsetX, y + sizeY - 1 + offsetY, z + sizeZ - 1 + offsetZ);
			case WEST:
				return new BlockBox(x - sizeZ + 1 + offsetZ, y + offsetY, z + offsetX, x + offsetZ, y + sizeY - 1 + offsetY, z + sizeX - 1 + offsetX);
			case EAST:
				return new BlockBox(x + offsetZ, y + offsetY, z + offsetX, x + sizeZ - 1 + offsetZ, y + sizeY - 1 + offsetY, z + sizeX - 1 + offsetX);
			default:
				return new BlockBox(x + offsetX, y + offsetY, z + offsetZ, x + sizeX - 1 + offsetX, y + sizeY - 1 + offsetY, z + sizeZ - 1 + offsetZ);
		}
	}

	public static BlockBox create(int x1, int y1, int z1, int x2, int y2, int z2) {
		return new BlockBox(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
	}

	public BlockBox(BlockBox blockBox) {
		this.minX = blockBox.minX;
		this.minY = blockBox.minY;
		this.minZ = blockBox.minZ;
		this.maxX = blockBox.maxX;
		this.maxY = blockBox.maxY;
		this.maxZ = blockBox.maxZ;
	}

	public BlockBox(int i, int j, int k, int l, int m, int n) {
		this.minX = i;
		this.minY = j;
		this.minZ = k;
		this.maxX = l;
		this.maxY = m;
		this.maxZ = n;
	}

	public BlockBox(Vec3i vec3i, Vec3i vec3i2) {
		this.minX = Math.min(vec3i.getX(), vec3i2.getX());
		this.minY = Math.min(vec3i.getY(), vec3i2.getY());
		this.minZ = Math.min(vec3i.getZ(), vec3i2.getZ());
		this.maxX = Math.max(vec3i.getX(), vec3i2.getX());
		this.maxY = Math.max(vec3i.getY(), vec3i2.getY());
		this.maxZ = Math.max(vec3i.getZ(), vec3i2.getZ());
	}

	public BlockBox(int i, int j, int k, int l) {
		this.minX = i;
		this.minZ = j;
		this.maxX = k;
		this.maxZ = l;
		this.minY = 1;
		this.maxY = 512;
	}

	public boolean intersects(BlockBox other) {
		return this.maxX >= other.minX
			&& this.minX <= other.maxX
			&& this.maxZ >= other.minZ
			&& this.minZ <= other.maxZ
			&& this.maxY >= other.minY
			&& this.minY <= other.maxY;
	}

	public boolean intersectsXZ(int minX, int minZ, int maxX, int maxZ) {
		return this.maxX >= minX && this.minX <= maxX && this.maxZ >= minZ && this.minZ <= maxZ;
	}

	public void encompass(BlockBox region) {
		this.minX = Math.min(this.minX, region.minX);
		this.minY = Math.min(this.minY, region.minY);
		this.minZ = Math.min(this.minZ, region.minZ);
		this.maxX = Math.max(this.maxX, region.maxX);
		this.maxY = Math.max(this.maxY, region.maxY);
		this.maxZ = Math.max(this.maxZ, region.maxZ);
	}

	public void move(int dx, int dy, int dz) {
		this.minX += dx;
		this.minY += dy;
		this.minZ += dz;
		this.maxX += dx;
		this.maxY += dy;
		this.maxZ += dz;
	}

	public boolean contains(Vec3i vec) {
		return vec.getX() >= this.minX
			&& vec.getX() <= this.maxX
			&& vec.getZ() >= this.minZ
			&& vec.getZ() <= this.maxZ
			&& vec.getY() >= this.minY
			&& vec.getY() <= this.maxY;
	}

	public Vec3i getDimensions() {
		return new Vec3i(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
	}

	public int getBlockCountX() {
		return this.maxX - this.minX + 1;
	}

	public int getBlockCountY() {
		return this.maxY - this.minY + 1;
	}

	public int getBlockCountZ() {
		return this.maxZ - this.minZ + 1;
	}

	public String toString() {
		return Objects.toStringHelper(this)
			.add("x0", this.minX)
			.add("y0", this.minY)
			.add("z0", this.minZ)
			.add("x1", this.maxX)
			.add("y1", this.maxY)
			.add("z1", this.maxZ)
			.toString();
	}

	public NbtIntArray toNbt() {
		return new NbtIntArray(new int[]{this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ});
	}
}
