package net.minecraft.util.math;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.hit.BlockHitResult;

public class Box {
	public final double x1;
	public final double y1;
	public final double z1;
	public final double x2;
	public final double y2;
	public final double z2;

	public Box(double d, double e, double f, double g, double h, double i) {
		this.x1 = Math.min(d, g);
		this.y1 = Math.min(e, h);
		this.z1 = Math.min(f, i);
		this.x2 = Math.max(d, g);
		this.y2 = Math.max(e, h);
		this.z2 = Math.max(f, i);
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

	public static Box from(BlockBox blockBox) {
		return new Box(
			(double)blockBox.minX, (double)blockBox.minY, (double)blockBox.minZ, (double)(blockBox.maxX + 1), (double)(blockBox.maxY + 1), (double)(blockBox.maxZ + 1)
		);
	}

	public double getMin(Direction.Axis axis) {
		return axis.choose(this.x1, this.y1, this.z1);
	}

	public double getMax(Direction.Axis axis) {
		return axis.choose(this.x2, this.y2, this.z2);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Box)) {
			return false;
		} else {
			Box box = (Box)object;
			if (Double.compare(box.x1, this.x1) != 0) {
				return false;
			} else if (Double.compare(box.y1, this.y1) != 0) {
				return false;
			} else if (Double.compare(box.z1, this.z1) != 0) {
				return false;
			} else if (Double.compare(box.x2, this.x2) != 0) {
				return false;
			} else {
				return Double.compare(box.y2, this.y2) != 0 ? false : Double.compare(box.z2, this.z2) == 0;
			}
		}
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.x1);
		int i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.y1);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.z1);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.x2);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.y2);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.z2);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public Box shrink(double d, double e, double f) {
		double g = this.x1;
		double h = this.y1;
		double i = this.z1;
		double j = this.x2;
		double k = this.y2;
		double l = this.z2;
		if (d < 0.0) {
			g -= d;
		} else if (d > 0.0) {
			j -= d;
		}

		if (e < 0.0) {
			h -= e;
		} else if (e > 0.0) {
			k -= e;
		}

		if (f < 0.0) {
			i -= f;
		} else if (f > 0.0) {
			l -= f;
		}

		return new Box(g, h, i, j, k, l);
	}

	public Box stretch(Vec3d vec3d) {
		return this.stretch(vec3d.x, vec3d.y, vec3d.z);
	}

	public Box stretch(double d, double e, double f) {
		double g = this.x1;
		double h = this.y1;
		double i = this.z1;
		double j = this.x2;
		double k = this.y2;
		double l = this.z2;
		if (d < 0.0) {
			g += d;
		} else if (d > 0.0) {
			j += d;
		}

		if (e < 0.0) {
			h += e;
		} else if (e > 0.0) {
			k += e;
		}

		if (f < 0.0) {
			i += f;
		} else if (f > 0.0) {
			l += f;
		}

		return new Box(g, h, i, j, k, l);
	}

	public Box expand(double d, double e, double f) {
		double g = this.x1 - d;
		double h = this.y1 - e;
		double i = this.z1 - f;
		double j = this.x2 + d;
		double k = this.y2 + e;
		double l = this.z2 + f;
		return new Box(g, h, i, j, k, l);
	}

	public Box expand(double d) {
		return this.expand(d, d, d);
	}

	public Box intersection(Box box) {
		double d = Math.max(this.x1, box.x1);
		double e = Math.max(this.y1, box.y1);
		double f = Math.max(this.z1, box.z1);
		double g = Math.min(this.x2, box.x2);
		double h = Math.min(this.y2, box.y2);
		double i = Math.min(this.z2, box.z2);
		return new Box(d, e, f, g, h, i);
	}

	public Box union(Box box) {
		double d = Math.min(this.x1, box.x1);
		double e = Math.min(this.y1, box.y1);
		double f = Math.min(this.z1, box.z1);
		double g = Math.max(this.x2, box.x2);
		double h = Math.max(this.y2, box.y2);
		double i = Math.max(this.z2, box.z2);
		return new Box(d, e, f, g, h, i);
	}

	public Box offset(double d, double e, double f) {
		return new Box(this.x1 + d, this.y1 + e, this.z1 + f, this.x2 + d, this.y2 + e, this.z2 + f);
	}

	public Box offset(BlockPos blockPos) {
		return new Box(
			this.x1 + (double)blockPos.getX(),
			this.y1 + (double)blockPos.getY(),
			this.z1 + (double)blockPos.getZ(),
			this.x2 + (double)blockPos.getX(),
			this.y2 + (double)blockPos.getY(),
			this.z2 + (double)blockPos.getZ()
		);
	}

	public Box offset(Vec3d vec3d) {
		return this.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	public boolean intersects(Box box) {
		return this.intersects(box.x1, box.y1, box.z1, box.x2, box.y2, box.z2);
	}

	public boolean intersects(double d, double e, double f, double g, double h, double i) {
		return this.x1 < g && this.x2 > d && this.y1 < h && this.y2 > e && this.z1 < i && this.z2 > f;
	}

	public boolean intersects(Vec3d vec3d, Vec3d vec3d2) {
		return this.intersects(
			Math.min(vec3d.x, vec3d2.x),
			Math.min(vec3d.y, vec3d2.y),
			Math.min(vec3d.z, vec3d2.z),
			Math.max(vec3d.x, vec3d2.x),
			Math.max(vec3d.y, vec3d2.y),
			Math.max(vec3d.z, vec3d2.z)
		);
	}

	public boolean contains(Vec3d vec3d) {
		return this.contains(vec3d.x, vec3d.y, vec3d.z);
	}

	public boolean contains(double d, double e, double f) {
		return d >= this.x1 && d < this.x2 && e >= this.y1 && e < this.y2 && f >= this.z1 && f < this.z2;
	}

	public double getAverageSideLength() {
		double d = this.getXLength();
		double e = this.getYLength();
		double f = this.getZLength();
		return (d + e + f) / 3.0;
	}

	public double getXLength() {
		return this.x2 - this.x1;
	}

	public double getYLength() {
		return this.y2 - this.y1;
	}

	public double getZLength() {
		return this.z2 - this.z1;
	}

	public Box contract(double d) {
		return this.expand(-d);
	}

	public Optional<Vec3d> rayTrace(Vec3d vec3d, Vec3d vec3d2) {
		double[] ds = new double[]{1.0};
		double d = vec3d2.x - vec3d.x;
		double e = vec3d2.y - vec3d.y;
		double f = vec3d2.z - vec3d.z;
		Direction direction = traceCollisionSide(this, vec3d, ds, null, d, e, f);
		if (direction == null) {
			return Optional.empty();
		} else {
			double g = ds[0];
			return Optional.of(vec3d.add(g * d, g * e, g * f));
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
			direction = traceCollisionSide(box.offset(blockPos), vec3d, ds, direction, d, e, f);
		}

		if (direction == null) {
			return null;
		} else {
			double g = ds[0];
			return new BlockHitResult(vec3d.add(g * d, g * e, g * f), direction, blockPos, false);
		}
	}

	@Nullable
	private static Direction traceCollisionSide(Box box, Vec3d vec3d, double[] ds, @Nullable Direction direction, double d, double e, double f) {
		if (d > 1.0E-7) {
			direction = traceCollisionSide(ds, direction, d, e, f, box.x1, box.y1, box.y2, box.z1, box.z2, Direction.field_11039, vec3d.x, vec3d.y, vec3d.z);
		} else if (d < -1.0E-7) {
			direction = traceCollisionSide(ds, direction, d, e, f, box.x2, box.y1, box.y2, box.z1, box.z2, Direction.field_11034, vec3d.x, vec3d.y, vec3d.z);
		}

		if (e > 1.0E-7) {
			direction = traceCollisionSide(ds, direction, e, f, d, box.y1, box.z1, box.z2, box.x1, box.x2, Direction.field_11033, vec3d.y, vec3d.z, vec3d.x);
		} else if (e < -1.0E-7) {
			direction = traceCollisionSide(ds, direction, e, f, d, box.y2, box.z1, box.z2, box.x1, box.x2, Direction.field_11036, vec3d.y, vec3d.z, vec3d.x);
		}

		if (f > 1.0E-7) {
			direction = traceCollisionSide(ds, direction, f, d, e, box.z1, box.x1, box.x2, box.y1, box.y2, Direction.field_11043, vec3d.z, vec3d.x, vec3d.y);
		} else if (f < -1.0E-7) {
			direction = traceCollisionSide(ds, direction, f, d, e, box.z2, box.x1, box.x2, box.y1, box.y2, Direction.field_11035, vec3d.z, vec3d.x, vec3d.y);
		}

		return direction;
	}

	@Nullable
	private static Direction traceCollisionSide(
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
		return "box[" + this.x1 + ", " + this.y1 + ", " + this.z1 + "] -> [" + this.x2 + ", " + this.y2 + ", " + this.z2 + "]";
	}

	public boolean isValid() {
		return Double.isNaN(this.x1) || Double.isNaN(this.y1) || Double.isNaN(this.z1) || Double.isNaN(this.x2) || Double.isNaN(this.y2) || Double.isNaN(this.z2);
	}

	public Vec3d getCenter() {
		return new Vec3d(MathHelper.lerp(0.5, this.x1, this.x2), MathHelper.lerp(0.5, this.y1, this.y2), MathHelper.lerp(0.5, this.z1, this.z2));
	}
}
