package net.minecraft.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class DistanceJson {
	public static final DistanceJson EMPTY = new DistanceJson(MinMaxJson.EMPTY, MinMaxJson.EMPTY, MinMaxJson.EMPTY, MinMaxJson.EMPTY, MinMaxJson.EMPTY);
	private final MinMaxJson x;
	private final MinMaxJson y;
	private final MinMaxJson z;
	private final MinMaxJson horizontal;
	private final MinMaxJson absolute;

	public DistanceJson(MinMaxJson minMaxJson, MinMaxJson minMaxJson2, MinMaxJson minMaxJson3, MinMaxJson minMaxJson4, MinMaxJson minMaxJson5) {
		this.x = minMaxJson;
		this.y = minMaxJson2;
		this.z = minMaxJson3;
		this.horizontal = minMaxJson4;
		this.absolute = minMaxJson5;
	}

	public boolean method_14124(double x0, double y0, double z0, double x1, double y1, double z1) {
		float f = (float)(x0 - x1);
		float g = (float)(y0 - y1);
		float h = (float)(z0 - z1);
		if (!this.x.method_14335(MathHelper.abs(f)) || !this.y.method_14335(MathHelper.abs(g)) || !this.z.method_14335(MathHelper.abs(h))) {
			return false;
		} else {
			return !this.horizontal.method_14334((double)(f * f + h * h)) ? false : this.absolute.method_14334((double)(f * f + g * g + h * h));
		}
	}

	public static DistanceJson fromJson(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(element, "distance");
			MinMaxJson minMaxJson = MinMaxJson.fromJson(jsonObject.get("x"));
			MinMaxJson minMaxJson2 = MinMaxJson.fromJson(jsonObject.get("y"));
			MinMaxJson minMaxJson3 = MinMaxJson.fromJson(jsonObject.get("z"));
			MinMaxJson minMaxJson4 = MinMaxJson.fromJson(jsonObject.get("horizontal"));
			MinMaxJson minMaxJson5 = MinMaxJson.fromJson(jsonObject.get("absolute"));
			return new DistanceJson(minMaxJson, minMaxJson2, minMaxJson3, minMaxJson4, minMaxJson5);
		} else {
			return EMPTY;
		}
	}
}
