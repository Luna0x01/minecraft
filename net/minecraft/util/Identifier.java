package net.minecraft.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class Identifier {
	protected final String namespace;
	protected final String path;

	protected Identifier(int i, String... strings) {
		this.namespace = StringUtils.isEmpty(strings[0]) ? "minecraft" : strings[0].toLowerCase();
		this.path = strings[1];
		Validate.notNull(this.path);
	}

	public Identifier(String string) {
		this(0, method_10737(string));
	}

	public Identifier(String string, String string2) {
		this(0, string, string2);
	}

	protected static String[] method_10737(String path) {
		String[] strings = new String[]{null, path};
		int i = path.indexOf(58);
		if (i >= 0) {
			strings[1] = path.substring(i + 1, path.length());
			if (i > 1) {
				strings[0] = path.substring(0, i);
			}
		}

		return strings;
	}

	public String getPath() {
		return this.path;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String toString() {
		return this.namespace + ':' + this.path;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Identifier)) {
			return false;
		} else {
			Identifier identifier = (Identifier)object;
			return this.namespace.equals(identifier.namespace) && this.path.equals(identifier.path);
		}
	}

	public int hashCode() {
		return 31 * this.namespace.hashCode() + this.path.hashCode();
	}
}
