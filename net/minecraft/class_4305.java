package net.minecraft;

import java.util.Arrays;
import net.minecraft.util.math.MathHelper;

public final class class_4305 {
	private final float[] field_21143;

	public class_4305() {
		this.field_21143 = new float[4];
		this.field_21143[4] = 1.0F;
	}

	public class_4305(float f, float g, float h, float i) {
		this.field_21143 = new float[4];
		this.field_21143[0] = f;
		this.field_21143[1] = g;
		this.field_21143[2] = h;
		this.field_21143[3] = i;
	}

	public class_4305(class_4306 arg, float f, boolean bl) {
		if (bl) {
			f *= (float) (Math.PI / 180.0);
		}

		float g = MathHelper.sin(f / 2.0F);
		this.field_21143 = new float[4];
		this.field_21143[0] = arg.method_19662() * g;
		this.field_21143[1] = arg.method_19667() * g;
		this.field_21143[2] = arg.method_19670() * g;
		this.field_21143[3] = MathHelper.cos(f / 2.0F);
	}

	public class_4305(float f, float g, float h, boolean bl) {
		if (bl) {
			f *= (float) (Math.PI / 180.0);
			g *= (float) (Math.PI / 180.0);
			h *= (float) (Math.PI / 180.0);
		}

		float i = MathHelper.sin(0.5F * f);
		float j = MathHelper.cos(0.5F * f);
		float k = MathHelper.sin(0.5F * g);
		float l = MathHelper.cos(0.5F * g);
		float m = MathHelper.sin(0.5F * h);
		float n = MathHelper.cos(0.5F * h);
		this.field_21143 = new float[4];
		this.field_21143[0] = i * l * n + j * k * m;
		this.field_21143[1] = j * k * n - i * l * m;
		this.field_21143[2] = i * k * n + j * l * m;
		this.field_21143[3] = j * l * n - i * k * m;
	}

	public class_4305(class_4305 arg) {
		this.field_21143 = Arrays.copyOf(arg.field_21143, 4);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4305 lv = (class_4305)object;
			return Arrays.equals(this.field_21143, lv.field_21143);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_21143);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Quaternion[").append(this.method_19660()).append(" + ");
		stringBuilder.append(this.method_19656()).append("i + ");
		stringBuilder.append(this.method_19658()).append("j + ");
		stringBuilder.append(this.method_19659()).append("k]");
		return stringBuilder.toString();
	}

	public float method_19656() {
		return this.field_21143[0];
	}

	public float method_19658() {
		return this.field_21143[1];
	}

	public float method_19659() {
		return this.field_21143[2];
	}

	public float method_19660() {
		return this.field_21143[3];
	}

	public void method_19657(class_4305 arg) {
		float f = this.method_19656();
		float g = this.method_19658();
		float h = this.method_19659();
		float i = this.method_19660();
		float j = arg.method_19656();
		float k = arg.method_19658();
		float l = arg.method_19659();
		float m = arg.method_19660();
		this.field_21143[0] = i * j + f * m + g * l - h * k;
		this.field_21143[1] = i * k - f * l + g * m + h * j;
		this.field_21143[2] = i * l + f * k - g * j + h * m;
		this.field_21143[3] = i * m - f * j - g * k - h * l;
	}

	public void method_19661() {
		this.field_21143[0] = -this.field_21143[0];
		this.field_21143[1] = -this.field_21143[1];
		this.field_21143[2] = -this.field_21143[2];
	}
}
