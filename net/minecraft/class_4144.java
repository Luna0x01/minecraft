package net.minecraft;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.Util;

public enum class_4144 {
	BITMAP("bitmap", class_4139.class_4140::method_18484),
	TTF("ttf", class_4148.class_4149::method_18506),
	LEGACY_UNICODE("legacy_unicode", class_4145.class_4146::method_18496);

	private static final Map<String, class_4144> field_20154 = Util.make(Maps.newHashMap(), hashMap -> {
		for (class_4144 lv : values()) {
			hashMap.put(lv.field_20155, lv);
		}
	});
	private final String field_20155;
	private final Function<JsonObject, class_4143> field_20156;

	private class_4144(String string2, Function<JsonObject, class_4143> function) {
		this.field_20155 = string2;
		this.field_20156 = function;
	}

	public static class_4144 method_18489(String string) {
		class_4144 lv = (class_4144)field_20154.get(string);
		if (lv == null) {
			throw new IllegalArgumentException("Invalid type: " + string);
		} else {
			return lv;
		}
	}

	public class_4143 method_18488(JsonObject jsonObject) {
		return (class_4143)this.field_20156.apply(jsonObject);
	}
}
