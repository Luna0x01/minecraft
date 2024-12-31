package net.minecraft.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class EntityJson {
	public static final EntityJson EMPTY = new EntityJson(null, DistanceJson.EMPTY, LocationJson.EMPTY, EffectsJson.EMPTY, NbtCompoundJson.EMPTY);
	private final Identifier type;
	private final DistanceJson distance;
	private final LocationJson location;
	private final EffectsJson effects;
	private final NbtCompoundJson nbt;

	public EntityJson(
		@Nullable Identifier identifier, DistanceJson distanceJson, LocationJson locationJson, EffectsJson effectsJson, NbtCompoundJson nbtCompoundJson
	) {
		this.type = identifier;
		this.distance = distanceJson;
		this.location = locationJson;
		this.effects = effectsJson;
		this.nbt = nbtCompoundJson;
	}

	public boolean method_14237(ServerPlayerEntity player, @Nullable Entity entity) {
		if (this == EMPTY) {
			return true;
		} else if (entity == null) {
			return false;
		} else if (this.type != null && !EntityType.method_13943(entity, this.type)) {
			return false;
		} else if (!this.distance.method_14124(player.x, player.y, player.z, entity.x, entity.y, entity.z)) {
			return false;
		} else if (!this.location.method_14322(player.getServerWorld(), entity.x, entity.y, entity.z)) {
			return false;
		} else {
			return !this.effects.method_14339(entity) ? false : this.nbt.method_14346(entity);
		}
	}

	public static EntityJson fromJson(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(element, "entity");
			Identifier identifier = null;
			if (jsonObject.has("type")) {
				identifier = new Identifier(JsonHelper.getString(jsonObject, "type"));
				if (!EntityType.isValid(identifier)) {
					throw new JsonSyntaxException("Unknown entity type '" + identifier + "', valid types are: " + EntityType.getTypesAsString());
				}
			}

			DistanceJson distanceJson = DistanceJson.fromJson(jsonObject.get("distance"));
			LocationJson locationJson = LocationJson.fromJson(jsonObject.get("location"));
			EffectsJson effectsJson = EffectsJson.fromJson(jsonObject.get("effects"));
			NbtCompoundJson nbtCompoundJson = NbtCompoundJson.fromJson(jsonObject.get("nbt"));
			return new EntityJson(identifier, distanceJson, locationJson, effectsJson, nbtCompoundJson);
		} else {
			return EMPTY;
		}
	}
}
