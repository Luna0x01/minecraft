package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.json.EntityJson;

public class class_3161 {
	public static class_3161 field_15576 = new class_3161();
	private final Boolean field_15577;
	private final Boolean field_15578;
	private final Boolean field_15579;
	private final Boolean field_15580;
	private final Boolean field_15581;
	private final Boolean field_15582;
	private final Boolean field_15583;
	private final EntityJson field_15584;
	private final EntityJson field_15585;

	public class_3161() {
		this.field_15577 = null;
		this.field_15578 = null;
		this.field_15579 = null;
		this.field_15580 = null;
		this.field_15581 = null;
		this.field_15582 = null;
		this.field_15583 = null;
		this.field_15584 = EntityJson.EMPTY;
		this.field_15585 = EntityJson.EMPTY;
	}

	public class_3161(
		@Nullable Boolean boolean_,
		@Nullable Boolean boolean2,
		@Nullable Boolean boolean3,
		@Nullable Boolean boolean4,
		@Nullable Boolean boolean5,
		@Nullable Boolean boolean6,
		@Nullable Boolean boolean7,
		EntityJson entityJson,
		EntityJson entityJson2
	) {
		this.field_15577 = boolean_;
		this.field_15578 = boolean2;
		this.field_15579 = boolean3;
		this.field_15580 = boolean4;
		this.field_15581 = boolean5;
		this.field_15582 = boolean6;
		this.field_15583 = boolean7;
		this.field_15584 = entityJson;
		this.field_15585 = entityJson2;
	}

	public boolean method_14120(ServerPlayerEntity serverPlayerEntity, DamageSource damageSource) {
		if (this == field_15576) {
			return true;
		} else if (this.field_15577 != null && this.field_15577 != damageSource.isProjectile()) {
			return false;
		} else if (this.field_15578 != null && this.field_15578 != damageSource.isExplosive()) {
			return false;
		} else if (this.field_15579 != null && this.field_15579 != damageSource.bypassesArmor()) {
			return false;
		} else if (this.field_15580 != null && this.field_15580 != damageSource.isOutOfWorld()) {
			return false;
		} else if (this.field_15581 != null && this.field_15581 != damageSource.isUnblockable()) {
			return false;
		} else if (this.field_15582 != null && this.field_15582 != damageSource.isFire()) {
			return false;
		} else if (this.field_15583 != null && this.field_15583 != damageSource.getMagic()) {
			return false;
		} else {
			return !this.field_15584.method_14237(serverPlayerEntity, damageSource.getSource())
				? false
				: this.field_15585.method_14237(serverPlayerEntity, damageSource.getAttacker());
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
			EntityJson entityJson = EntityJson.fromJson(jsonObject.get("direct_entity"));
			EntityJson entityJson2 = EntityJson.fromJson(jsonObject.get("source_entity"));
			return new class_3161(boolean_, boolean2, boolean3, boolean4, boolean5, boolean6, boolean7, entityJson, entityJson2);
		} else {
			return field_15576;
		}
	}

	@Nullable
	private static Boolean method_14119(JsonObject jsonObject, String string) {
		return jsonObject.has(string) ? JsonHelper.getBoolean(jsonObject, string) : null;
	}
}
