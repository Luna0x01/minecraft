package net.minecraft.util.math;

import net.minecraft.client.util.math.Vector3f;

public final class Quaternion {
	public static final Quaternion IDENTITY = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
	private float b;
	private float c;
	private float d;
	private float a;

	public Quaternion(float f, float g, float h, float i) {
		this.b = f;
		this.c = g;
		this.d = h;
		this.a = i;
	}

	public Quaternion(Vector3f vector3f, float f, boolean bl) {
		if (bl) {
			f *= (float) (Math.PI / 180.0);
		}

		float g = sin(f / 2.0F);
		this.b = vector3f.getX() * g;
		this.c = vector3f.getY() * g;
		this.d = vector3f.getZ() * g;
		this.a = cos(f / 2.0F);
	}

	public Quaternion(float f, float g, float h, boolean bl) {
		if (bl) {
			f *= (float) (Math.PI / 180.0);
			g *= (float) (Math.PI / 180.0);
			h *= (float) (Math.PI / 180.0);
		}

		float i = sin(0.5F * f);
		float j = cos(0.5F * f);
		float k = sin(0.5F * g);
		float l = cos(0.5F * g);
		float m = sin(0.5F * h);
		float n = cos(0.5F * h);
		this.b = i * l * n + j * k * m;
		this.c = j * k * n - i * l * m;
		this.d = i * k * n + j * l * m;
		this.a = j * l * n - i * k * m;
	}

	public Quaternion(Quaternion quaternion) {
		this.b = quaternion.b;
		this.c = quaternion.c;
		this.d = quaternion.d;
		this.a = quaternion.a;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Quaternion quaternion = (Quaternion)object;
			if (Float.compare(quaternion.b, this.b) != 0) {
				return false;
			} else if (Float.compare(quaternion.c, this.c) != 0) {
				return false;
			} else {
				return Float.compare(quaternion.d, this.d) != 0 ? false : Float.compare(quaternion.a, this.a) == 0;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = Float.floatToIntBits(this.b);
		i = 31 * i + Float.floatToIntBits(this.c);
		i = 31 * i + Float.floatToIntBits(this.d);
		return 31 * i + Float.floatToIntBits(this.a);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Quaternion[").append(this.getA()).append(" + ");
		stringBuilder.append(this.getB()).append("i + ");
		stringBuilder.append(this.getC()).append("j + ");
		stringBuilder.append(this.getD()).append("k]");
		return stringBuilder.toString();
	}

	public float getB() {
		return this.b;
	}

	public float getC() {
		return this.c;
	}

	public float getD() {
		return this.d;
	}

	public float getA() {
		return this.a;
	}

	public void hamiltonProduct(Quaternion quaternion) {
		float f = this.getB();
		float g = this.getC();
		float h = this.getD();
		float i = this.getA();
		float j = quaternion.getB();
		float k = quaternion.getC();
		float l = quaternion.getD();
		float m = quaternion.getA();
		this.b = i * j + f * m + g * l - h * k;
		this.c = i * k - f * l + g * m + h * j;
		this.d = i * l + f * k - g * j + h * m;
		this.a = i * m - f * j - g * k - h * l;
	}

	public void scale(float f) {
		this.b *= f;
		this.c *= f;
		this.d *= f;
		this.a *= f;
	}

	public void conjugate() {
		this.b = -this.b;
		this.c = -this.c;
		this.d = -this.d;
	}

	public void set(float f, float g, float h, float i) {
		this.b = f;
		this.c = g;
		this.d = h;
		this.a = i;
	}

	private static float cos(float f) {
		return (float)Math.cos((double)f);
	}

	private static float sin(float f) {
		return (float)Math.sin((double)f);
	}

	public void normalize() {
		float f = this.getB() * this.getB() + this.getC() * this.getC() + this.getD() * this.getD() + this.getA() * this.getA();
		if (f > 1.0E-6F) {
			float g = MathHelper.fastInverseSqrt(f);
			this.b *= g;
			this.c *= g;
			this.d *= g;
			this.a *= g;
		} else {
			this.b = 0.0F;
			this.c = 0.0F;
			this.d = 0.0F;
			this.a = 0.0F;
		}
	}

	public Quaternion copy() {
		return new Quaternion(this);
	}
}
