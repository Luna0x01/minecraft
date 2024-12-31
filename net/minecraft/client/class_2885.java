package net.minecraft.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.util.JsonHelper;

public class class_2885 {
	private final class_2880 field_13593;
	private final class_2877 field_13594;

	public class_2885(class_2880 arg, class_2877 arg2) {
		if (arg == null) {
			throw new IllegalArgumentException("Missing condition for selector");
		} else if (arg2 == null) {
			throw new IllegalArgumentException("Missing variant for selector");
		} else {
			this.field_13593 = arg;
			this.field_13594 = arg2;
		}
	}

	public class_2877 method_12393() {
		return this.field_13594;
	}

	public Predicate<BlockState> method_12394(StateManager stateManager) {
		return this.field_13593.method_12379(stateManager);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof class_2885) {
				class_2885 lv = (class_2885)object;
				if (this.field_13593.equals(lv.field_13593)) {
					return this.field_13594.equals(lv.field_13594);
				}
			}

			return false;
		}
	}

	public int hashCode() {
		return 31 * this.field_13593.hashCode() + this.field_13594.hashCode();
	}

	public static class class_2886 implements JsonDeserializer<class_2885> {
		private static final Function<JsonElement, class_2880> field_13595 = new Function<JsonElement, class_2880>() {
			@Nullable
			public class_2880 apply(@Nullable JsonElement jsonElement) {
				return jsonElement == null ? null : class_2885.class_2886.method_12396(jsonElement.getAsJsonObject());
			}
		};
		private static final Function<Entry<String, JsonElement>, class_2880> field_13596 = new Function<Entry<String, JsonElement>, class_2880>() {
			@Nullable
			public class_2880 apply(@Nullable Entry<String, JsonElement> entry) {
				return entry == null ? null : class_2885.class_2886.method_12399(entry);
			}
		};

		public class_2885 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			return new class_2885(this.method_12398(jsonObject), (class_2877)jsonDeserializationContext.deserialize(jsonObject.get("apply"), class_2877.class));
		}

		private class_2880 method_12398(JsonObject jsonObject) {
			return jsonObject.has("when") ? method_12396(JsonHelper.getObject(jsonObject, "when")) : class_2880.field_13576;
		}

		@VisibleForTesting
		static class_2880 method_12396(JsonObject jsonObject) {
			Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
			if (set.isEmpty()) {
				throw new JsonParseException("No elements found in selector");
			} else if (set.size() == 1) {
				if (jsonObject.has("OR")) {
					return new class_2884(Iterables.transform(JsonHelper.getArray(jsonObject, "OR"), field_13595));
				} else {
					return (class_2880)(jsonObject.has("AND")
						? new class_2879(Iterables.transform(JsonHelper.getArray(jsonObject, "AND"), field_13595))
						: method_12399((Entry<String, JsonElement>)set.iterator().next()));
				}
			} else {
				return new class_2879(Iterables.transform(set, field_13596));
			}
		}

		private static class_2881 method_12399(Entry<String, JsonElement> entry) {
			return new class_2881((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
		}
	}
}
