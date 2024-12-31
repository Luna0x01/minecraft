package net.minecraft.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class LocationJson {
	public static LocationJson EMPTY = new LocationJson(MinMaxJson.EMPTY, MinMaxJson.EMPTY, MinMaxJson.EMPTY, null, null, null);
	private final MinMaxJson x;
	private final MinMaxJson y;
	private final MinMaxJson z;
	@Nullable
	final Biome biome;
	@Nullable
	private final String feature;
	@Nullable
	private final DimensionType dimension;

	public LocationJson(
		MinMaxJson minMaxJson, MinMaxJson minMaxJson2, MinMaxJson minMaxJson3, @Nullable Biome biome, @Nullable String string, @Nullable DimensionType dimensionType
	) {
		this.x = minMaxJson;
		this.y = minMaxJson2;
		this.z = minMaxJson3;
		this.biome = biome;
		this.feature = string;
		this.dimension = dimensionType;
	}

	public boolean method_14322(ServerWorld world, double x, double y, double z) {
		return this.method_14323(world, (float)x, (float)y, (float)z);
	}

	public boolean method_14323(ServerWorld world, float x, float y, float z) {
		if (!this.x.method_14335(x)) {
			return false;
		} else if (!this.y.method_14335(y)) {
			return false;
		} else if (!this.z.method_14335(z)) {
			return false;
		} else if (this.dimension != null && this.dimension != world.dimension.getDimensionType()) {
			return false;
		} else {
			BlockPos blockPos = new BlockPos((double)x, (double)y, (double)z);
			return this.biome != null && this.biome != world.getBiome(blockPos)
				? false
				: this.feature == null || world.getChunkProvider().method_14961(world, this.feature, blockPos);
		}
	}

	public static LocationJson fromJson(@Nullable JsonElement element) {
		if (element != null && !element.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(element, "location");
			JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "position", new JsonObject());
			MinMaxJson minMaxJson = MinMaxJson.fromJson(jsonObject2.get("x"));
			MinMaxJson minMaxJson2 = MinMaxJson.fromJson(jsonObject2.get("y"));
			MinMaxJson minMaxJson3 = MinMaxJson.fromJson(jsonObject2.get("z"));
			DimensionType dimensionType = jsonObject.has("dimension") ? DimensionType.fromName(JsonHelper.getString(jsonObject, "dimension")) : null;
			String string = jsonObject.has("feature") ? JsonHelper.getString(jsonObject, "feature") : null;
			Biome biome = null;
			if (jsonObject.has("biome")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "biome"));
				biome = Biome.REGISTRY.get(identifier);
				if (biome == null) {
					throw new JsonSyntaxException("Unknown biome '" + identifier + "'");
				}
			}

			return new LocationJson(minMaxJson, minMaxJson2, minMaxJson3, biome, string, dimensionType);
		} else {
			return EMPTY;
		}
	}
}
