package net.minecraft;

import java.util.Arrays;
import net.minecraft.client.util.math.Matrix4f;

public class class_4307 {
	private final float[] field_21145;

	public class_4307(class_4307 arg) {
		this.field_21145 = Arrays.copyOf(arg.field_21145, 4);
	}

	public class_4307() {
		this.field_21145 = new float[4];
	}

	public class_4307(float f, float g, float h, float i) {
		this.field_21145 = new float[]{f, g, h, i};
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4307 lv = (class_4307)object;
			return Arrays.equals(this.field_21145, lv.field_21145);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.field_21145);
	}

	public float method_19673() {
		return this.field_21145[0];
	}

	public float method_19678() {
		return this.field_21145[1];
	}

	public float method_19679() {
		return this.field_21145[2];
	}

	public float method_19680() {
		return this.field_21145[3];
	}

	public void method_19677(class_4306 arg) {
		this.field_21145[0] = this.field_21145[0] * arg.method_19662();
		this.field_21145[1] = this.field_21145[1] * arg.method_19667();
		this.field_21145[2] = this.field_21145[2] * arg.method_19670();
	}

	public void method_19674(float f, float g, float h, float i) {
		this.field_21145[0] = f;
		this.field_21145[1] = g;
		this.field_21145[2] = h;
		this.field_21145[3] = i;
	}

	public void method_19675(Matrix4f matrix4f) {
		float[] fs = Arrays.copyOf(this.field_21145, 4);

		for (int i = 0; i < 4; i++) {
			this.field_21145[i] = 0.0F;

			for (int j = 0; j < 4; j++) {
				this.field_21145[i] = this.field_21145[i] + matrix4f.method_19645(i, j) * fs[j];
			}
		}
	}

	public void method_19676(class_4305 arg) {
		class_4305 lv = new class_4305(arg);
		lv.method_19657(new class_4305(this.method_19673(), this.method_19678(), this.method_19679(), 0.0F));
		class_4305 lv2 = new class_4305(arg);
		lv2.method_19661();
		lv.method_19657(lv2);
		this.method_19674(lv.method_19656(), lv.method_19658(), lv.method_19659(), this.method_19680());
	}
}
