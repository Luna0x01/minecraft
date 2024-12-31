package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
	public static final Vec3i ZERO = new Vec3i(0, 0, 0);
	private final int x;
	private final int y;
	private final int z;

	public Vec3i(int i, int j, int k) {
		this.x = i;
		this.y = j;
		this.z = k;
	}

	public Vec3i(double d, double e, double f) {
		this(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Vec3i)) {
			return false;
		} else {
			Vec3i vec3i = (Vec3i)object;
			if (this.getX() != vec3i.getX()) {
				return false;
			} else {
				return this.getY() != vec3i.getY() ? false : this.getZ() == vec3i.getZ();
			}
		}
	}

	public int hashCode() {
		return (this.getY() + this.getZ() * 31) * 31 + this.getX();
	}

	public int compareTo(Vec3i vec3i) {
		if (this.getY() == vec3i.getY()) {
			return this.getZ() == vec3i.getZ() ? this.getX() - vec3i.getX() : this.getZ() - vec3i.getZ();
		} else {
			return this.getY() - vec3i.getY();
		}
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public Vec3i crossProduct(Vec3i vec) {
		return new Vec3i(
			this.getY() * vec.getZ() - this.getZ() * vec.getY(),
			this.getZ() * vec.getX() - this.getX() * vec.getZ(),
			this.getX() * vec.getY() - this.getY() * vec.getX()
		);
	}

	public double distanceTo(int x, int y, int z) {
		double d = (double)(this.getX() - x);
		double e = (double)(this.getY() - y);
		double f = (double)(this.getZ() - z);
		return Math.sqrt(d * d + e * e + f * f);
	}

	public double squaredDistanceTo(double x, double y, double z) {
		double d = (double)this.getX() - x;
		double e = (double)this.getY() - y;
		double f = (double)this.getZ() - z;
		return d * d + e * e + f * f;
	}

	public double squaredDistanceToCenter(double x, double y, double z) {
		double d = (double)this.getX() + 0.5 - x;
		double e = (double)this.getY() + 0.5 - y;
		double f = (double)this.getZ() + 0.5 - z;
		return d * d + e * e + f * f;
	}

	public double getSquaredDistance(Vec3i vec) {
		return this.squaredDistanceTo((double)vec.getX(), (double)vec.getY(), (double)vec.getZ());
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
	}
}
