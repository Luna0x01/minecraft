package net.minecraft.client.util;

import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

public class ModelIdentifier extends Identifier {
	private final String variant;

	protected ModelIdentifier(int i, String... strings) {
		super(0, strings[0], strings[1]);
		this.variant = StringUtils.isEmpty(strings[2]) ? "normal" : strings[2].toLowerCase();
	}

	public ModelIdentifier(String string) {
		this(0, split(string));
	}

	public ModelIdentifier(Identifier identifier, String string) {
		this(identifier.toString(), string);
	}

	public ModelIdentifier(String string, String string2) {
		this(0, split(string + '#' + (string2 == null ? "normal" : string2)));
	}

	protected static String[] split(String id) {
		String[] strings = new String[]{null, id, null};
		int i = id.indexOf(35);
		String string = id;
		if (i >= 0) {
			strings[2] = id.substring(i + 1, id.length());
			if (i > 1) {
				string = id.substring(0, i);
			}
		}

		System.arraycopy(Identifier.method_10737(string), 0, strings, 0, 2);
		return strings;
	}

	public String getVariant() {
		return this.variant;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ModelIdentifier && super.equals(object)) {
			ModelIdentifier modelIdentifier = (ModelIdentifier)object;
			return this.variant.equals(modelIdentifier.variant);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + this.variant.hashCode();
	}

	@Override
	public String toString() {
		return super.toString() + '#' + this.variant;
	}
}
