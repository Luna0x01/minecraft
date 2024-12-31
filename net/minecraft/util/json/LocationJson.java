package net.minecraft.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.class_3638;
import net.minecraft.class_3844;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class LocationJson {
	public static final LocationJson EMPTY = new LocationJson(
		class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, null, null, null
	);
	private final class_3638.class_3641 field_15752;
	private final class_3638.class_3641 field_15753;
	private final class_3638.class_3641 field_15754;
	@Nullable
	private final Biome biome;
	@Nullable
	private final String feature;
	@Nullable
	private final DimensionType field_15756;

	public LocationJson(
		class_3638.class_3641 arg,
		class_3638.class_3641 arg2,
		class_3638.class_3641 arg3,
		@Nullable Biome biome,
		@Nullable String string,
		@Nullable DimensionType dimensionType
	) {
		this.field_15752 = arg;
		this.field_15753 = arg2;
		this.field_15754 = arg3;
		this.biome = biome;
		this.feature = string;
		this.field_15756 = dimensionType;
	}

	public static LocationJson method_16353(Biome biome) {
		return new LocationJson(class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, biome, null, null);
	}

	public static LocationJson method_16354(DimensionType dimensionType) {
		return new LocationJson(class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, null, null, dimensionType);
	}

	public static LocationJson method_16355(String string) {
		return new LocationJson(class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, class_3638.class_3641.field_17695, null, string, null);
	}

	public boolean method_14322(ServerWorld world, double x, double y, double z) {
		return this.method_14323(world, (float)x, (float)y, (float)z);
	}

	public boolean method_14323(ServerWorld world, float x, float y, float z) {
		if (!this.field_15752.method_16522(x)) {
			return false;
		} else if (!this.field_15753.method_16522(y)) {
			return false;
		} else if (!this.field_15754.method_16522(z)) {
			return false;
		} else if (this.field_15756 != null && this.field_15756 != world.dimension.method_11789()) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos((double)x, (double)y, (double)z);
			return this.biome != null && this.biome != world.method_8577(blockPos)
				? false
				: this.feature == null || class_3844.method_17345(world, this.feature, blockPos);
		}
	}

	public JsonElement method_16352() {
		if (this == EMPTY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (!this.field_15752.method_16512() || !this.field_15753.method_16512() || !this.field_15754.method_16512()) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.add("x", this.field_15752.method_16513());
				jsonObject2.add("y", this.field_15753.method_16513());
				jsonObject2.add("z", this.field_15754.method_16513());
				jsonObject.add("position", jsonObject2);
			}

			if (this.field_15756 != null) {
				jsonObject.addProperty("dimension", DimensionType.method_17196(this.field_15756).toString());
			}

			if (this.feature != null) {
				jsonObject.addProperty("feature", this.feature);
			}

			if (this.biome != null) {
				jsonObject.addProperty("biome", Registry.BIOME.getId(this.biome).toString());
			}

			return jsonObject;
		}
	}

	public static LocationJson fromJson(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(element, "location");
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "position", new JsonObject());
			class_3638.class_3641 lv = class_3638.class_3641.method_16515(jsonObject2.get("x"));
			class_3638.class_3641 lv2 = class_3638.class_3641.method_16515(jsonObject2.get("y"));
			class_3638.class_3641 lv3 = class_3638.class_3641.method_16515(jsonObject2.get("z"));
			DimensionType dimensionType = jsonObject.has("dimension") ? DimensionType.method_17199(new Identifier(JsonHelper.getString(jsonObject, "dimension"))) : null;
			String string = jsonObject.has("feature") ? JsonHelper.getString(jsonObject, "feature") : null;
			Biome biome = null;
			if (jsonObject.has("biome")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "biome"));
				biome = Registry.BIOME.getByIdentifier(identifier);
				if (biome == null) {
					throw new JsonSyntaxException("Unknown biome '" + identifier + "'");
				}
			}

			return new LocationJson(lv, lv2, lv3, biome, string, dimensionType);
		} else {
			return EMPTY;
		}
	}
}
