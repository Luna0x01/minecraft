package net.minecraft.util.math;

import javax.annotation.Nullable;

public class Vec3d {
	public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);
	public final double x;
	public final double y;
	public final double z;

	public Vec3d(double d, double e, double f) {
		if (d == -0.0) {
			d = 0.0;
		}

		if (e == -0.0) {
			e = 0.0;
		}

		if (f == -0.0) {
			f = 0.0;
		}

		this.x = d;
		this.y = e;
		this.z = f;
	}

	public Vec3d(Vec3i vec3i) {
		this((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ());
	}

	public Vec3d reverseSubtract(Vec3d vec) {
		return new Vec3d(vec.x - this.x, vec.y - this.y, vec.z - this.z);
	}

	public Vec3d normalize() {
		double d = (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		return d < 1.0E-4 ? ZERO : new Vec3d(this.x / d, this.y / d, this.z / d);
	}

	public double dotProduct(Vec3d vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}

	public Vec3d crossProduct(Vec3d vec) {
		return new Vec3d(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
	}

	public Vec3d subtract(Vec3d vec) {
		return this.subtract(vec.x, vec.y, vec.z);
	}

	public Vec3d subtract(double x, double y, double z) {
		return this.add(-x, -y, -z);
	}

	public Vec3d add(Vec3d vec) {
		return this.add(vec.x, vec.y, vec.z);
	}

	public Vec3d add(double x, double y, double z) {
		return new Vec3d(this.x + x, this.y + y, this.z + z);
	}

	public double distanceTo(Vec3d vec) {
		double d = vec.x - this.x;
		double e = vec.y - this.y;
		double f = vec.z - this.z;
		return (double)MathHelper.sqrt(d * d + e * e + f * f);
	}

	public double squaredDistanceTo(Vec3d vec) {
		double d = vec.x - this.x;
		double e = vec.y - this.y;
		double f = vec.z - this.z;
		return d * d + e * e + f * f;
	}

	public double method_12126(double d, double e, double f) {
		double g = d - this.x;
		double h = e - this.y;
		double i = f - this.z;
		return g * g + h * h + i * i;
	}

	public Vec3d multiply(double value) {
		return new Vec3d(this.x * value, this.y * value, this.z * value);
	}

	public double length() {
		return (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public double squaredLength() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	@Nullable
	public Vec3d method_12124(Vec3d vec3d, double d) {
		double e = vec3d.x - this.x;
		double f = vec3d.y - this.y;
		double g = vec3d.z - this.z;
		if (e * e < 1.0E-7F) {
			return null;
		} else {
			double h = (d - this.x) / e;
			return !(h < 0.0) && !(h > 1.0) ? new Vec3d(this.x + e * h, this.y + f * h, this.z + g * h) : null;
		}
	}

	@Nullable
	public Vec3d method_12125(Vec3d vec3d, double d) {
		double e = vec3d.x - this.x;
		double f = vec3d.y - this.y;
		double g = vec3d.z - this.z;
		if (f * f < 1.0E-7F) {
			return null;
		} else {
			double h = (d - this.y) / f;
			return !(h < 0.0) && !(h > 1.0) ? new Vec3d(this.x + e * h, this.y + f * h, this.z + g * h) : null;
		}
	}

	@Nullable
	public Vec3d method_12127(Vec3d vec3d, double d) {
		double e = vec3d.x - this.x;
		double f = vec3d.y - this.y;
		double g = vec3d.z - this.z;
		if (g * g < 1.0E-7F) {
			return null;
		} else {
			double h = (d - this.z) / g;
			return !(h < 0.0) && !(h > 1.0) ? new Vec3d(this.x + e * h, this.y + f * h, this.z + g * h) : null;
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Vec3d)) {
			return false;
		} else {
			Vec3d vec3d = (Vec3d)object;
			if (Double.compare(vec3d.x, this.x) != 0) {
				return false;
			} else {
				return Double.compare(vec3d.y, this.y) != 0 ? false : Double.compare(vec3d.z, this.z) == 0;
			}
		}
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.x);
		int i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.y);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.z);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public Vec3d rotateX(float degrees) {
		float f = MathHelper.cos(degrees);
		float g = MathHelper.sin(degrees);
		double d = this.x;
		double e = this.y * (double)f + this.z * (double)g;
		double h = this.z * (double)f - this.y * (double)g;
		return new Vec3d(d, e, h);
	}

	public Vec3d rotateY(float degrees) {
		float f = MathHelper.cos(degrees);
		float g = MathHelper.sin(degrees);
		double d = this.x * (double)f + this.z * (double)g;
		double e = this.y;
		double h = this.z * (double)f - this.x * (double)g;
		return new Vec3d(d, e, h);
	}

	public static Vec3d fromPolar(Vec2f polar) {
		return fromPolar(polar.x, polar.y);
	}

	public static Vec3d fromPolar(float x, float y) {
		float f = MathHelper.cos(-y * (float) (Math.PI / 180.0) - (float) Math.PI);
		float g = MathHelper.sin(-y * (float) (Math.PI / 180.0) - (float) Math.PI);
		float h = -MathHelper.cos(-x * (float) (Math.PI / 180.0));
		float i = MathHelper.sin(-x * (float) (Math.PI / 180.0));
		return new Vec3d((double)(g * h), (double)i, (double)(f * h));
	}
}
