package net.minecraft.util.math;

public class Vec3d {
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
		return d < 1.0E-4 ? new Vec3d(0.0, 0.0, 0.0) : new Vec3d(this.x / d, this.y / d, this.z / d);
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

	public double length() {
		return (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public Vec3d lerpForX(Vec3d other, double x) {
		double d = other.x - this.x;
		double e = other.y - this.y;
		double f = other.z - this.z;
		if (d * d < 1.0E-7F) {
			return null;
		} else {
			double g = (x - this.x) / d;
			return !(g < 0.0) && !(g > 1.0) ? new Vec3d(this.x + d * g, this.y + e * g, this.z + f * g) : null;
		}
	}

	public Vec3d lerpForY(Vec3d other, double y) {
		double d = other.x - this.x;
		double e = other.y - this.y;
		double f = other.z - this.z;
		if (e * e < 1.0E-7F) {
			return null;
		} else {
			double g = (y - this.y) / e;
			return !(g < 0.0) && !(g > 1.0) ? new Vec3d(this.x + d * g, this.y + e * g, this.z + f * g) : null;
		}
	}

	public Vec3d lerpForZ(Vec3d other, double z) {
		double d = other.x - this.x;
		double e = other.y - this.y;
		double f = other.z - this.z;
		if (f * f < 1.0E-7F) {
			return null;
		} else {
			double g = (z - this.z) / f;
			return !(g < 0.0) && !(g > 1.0) ? new Vec3d(this.x + d * g, this.y + e * g, this.z + f * g) : null;
		}
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
}
