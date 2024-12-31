package net.minecraft.util.math;

import net.minecraft.util.hit.BlockHitResult;

public class Box {
	public final double minX;
	public final double minY;
	public final double minZ;
	public final double maxX;
	public final double maxY;
	public final double maxZ;

	public Box(double d, double e, double f, double g, double h, double i) {
		this.minX = Math.min(d, g);
		this.minY = Math.min(e, h);
		this.minZ = Math.min(f, i);
		this.maxX = Math.max(d, g);
		this.maxY = Math.max(e, h);
		this.maxZ = Math.max(f, i);
	}

	public Box(BlockPos blockPos, BlockPos blockPos2) {
		this.minX = (double)blockPos.getX();
		this.minY = (double)blockPos.getY();
		this.minZ = (double)blockPos.getZ();
		this.maxX = (double)blockPos2.getX();
		this.maxY = (double)blockPos2.getY();
		this.maxZ = (double)blockPos2.getZ();
	}

	public Box stretch(double x, double y, double z) {
		double d = this.minX;
		double e = this.minY;
		double f = this.minZ;
		double g = this.maxX;
		double h = this.maxY;
		double i = this.maxZ;
		if (x < 0.0) {
			d += x;
		} else if (x > 0.0) {
			g += x;
		}

		if (y < 0.0) {
			e += y;
		} else if (y > 0.0) {
			h += y;
		}

		if (z < 0.0) {
			f += z;
		} else if (z > 0.0) {
			i += z;
		}

		return new Box(d, e, f, g, h, i);
	}

	public Box expand(double x, double y, double z) {
		double d = this.minX - x;
		double e = this.minY - y;
		double f = this.minZ - z;
		double g = this.maxX + x;
		double h = this.maxY + y;
		double i = this.maxZ + z;
		return new Box(d, e, f, g, h, i);
	}

	public Box union(Box box) {
		double d = Math.min(this.minX, box.minX);
		double e = Math.min(this.minY, box.minY);
		double f = Math.min(this.minZ, box.minZ);
		double g = Math.max(this.maxX, box.maxX);
		double h = Math.max(this.maxY, box.maxY);
		double i = Math.max(this.maxZ, box.maxZ);
		return new Box(d, e, f, g, h, i);
	}

	public static Box createNewBox(double x1, double y1, double z1, double x2, double y2, double z2) {
		double d = Math.min(x1, x2);
		double e = Math.min(y1, y2);
		double f = Math.min(z1, z2);
		double g = Math.max(x1, x2);
		double h = Math.max(y1, y2);
		double i = Math.max(z1, z2);
		return new Box(d, e, f, g, h, i);
	}

	public Box offset(double x, double y, double z) {
		return new Box(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}

	public double method_583(Box box, double d) {
		if (!(box.maxY <= this.minY) && !(box.minY >= this.maxY) && !(box.maxZ <= this.minZ) && !(box.minZ >= this.maxZ)) {
			if (d > 0.0 && box.maxX <= this.minX) {
				double e = this.minX - box.maxX;
				if (e < d) {
					d = e;
				}
			} else if (d < 0.0 && box.minX >= this.maxX) {
				double f = this.maxX - box.minX;
				if (f > d) {
					d = f;
				}
			}

			return d;
		} else {
			return d;
		}
	}

	public double method_589(Box box, double d) {
		if (!(box.maxX <= this.minX) && !(box.minX >= this.maxX) && !(box.maxZ <= this.minZ) && !(box.minZ >= this.maxZ)) {
			if (d > 0.0 && box.maxY <= this.minY) {
				double e = this.minY - box.maxY;
				if (e < d) {
					d = e;
				}
			} else if (d < 0.0 && box.minY >= this.maxY) {
				double f = this.maxY - box.minY;
				if (f > d) {
					d = f;
				}
			}

			return d;
		} else {
			return d;
		}
	}

	public double method_594(Box box, double d) {
		if (!(box.maxX <= this.minX) && !(box.minX >= this.maxX) && !(box.maxY <= this.minY) && !(box.minY >= this.maxY)) {
			if (d > 0.0 && box.maxZ <= this.minZ) {
				double e = this.minZ - box.maxZ;
				if (e < d) {
					d = e;
				}
			} else if (d < 0.0 && box.minZ >= this.maxZ) {
				double f = this.maxZ - box.minZ;
				if (f > d) {
					d = f;
				}
			}

			return d;
		} else {
			return d;
		}
	}

	public boolean intersects(Box box) {
		if (box.maxX <= this.minX || box.minX >= this.maxX) {
			return false;
		} else {
			return box.maxY <= this.minY || box.minY >= this.maxY ? false : !(box.maxZ <= this.minZ) && !(box.minZ >= this.maxZ);
		}
	}

	public boolean contains(Vec3d vec) {
		if (vec.x <= this.minX || vec.x >= this.maxX) {
			return false;
		} else {
			return vec.y <= this.minY || vec.y >= this.maxY ? false : !(vec.z <= this.minZ) && !(vec.z >= this.maxZ);
		}
	}

	public double getAverage() {
		double d = this.maxX - this.minX;
		double e = this.maxY - this.minY;
		double f = this.maxZ - this.minZ;
		return (d + e + f) / 3.0;
	}

	public Box increment(double x, double y, double z) {
		double d = this.minX + x;
		double e = this.minY + y;
		double f = this.minZ + z;
		double g = this.maxX - x;
		double h = this.maxY - y;
		double i = this.maxZ - z;
		return new Box(d, e, f, g, h, i);
	}

	public BlockHitResult method_585(Vec3d vec1, Vec3d vec2) {
		Vec3d vec3d = vec1.lerpForX(vec2, this.minX);
		Vec3d vec3d2 = vec1.lerpForX(vec2, this.maxX);
		Vec3d vec3d3 = vec1.lerpForY(vec2, this.minY);
		Vec3d vec3d4 = vec1.lerpForY(vec2, this.maxY);
		Vec3d vec3d5 = vec1.lerpForZ(vec2, this.minZ);
		Vec3d vec3d6 = vec1.lerpForZ(vec2, this.maxZ);
		if (!this.method_590(vec3d)) {
			vec3d = null;
		}

		if (!this.method_590(vec3d2)) {
			vec3d2 = null;
		}

		if (!this.method_595(vec3d3)) {
			vec3d3 = null;
		}

		if (!this.method_595(vec3d4)) {
			vec3d4 = null;
		}

		if (!this.method_597(vec3d5)) {
			vec3d5 = null;
		}

		if (!this.method_597(vec3d6)) {
			vec3d6 = null;
		}

		Vec3d vec3d7 = null;
		if (vec3d != null) {
			vec3d7 = vec3d;
		}

		if (vec3d2 != null && (vec3d7 == null || vec1.squaredDistanceTo(vec3d2) < vec1.squaredDistanceTo(vec3d7))) {
			vec3d7 = vec3d2;
		}

		if (vec3d3 != null && (vec3d7 == null || vec1.squaredDistanceTo(vec3d3) < vec1.squaredDistanceTo(vec3d7))) {
			vec3d7 = vec3d3;
		}

		if (vec3d4 != null && (vec3d7 == null || vec1.squaredDistanceTo(vec3d4) < vec1.squaredDistanceTo(vec3d7))) {
			vec3d7 = vec3d4;
		}

		if (vec3d5 != null && (vec3d7 == null || vec1.squaredDistanceTo(vec3d5) < vec1.squaredDistanceTo(vec3d7))) {
			vec3d7 = vec3d5;
		}

		if (vec3d6 != null && (vec3d7 == null || vec1.squaredDistanceTo(vec3d6) < vec1.squaredDistanceTo(vec3d7))) {
			vec3d7 = vec3d6;
		}

		if (vec3d7 == null) {
			return null;
		} else {
			Direction direction = null;
			if (vec3d7 == vec3d) {
				direction = Direction.WEST;
			} else if (vec3d7 == vec3d2) {
				direction = Direction.EAST;
			} else if (vec3d7 == vec3d3) {
				direction = Direction.DOWN;
			} else if (vec3d7 == vec3d4) {
				direction = Direction.UP;
			} else if (vec3d7 == vec3d5) {
				direction = Direction.NORTH;
			} else {
				direction = Direction.SOUTH;
			}

			return new BlockHitResult(vec3d7, direction);
		}
	}

	private boolean method_590(Vec3d vec3d) {
		return vec3d == null ? false : vec3d.y >= this.minY && vec3d.y <= this.maxY && vec3d.z >= this.minZ && vec3d.z <= this.maxZ;
	}

	private boolean method_595(Vec3d vec3d) {
		return vec3d == null ? false : vec3d.x >= this.minX && vec3d.x <= this.maxX && vec3d.z >= this.minZ && vec3d.z <= this.maxZ;
	}

	private boolean method_597(Vec3d vec3d) {
		return vec3d == null ? false : vec3d.x >= this.minX && vec3d.x <= this.maxX && vec3d.y >= this.minY && vec3d.y <= this.maxY;
	}

	public String toString() {
		return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
	}

	public boolean isInvalid() {
		return Double.isNaN(this.minX)
			|| Double.isNaN(this.minY)
			|| Double.isNaN(this.minZ)
			|| Double.isNaN(this.maxX)
			|| Double.isNaN(this.maxY)
			|| Double.isNaN(this.maxZ);
	}
}
