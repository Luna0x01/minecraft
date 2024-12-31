package net.minecraft;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

public final class class_4115 {
	private final int field_20015;
	private final int field_20016;
	private final int field_20017;
	private final int field_20018;
	private final int field_20019;
	private final int field_20020;
	private static final Pattern field_20021 = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

	public class_4115(int i, int j, int k, int l, int m, int n) {
		this.field_20015 = i;
		this.field_20016 = j;
		this.field_20017 = k;
		this.field_20018 = l;
		this.field_20019 = m;
		this.field_20020 = n;
	}

	public class_4115(Buffer buffer) {
		this.field_20015 = buffer.width();
		this.field_20016 = buffer.height();
		this.field_20017 = buffer.redBits();
		this.field_20018 = buffer.greenBits();
		this.field_20019 = buffer.blueBits();
		this.field_20020 = buffer.refreshRate();
	}

	public class_4115(GLFWVidMode gLFWVidMode) {
		this.field_20015 = gLFWVidMode.width();
		this.field_20016 = gLFWVidMode.height();
		this.field_20017 = gLFWVidMode.redBits();
		this.field_20018 = gLFWVidMode.greenBits();
		this.field_20019 = gLFWVidMode.blueBits();
		this.field_20020 = gLFWVidMode.refreshRate();
	}

	public int method_18280() {
		return this.field_20015;
	}

	public int method_18282() {
		return this.field_20016;
	}

	public int method_18283() {
		return this.field_20017;
	}

	public int method_18284() {
		return this.field_20018;
	}

	public int method_18285() {
		return this.field_20019;
	}

	public int method_18286() {
		return this.field_20020;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4115 lv = (class_4115)object;
			return this.field_20015 == lv.field_20015
				&& this.field_20016 == lv.field_20016
				&& this.field_20017 == lv.field_20017
				&& this.field_20018 == lv.field_20018
				&& this.field_20019 == lv.field_20019
				&& this.field_20020 == lv.field_20020;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_20015, this.field_20016, this.field_20017, this.field_20018, this.field_20019, this.field_20020});
	}

	public String toString() {
		return String.format("%sx%s@%s (%sbit)", this.field_20015, this.field_20016, this.field_20020, this.field_20017 + this.field_20018 + this.field_20019);
	}

	public static Optional<class_4115> method_18281(String string) {
		try {
			Matcher matcher = field_20021.matcher(string);
			if (matcher.matches()) {
				int i = Integer.parseInt(matcher.group(1));
				int j = Integer.parseInt(matcher.group(2));
				String string2 = matcher.group(3);
				int k;
				if (string2 == null) {
					k = 60;
				} else {
					k = Integer.parseInt(string2);
				}

				String string3 = matcher.group(4);
				int m;
				if (string3 == null) {
					m = 24;
				} else {
					m = Integer.parseInt(string3);
				}

				int o = m / 3;
				return Optional.of(new class_4115(i, j, o, o, o, k));
			}
		} catch (Exception var9) {
		}

		return Optional.empty();
	}

	public String method_18287() {
		return String.format("%sx%s@%s:%s", this.field_20015, this.field_20016, this.field_20020, this.field_20017 + this.field_20018 + this.field_20019);
	}
}
