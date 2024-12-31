package net.minecraft.world.gen;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class CustomizedWorldProperties {
	public final float coordinateScale;
	public final float heightScale;
	public final float upperLimitScale;
	public final float lowerLimitScale;
	public final float depthNoiseScaleX;
	public final float depthNoiseScaleZ;
	public final float depthNoiseScaleExponent;
	public final float mainNoiseScaleX;
	public final float mainNoiseScaleY;
	public final float mainNoiseScaleZ;
	public final float baseSize;
	public final float stretchY;
	public final float biomeDepthWeight;
	public final float biomeDepthOffset;
	public final float biomeScaleWeight;
	public final float biomeScaleOffset;
	public final int seaLevel;
	public final boolean useCaves;
	public final boolean useDungeons;
	public final int dungeonChance;
	public final boolean useStrongholds;
	public final boolean useVillages;
	public final boolean useMineshafts;
	public final boolean useTemples;
	public final boolean useMonuments;
	public final boolean useRavines;
	public final boolean useWaterLakes;
	public final int waterLakeChance;
	public final boolean useLavaLakes;
	public final int lavaLakeChance;
	public final boolean useLavaOceans;
	public final int fixedBiome;
	public final int biomeSize;
	public final int riverSize;
	public final int dirtSize;
	public final int dirtCount;
	public final int dirtMinHeight;
	public final int dirtMaxHeight;
	public final int gravelSize;
	public final int gravelCount;
	public final int gravelMinHeight;
	public final int gravelMaxHeight;
	public final int graniteSize;
	public final int graniteCount;
	public final int graniteMinHeight;
	public final int graniteMaxHeight;
	public final int dioriteSize;
	public final int dioriteCount;
	public final int dioriteMinHeight;
	public final int dioriteMaxHeight;
	public final int andesiteSize;
	public final int andesiteCount;
	public final int andesiteMinHeight;
	public final int andesiteMaxHeight;
	public final int coalSize;
	public final int coalCount;
	public final int coalMinHeight;
	public final int coalMaxHeight;
	public final int ironSize;
	public final int ironCount;
	public final int ironMinHeight;
	public final int ironMaxHeight;
	public final int goldSize;
	public final int goldCount;
	public final int goldMinHeight;
	public final int goldMaxHeight;
	public final int redstoneSize;
	public final int redstoneCount;
	public final int redstoneMinHeight;
	public final int redstoneMaxHeight;
	public final int diamondSize;
	public final int diamondCount;
	public final int diamondMinHeight;
	public final int diamondMaxHeight;
	public final int lapisSize;
	public final int lapisCount;
	public final int lapisCenterHeight;
	public final int lapisSpread;

	private CustomizedWorldProperties(CustomizedWorldProperties.Builder builder) {
		this.coordinateScale = builder.coordinateScale;
		this.heightScale = builder.heightScale;
		this.upperLimitScale = builder.upperLimitScale;
		this.lowerLimitScale = builder.lowerLimitScale;
		this.depthNoiseScaleX = builder.depthNoiseScaleX;
		this.depthNoiseScaleZ = builder.depthNoiseScaleZ;
		this.depthNoiseScaleExponent = builder.depthNoiseScaleExponent;
		this.mainNoiseScaleX = builder.mainNoiseScaleX;
		this.mainNoiseScaleY = builder.mainNoiseScaleY;
		this.mainNoiseScaleZ = builder.mainNoiseScaleZ;
		this.baseSize = builder.baseSize;
		this.stretchY = builder.stretchY;
		this.biomeDepthWeight = builder.biomeDepthWeight;
		this.biomeDepthOffset = builder.biomeDepthOffset;
		this.biomeScaleWeight = builder.biomeScaleWeight;
		this.biomeScaleOffset = builder.biomeScaleOffset;
		this.seaLevel = builder.seaLevel;
		this.useCaves = builder.useCaves;
		this.useDungeons = builder.useDungeons;
		this.dungeonChance = builder.dungeonChance;
		this.useStrongholds = builder.useStrongholds;
		this.useVillages = builder.useVillages;
		this.useMineshafts = builder.useMineshafts;
		this.useTemples = builder.useTemples;
		this.useMonuments = builder.useMonuments;
		this.useRavines = builder.useRavines;
		this.useWaterLakes = builder.useWaterLakes;
		this.waterLakeChance = builder.waterLakeChance;
		this.useLavaLakes = builder.useLavaLakes;
		this.lavaLakeChance = builder.lavaLakeChance;
		this.useLavaOceans = builder.useLavaOceans;
		this.fixedBiome = builder.fixedBiome;
		this.biomeSize = builder.biomeSize;
		this.riverSize = builder.riverSize;
		this.dirtSize = builder.dirtSize;
		this.dirtCount = builder.dirtCount;
		this.dirtMinHeight = builder.dirtMinHeight;
		this.dirtMaxHeight = builder.dirtMaxHeight;
		this.gravelSize = builder.gravelSize;
		this.gravelCount = builder.gravelCount;
		this.gravelMinHeight = builder.gravelMinHeight;
		this.gravelMaxHeight = builder.gravelMaxHeight;
		this.graniteSize = builder.graniteSize;
		this.graniteCount = builder.graniteCount;
		this.graniteMinHeight = builder.graniteMinHeight;
		this.graniteMaxHeight = builder.graniteMaxHeight;
		this.dioriteSize = builder.dioriteSize;
		this.dioriteCount = builder.dioriteCount;
		this.dioriteMinHeight = builder.dioriteMinHeight;
		this.dioriteMaxHeight = builder.dioriteMaxHeight;
		this.andesiteSize = builder.andesiteSize;
		this.andesiteCount = builder.andesiteCount;
		this.andesiteMinHeight = builder.andesiteMinHeight;
		this.andesiteMaxHeight = builder.andesiteMaxHeight;
		this.coalSize = builder.coalSize;
		this.coalCount = builder.coalCount;
		this.coalMinHeight = builder.coalMinHeight;
		this.coalMaxHeight = builder.coalMaxHeight;
		this.ironSize = builder.ironSize;
		this.ironCount = builder.ironCount;
		this.ironMinHeight = builder.ironMinHeight;
		this.ironMaxHeight = builder.ironMaxHeight;
		this.goldSize = builder.goldSize;
		this.goldCount = builder.goldCount;
		this.goldMinHeight = builder.goldMinHeight;
		this.goldMaxHeight = builder.goldMaxHeight;
		this.redstoneSize = builder.redstoneSize;
		this.redstoneCount = builder.redstoneCount;
		this.redstoneMinHeight = builder.redstoneMinHeight;
		this.redstoneMaxHeight = builder.redstoneMaxHeight;
		this.diamondSize = builder.diamondSize;
		this.diamondCount = builder.diamondCount;
		this.diamondMinHeight = builder.diamondMinHeight;
		this.diamondMaxHeight = builder.diamondMaxHeight;
		this.lapisSize = builder.lapisSize;
		this.lapisCount = builder.lapisCount;
		this.lapisCenterHeight = builder.lapisCenterHeight;
		this.lapisSpread = builder.lapisSpread;
	}

	public static class Builder {
		@VisibleForTesting
		static final Gson GSON = new GsonBuilder().registerTypeAdapter(CustomizedWorldProperties.Builder.class, new CustomizedWorldProperties.Serializer()).create();
		public float coordinateScale = 684.412F;
		public float heightScale = 684.412F;
		public float upperLimitScale = 512.0F;
		public float lowerLimitScale = 512.0F;
		public float depthNoiseScaleX = 200.0F;
		public float depthNoiseScaleZ = 200.0F;
		public float depthNoiseScaleExponent = 0.5F;
		public float mainNoiseScaleX = 80.0F;
		public float mainNoiseScaleY = 160.0F;
		public float mainNoiseScaleZ = 80.0F;
		public float baseSize = 8.5F;
		public float stretchY = 12.0F;
		public float biomeDepthWeight = 1.0F;
		public float biomeDepthOffset = 0.0F;
		public float biomeScaleWeight = 1.0F;
		public float biomeScaleOffset = 0.0F;
		public int seaLevel = 63;
		public boolean useCaves = true;
		public boolean useDungeons = true;
		public int dungeonChance = 8;
		public boolean useStrongholds = true;
		public boolean useVillages = true;
		public boolean useMineshafts = true;
		public boolean useTemples = true;
		public boolean useMonuments = true;
		public boolean useRavines = true;
		public boolean useWaterLakes = true;
		public int waterLakeChance = 4;
		public boolean useLavaLakes = true;
		public int lavaLakeChance = 80;
		public boolean useLavaOceans = false;
		public int fixedBiome = -1;
		public int biomeSize = 4;
		public int riverSize = 4;
		public int dirtSize = 33;
		public int dirtCount = 10;
		public int dirtMinHeight = 0;
		public int dirtMaxHeight = 256;
		public int gravelSize = 33;
		public int gravelCount = 8;
		public int gravelMinHeight = 0;
		public int gravelMaxHeight = 256;
		public int graniteSize = 33;
		public int graniteCount = 10;
		public int graniteMinHeight = 0;
		public int graniteMaxHeight = 80;
		public int dioriteSize = 33;
		public int dioriteCount = 10;
		public int dioriteMinHeight = 0;
		public int dioriteMaxHeight = 80;
		public int andesiteSize = 33;
		public int andesiteCount = 10;
		public int andesiteMinHeight = 0;
		public int andesiteMaxHeight = 80;
		public int coalSize = 17;
		public int coalCount = 20;
		public int coalMinHeight = 0;
		public int coalMaxHeight = 128;
		public int ironSize = 9;
		public int ironCount = 20;
		public int ironMinHeight = 0;
		public int ironMaxHeight = 64;
		public int goldSize = 9;
		public int goldCount = 2;
		public int goldMinHeight = 0;
		public int goldMaxHeight = 32;
		public int redstoneSize = 8;
		public int redstoneCount = 8;
		public int redstoneMinHeight = 0;
		public int redstoneMaxHeight = 16;
		public int diamondSize = 8;
		public int diamondCount = 1;
		public int diamondMinHeight = 0;
		public int diamondMaxHeight = 16;
		public int lapisSize = 7;
		public int lapisCount = 1;
		public int lapisCenterHeight = 16;
		public int lapisSpread = 16;

		public static CustomizedWorldProperties.Builder fromJson(String json) {
			if (json.isEmpty()) {
				return new CustomizedWorldProperties.Builder();
			} else {
				try {
					return JsonHelper.deserialize(GSON, json, CustomizedWorldProperties.Builder.class);
				} catch (Exception var2) {
					return new CustomizedWorldProperties.Builder();
				}
			}
		}

		public String toString() {
			return GSON.toJson(this);
		}

		public Builder() {
			this.resetToDefault();
		}

		public void resetToDefault() {
			this.coordinateScale = 684.412F;
			this.heightScale = 684.412F;
			this.upperLimitScale = 512.0F;
			this.lowerLimitScale = 512.0F;
			this.depthNoiseScaleX = 200.0F;
			this.depthNoiseScaleZ = 200.0F;
			this.depthNoiseScaleExponent = 0.5F;
			this.mainNoiseScaleX = 80.0F;
			this.mainNoiseScaleY = 160.0F;
			this.mainNoiseScaleZ = 80.0F;
			this.baseSize = 8.5F;
			this.stretchY = 12.0F;
			this.biomeDepthWeight = 1.0F;
			this.biomeDepthOffset = 0.0F;
			this.biomeScaleWeight = 1.0F;
			this.biomeScaleOffset = 0.0F;
			this.seaLevel = 63;
			this.useCaves = true;
			this.useDungeons = true;
			this.dungeonChance = 8;
			this.useStrongholds = true;
			this.useVillages = true;
			this.useMineshafts = true;
			this.useTemples = true;
			this.useMonuments = true;
			this.useRavines = true;
			this.useWaterLakes = true;
			this.waterLakeChance = 4;
			this.useLavaLakes = true;
			this.lavaLakeChance = 80;
			this.useLavaOceans = false;
			this.fixedBiome = -1;
			this.biomeSize = 4;
			this.riverSize = 4;
			this.dirtSize = 33;
			this.dirtCount = 10;
			this.dirtMinHeight = 0;
			this.dirtMaxHeight = 256;
			this.gravelSize = 33;
			this.gravelCount = 8;
			this.gravelMinHeight = 0;
			this.gravelMaxHeight = 256;
			this.graniteSize = 33;
			this.graniteCount = 10;
			this.graniteMinHeight = 0;
			this.graniteMaxHeight = 80;
			this.dioriteSize = 33;
			this.dioriteCount = 10;
			this.dioriteMinHeight = 0;
			this.dioriteMaxHeight = 80;
			this.andesiteSize = 33;
			this.andesiteCount = 10;
			this.andesiteMinHeight = 0;
			this.andesiteMaxHeight = 80;
			this.coalSize = 17;
			this.coalCount = 20;
			this.coalMinHeight = 0;
			this.coalMaxHeight = 128;
			this.ironSize = 9;
			this.ironCount = 20;
			this.ironMinHeight = 0;
			this.ironMaxHeight = 64;
			this.goldSize = 9;
			this.goldCount = 2;
			this.goldMinHeight = 0;
			this.goldMaxHeight = 32;
			this.redstoneSize = 8;
			this.redstoneCount = 8;
			this.redstoneMinHeight = 0;
			this.redstoneMaxHeight = 16;
			this.diamondSize = 8;
			this.diamondCount = 1;
			this.diamondMinHeight = 0;
			this.diamondMaxHeight = 16;
			this.lapisSize = 7;
			this.lapisCount = 1;
			this.lapisCenterHeight = 16;
			this.lapisSpread = 16;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj != null && this.getClass() == obj.getClass()) {
				CustomizedWorldProperties.Builder builder = (CustomizedWorldProperties.Builder)obj;
				if (this.andesiteCount != builder.andesiteCount) {
					return false;
				} else if (this.andesiteMaxHeight != builder.andesiteMaxHeight) {
					return false;
				} else if (this.andesiteMinHeight != builder.andesiteMinHeight) {
					return false;
				} else if (this.andesiteSize != builder.andesiteSize) {
					return false;
				} else if (Float.compare(builder.baseSize, this.baseSize) != 0) {
					return false;
				} else if (Float.compare(builder.biomeDepthOffset, this.biomeDepthOffset) != 0) {
					return false;
				} else if (Float.compare(builder.biomeDepthWeight, this.biomeDepthWeight) != 0) {
					return false;
				} else if (Float.compare(builder.biomeScaleOffset, this.biomeScaleOffset) != 0) {
					return false;
				} else if (Float.compare(builder.biomeScaleWeight, this.biomeScaleWeight) != 0) {
					return false;
				} else if (this.biomeSize != builder.biomeSize) {
					return false;
				} else if (this.coalCount != builder.coalCount) {
					return false;
				} else if (this.coalMaxHeight != builder.coalMaxHeight) {
					return false;
				} else if (this.coalMinHeight != builder.coalMinHeight) {
					return false;
				} else if (this.coalSize != builder.coalSize) {
					return false;
				} else if (Float.compare(builder.coordinateScale, this.coordinateScale) != 0) {
					return false;
				} else if (Float.compare(builder.depthNoiseScaleExponent, this.depthNoiseScaleExponent) != 0) {
					return false;
				} else if (Float.compare(builder.depthNoiseScaleX, this.depthNoiseScaleX) != 0) {
					return false;
				} else if (Float.compare(builder.depthNoiseScaleZ, this.depthNoiseScaleZ) != 0) {
					return false;
				} else if (this.diamondCount != builder.diamondCount) {
					return false;
				} else if (this.diamondMaxHeight != builder.diamondMaxHeight) {
					return false;
				} else if (this.diamondMinHeight != builder.diamondMinHeight) {
					return false;
				} else if (this.diamondSize != builder.diamondSize) {
					return false;
				} else if (this.dioriteCount != builder.dioriteCount) {
					return false;
				} else if (this.dioriteMaxHeight != builder.dioriteMaxHeight) {
					return false;
				} else if (this.dioriteMinHeight != builder.dioriteMinHeight) {
					return false;
				} else if (this.dioriteSize != builder.dioriteSize) {
					return false;
				} else if (this.dirtCount != builder.dirtCount) {
					return false;
				} else if (this.dirtMaxHeight != builder.dirtMaxHeight) {
					return false;
				} else if (this.dirtMinHeight != builder.dirtMinHeight) {
					return false;
				} else if (this.dirtSize != builder.dirtSize) {
					return false;
				} else if (this.dungeonChance != builder.dungeonChance) {
					return false;
				} else if (this.fixedBiome != builder.fixedBiome) {
					return false;
				} else if (this.goldCount != builder.goldCount) {
					return false;
				} else if (this.goldMaxHeight != builder.goldMaxHeight) {
					return false;
				} else if (this.goldMinHeight != builder.goldMinHeight) {
					return false;
				} else if (this.goldSize != builder.goldSize) {
					return false;
				} else if (this.graniteCount != builder.graniteCount) {
					return false;
				} else if (this.graniteMaxHeight != builder.graniteMaxHeight) {
					return false;
				} else if (this.graniteMinHeight != builder.graniteMinHeight) {
					return false;
				} else if (this.graniteSize != builder.graniteSize) {
					return false;
				} else if (this.gravelCount != builder.gravelCount) {
					return false;
				} else if (this.gravelMaxHeight != builder.gravelMaxHeight) {
					return false;
				} else if (this.gravelMinHeight != builder.gravelMinHeight) {
					return false;
				} else if (this.gravelSize != builder.gravelSize) {
					return false;
				} else if (Float.compare(builder.heightScale, this.heightScale) != 0) {
					return false;
				} else if (this.ironCount != builder.ironCount) {
					return false;
				} else if (this.ironMaxHeight != builder.ironMaxHeight) {
					return false;
				} else if (this.ironMinHeight != builder.ironMinHeight) {
					return false;
				} else if (this.ironSize != builder.ironSize) {
					return false;
				} else if (this.lapisCenterHeight != builder.lapisCenterHeight) {
					return false;
				} else if (this.lapisCount != builder.lapisCount) {
					return false;
				} else if (this.lapisSize != builder.lapisSize) {
					return false;
				} else if (this.lapisSpread != builder.lapisSpread) {
					return false;
				} else if (this.lavaLakeChance != builder.lavaLakeChance) {
					return false;
				} else if (Float.compare(builder.lowerLimitScale, this.lowerLimitScale) != 0) {
					return false;
				} else if (Float.compare(builder.mainNoiseScaleX, this.mainNoiseScaleX) != 0) {
					return false;
				} else if (Float.compare(builder.mainNoiseScaleY, this.mainNoiseScaleY) != 0) {
					return false;
				} else if (Float.compare(builder.mainNoiseScaleZ, this.mainNoiseScaleZ) != 0) {
					return false;
				} else if (this.redstoneCount != builder.redstoneCount) {
					return false;
				} else if (this.redstoneMaxHeight != builder.redstoneMaxHeight) {
					return false;
				} else if (this.redstoneMinHeight != builder.redstoneMinHeight) {
					return false;
				} else if (this.redstoneSize != builder.redstoneSize) {
					return false;
				} else if (this.riverSize != builder.riverSize) {
					return false;
				} else if (this.seaLevel != builder.seaLevel) {
					return false;
				} else if (Float.compare(builder.stretchY, this.stretchY) != 0) {
					return false;
				} else if (Float.compare(builder.upperLimitScale, this.upperLimitScale) != 0) {
					return false;
				} else if (this.useCaves != builder.useCaves) {
					return false;
				} else if (this.useDungeons != builder.useDungeons) {
					return false;
				} else if (this.useLavaLakes != builder.useLavaLakes) {
					return false;
				} else if (this.useLavaOceans != builder.useLavaOceans) {
					return false;
				} else if (this.useMineshafts != builder.useMineshafts) {
					return false;
				} else if (this.useRavines != builder.useRavines) {
					return false;
				} else if (this.useStrongholds != builder.useStrongholds) {
					return false;
				} else if (this.useTemples != builder.useTemples) {
					return false;
				} else if (this.useMonuments != builder.useMonuments) {
					return false;
				} else if (this.useVillages != builder.useVillages) {
					return false;
				} else {
					return this.useWaterLakes != builder.useWaterLakes ? false : this.waterLakeChance == builder.waterLakeChance;
				}
			} else {
				return false;
			}
		}

		public int hashCode() {
			int i = this.coordinateScale != 0.0F ? Float.floatToIntBits(this.coordinateScale) : 0;
			i = 31 * i + (this.heightScale != 0.0F ? Float.floatToIntBits(this.heightScale) : 0);
			i = 31 * i + (this.upperLimitScale != 0.0F ? Float.floatToIntBits(this.upperLimitScale) : 0);
			i = 31 * i + (this.lowerLimitScale != 0.0F ? Float.floatToIntBits(this.lowerLimitScale) : 0);
			i = 31 * i + (this.depthNoiseScaleX != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleX) : 0);
			i = 31 * i + (this.depthNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleZ) : 0);
			i = 31 * i + (this.depthNoiseScaleExponent != 0.0F ? Float.floatToIntBits(this.depthNoiseScaleExponent) : 0);
			i = 31 * i + (this.mainNoiseScaleX != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleX) : 0);
			i = 31 * i + (this.mainNoiseScaleY != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleY) : 0);
			i = 31 * i + (this.mainNoiseScaleZ != 0.0F ? Float.floatToIntBits(this.mainNoiseScaleZ) : 0);
			i = 31 * i + (this.baseSize != 0.0F ? Float.floatToIntBits(this.baseSize) : 0);
			i = 31 * i + (this.stretchY != 0.0F ? Float.floatToIntBits(this.stretchY) : 0);
			i = 31 * i + (this.biomeDepthWeight != 0.0F ? Float.floatToIntBits(this.biomeDepthWeight) : 0);
			i = 31 * i + (this.biomeDepthOffset != 0.0F ? Float.floatToIntBits(this.biomeDepthOffset) : 0);
			i = 31 * i + (this.biomeScaleWeight != 0.0F ? Float.floatToIntBits(this.biomeScaleWeight) : 0);
			i = 31 * i + (this.biomeScaleOffset != 0.0F ? Float.floatToIntBits(this.biomeScaleOffset) : 0);
			i = 31 * i + this.seaLevel;
			i = 31 * i + (this.useCaves ? 1 : 0);
			i = 31 * i + (this.useDungeons ? 1 : 0);
			i = 31 * i + this.dungeonChance;
			i = 31 * i + (this.useStrongholds ? 1 : 0);
			i = 31 * i + (this.useVillages ? 1 : 0);
			i = 31 * i + (this.useMineshafts ? 1 : 0);
			i = 31 * i + (this.useTemples ? 1 : 0);
			i = 31 * i + (this.useMonuments ? 1 : 0);
			i = 31 * i + (this.useRavines ? 1 : 0);
			i = 31 * i + (this.useWaterLakes ? 1 : 0);
			i = 31 * i + this.waterLakeChance;
			i = 31 * i + (this.useLavaLakes ? 1 : 0);
			i = 31 * i + this.lavaLakeChance;
			i = 31 * i + (this.useLavaOceans ? 1 : 0);
			i = 31 * i + this.fixedBiome;
			i = 31 * i + this.biomeSize;
			i = 31 * i + this.riverSize;
			i = 31 * i + this.dirtSize;
			i = 31 * i + this.dirtCount;
			i = 31 * i + this.dirtMinHeight;
			i = 31 * i + this.dirtMaxHeight;
			i = 31 * i + this.gravelSize;
			i = 31 * i + this.gravelCount;
			i = 31 * i + this.gravelMinHeight;
			i = 31 * i + this.gravelMaxHeight;
			i = 31 * i + this.graniteSize;
			i = 31 * i + this.graniteCount;
			i = 31 * i + this.graniteMinHeight;
			i = 31 * i + this.graniteMaxHeight;
			i = 31 * i + this.dioriteSize;
			i = 31 * i + this.dioriteCount;
			i = 31 * i + this.dioriteMinHeight;
			i = 31 * i + this.dioriteMaxHeight;
			i = 31 * i + this.andesiteSize;
			i = 31 * i + this.andesiteCount;
			i = 31 * i + this.andesiteMinHeight;
			i = 31 * i + this.andesiteMaxHeight;
			i = 31 * i + this.coalSize;
			i = 31 * i + this.coalCount;
			i = 31 * i + this.coalMinHeight;
			i = 31 * i + this.coalMaxHeight;
			i = 31 * i + this.ironSize;
			i = 31 * i + this.ironCount;
			i = 31 * i + this.ironMinHeight;
			i = 31 * i + this.ironMaxHeight;
			i = 31 * i + this.goldSize;
			i = 31 * i + this.goldCount;
			i = 31 * i + this.goldMinHeight;
			i = 31 * i + this.goldMaxHeight;
			i = 31 * i + this.redstoneSize;
			i = 31 * i + this.redstoneCount;
			i = 31 * i + this.redstoneMinHeight;
			i = 31 * i + this.redstoneMaxHeight;
			i = 31 * i + this.diamondSize;
			i = 31 * i + this.diamondCount;
			i = 31 * i + this.diamondMinHeight;
			i = 31 * i + this.diamondMaxHeight;
			i = 31 * i + this.lapisSize;
			i = 31 * i + this.lapisCount;
			i = 31 * i + this.lapisCenterHeight;
			return 31 * i + this.lapisSpread;
		}

		public CustomizedWorldProperties build() {
			return new CustomizedWorldProperties(this);
		}
	}

	public static class Serializer implements JsonDeserializer<CustomizedWorldProperties.Builder>, JsonSerializer<CustomizedWorldProperties.Builder> {
		public CustomizedWorldProperties.Builder deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			CustomizedWorldProperties.Builder builder = new CustomizedWorldProperties.Builder();

			try {
				builder.coordinateScale = JsonHelper.getFloat(jsonObject, "coordinateScale", builder.coordinateScale);
				builder.heightScale = JsonHelper.getFloat(jsonObject, "heightScale", builder.heightScale);
				builder.lowerLimitScale = JsonHelper.getFloat(jsonObject, "lowerLimitScale", builder.lowerLimitScale);
				builder.upperLimitScale = JsonHelper.getFloat(jsonObject, "upperLimitScale", builder.upperLimitScale);
				builder.depthNoiseScaleX = JsonHelper.getFloat(jsonObject, "depthNoiseScaleX", builder.depthNoiseScaleX);
				builder.depthNoiseScaleZ = JsonHelper.getFloat(jsonObject, "depthNoiseScaleZ", builder.depthNoiseScaleZ);
				builder.depthNoiseScaleExponent = JsonHelper.getFloat(jsonObject, "depthNoiseScaleExponent", builder.depthNoiseScaleExponent);
				builder.mainNoiseScaleX = JsonHelper.getFloat(jsonObject, "mainNoiseScaleX", builder.mainNoiseScaleX);
				builder.mainNoiseScaleY = JsonHelper.getFloat(jsonObject, "mainNoiseScaleY", builder.mainNoiseScaleY);
				builder.mainNoiseScaleZ = JsonHelper.getFloat(jsonObject, "mainNoiseScaleZ", builder.mainNoiseScaleZ);
				builder.baseSize = JsonHelper.getFloat(jsonObject, "baseSize", builder.baseSize);
				builder.stretchY = JsonHelper.getFloat(jsonObject, "stretchY", builder.stretchY);
				builder.biomeDepthWeight = JsonHelper.getFloat(jsonObject, "biomeDepthWeight", builder.biomeDepthWeight);
				builder.biomeDepthOffset = JsonHelper.getFloat(jsonObject, "biomeDepthOffset", builder.biomeDepthOffset);
				builder.biomeScaleWeight = JsonHelper.getFloat(jsonObject, "biomeScaleWeight", builder.biomeScaleWeight);
				builder.biomeScaleOffset = JsonHelper.getFloat(jsonObject, "biomeScaleOffset", builder.biomeScaleOffset);
				builder.seaLevel = JsonHelper.getInt(jsonObject, "seaLevel", builder.seaLevel);
				builder.useCaves = JsonHelper.getBoolean(jsonObject, "useCaves", builder.useCaves);
				builder.useDungeons = JsonHelper.getBoolean(jsonObject, "useDungeons", builder.useDungeons);
				builder.dungeonChance = JsonHelper.getInt(jsonObject, "dungeonChance", builder.dungeonChance);
				builder.useStrongholds = JsonHelper.getBoolean(jsonObject, "useStrongholds", builder.useStrongholds);
				builder.useVillages = JsonHelper.getBoolean(jsonObject, "useVillages", builder.useVillages);
				builder.useMineshafts = JsonHelper.getBoolean(jsonObject, "useMineShafts", builder.useMineshafts);
				builder.useTemples = JsonHelper.getBoolean(jsonObject, "useTemples", builder.useTemples);
				builder.useMonuments = JsonHelper.getBoolean(jsonObject, "useMonuments", builder.useMonuments);
				builder.useRavines = JsonHelper.getBoolean(jsonObject, "useRavines", builder.useRavines);
				builder.useWaterLakes = JsonHelper.getBoolean(jsonObject, "useWaterLakes", builder.useWaterLakes);
				builder.waterLakeChance = JsonHelper.getInt(jsonObject, "waterLakeChance", builder.waterLakeChance);
				builder.useLavaLakes = JsonHelper.getBoolean(jsonObject, "useLavaLakes", builder.useLavaLakes);
				builder.lavaLakeChance = JsonHelper.getInt(jsonObject, "lavaLakeChance", builder.lavaLakeChance);
				builder.useLavaOceans = JsonHelper.getBoolean(jsonObject, "useLavaOceans", builder.useLavaOceans);
				builder.fixedBiome = JsonHelper.getInt(jsonObject, "fixedBiome", builder.fixedBiome);
				if (builder.fixedBiome < 38 && builder.fixedBiome >= -1) {
					if (builder.fixedBiome >= Biome.getBiomeIndex(Biomes.NETHER)) {
						builder.fixedBiome += 2;
					}
				} else {
					builder.fixedBiome = -1;
				}

				builder.biomeSize = JsonHelper.getInt(jsonObject, "biomeSize", builder.biomeSize);
				builder.riverSize = JsonHelper.getInt(jsonObject, "riverSize", builder.riverSize);
				builder.dirtSize = JsonHelper.getInt(jsonObject, "dirtSize", builder.dirtSize);
				builder.dirtCount = JsonHelper.getInt(jsonObject, "dirtCount", builder.dirtCount);
				builder.dirtMinHeight = JsonHelper.getInt(jsonObject, "dirtMinHeight", builder.dirtMinHeight);
				builder.dirtMaxHeight = JsonHelper.getInt(jsonObject, "dirtMaxHeight", builder.dirtMaxHeight);
				builder.gravelSize = JsonHelper.getInt(jsonObject, "gravelSize", builder.gravelSize);
				builder.gravelCount = JsonHelper.getInt(jsonObject, "gravelCount", builder.gravelCount);
				builder.gravelMinHeight = JsonHelper.getInt(jsonObject, "gravelMinHeight", builder.gravelMinHeight);
				builder.gravelMaxHeight = JsonHelper.getInt(jsonObject, "gravelMaxHeight", builder.gravelMaxHeight);
				builder.graniteSize = JsonHelper.getInt(jsonObject, "graniteSize", builder.graniteSize);
				builder.graniteCount = JsonHelper.getInt(jsonObject, "graniteCount", builder.graniteCount);
				builder.graniteMinHeight = JsonHelper.getInt(jsonObject, "graniteMinHeight", builder.graniteMinHeight);
				builder.graniteMaxHeight = JsonHelper.getInt(jsonObject, "graniteMaxHeight", builder.graniteMaxHeight);
				builder.dioriteSize = JsonHelper.getInt(jsonObject, "dioriteSize", builder.dioriteSize);
				builder.dioriteCount = JsonHelper.getInt(jsonObject, "dioriteCount", builder.dioriteCount);
				builder.dioriteMinHeight = JsonHelper.getInt(jsonObject, "dioriteMinHeight", builder.dioriteMinHeight);
				builder.dioriteMaxHeight = JsonHelper.getInt(jsonObject, "dioriteMaxHeight", builder.dioriteMaxHeight);
				builder.andesiteSize = JsonHelper.getInt(jsonObject, "andesiteSize", builder.andesiteSize);
				builder.andesiteCount = JsonHelper.getInt(jsonObject, "andesiteCount", builder.andesiteCount);
				builder.andesiteMinHeight = JsonHelper.getInt(jsonObject, "andesiteMinHeight", builder.andesiteMinHeight);
				builder.andesiteMaxHeight = JsonHelper.getInt(jsonObject, "andesiteMaxHeight", builder.andesiteMaxHeight);
				builder.coalSize = JsonHelper.getInt(jsonObject, "coalSize", builder.coalSize);
				builder.coalCount = JsonHelper.getInt(jsonObject, "coalCount", builder.coalCount);
				builder.coalMinHeight = JsonHelper.getInt(jsonObject, "coalMinHeight", builder.coalMinHeight);
				builder.coalMaxHeight = JsonHelper.getInt(jsonObject, "coalMaxHeight", builder.coalMaxHeight);
				builder.ironSize = JsonHelper.getInt(jsonObject, "ironSize", builder.ironSize);
				builder.ironCount = JsonHelper.getInt(jsonObject, "ironCount", builder.ironCount);
				builder.ironMinHeight = JsonHelper.getInt(jsonObject, "ironMinHeight", builder.ironMinHeight);
				builder.ironMaxHeight = JsonHelper.getInt(jsonObject, "ironMaxHeight", builder.ironMaxHeight);
				builder.goldSize = JsonHelper.getInt(jsonObject, "goldSize", builder.goldSize);
				builder.goldCount = JsonHelper.getInt(jsonObject, "goldCount", builder.goldCount);
				builder.goldMinHeight = JsonHelper.getInt(jsonObject, "goldMinHeight", builder.goldMinHeight);
				builder.goldMaxHeight = JsonHelper.getInt(jsonObject, "goldMaxHeight", builder.goldMaxHeight);
				builder.redstoneSize = JsonHelper.getInt(jsonObject, "redstoneSize", builder.redstoneSize);
				builder.redstoneCount = JsonHelper.getInt(jsonObject, "redstoneCount", builder.redstoneCount);
				builder.redstoneMinHeight = JsonHelper.getInt(jsonObject, "redstoneMinHeight", builder.redstoneMinHeight);
				builder.redstoneMaxHeight = JsonHelper.getInt(jsonObject, "redstoneMaxHeight", builder.redstoneMaxHeight);
				builder.diamondSize = JsonHelper.getInt(jsonObject, "diamondSize", builder.diamondSize);
				builder.diamondCount = JsonHelper.getInt(jsonObject, "diamondCount", builder.diamondCount);
				builder.diamondMinHeight = JsonHelper.getInt(jsonObject, "diamondMinHeight", builder.diamondMinHeight);
				builder.diamondMaxHeight = JsonHelper.getInt(jsonObject, "diamondMaxHeight", builder.diamondMaxHeight);
				builder.lapisSize = JsonHelper.getInt(jsonObject, "lapisSize", builder.lapisSize);
				builder.lapisCount = JsonHelper.getInt(jsonObject, "lapisCount", builder.lapisCount);
				builder.lapisCenterHeight = JsonHelper.getInt(jsonObject, "lapisCenterHeight", builder.lapisCenterHeight);
				builder.lapisSpread = JsonHelper.getInt(jsonObject, "lapisSpread", builder.lapisSpread);
			} catch (Exception var7) {
			}

			return builder;
		}

		public JsonElement serialize(CustomizedWorldProperties.Builder builder, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("coordinateScale", builder.coordinateScale);
			jsonObject.addProperty("heightScale", builder.heightScale);
			jsonObject.addProperty("lowerLimitScale", builder.lowerLimitScale);
			jsonObject.addProperty("upperLimitScale", builder.upperLimitScale);
			jsonObject.addProperty("depthNoiseScaleX", builder.depthNoiseScaleX);
			jsonObject.addProperty("depthNoiseScaleZ", builder.depthNoiseScaleZ);
			jsonObject.addProperty("depthNoiseScaleExponent", builder.depthNoiseScaleExponent);
			jsonObject.addProperty("mainNoiseScaleX", builder.mainNoiseScaleX);
			jsonObject.addProperty("mainNoiseScaleY", builder.mainNoiseScaleY);
			jsonObject.addProperty("mainNoiseScaleZ", builder.mainNoiseScaleZ);
			jsonObject.addProperty("baseSize", builder.baseSize);
			jsonObject.addProperty("stretchY", builder.stretchY);
			jsonObject.addProperty("biomeDepthWeight", builder.biomeDepthWeight);
			jsonObject.addProperty("biomeDepthOffset", builder.biomeDepthOffset);
			jsonObject.addProperty("biomeScaleWeight", builder.biomeScaleWeight);
			jsonObject.addProperty("biomeScaleOffset", builder.biomeScaleOffset);
			jsonObject.addProperty("seaLevel", builder.seaLevel);
			jsonObject.addProperty("useCaves", builder.useCaves);
			jsonObject.addProperty("useDungeons", builder.useDungeons);
			jsonObject.addProperty("dungeonChance", builder.dungeonChance);
			jsonObject.addProperty("useStrongholds", builder.useStrongholds);
			jsonObject.addProperty("useVillages", builder.useVillages);
			jsonObject.addProperty("useMineShafts", builder.useMineshafts);
			jsonObject.addProperty("useTemples", builder.useTemples);
			jsonObject.addProperty("useMonuments", builder.useMonuments);
			jsonObject.addProperty("useRavines", builder.useRavines);
			jsonObject.addProperty("useWaterLakes", builder.useWaterLakes);
			jsonObject.addProperty("waterLakeChance", builder.waterLakeChance);
			jsonObject.addProperty("useLavaLakes", builder.useLavaLakes);
			jsonObject.addProperty("lavaLakeChance", builder.lavaLakeChance);
			jsonObject.addProperty("useLavaOceans", builder.useLavaOceans);
			jsonObject.addProperty("fixedBiome", builder.fixedBiome);
			jsonObject.addProperty("biomeSize", builder.biomeSize);
			jsonObject.addProperty("riverSize", builder.riverSize);
			jsonObject.addProperty("dirtSize", builder.dirtSize);
			jsonObject.addProperty("dirtCount", builder.dirtCount);
			jsonObject.addProperty("dirtMinHeight", builder.dirtMinHeight);
			jsonObject.addProperty("dirtMaxHeight", builder.dirtMaxHeight);
			jsonObject.addProperty("gravelSize", builder.gravelSize);
			jsonObject.addProperty("gravelCount", builder.gravelCount);
			jsonObject.addProperty("gravelMinHeight", builder.gravelMinHeight);
			jsonObject.addProperty("gravelMaxHeight", builder.gravelMaxHeight);
			jsonObject.addProperty("graniteSize", builder.graniteSize);
			jsonObject.addProperty("graniteCount", builder.graniteCount);
			jsonObject.addProperty("graniteMinHeight", builder.graniteMinHeight);
			jsonObject.addProperty("graniteMaxHeight", builder.graniteMaxHeight);
			jsonObject.addProperty("dioriteSize", builder.dioriteSize);
			jsonObject.addProperty("dioriteCount", builder.dioriteCount);
			jsonObject.addProperty("dioriteMinHeight", builder.dioriteMinHeight);
			jsonObject.addProperty("dioriteMaxHeight", builder.dioriteMaxHeight);
			jsonObject.addProperty("andesiteSize", builder.andesiteSize);
			jsonObject.addProperty("andesiteCount", builder.andesiteCount);
			jsonObject.addProperty("andesiteMinHeight", builder.andesiteMinHeight);
			jsonObject.addProperty("andesiteMaxHeight", builder.andesiteMaxHeight);
			jsonObject.addProperty("coalSize", builder.coalSize);
			jsonObject.addProperty("coalCount", builder.coalCount);
			jsonObject.addProperty("coalMinHeight", builder.coalMinHeight);
			jsonObject.addProperty("coalMaxHeight", builder.coalMaxHeight);
			jsonObject.addProperty("ironSize", builder.ironSize);
			jsonObject.addProperty("ironCount", builder.ironCount);
			jsonObject.addProperty("ironMinHeight", builder.ironMinHeight);
			jsonObject.addProperty("ironMaxHeight", builder.ironMaxHeight);
			jsonObject.addProperty("goldSize", builder.goldSize);
			jsonObject.addProperty("goldCount", builder.goldCount);
			jsonObject.addProperty("goldMinHeight", builder.goldMinHeight);
			jsonObject.addProperty("goldMaxHeight", builder.goldMaxHeight);
			jsonObject.addProperty("redstoneSize", builder.redstoneSize);
			jsonObject.addProperty("redstoneCount", builder.redstoneCount);
			jsonObject.addProperty("redstoneMinHeight", builder.redstoneMinHeight);
			jsonObject.addProperty("redstoneMaxHeight", builder.redstoneMaxHeight);
			jsonObject.addProperty("diamondSize", builder.diamondSize);
			jsonObject.addProperty("diamondCount", builder.diamondCount);
			jsonObject.addProperty("diamondMinHeight", builder.diamondMinHeight);
			jsonObject.addProperty("diamondMaxHeight", builder.diamondMaxHeight);
			jsonObject.addProperty("lapisSize", builder.lapisSize);
			jsonObject.addProperty("lapisCount", builder.lapisCount);
			jsonObject.addProperty("lapisCenterHeight", builder.lapisCenterHeight);
			jsonObject.addProperty("lapisSpread", builder.lapisSpread);
			return jsonObject;
		}
	}
}
