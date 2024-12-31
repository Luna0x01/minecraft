package net.minecraft.util.math;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
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

	public Box(BlockPos blockPos) {
		this(
			(double)blockPos.getX(),
			(double)blockPos.getY(),
			(double)blockPos.getZ(),
			(double)(blockPos.getX() + 1),
			(double)(blockPos.getY() + 1),
			(double)(blockPos.getZ() + 1)
		);
	}

	public Box(BlockPos blockPos, BlockPos blockPos2) {
		this((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
	}

	public Box(Vec3d vec3d, Vec3d vec3d2) {
		this(vec3d.x, vec3d.y, vec3d.z, vec3d2.x, vec3d2.y, vec3d2.z);
	}

	public Box withMaxY(double maxY) {
		return new Box(this.minX, this.minY, this.minZ, this.maxX, maxY, this.maxZ);
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (!(other instanceof Box)) {
			return false;
		} else {
			Box box = (Box)other;
			if (Double.compare(box.minX, this.minX) != 0) {
				return false;
			} else if (Double.compare(box.minY, this.minY) != 0) {
				return false;
			} else if (Double.compare(box.minZ, this.minZ) != 0) {
				return false;
			} else if (Double.compare(box.maxX, this.maxX) != 0) {
				return false;
			} else {
				return Double.compare(box.maxY, this.maxY) != 0 ? false : Double.compare(box.maxZ, this.maxZ) == 0;
			}
		}
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.minX);
		int i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minY);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minZ);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxX);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxY);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxZ);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public Box shrink(double x, double y, double z) {
		double d = this.minX;
		double e = this.minY;
		double f = this.minZ;
		double g = this.maxX;
		double h = this.maxY;
		double i = this.maxZ;
		if (x < 0.0) {
			d -= x;
		} else if (x > 0.0) {
			g -= x;
		}

		if (y < 0.0) {
			e -= y;
		} else if (y > 0.0) {
			h -= y;
		}

		if (z < 0.0) {
			f -= z;
		} else if (z > 0.0) {
			i -= z;
		}

		return new Box(d, e, f, g, h, i);
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

	public Box expand(double value) {
		return this.expand(value, value, value);
	}

	public Box intersection(Box box) {
		double d = Math.max(this.minX, box.minX);
		double e = Math.max(this.minY, box.minY);
		double f = Math.max(this.minZ, box.minZ);
		double g = Math.min(this.maxX, box.maxX);
		double h = Math.min(this.maxY, box.maxY);
		double i = Math.min(this.maxZ, box.maxZ);
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

	public Box offset(double x, double y, double z) {
		return new Box(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}

	public Box offset(BlockPos pos) {
		return new Box(
			this.minX + (double)pos.getX(),
			this.minY + (double)pos.getY(),
			this.minZ + (double)pos.getZ(),
			this.maxX + (double)pos.getX(),
			this.maxY + (double)pos.getY(),
			this.maxZ + (double)pos.getZ()
		);
	}

	public Box offset(Vec3d ved) {
		return this.offset(ved.x, ved.y, ved.z);
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
		return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
	}

	public boolean intersects(Vec3d vec1, Vec3d vec2) {
		return this.intersects(
			Math.min(vec1.x, vec2.x), Math.min(vec1.y, vec2.y), Math.min(vec1.z, vec2.z), Math.max(vec1.x, vec2.x), Math.max(vec1.y, vec2.y), Math.max(vec1.z, vec2.z)
		);
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

	public Box contract(double value) {
		return this.expand(-value);
	}

	@Nullable
	public BlockHitResult method_585(Vec3d vec1, Vec3d vec2) {
		Vec3d vec3d = this.method_12111(this.minX, vec1, vec2);
		Direction direction = Direction.WEST;
		Vec3d vec3d2 = this.method_12111(this.maxX, vec1, vec2);
		if (vec3d2 != null && this.method_12112(vec1, vec3d, vec3d2)) {
			vec3d = vec3d2;
			direction = Direction.EAST;
		}

		vec3d2 = this.method_12114(this.minY, vec1, vec2);
		if (vec3d2 != null && this.method_12112(vec1, vec3d, vec3d2)) {
			vec3d = vec3d2;
			direction = Direction.DOWN;
		}

		vec3d2 = this.method_12114(this.maxY, vec1, vec2);
		if (vec3d2 != null && this.method_12112(vec1, vec3d, vec3d2)) {
			vec3d = vec3d2;
			direction = Direction.UP;
		}

		vec3d2 = this.method_12117(this.minZ, vec1, vec2);
		if (vec3d2 != null && this.method_12112(vec1, vec3d, vec3d2)) {
			vec3d = vec3d2;
			direction = Direction.NORTH;
		}

		vec3d2 = this.method_12117(this.maxZ, vec1, vec2);
		if (vec3d2 != null && this.method_12112(vec1, vec3d, vec3d2)) {
			vec3d = vec3d2;
			direction = Direction.SOUTH;
		}

		return vec3d == null ? null : new BlockHitResult(vec3d, direction);
	}

	@VisibleForTesting
	boolean method_12112(Vec3d vec3d, @Nullable Vec3d vec3d2, Vec3d vec3d3) {
		return vec3d2 == null || vec3d.squaredDistanceTo(vec3d3) < vec3d.squaredDistanceTo(vec3d2);
	}

	@Nullable
	@VisibleForTesting
	Vec3d method_12111(double d, Vec3d vec3d, Vec3d vec3d2) {
		Vec3d vec3d3 = vec3d.method_12124(vec3d2, d);
		return vec3d3 != null && this.intersectsYZ(vec3d3) ? vec3d3 : null;
	}

	@Nullable
	@VisibleForTesting
	Vec3d method_12114(double d, Vec3d vec3d, Vec3d vec3d2) {
		Vec3d vec3d3 = vec3d.method_12125(vec3d2, d);
		return vec3d3 != null && this.intersectsXZ(vec3d3) ? vec3d3 : null;
	}

	@Nullable
	@VisibleForTesting
	Vec3d method_12117(double d, Vec3d vec3d, Vec3d vec3d2) {
		Vec3d vec3d3 = vec3d.method_12127(vec3d2, d);
		return vec3d3 != null && this.intersectsXY(vec3d3) ? vec3d3 : null;
	}

	@VisibleForTesting
	public boolean intersectsYZ(Vec3d vec) {
		return vec.y >= this.minY && vec.y <= this.maxY && vec.z >= this.minZ && vec.z <= this.maxZ;
	}

	@VisibleForTesting
	public boolean intersectsXZ(Vec3d vec) {
		return vec.x >= this.minX && vec.x <= this.maxX && vec.z >= this.minZ && vec.z <= this.maxZ;
	}

	@VisibleForTesting
	public boolean intersectsXY(Vec3d vec) {
		return vec.x >= this.minX && vec.x <= this.maxX && vec.y >= this.minY && vec.y <= this.maxY;
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

	public Vec3d getCenter() {
		return new Vec3d(this.minX + (this.maxX - this.minX) * 0.5, this.minY + (this.maxY - this.minY) * 0.5, this.minZ + (this.maxZ - this.minZ) * 0.5);
	}
}
