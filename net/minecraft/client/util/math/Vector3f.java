package net.minecraft.client.util.math;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class Vector3f {
	public static Vector3f NEGATIVE_X = new Vector3f(-1.0F, 0.0F, 0.0F);
	public static Vector3f POSITIVE_X = new Vector3f(1.0F, 0.0F, 0.0F);
	public static Vector3f NEGATIVE_Y = new Vector3f(0.0F, -1.0F, 0.0F);
	public static Vector3f POSITIVE_Y = new Vector3f(0.0F, 1.0F, 0.0F);
	public static Vector3f NEGATIVE_Z = new Vector3f(0.0F, 0.0F, -1.0F);
	public static Vector3f POSITIVE_Z = new Vector3f(0.0F, 0.0F, 1.0F);
	private float x;
	private float y;
	private float z;

	public Vector3f() {
	}

	public Vector3f(float f, float g, float h) {
		this.x = f;
		this.y = g;
		this.z = h;
	}

	public Vector3f(Vec3d vec3d) {
		this((float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Vector3f vector3f = (Vector3f)object;
			if (Float.compare(vector3f.x, this.x) != 0) {
				return false;
			} else {
				return Float.compare(vector3f.y, this.y) != 0 ? false : Float.compare(vector3f.z, this.z) == 0;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = Float.floatToIntBits(this.x);
		i = 31 * i + Float.floatToIntBits(this.y);
		return 31 * i + Float.floatToIntBits(this.z);
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public void scale(float f) {
		this.x *= f;
		this.y *= f;
		this.z *= f;
	}

	public void multiplyComponentwise(float f, float g, float h) {
		this.x *= f;
		this.y *= g;
		this.z *= h;
	}

	public void clamp(float f, float g) {
		this.x = MathHelper.clamp(this.x, f, g);
		this.y = MathHelper.clamp(this.y, f, g);
		this.z = MathHelper.clamp(this.z, f, g);
	}

	public void set(float f, float g, float h) {
		this.x = f;
		this.y = g;
		this.z = h;
	}

	public void add(float f, float g, float h) {
		this.x += f;
		this.y += g;
		this.z += h;
	}

	public void add(Vector3f vector3f) {
		this.x = this.x + vector3f.x;
		this.y = this.y + vector3f.y;
		this.z = this.z + vector3f.z;
	}

	public void subtract(Vector3f vector3f) {
		this.x = this.x - vector3f.x;
		this.y = this.y - vector3f.y;
		this.z = this.z - vector3f.z;
	}

	public float dot(Vector3f vector3f) {
		return this.x * vector3f.x + this.y * vector3f.y + this.z * vector3f.z;
	}

	public boolean normalize() {
		float f = this.x * this.x + this.y * this.y + this.z * this.z;
		if ((double)f < 1.0E-5) {
			return false;
		} else {
			float g = MathHelper.fastInverseSqrt(f);
			this.x *= g;
			this.y *= g;
			this.z *= g;
			return true;
		}
	}

	public void cross(Vector3f vector3f) {
		float f = this.x;
		float g = this.y;
		float h = this.z;
		float i = vector3f.getX();
		float j = vector3f.getY();
		float k = vector3f.getZ();
		this.x = g * k - h * j;
		this.y = h * i - f * k;
		this.z = f * j - g * i;
	}

	public void transform(Matrix3f matrix3f) {
		float f = this.x;
		float g = this.y;
		float h = this.z;
		this.x = matrix3f.a00 * f + matrix3f.a01 * g + matrix3f.a02 * h;
		this.y = matrix3f.a10 * f + matrix3f.a11 * g + matrix3f.a12 * h;
		this.z = matrix3f.a20 * f + matrix3f.a21 * g + matrix3f.a22 * h;
	}

	public void rotate(Quaternion quaternion) {
		Quaternion quaternion2 = new Quaternion(quaternion);
		quaternion2.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
		Quaternion quaternion3 = new Quaternion(quaternion);
		quaternion3.conjugate();
		quaternion2.hamiltonProduct(quaternion3);
		this.set(quaternion2.getB(), quaternion2.getC(), quaternion2.getD());
	}

	public void lerp(Vector3f vector3f, float f) {
		float g = 1.0F - f;
		this.x = this.x * g + vector3f.x * f;
		this.y = this.y * g + vector3f.y * f;
		this.z = this.z * g + vector3f.z * f;
	}

	public Quaternion getRadialQuaternion(float f) {
		return new Quaternion(this, f, false);
	}

	public Quaternion getDegreesQuaternion(float f) {
		return new Quaternion(this, f, true);
	}

	public Vector3f copy() {
		return new Vector3f(this.x, this.y, this.z);
	}

	public void modify(Float2FloatFunction float2FloatFunction) {
		this.x = float2FloatFunction.get(this.x);
		this.y = float2FloatFunction.get(this.y);
		this.z = float2FloatFunction.get(this.z);
	}

	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + "]";
	}
}
