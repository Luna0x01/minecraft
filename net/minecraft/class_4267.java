package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class class_4267 implements class_4261 {
	private final double field_20952;
	private final double field_20953;
	private final double field_20954;

	public class_4267(double d, double e, double f) {
		this.field_20952 = d;
		this.field_20953 = e;
		this.field_20954 = f;
	}

	@Override
	public Vec3d method_19411(class_3915 arg) {
		Vec2f vec2f = arg.method_17472();
		Vec3d vec3d = arg.method_17474().method_17871(arg);
		float f = MathHelper.cos((vec2f.y + 90.0F) * (float) (Math.PI / 180.0));
		float g = MathHelper.sin((vec2f.y + 90.0F) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(-vec2f.x * (float) (Math.PI / 180.0));
		float i = MathHelper.sin(-vec2f.x * (float) (Math.PI / 180.0));
		float j = MathHelper.cos((-vec2f.x + 90.0F) * (float) (Math.PI / 180.0));
		float k = MathHelper.sin((-vec2f.x + 90.0F) * (float) (Math.PI / 180.0));
		Vec3d vec3d2 = new Vec3d((double)(f * h), (double)i, (double)(g * h));
		Vec3d vec3d3 = new Vec3d((double)(f * j), (double)k, (double)(g * j));
		Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).multiply(-1.0);
		double d = vec3d2.x * this.field_20954 + vec3d3.x * this.field_20953 + vec3d4.x * this.field_20952;
		double e = vec3d2.y * this.field_20954 + vec3d3.y * this.field_20953 + vec3d4.y * this.field_20952;
		double l = vec3d2.z * this.field_20954 + vec3d3.z * this.field_20953 + vec3d4.z * this.field_20952;
		return new Vec3d(vec3d.x + d, vec3d.y + e, vec3d.z + l);
	}

	@Override
	public Vec2f method_19413(class_3915 arg) {
		return Vec2f.ZERO;
	}

	@Override
	public boolean method_19410() {
		return true;
	}

	@Override
	public boolean method_19412() {
		return true;
	}

	@Override
	public boolean method_19414() {
		return true;
	}

	public static class_4267 method_19428(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		double d = method_19429(stringReader, i);
		if (stringReader.canRead() && stringReader.peek() == ' ') {
			stringReader.skip();
			double e = method_19429(stringReader, i);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				double f = method_19429(stringReader, i);
				return new class_4267(d, e, f);
			} else {
				stringReader.setCursor(i);
				throw class_4287.field_21051.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw class_4287.field_21051.createWithContext(stringReader);
		}
	}

	private static double method_19429(StringReader stringReader, int i) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw class_4298.field_21105.createWithContext(stringReader);
		} else if (stringReader.peek() != '^') {
			stringReader.setCursor(i);
			throw class_4287.field_21052.createWithContext(stringReader);
		} else {
			stringReader.skip();
			return stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readDouble() : 0.0;
		}
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_4267)) {
			return false;
		} else {
			class_4267 lv = (class_4267)object;
			return this.field_20952 == lv.field_20952 && this.field_20953 == lv.field_20953 && this.field_20954 == lv.field_20954;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_20952, this.field_20953, this.field_20954});
	}
}
