package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.class_4374;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;

public class Identifier implements Comparable<Identifier> {
	private static final SimpleCommandExceptionType INVALID_ID = new SimpleCommandExceptionType(new TranslatableText("argument.id.invalid"));
	protected final String namespace;
	protected final String path;

	protected Identifier(String[] strings) {
		this.namespace = StringUtils.isEmpty(strings[0]) ? "minecraft" : strings[0];
		this.path = strings[1];
		if (!this.namespace.chars().allMatch(i -> i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 46)) {
			throw new class_4374("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
		} else if (!this.path.chars().allMatch(i -> i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 47 || i == 46)) {
			throw new class_4374("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
		}
	}

	public Identifier(String string) {
		this(method_20446(string, ':'));
	}

	public Identifier(String string, String string2) {
		this(new String[]{string, string2});
	}

	public static Identifier method_20444(String string, char c) {
		return new Identifier(method_20446(string, c));
	}

	@Nullable
	public static Identifier fromString(String identifier) {
		try {
			return new Identifier(identifier);
		} catch (class_4374 var2) {
			return null;
		}
	}

	protected static String[] method_20446(String string, char c) {
		String[] strings = new String[]{"minecraft", string};
		int i = string.indexOf(c);
		if (i >= 0) {
			strings[1] = string.substring(i + 1, string.length());
			if (i >= 1) {
				strings[0] = string.substring(0, i);
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
		int i = this.path.compareTo(identifier.path);
		if (i == 0) {
			i = this.namespace.compareTo(identifier.namespace);
		}

		return i;
	}

	public static Identifier method_20442(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();

		while (stringReader.canRead() && method_20440(stringReader.peek())) {
			stringReader.skip();
		}

		String string = stringReader.getString().substring(i, stringReader.getCursor());

		try {
			return new Identifier(string);
		} catch (class_4374 var4) {
			stringReader.setCursor(i);
			throw INVALID_ID.createWithContext(stringReader);
		}
	}

	public static boolean method_20440(char c) {
		return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
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
