package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JsonHelper;

public class class_3161 {
	public static final class_3161 field_16861 = class_3161.class_3472.method_15690().method_15693();
	private final Boolean field_16862;
	private final Boolean field_16863;
	private final Boolean field_16864;
	private final Boolean field_16865;
	private final Boolean field_16866;
	private final Boolean field_16867;
	private final Boolean field_16868;
	private final class_3528 field_16869;
	private final class_3528 field_16870;

	public class_3161(
		@Nullable Boolean boolean_,
		@Nullable Boolean boolean2,
		@Nullable Boolean boolean3,
		@Nullable Boolean boolean4,
		@Nullable Boolean boolean5,
		@Nullable Boolean boolean6,
		@Nullable Boolean boolean7,
		class_3528 arg,
		class_3528 arg2
	) {
		this.field_16862 = boolean_;
		this.field_16863 = boolean2;
		this.field_16864 = boolean3;
		this.field_16865 = boolean4;
		this.field_16866 = boolean5;
		this.field_16867 = boolean6;
		this.field_16868 = boolean7;
		this.field_16869 = arg;
		this.field_16870 = arg2;
	}

	public boolean method_14120(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource) {
		if (this == field_16861) {
			return true;
		} else if (this.field_16862 != null && this.field_16862 != damageSource.isProjectile()) {
			return false;
		} else if (this.field_16863 != null && this.field_16863 != damageSource.isExplosive()) {
			return false;
		} else if (this.field_16864 != null && this.field_16864 != damageSource.bypassesArmor()) {
			return false;
		} else if (this.field_16865 != null && this.field_16865 != damageSource.isOutOfWorld()) {
			return false;
		} else if (this.field_16866 != null && this.field_16866 != damageSource.isUnblockable()) {
			return false;
		} else if (this.field_16867 != null && this.field_16867 != damageSource.isFire()) {
			return false;
		} else if (this.field_16868 != null && this.field_16868 != damageSource.getMagic()) {
			return false;
		} else {
			return !this.field_16869.method_15906(serverPlayerEntity, damageSource.getSource())
				? false
				: this.field_16870.method_15906(serverPlayerEntity, damageSource.getAttacker());
		}
	}

	public static class_3161 method_14118(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "damage type");
			Boolean boolean_ = method_14119(jsonObject, "is_projectile");
			Boolean boolean2 = method_14119(jsonObject, "is_explosion");
			Boolean boolean3 = method_14119(jsonObject, "bypasses_armor");
			Boolean boolean4 = method_14119(jsonObject, "bypasses_invulnerability");
			Boolean boolean5 = method_14119(jsonObject, "bypasses_magic");
			Boolean boolean6 = method_14119(jsonObject, "is_fire");
			Boolean boolean7 = method_14119(jsonObject, "is_magic");
			class_3528 lv = class_3528.method_15905(jsonObject.get("direct_entity"));
			class_3528 lv2 = class_3528.method_15905(jsonObject.get("source_entity"));
			return new class_3161(boolean_, boolean2, boolean3, boolean4, boolean5, boolean6, boolean7, lv, lv2);
		} else {
			return field_16861;
		}
	}

	@Nullable
	private static Boolean method_14119(JsonObject jsonObject, String string) {
		return jsonObject.has(string) ? JsonHelper.getBoolean(jsonObject, string) : null;
	}

	public JsonElement method_15688() {
		if (this == field_16861) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			this.method_15689(jsonObject, "is_projectile", this.field_16862);
			this.method_15689(jsonObject, "is_explosion", this.field_16863);
			this.method_15689(jsonObject, "bypasses_armor", this.field_16864);
			this.method_15689(jsonObject, "bypasses_invulnerability", this.field_16865);
			this.method_15689(jsonObject, "bypasses_magic", this.field_16866);
			this.method_15689(jsonObject, "is_fire", this.field_16867);
			this.method_15689(jsonObject, "is_magic", this.field_16868);
			jsonObject.add("direct_entity", this.field_16869.method_15904());
			jsonObject.add("source_entity", this.field_16870.method_15904());
			return jsonObject;
		}
	}

	private void method_15689(JsonObject jsonObject, String string, @Nullable Boolean boolean_) {
		if (boolean_ != null) {
			jsonObject.addProperty(string, boolean_);
		}
	}

	public static class class_3472 {
		private Boolean field_16871;
		private Boolean field_16872;
		private Boolean field_16873;
		private Boolean field_16874;
		private Boolean field_16875;
		private Boolean field_16876;
		private Boolean field_16877;
		private class_3528 field_16878 = class_3528.field_17075;
		private class_3528 field_16879 = class_3528.field_17075;

		public static class_3161.class_3472 method_15690() {
			return new class_3161.class_3472();
		}

		public class_3161.class_3472 method_15692(Boolean boolean_) {
			this.field_16871 = boolean_;
			return this;
		}

		public class_3161.class_3472 method_15691(class_3528.class_3529 arg) {
			this.field_16878 = arg.method_15916();
			return this;
		}

		public class_3161 method_15693() {
			return new class_3161(
				this.field_16871,
				this.field_16872,
				this.field_16873,
				this.field_16874,
				this.field_16875,
				this.field_16876,
				this.field_16877,
				this.field_16878,
				this.field_16879
			);
		}
	}
}
