package net.minecraft;

import java.util.Arrays;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public final class class_4306 {
	private final float[] field_21144;

	public class_4306(class_4306 arg) {
		this.field_21144 = Arrays.copyOf(arg.field_21144, 3);
	}

	public class_4306() {
		this.field_21144 = new float[3];
	}

	public class_4306(float f, float g, float h) {
		this.field_21144 = new float[]{f, g, h};
	}

	public class_4306(Direction direction) {
		Vec3i vec3i = direction.getVector();
		this.field_21144 = new float[]{(float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ()};
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4306 lv = (class_4306)object;
			return Arrays.equals(this.field_21144, lv.field_21144);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_21144);
	}

	public float method_19662() {
		return this.field_21144[0];
	}

	public float method_19667() {
		return this.field_21144[1];
	}

	public float method_19670() {
		return this.field_21144[2];
	}

	public void method_19663(float f) {
		for (int i = 0; i < 3; i++) {
			this.field_21144[i] = this.field_21144[i] * f;
		}
	}

	public void method_19664(float f, float g) {
		this.field_21144[0] = MathHelper.clamp(this.field_21144[0], f, g);
		this.field_21144[1] = MathHelper.clamp(this.field_21144[1], f, g);
		this.field_21144[2] = MathHelper.clamp(this.field_21144[2], f, g);
	}

	public void method_19665(float f, float g, float h) {
		this.field_21144[0] = f;
		this.field_21144[1] = g;
		this.field_21144[2] = h;
	}

	public void method_19668(float f, float g, float h) {
		this.field_21144[0] = this.field_21144[0] + f;
		this.field_21144[1] = this.field_21144[1] + g;
		this.field_21144[2] = this.field_21144[2] + h;
	}

	public void method_19666(class_4306 arg) {
		for (int i = 0; i < 3; i++) {
			this.field_21144[i] = this.field_21144[i] - arg.field_21144[i];
		}
	}

	public float method_19669(class_4306 arg) {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.field_21144[i] * arg.field_21144[i];
		}

		return f;
	}

	public void method_19672() {
		float f = 0.0F;

		for (int i = 0; i < 3; i++) {
			f += this.field_21144[i] * this.field_21144[i];
		}

		for (int j = 0; j < 3; j++) {
			this.field_21144[j] = this.field_21144[j] / f;
		}
	}

	public void method_19671(class_4306 arg) {
		float f = this.field_21144[0];
		float g = this.field_21144[1];
		float h = this.field_21144[2];
		float i = arg.method_19662();
		float j = arg.method_19667();
		float k = arg.method_19670();
		this.field_21144[0] = g * k - h * j;
		this.field_21144[1] = h * i - f * k;
		this.field_21144[2] = f * j - g * i;
	}
}
