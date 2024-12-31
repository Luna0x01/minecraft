package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.json.DistanceJson;
import net.minecraft.util.json.EffectsJson;
import net.minecraft.util.json.LocationJson;
import net.minecraft.util.json.NbtCompoundJson;

public class class_3528 {
	public static final class_3528 field_17075 = new class_3528(
		class_3530.field_17099, DistanceJson.EMPTY, LocationJson.EMPTY, EffectsJson.EMPTY, NbtCompoundJson.EMPTY
	);
	public static final class_3528[] field_17076 = new class_3528[0];
	private final class_3530 field_17077;
	private final DistanceJson field_17078;
	private final LocationJson field_17079;
	private final EffectsJson field_17080;
	private final NbtCompoundJson field_17081;

	private class_3528(class_3530 arg, DistanceJson distanceJson, LocationJson locationJson, EffectsJson effectsJson, NbtCompoundJson nbtCompoundJson) {
		this.field_17077 = arg;
		this.field_17078 = distanceJson;
		this.field_17079 = locationJson;
		this.field_17080 = effectsJson;
		this.field_17081 = nbtCompoundJson;
	}

	public boolean method_15906(ServerPlayerEntity serverPlayerEntity, @Nullable Entity entity) {
		if (this == field_17075) {
			return true;
		} else if (entity == null) {
			return false;
		} else if (!this.field_17077.method_15956(entity.method_15557())) {
			return false;
		} else if (!this.field_17078.method_14124(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, entity.x, entity.y, entity.z)) {
			return false;
		} else if (!this.field_17079.method_14322(serverPlayerEntity.getServerWorld(), entity.x, entity.y, entity.z)) {
			return false;
		} else {
			return !this.field_17080.method_14339(entity) ? false : this.field_17081.method_14346(entity);
		}
	}

	public static class_3528 method_15905(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entity");
			class_3530 lv = class_3530.method_15957(jsonObject.get("type"));
			DistanceJson distanceJson = DistanceJson.fromJson(jsonObject.get("distance"));
			LocationJson locationJson = LocationJson.fromJson(jsonObject.get("location"));
			EffectsJson effectsJson = EffectsJson.fromJson(jsonObject.get("effects"));
			NbtCompoundJson nbtCompoundJson = NbtCompoundJson.fromJson(jsonObject.get("nbt"));
			return new class_3528.class_3529()
				.method_15912(lv)
				.method_15911(distanceJson)
				.method_15913(locationJson)
				.method_15914(effectsJson)
				.method_15915(nbtCompoundJson)
				.method_15916();
		} else {
			return field_17075;
		}
	}

	public static class_3528[] method_15908(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(jsonElement, "entities");
			class_3528[] lvs = new class_3528[jsonArray.size()];

			for (int i = 0; i < jsonArray.size(); i++) {
				lvs[i] = method_15905(jsonArray.get(i));
			}

			return lvs;
		} else {
			return field_17076;
		}
	}

	public JsonElement method_15904() {
		if (this == field_17075) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("type", this.field_17077.method_15955());
			jsonObject.add("distance", this.field_17078.method_15702());
			jsonObject.add("location", this.field_17079.method_16352());
			jsonObject.add("effects", this.field_17080.method_16539());
			jsonObject.add("nbt", this.field_17081.method_16545());
			return jsonObject;
		}
	}

	public static JsonElement method_15907(class_3528[] args) {
		if (args == field_17076) {
			return JsonNull.INSTANCE;
		} else {
			JsonArray jsonArray = new JsonArray();

			for (int i = 0; i < args.length; i++) {
				JsonElement jsonElement = args[i].method_15904();
				if (!jsonElement.isJsonNull()) {
					jsonArray.add(jsonElement);
				}
			}

			return jsonArray;
		}
	}

	public static class class_3529 {
		private class_3530 field_17082 = class_3530.field_17099;
		private DistanceJson field_17083 = DistanceJson.EMPTY;
		private LocationJson field_17084 = LocationJson.EMPTY;
		private EffectsJson field_17085 = EffectsJson.EMPTY;
		private NbtCompoundJson field_17086 = NbtCompoundJson.EMPTY;

		public static class_3528.class_3529 method_15909() {
			return new class_3528.class_3529();
		}

		public class_3528.class_3529 method_15910(EntityType<?> entityType) {
			this.field_17082 = new class_3530(entityType);
			return this;
		}

		public class_3528.class_3529 method_15912(class_3530 arg) {
			this.field_17082 = arg;
			return this;
		}

		public class_3528.class_3529 method_15911(DistanceJson distanceJson) {
			this.field_17083 = distanceJson;
			return this;
		}

		public class_3528.class_3529 method_15913(LocationJson locationJson) {
			this.field_17084 = locationJson;
			return this;
		}

		public class_3528.class_3529 method_15914(EffectsJson effectsJson) {
			this.field_17085 = effectsJson;
			return this;
		}

		public class_3528.class_3529 method_15915(NbtCompoundJson nbtCompoundJson) {
			this.field_17086 = nbtCompoundJson;
			return this;
		}

		public class_3528 method_15916() {
			return this.field_17082 == class_3530.field_17099
					&& this.field_17083 == DistanceJson.EMPTY
					&& this.field_17084 == LocationJson.EMPTY
					&& this.field_17085 == EffectsJson.EMPTY
					&& this.field_17086 == NbtCompoundJson.EMPTY
				? class_3528.field_17075
				: new class_3528(this.field_17082, this.field_17083, this.field_17084, this.field_17085, this.field_17086);
		}
	}
}
