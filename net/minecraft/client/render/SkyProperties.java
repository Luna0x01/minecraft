package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public abstract class SkyProperties {
	private static final Object2ObjectMap<Identifier, SkyProperties> BY_IDENTIFIER = Util.make(new Object2ObjectArrayMap(), map -> {
		SkyProperties.Overworld overworld = new SkyProperties.Overworld();
		map.defaultReturnValue(overworld);
		map.put(DimensionType.OVERWORLD_ID, overworld);
		map.put(DimensionType.THE_NETHER_ID, new SkyProperties.Nether());
		map.put(DimensionType.THE_END_ID, new SkyProperties.End());
	});
	private final float[] rgba = new float[4];
	private final float cloudsHeight;
	private final boolean alternateSkyColor;
	private final SkyProperties.SkyType skyType;
	private final boolean brightenLighting;
	private final boolean darkened;

	public SkyProperties(float cloudsHeight, boolean alternateSkyColor, SkyProperties.SkyType skyType, boolean brightenLighting, boolean darkened) {
		this.cloudsHeight = cloudsHeight;
		this.alternateSkyColor = alternateSkyColor;
		this.skyType = skyType;
		this.brightenLighting = brightenLighting;
		this.darkened = darkened;
	}

	public static SkyProperties byDimensionType(DimensionType dimensionType) {
		return (SkyProperties)BY_IDENTIFIER.get(dimensionType.getSkyProperties());
	}

	@Nullable
	public float[] getFogColorOverride(float skyAngle, float tickDelta) {
		float f = 0.4F;
		float g = MathHelper.cos(skyAngle * (float) (Math.PI * 2)) - 0.0F;
		float h = -0.0F;
		if (g >= -0.4F && g <= 0.4F) {
			float i = (g - -0.0F) / 0.4F * 0.5F + 0.5F;
			float j = 1.0F - (1.0F - MathHelper.sin(i * (float) Math.PI)) * 0.99F;
			j *= j;
			this.rgba[0] = i * 0.3F + 0.7F;
			this.rgba[1] = i * i * 0.7F + 0.2F;
			this.rgba[2] = i * i * 0.0F + 0.2F;
			this.rgba[3] = j;
			return this.rgba;
		} else {
			return null;
		}
	}

	public float getCloudsHeight() {
		return this.cloudsHeight;
	}

	public boolean isAlternateSkyColor() {
		return this.alternateSkyColor;
	}

	public abstract Vec3d adjustFogColor(Vec3d color, float sunHeight);

	public abstract boolean useThickFog(int camX, int camY);

	public SkyProperties.SkyType getSkyType() {
		return this.skyType;
	}

	public boolean shouldBrightenLighting() {
		return this.brightenLighting;
	}

	public boolean isDarkened() {
		return this.darkened;
	}

	public static class End extends SkyProperties {
		public End() {
			super(Float.NaN, false, SkyProperties.SkyType.END, true, false);
		}

		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
			return color.multiply(0.15F);
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return false;
		}

		@Nullable
		@Override
		public float[] getFogColorOverride(float skyAngle, float tickDelta) {
			return null;
		}
	}

	public static class Nether extends SkyProperties {
		public Nether() {
			super(Float.NaN, true, SkyProperties.SkyType.NONE, false, true);
		}

		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
			return color;
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return true;
		}
	}

	public static class Overworld extends SkyProperties {
		public static final int CLOUDS_HEIGHT = 128;

		public Overworld() {
			super(128.0F, true, SkyProperties.SkyType.NORMAL, false, false);
		}

		@Override
		public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
			return color.multiply((double)(sunHeight * 0.94F + 0.06F), (double)(sunHeight * 0.94F + 0.06F), (double)(sunHeight * 0.91F + 0.09F));
		}

		@Override
		public boolean useThickFog(int camX, int camY) {
			return false;
		}
	}

	public static enum SkyType {
		NONE,
		NORMAL,
		END;
	}
}
