package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import net.minecraft.state.StateManager;

public class class_2882 {
	private final List<class_2885> field_13588;
	private StateManager field_13589;

	public class_2882(List<class_2885> list) {
		this.field_13588 = list;
	}

	public List<class_2885> method_12386() {
		return this.field_13588;
	}

	public Set<class_2877> method_12388() {
		Set<class_2877> set = Sets.newHashSet();

		for (class_2885 lv : this.field_13588) {
			set.add(lv.method_12393());
		}

		return set;
	}

	public void method_12387(StateManager stateManager) {
		this.field_13589 = stateManager;
	}

	public StateManager method_12389() {
		return this.field_13589;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof class_2882) {
				class_2882 lv = (class_2882)object;
				if (this.field_13588.equals(lv.field_13588)) {
					if (this.field_13589 == null) {
						return lv.field_13589 == null;
					}

					return this.field_13589.equals(lv.field_13589);
				}
			}

			return false;
		}
	}

	public int hashCode() {
		return 31 * this.field_13588.hashCode() + (this.field_13589 == null ? 0 : this.field_13589.hashCode());
	}

	public static class class_2883 implements JsonDeserializer<class_2882> {
		public class_2882 deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new class_2882(this.method_12390(jsonDeserializationContext, jsonElement.getAsJsonArray()));
		}

		private List<class_2885> method_12390(JsonDeserializationContext jsonDeserializationContext, JsonArray jsonArray) {
			List<class_2885> list = Lists.newArrayList();

			for (JsonElement jsonElement : jsonArray) {
				list.add((class_2885)jsonDeserializationContext.deserialize(jsonElement, class_2885.class));
			}

			return list;
		}
	}
}
