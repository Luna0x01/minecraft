package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class class_3180 {
	public static final class_3180 field_15646 = new class_3180();
	private final Enchantment field_15647;
	private final class_3638.class_3642 field_15648;

	public class_3180() {
		this.field_15647 = null;
		this.field_15648 = class_3638.class_3642.field_17698;
	}

	public class_3180(@Nullable Enchantment enchantment, class_3638.class_3642 arg) {
		this.field_15647 = enchantment;
		this.field_15648 = arg;
	}

	public boolean method_14208(Map<Enchantment, Integer> map) {
		if (this.field_15647 != null) {
			if (!map.containsKey(this.field_15647)) {
				return false;
			}

			int i = (Integer)map.get(this.field_15647);
			if (this.field_15648 != null && !this.field_15648.method_16531(i)) {
				return false;
			}
		} else if (this.field_15648 != null) {
			for (Integer integer : map.values()) {
				if (this.field_15648.method_16531(integer)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}

	public JsonElement method_15832() {
		if (this == field_15646) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_15647 != null) {
				jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getId(this.field_15647).toString());
			}

			jsonObject.add("levels", this.field_15648.method_16513());
			return jsonObject;
		}
	}

	public static class_3180 method_14207(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "enchantment");
			Enchantment enchantment = null;
			if (jsonObject.has("enchantment")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "enchantment"));
				enchantment = Registry.ENCHANTMENT.getByIdentifier(identifier);
				if (enchantment == null) {
					throw new JsonSyntaxException("Unknown enchantment '" + identifier + "'");
				}
			}

			class_3638.class_3642 lv = class_3638.class_3642.method_16524(jsonObject.get("levels"));
			return new class_3180(enchantment, lv);
		} else {
			return field_15646;
		}
	}

	public static class_3180[] method_14209(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(jsonElement, "enchantments");
			class_3180[] lvs = new class_3180[jsonArray.size()];

			for (int i = 0; i < lvs.length; i++) {
				lvs[i] = method_14207(jsonArray.get(i));
			}

			return lvs;
		} else {
			return new class_3180[0];
		}
	}
}
