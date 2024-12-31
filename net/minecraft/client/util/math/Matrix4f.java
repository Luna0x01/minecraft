package net.minecraft.client.util.math;

import java.nio.FloatBuffer;
import java.util.Arrays;
import net.minecraft.class_4305;

public final class Matrix4f {
	private final float[] field_21142;

	public Matrix4f() {
		this.field_21142 = new float[16];
	}

	public Matrix4f(class_4305 arg) {
		this();
		float f = arg.method_19656();
		float g = arg.method_19658();
		float h = arg.method_19659();
		float i = arg.method_19660();
		float j = 2.0F * f * f;
		float k = 2.0F * g * g;
		float l = 2.0F * h * h;
		this.field_21142[0] = 1.0F - k - l;
		this.field_21142[5] = 1.0F - l - j;
		this.field_21142[10] = 1.0F - j - k;
		this.field_21142[15] = 1.0F;
		float m = f * g;
		float n = g * h;
		float o = h * f;
		float p = f * i;
		float q = g * i;
		float r = h * i;
		this.field_21142[1] = 2.0F * (m + r);
		this.field_21142[4] = 2.0F * (m - r);
		this.field_21142[2] = 2.0F * (o - q);
		this.field_21142[8] = 2.0F * (o + q);
		this.field_21142[6] = 2.0F * (n + p);
		this.field_21142[9] = 2.0F * (n - p);
	}

	public Matrix4f(Matrix4f matrix4f) {
		this.field_21142 = Arrays.copyOf(matrix4f.field_21142, 16);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Matrix4f matrix4f = (Matrix4f)object;
			return Arrays.equals(this.field_21142, matrix4f.field_21142);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_21142);
	}

	public void method_19648(FloatBuffer floatBuffer) {
		this.method_19649(floatBuffer, false);
	}

	public void method_19649(FloatBuffer floatBuffer, boolean bl) {
		if (bl) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					this.field_21142[i * 4 + j] = floatBuffer.get(j * 4 + i);
				}
			}
		} else {
			floatBuffer.get(this.field_21142);
		}
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Matrix4f:\n");

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				stringBuilder.append(this.field_21142[i + j * 4]);
				if (j != 3) {
					stringBuilder.append(" ");
				}
			}

			stringBuilder.append("\n");
		}

		return stringBuilder.toString();
	}

	public void method_19652(FloatBuffer floatBuffer) {
		this.method_19653(floatBuffer, false);
	}

	public void method_19653(FloatBuffer floatBuffer, boolean bl) {
		if (bl) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					floatBuffer.put(j * 4 + i, this.field_21142[i * 4 + j]);
				}
			}
		} else {
			floatBuffer.put(this.field_21142);
		}
	}

	public void method_19641() {
		this.field_21142[0] = 1.0F;
		this.field_21142[1] = 0.0F;
		this.field_21142[2] = 0.0F;
		this.field_21142[3] = 0.0F;
		this.field_21142[4] = 0.0F;
		this.field_21142[5] = 1.0F;
		this.field_21142[6] = 0.0F;
		this.field_21142[7] = 0.0F;
		this.field_21142[8] = 0.0F;
		this.field_21142[9] = 0.0F;
		this.field_21142[10] = 1.0F;
		this.field_21142[11] = 0.0F;
		this.field_21142[12] = 0.0F;
		this.field_21142[13] = 0.0F;
		this.field_21142[14] = 0.0F;
		this.field_21142[15] = 1.0F;
	}

	public float method_19645(int i, int j) {
		return this.field_21142[i + 4 * j];
	}

	public void method_19646(int i, int j, float f) {
		this.field_21142[i + 4 * j] = f;
	}

	public void method_19647(Matrix4f matrix4f) {
		float[] fs = Arrays.copyOf(this.field_21142, 16);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				this.field_21142[i + j * 4] = 0.0F;

				for (int k = 0; k < 4; k++) {
					this.field_21142[i + j * 4] = this.field_21142[i + j * 4] + fs[i + k * 4] * matrix4f.field_21142[k + j * 4];
				}
			}
		}
	}

	public void method_19643(float f) {
		for (int i = 0; i < 16; i++) {
			this.field_21142[i] = this.field_21142[i] * f;
		}
	}

	public void method_19651(Matrix4f matrix4f) {
		for (int i = 0; i < 16; i++) {
			this.field_21142[i] = this.field_21142[i] + matrix4f.field_21142[i];
		}
	}

	public void method_19655(Matrix4f matrix4f) {
		for (int i = 0; i < 16; i++) {
			this.field_21142[i] = this.field_21142[i] - matrix4f.field_21142[i];
		}
	}

	public float method_19650() {
		float f = 0.0F;

		for (int i = 0; i < 4; i++) {
			f += this.field_21142[i + 4 * i];
		}

		return f;
	}

	public void method_19654() {
		Matrix4f matrix4f = new Matrix4f();
		Matrix4f matrix4f2 = new Matrix4f(this);
		Matrix4f matrix4f3 = new Matrix4f(this);
		matrix4f2.method_19647(this);
		matrix4f3.method_19647(matrix4f2);
		float f = this.method_19650();
		float g = matrix4f2.method_19650();
		float h = matrix4f3.method_19650();
		this.method_19643((g - f * f) / 2.0F);
		matrix4f.method_19641();
		matrix4f.method_19643((f * f * f - 3.0F * f * g + 2.0F * h) / 6.0F);
		this.method_19651(matrix4f);
		matrix4f2.method_19643(f);
		this.method_19651(matrix4f2);
		this.method_19655(matrix4f3);
	}

	public static Matrix4f method_19642(double d, float f, float g, float h) {
		float i = (float)(1.0 / Math.tan(d * (float) (Math.PI / 180.0) / 2.0));
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_19646(0, 0, i / f);
		matrix4f.method_19646(1, 1, i);
		matrix4f.method_19646(2, 2, (h + g) / (g - h));
		matrix4f.method_19646(3, 2, -1.0F);
		matrix4f.method_19646(2, 3, 2.0F * h * g / (g - h));
		return matrix4f;
	}

	public static Matrix4f method_19644(float f, float g, float h, float i) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_19646(0, 0, 2.0F / f);
		matrix4f.method_19646(1, 1, 2.0F / g);
		float j = i - h;
		matrix4f.method_19646(2, 2, -2.0F / j);
		matrix4f.method_19646(3, 3, 1.0F);
		matrix4f.method_19646(0, 3, -1.0F);
		matrix4f.method_19646(1, 3, -1.0F);
		matrix4f.method_19646(2, 3, -(i + h) / j);
		return matrix4f;
	}
}
