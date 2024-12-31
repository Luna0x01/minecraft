package net.minecraft.client;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.class_4234;
import net.minecraft.class_4235;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.util.JsonHelper;

public class class_2885 {
	private final class_4235 field_13593;
	private final class_4234 field_13594;

	public class_2885(class_4235 arg, class_4234 arg2) {
		if (arg == null) {
			throw new IllegalArgumentException("Missing condition for selector");
		} else if (arg2 == null) {
			throw new IllegalArgumentException("Missing variant for selector");
		} else {
			this.field_13593 = arg;
			this.field_13594 = arg2;
		}
	}

	public class_4234 method_12393() {
		return this.field_13594;
	}

	public Predicate<BlockState> method_19276(StateManager<Block, BlockState> stateManager) {
		return this.field_13593.getPredicate(stateManager);
	}

	public boolean equals(Object object) {
		return this == object;
	}

	public int hashCode() {
		return System.identityHashCode(this);
	}

	public static class class_4237 implements JsonDeserializer<class_2885> {
		public class_2885 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			return new class_2885(this.method_19282(jsonObject), (class_4234)jsonDeserializationContext.deserialize(jsonObject.get("apply"), class_4234.class));
		}

		private class_4235 method_19282(JsonObject jsonObject) {
			return jsonObject.has("when") ? method_19279(JsonHelper.getObject(jsonObject, "when")) : class_4235.TRUE;
		}

		@VisibleForTesting
		static class_4235 method_19279(JsonObject jsonObject) {
			Set<Entry<String, JsonElement>> set = jsonObject.entrySet();
			if (set.isEmpty()) {
				throw new JsonParseException("No elements found in selector");
			} else if (set.size() == 1) {
				if (jsonObject.has("OR")) {
					List<class_4235> list = (List<class_4235>)Streams.stream(JsonHelper.getArray(jsonObject, "OR"))
						.map(jsonElement -> method_19279(jsonElement.getAsJsonObject()))
						.collect(Collectors.toList());
					return new class_2884(list);
				} else if (jsonObject.has("AND")) {
					List<class_4235> list2 = (List<class_4235>)Streams.stream(JsonHelper.getArray(jsonObject, "AND"))
						.map(jsonElement -> method_19279(jsonElement.getAsJsonObject()))
						.collect(Collectors.toList());
					return new class_2879(list2);
				} else {
					return method_19280((Entry<String, JsonElement>)set.iterator().next());
				}
			} else {
				return new class_2879((Iterable<? extends class_4235>)set.stream().map(entry -> method_19280(entry)).collect(Collectors.toList()));
			}
		}

		private static class_4235 method_19280(Entry<String, JsonElement> entry) {
			return new class_2881((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
		}
	}
}
