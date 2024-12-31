package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JsonHelper;

public class class_3160 {
	public static final class_3160 field_16832 = class_3160.class_3466.method_15669().method_15672();
	private final class_3638.class_3641 field_15571;
	private final class_3638.class_3641 field_15572;
	private final class_3528 field_15573;
	private final Boolean field_15574;
	private final class_3161 field_15575;

	public class_3160() {
		this.field_15571 = class_3638.class_3641.field_17695;
		this.field_15572 = class_3638.class_3641.field_17695;
		this.field_15573 = class_3528.field_17075;
		this.field_15574 = null;
		this.field_15575 = class_3161.field_16861;
	}

	public class_3160(class_3638.class_3641 arg, class_3638.class_3641 arg2, class_3528 arg3, @Nullable Boolean boolean_, class_3161 arg4) {
		this.field_15571 = arg;
		this.field_15572 = arg2;
		this.field_15573 = arg3;
		this.field_15574 = boolean_;
		this.field_15575 = arg4;
	}

	public boolean method_14117(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource, float f, float g, boolean bl) {
		if (this == field_16832) {
			return true;
		} else if (!this.field_15571.method_16522(f)) {
			return false;
		} else if (!this.field_15572.method_16522(g)) {
			return false;
		} else if (!this.field_15573.method_15906(serverPlayerEntity, damageSource.getAttacker())) {
			return false;
		} else {
			return this.field_15574 != null && this.field_15574 != bl ? false : this.field_15575.method_14120(serverPlayerEntity, damageSource);
		}
	}

	public static class_3160 method_14116(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage");
			class_3638.class_3641 lv = class_3638.class_3641.method_16515(jsonObject.get("dealt"));
			class_3638.class_3641 lv2 = class_3638.class_3641.method_16515(jsonObject.get("taken"));
			Boolean boolean_ = jsonObject.has("blocked") ? JsonHelper.getBoolean(jsonObject, "blocked") : null;
			class_3528 lv3 = class_3528.method_15905(jsonObject.get("source_entity"));
			class_3161 lv4 = class_3161.method_14118(jsonObject.get("type"));
			return new class_3160(lv, lv2, lv3, boolean_, lv4);
		} else {
			return field_16832;
		}
	}

	public JsonElement method_15668() {
		if (this == field_16832) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("dealt", this.field_15571.method_16513());
			jsonObject.add("taken", this.field_15572.method_16513());
			jsonObject.add("source_entity", this.field_15573.method_15904());
			jsonObject.add("type", this.field_15575.method_15688());
			if (this.field_15574 != null) {
				jsonObject.addProperty("blocked", this.field_15574);
			}

			return jsonObject;
		}
	}

	public static class class_3466 {
		private class_3638.class_3641 field_16833 = class_3638.class_3641.field_17695;
		private class_3638.class_3641 field_16834 = class_3638.class_3641.field_17695;
		private class_3528 field_16835 = class_3528.field_17075;
		private Boolean field_16836;
		private class_3161 field_16837 = class_3161.field_16861;

		public static class_3160.class_3466 method_15669() {
			return new class_3160.class_3466();
		}

		public class_3160.class_3466 method_15671(Boolean boolean_) {
			this.field_16836 = boolean_;
			return this;
		}

		public class_3160.class_3466 method_15670(class_3161.class_3472 arg) {
			this.field_16837 = arg.method_15693();
			return this;
		}

		public class_3160 method_15672() {
			return new class_3160(this.field_16833, this.field_16834, this.field_16835, this.field_16836, this.field_16837);
		}
	}
}
