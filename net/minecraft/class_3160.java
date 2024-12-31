package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.json.EntityJson;
import net.minecraft.util.json.MinMaxJson;

public class class_3160 {
	public static class_3160 field_15570 = new class_3160();
	private final MinMaxJson field_15571;
	private final MinMaxJson field_15572;
	private final EntityJson field_15573;
	private final Boolean field_15574;
	private final class_3161 field_15575;

	public class_3160() {
		this.field_15571 = MinMaxJson.EMPTY;
		this.field_15572 = MinMaxJson.EMPTY;
		this.field_15573 = EntityJson.EMPTY;
		this.field_15574 = null;
		this.field_15575 = class_3161.field_15576;
	}

	public class_3160(MinMaxJson minMaxJson, MinMaxJson minMaxJson2, EntityJson entityJson, @Nullable Boolean boolean_, class_3161 arg) {
		this.field_15571 = minMaxJson;
		this.field_15572 = minMaxJson2;
		this.field_15573 = entityJson;
		this.field_15574 = boolean_;
		this.field_15575 = arg;
	}

	public boolean method_14117(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
		if (this == field_15570) {
			return true;
		} else if (!this.field_15571.method_14335(f)) {
			return false;
		} else if (!this.field_15572.method_14335(g)) {
			return false;
		} else if (!this.field_15573.method_14237(serverPlayerEntity, damageSource.getAttacker())) {
			return false;
		} else {
			return this.field_15574 != null && this.field_15574 != bl ? false : this.field_15575.method_14120(serverPlayerEntity, damageSource);
		}
	}

	public static class_3160 method_14116(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage");
			MinMaxJson minMaxJson = MinMaxJson.fromJson(jsonObject.get("dealt"));
			MinMaxJson minMaxJson2 = MinMaxJson.fromJson(jsonObject.get("taken"));
			Boolean boolean_ = jsonObject.has("blocked") ? JsonHelper.getBoolean(jsonObject, "blocked") : null;
			EntityJson entityJson = EntityJson.fromJson(jsonObject.get("source_entity"));
			class_3161 lv = class_3161.method_14118(jsonObject.get("type"));
			return new class_3160(minMaxJson, minMaxJson2, entityJson, boolean_, lv);
		} else {
			return field_15570;
		}
	}
}
