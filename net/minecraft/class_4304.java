package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class class_4304 implements class_4261 {
	private final class_4298 field_21139;
	private final class_4298 field_21140;
	private final class_4298 field_21141;

	public class_4304(class_4298 arg, class_4298 arg2, class_4298 arg3) {
		this.field_21139 = arg;
		this.field_21140 = arg2;
		this.field_21141 = arg3;
	}

	@Override
	public Vec3d method_19411(class_3915 arg) {
		Vec3d vec3d = arg.method_17467();
		return new Vec3d(this.field_21139.method_19607(vec3d.x), this.field_21140.method_19607(vec3d.y), this.field_21141.method_19607(vec3d.z));
	}

	@Override
	public Vec2f method_19413(class_3915 arg) {
		Vec2f vec2f = arg.method_17472();
		return new Vec2f((float)this.field_21139.method_19607((double)vec2f.x), (float)this.field_21140.method_19607((double)vec2f.y));
	}

	@Override
	public boolean method_19410() {
		return this.field_21139.method_19606();
	}

	@Override
	public boolean method_19412() {
		return this.field_21140.method_19606();
	}

	@Override
	public boolean method_19414() {
		return this.field_21141.method_19606();
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_4304)) {
			return false;
		} else {
			class_4304 lv = (class_4304)object;
			if (!this.field_21139.equals(lv.field_21139)) {
				return false;
			} else {
				return !this.field_21140.equals(lv.field_21140) ? false : this.field_21141.equals(lv.field_21141);
			}
		}
	}

	public static class_4304 method_19636(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		class_4298 lv = class_4298.method_19608(stringReader);
		if (stringReader.canRead() && stringReader.peek() == ' ') {
			stringReader.skip();
			class_4298 lv2 = class_4298.method_19608(stringReader);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_4298 lv3 = class_4298.method_19608(stringReader);
				return new class_4304(lv, lv2, lv3);
			} else {
				stringReader.setCursor(i);
				throw class_4287.field_21051.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw class_4287.field_21051.createWithContext(stringReader);
		}
	}

	public static class_4304 method_19637(StringReader stringReader, boolean bl) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		class_4298 lv = class_4298.method_19609(stringReader, bl);
		if (stringReader.canRead() && stringReader.peek() == ' ') {
			stringReader.skip();
			class_4298 lv2 = class_4298.method_19609(stringReader, false);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_4298 lv3 = class_4298.method_19609(stringReader, bl);
				return new class_4304(lv, lv2, lv3);
			} else {
				stringReader.setCursor(i);
				throw class_4287.field_21051.createWithContext(stringReader);
			}
		} else {
			stringReader.setCursor(i);
			throw class_4287.field_21051.createWithContext(stringReader);
		}
	}

	public static class_4304 method_19638() {
		return new class_4304(new class_4298(true, 0.0), new class_4298(true, 0.0), new class_4298(true, 0.0));
	}

	public int hashCode() {
		int i = this.field_21139.hashCode();
		i = 31 * i + this.field_21140.hashCode();
		return 31 * i + this.field_21141.hashCode();
	}
}
