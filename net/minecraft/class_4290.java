package net.minecraft;

import java.util.Locale;
import net.minecraft.util.Identifier;

public class class_4290 extends Identifier {
	private final String field_21091;

	protected class_4290(String[] strings) {
		super(strings);
		this.field_21091 = strings[2].toLowerCase(Locale.ROOT);
	}

	public class_4290(String string) {
		this(method_19595(string));
	}

	public class_4290(Identifier identifier, String string) {
		this(identifier.toString(), string);
	}

	public class_4290(String string, String string2) {
		this(method_19595(string + '#' + string2));
	}

	protected static String[] method_19595(String string) {
		String[] strings = new String[]{null, string, ""};
		int i = string.indexOf(35);
		String string2 = string;
		if (i >= 0) {
			strings[2] = string.substring(i + 1, string.length());
			if (i > 1) {
				string2 = string.substring(0, i);
			}
		}

		System.arraycopy(Identifier.method_20446(string2, ':'), 0, strings, 0, 2);
		return strings;
	}

	public String method_19596() {
		return this.field_21091;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof class_4290 && super.equals(object)) {
			class_4290 lv = (class_4290)object;
			return this.field_21091.equals(lv.field_21091);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + this.field_21091.hashCode();
	}

	@Override
	public String toString() {
		return super.toString() + '#' + this.field_21091;
	}
}
