package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.level.LevelGeneratorType;

public abstract class Dimension {
	public static final float[] field_18952 = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	protected World world;
	protected boolean waterVaporizes;
	protected boolean hasNoSkylight;
	protected boolean field_18953;
	protected final float[] lightLevelToBrightness = new float[16];
	private final float[] backgroundColor = new float[4];

	public final void copyFromWorld(World world) {
		this.world = world;
		this.init();
		this.initializeLightLevelToBrightness();
	}

	protected void initializeLightLevelToBrightness() {
		float f = 0.0F;

		for (int i = 0; i <= 15; i++) {
			float g = 1.0F - (float)i / 15.0F;
			this.lightLevelToBrightness[i] = (1.0F - g) / (g * 3.0F + 1.0F) * 1.0F + 0.0F;
		}
	}

	public int getMoonPhase(long time) {
		return (int)(time / 24000L % 8L + 8L) % 8;
	}

	@Nullable
	public float[] getBackgroundColor(float skyAngle, float tickDelta) {
		float f = 0.4F;
		float g = MathHelper.cos(skyAngle * (float) (Math.PI * 2)) - 0.0F;
		float h = -0.0F;
		if (g >= -0.4F && g <= 0.4F) {
			float i = (g - -0.0F) / 0.4F * 0.5F + 0.5F;
			float j = 1.0F - (1.0F - MathHelper.sin(i * (float) Math.PI)) * 0.99F;
			j *= j;
			this.backgroundColor[0] = i * 0.3F + 0.7F;
			this.backgroundColor[1] = i * i * 0.7F + 0.2F;
			this.backgroundColor[2] = i * i * 0.0F + 0.2F;
			this.backgroundColor[3] = j;
			return this.backgroundColor;
		} else {
			return null;
		}
	}

	public float getCloudHeight() {
		return 128.0F;
	}

	public boolean hasGround() {
		return true;
	}

	@Nullable
	public BlockPos getForcedSpawnPoint() {
		return null;
	}

	public double method_17192() {
		return this.world.method_3588().getGeneratorType() == LevelGeneratorType.FLAT ? 1.0 : 0.03125;
	}

	public boolean doesWaterVaporize() {
		return this.waterVaporizes;
	}

	public boolean isOverworld() {
		return this.field_18953;
	}

	public boolean hasNoSkylight() {
		return this.hasNoSkylight;
	}

	public float[] getLightLevelToBrightness() {
		return this.lightLevelToBrightness;
	}

	public WorldBorder createWorldBorder() {
		return new WorldBorder();
	}

	public void method_11786(ServerPlayerEntity player) {
	}

	public void method_11787(ServerPlayerEntity player) {
	}

	public void method_11790() {
	}

	public void method_11791() {
	}

	public boolean method_17189(int i, int j) {
		return !this.world.method_16335(i, j);
	}

	protected abstract void init();

	public abstract ChunkGenerator<?> method_17193();

	@Nullable
	public abstract BlockPos method_17191(ChunkPos chunkPos, boolean bl);

	@Nullable
	public abstract BlockPos method_17190(int i, int j, boolean bl);

	public abstract float getSkyAngle(long timeOfDay, float tickDelta);

	public abstract boolean canPlayersSleep();

	public abstract Vec3d getFogColor(float skyAngle, float tickDelta);

	public abstract boolean containsWorldSpawn();

	public abstract boolean isFogThick(int x, int z);

	public abstract DimensionType method_11789();
}
