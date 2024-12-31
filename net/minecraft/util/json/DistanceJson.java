package net.minecraft.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.class_3638;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class DistanceJson {
	public static final DistanceJson EMPTY = new DistanceJson(
		class_3638.class_3641.field_17695,
		class_3638.class_3641.field_17695,
		class_3638.class_3641.field_17695,
		class_3638.class_3641.field_17695,
		class_3638.class_3641.field_17695
	);
	private final class_3638.class_3641 field_15593;
	private final class_3638.class_3641 field_15594;
	private final class_3638.class_3641 field_15595;
	private final class_3638.class_3641 field_15596;
	private final class_3638.class_3641 field_15597;

	public DistanceJson(class_3638.class_3641 arg, class_3638.class_3641 arg2, class_3638.class_3641 arg3, class_3638.class_3641 arg4, class_3638.class_3641 arg5) {
		this.field_15593 = arg;
		this.field_15594 = arg2;
		this.field_15595 = arg3;
		this.field_15596 = arg4;
		this.field_15597 = arg5;
	}

	public static DistanceJson method_15703(class_3638.class_3641 arg) {
		return new DistanceJson(
			class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, arg, class_3638.class_3641.field_17695
		);
	}

	public static DistanceJson method_15704(class_3638.class_3641 arg) {
		return new DistanceJson(
			class_3638.class_3641.field_17695, arg, class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, class_3638.class_3641.field_17695
		);
	}

	public boolean method_14124(double x0, double y0, double z0, double x1, double y1, double z1) {
		float f = (float)(x0 - x1);
		float g = (float)(y0 - y1);
		float h = (float)(z0 - z1);
		if (!this.field_15593.method_16522(MathHelper.abs(f))
			|| !this.field_15594.method_16522(MathHelper.abs(g))
			|| !this.field_15595.method_16522(MathHelper.abs(h))) {
			return false;
		} else {
			return !this.field_15596.method_16514((double)(f * f + h * h)) ? false : this.field_15597.method_16514((double)(f * f + g * g + h * h));
		}
	}

	public static DistanceJson fromJson(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(element, "distance");
			class_3638.class_3641 lv = class_3638.class_3641.method_16515(jsonObject.get("x"));
			class_3638.class_3641 lv2 = class_3638.class_3641.method_16515(jsonObject.get("y"));
			class_3638.class_3641 lv3 = class_3638.class_3641.method_16515(jsonObject.get("z"));
			class_3638.class_3641 lv4 = class_3638.class_3641.method_16515(jsonObject.get("horizontal"));
			class_3638.class_3641 lv5 = class_3638.class_3641.method_16515(jsonObject.get("absolute"));
			return new DistanceJson(lv, lv2, lv3, lv4, lv5);
		} else {
			return EMPTY;
		}
	}

	public JsonElement method_15702() {
		if (this == EMPTY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("x", this.field_15593.method_16513());
			jsonObject.add("y", this.field_15594.method_16513());
			jsonObject.add("z", this.field_15595.method_16513());
			jsonObject.add("horizontal", this.field_15596.method_16513());
			jsonObject.add("absolute", this.field_15597.method_16513());
			return jsonObject;
		}
	}
}
