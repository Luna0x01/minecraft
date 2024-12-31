package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class Identifier implements Comparable<Identifier> {
	protected final String namespace;
	protected final String path;

	protected Identifier(int i, String... strings) {
		this.namespace = StringUtils.isEmpty(strings[0]) ? "minecraft" : strings[0].toLowerCase(Locale.ROOT);
		this.path = strings[1].toLowerCase(Locale.ROOT);
		Validate.notNull(this.path);
	}

	public Identifier(String string) {
		this(0, method_10737(string));
	}

	public Identifier(String string, String string2) {
		this(0, string, string2);
	}

	protected static String[] method_10737(String path) {
		String[] strings = new String[]{"minecraft", path};
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

	public int compareTo(Identifier identifier) {
		int i = this.namespace.compareTo(identifier.namespace);
		if (i == 0) {
			i = this.path.compareTo(identifier.path);
		}

		return i;
	}

	public static class class_3346 implements JsonDeserializer<Identifier>, JsonSerializer<Identifier> {
		public Identifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new Identifier(JsonHelper.asString(jsonElement, "location"));
		}

		public JsonElement serialize(Identifier identifier, Type type, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(identifier.toString());
		}
	}
}
