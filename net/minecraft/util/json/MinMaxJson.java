package net.minecraft.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JsonHelper;

public class MinMaxJson {
	public static final MinMaxJson EMPTY = new MinMaxJson(null, null);
	private final Float min;
	private final Float max;

	public MinMaxJson(@Nullable Float float_, @Nullable Float float2) {
		this.min = float_;
		this.max = float2;
	}

	public boolean method_14335(float f) {
		return this.min != null && this.min > f ? false : this.max == null || !(this.max < f);
	}

	public boolean method_14334(double d) {
		return this.min != null && (double)(this.min * this.min) > d ? false : this.max == null || !((double)(this.max * this.max) < d);
	}

	public static MinMaxJson fromJson(@Nullable JsonElement element) {
		if (element == null || element.isJsonNull()) {
			return EMPTY;
		} else if (JsonHelper.isNumber(element)) {
			float f = JsonHelper.asFloat(element, "value");
			return new MinMaxJson(f, f);
		} else {
			JsonObject jsonObject = JsonHelper.asObject(element, "value");
			Float float_ = jsonObject.has("min") ? JsonHelper.getFloat(jsonObject, "min") : null;
			Float float2 = jsonObject.has("max") ? JsonHelper.getFloat(jsonObject, "max") : null;
			return new MinMaxJson(float_, float2);
		}
	}
}
