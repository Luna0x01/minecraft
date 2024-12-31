package net.minecraft.util.math;

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

	public double getMin(Direction.Axis axis) {
		return axis.method_19947(this.minX, this.minY, this.minZ);
	}

	public double getMax(Direction.Axis axis) {
		return axis.method_19947(this.maxX, this.maxY, this.maxZ);
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
		return this.method_18007(vec.x, vec.y, vec.z);
	}

	public boolean method_18007(double d, double e, double f) {
		return d >= this.minX && d < this.maxX && e >= this.minY && e < this.maxY && f >= this.minZ && f < this.maxZ;
	}

	public double getAverage() {
		double d = this.maxX - this.minX;
		double e = this.maxY - this.minY;
		double f = this.maxZ - this.minZ;
		return (d + e + f) / 3.0;
	}

	public Box method_18008(double d, double e, double f) {
		return this.expand(-d, -e, -f);
	}

	public Box contract(double value) {
		return this.expand(-value);
	}

	@Nullable
	public BlockHitResult method_585(Vec3d vec1, Vec3d vec2) {
		return this.method_18002(vec1, vec2, null);
	}

	@Nullable
	public BlockHitResult method_18002(Vec3d vec3d, Vec3d vec3d2, @Nullable BlockPos blockPos) {
		double[] ds = new double[]{1.0};
		Direction direction = null;
		double d = vec3d2.x - vec3d.x;
		double e = vec3d2.y - vec3d.y;
		double f = vec3d2.z - vec3d.z;
		direction = method_18001(blockPos == null ? this : this.offset(blockPos), vec3d, ds, direction, d, e, f);
		if (direction == null) {
			return null;
		} else {
			double g = ds[0];
			return new BlockHitResult(vec3d.add(g * d, g * e, g * f), direction, blockPos == null ? BlockPos.ORIGIN : blockPos);
		}
	}

	@Nullable
	public static BlockHitResult rayTrace(Iterable<Box> iterable, Vec3d vec3d, Vec3d vec3d2, BlockPos blockPos) {
		double[] ds = new double[]{1.0};
		Direction direction = null;
		double d = vec3d2.x - vec3d.x;
		double e = vec3d2.y - vec3d.y;
		double f = vec3d2.z - vec3d.z;

		for (Box box : iterable) {
			direction = method_18001(box.offset(blockPos), vec3d, ds, direction, d, e, f);
		}

		if (direction == null) {
			return null;
		} else {
			double g = ds[0];
			return new BlockHitResult(vec3d.add(g * d, g * e, g * f), direction, blockPos);
		}
	}

	@Nullable
	private static Direction method_18001(Box box, Vec3d vec3d, double[] ds, @Nullable Direction direction, double d, double e, double f) {
		if (d > 1.0E-7) {
			direction = method_18005(ds, direction, d, e, f, box.minX, box.minY, box.maxY, box.minZ, box.maxZ, Direction.WEST, vec3d.x, vec3d.y, vec3d.z);
		} else if (d < -1.0E-7) {
			direction = method_18005(ds, direction, d, e, f, box.maxX, box.minY, box.maxY, box.minZ, box.maxZ, Direction.EAST, vec3d.x, vec3d.y, vec3d.z);
		}

		if (e > 1.0E-7) {
			direction = method_18005(ds, direction, e, f, d, box.minY, box.minZ, box.maxZ, box.minX, box.maxX, Direction.DOWN, vec3d.y, vec3d.z, vec3d.x);
		} else if (e < -1.0E-7) {
			direction = method_18005(ds, direction, e, f, d, box.maxY, box.minZ, box.maxZ, box.minX, box.maxX, Direction.UP, vec3d.y, vec3d.z, vec3d.x);
		}

		if (f > 1.0E-7) {
			direction = method_18005(ds, direction, f, d, e, box.minZ, box.minX, box.maxX, box.minY, box.maxY, Direction.NORTH, vec3d.z, vec3d.x, vec3d.y);
		} else if (f < -1.0E-7) {
			direction = method_18005(ds, direction, f, d, e, box.maxZ, box.minX, box.maxX, box.minY, box.maxY, Direction.SOUTH, vec3d.z, vec3d.x, vec3d.y);
		}

		return direction;
	}

	@Nullable
	private static Direction method_18005(
		double[] ds,
		@Nullable Direction direction,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		double j,
		double k,
		Direction direction2,
		double l,
		double m,
		double n
	) {
		double o = (g - l) / d;
		double p = m + o * e;
		double q = n + o * f;
		if (0.0 < o && o < ds[0] && h - 1.0E-7 < p && p < i + 1.0E-7 && j - 1.0E-7 < q && q < k + 1.0E-7) {
			ds[0] = o;
			return direction2;
		} else {
			return direction;
		}
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
