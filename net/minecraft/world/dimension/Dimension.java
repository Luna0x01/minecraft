package net.minecraft.world.dimension;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LayeredBiomeSource;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.DebugChunkGenerator;
import net.minecraft.world.chunk.FlatChunkGenerator;
import net.minecraft.world.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.FlatWorldHelper;
import net.minecraft.world.level.LevelGeneratorType;

public abstract class Dimension {
	public static final float[] MOON_PHASE_TO_SIZE = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
	protected World world;
	private LevelGeneratorType generatorType;
	private String generatorOptions;
	protected LayeredBiomeSource biomeSource;
	protected boolean waterVaporizes;
	protected boolean hasNoSkylight;
	protected final float[] lightLevelToBrightness = new float[16];
	protected int dimensionType;
	private final float[] backgroundColor = new float[4];

	public final void copyFromWorld(World world) {
		this.world = world;
		this.generatorType = world.getLevelProperties().getGeneratorType();
		this.generatorOptions = world.getLevelProperties().getGeneratorOptions();
		this.init();
		this.initializeLightLevelToBrightness();
	}

	protected void initializeLightLevelToBrightness() {
		float f = 0.0F;

		for (int i = 0; i <= 15; i++) {
			float g = 1.0F - (float)i / 15.0F;
			this.lightLevelToBrightness[i] = (1.0F - g) / (g * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}

	protected void init() {
		LevelGeneratorType levelGeneratorType = this.world.getLevelProperties().getGeneratorType();
		if (levelGeneratorType == LevelGeneratorType.FLAT) {
			FlatWorldHelper flatWorldHelper = FlatWorldHelper.getHelper(this.world.getLevelProperties().getGeneratorOptions());
			this.biomeSource = new SingletonBiomeSource(Biome.getBiomeById(flatWorldHelper.getBiomeId(), Biome.DEFAULT), 0.5F);
		} else if (levelGeneratorType == LevelGeneratorType.DEBUG) {
			this.biomeSource = new SingletonBiomeSource(Biome.PLAINS, 0.0F);
		} else {
			this.biomeSource = new LayeredBiomeSource(this.world);
		}
	}

	public ChunkProvider createChunkGenerator() {
		if (this.generatorType == LevelGeneratorType.FLAT) {
			return new FlatChunkGenerator(this.world, this.world.getSeed(), this.world.getLevelProperties().hasStructures(), this.generatorOptions);
		} else if (this.generatorType == LevelGeneratorType.DEBUG) {
			return new DebugChunkGenerator(this.world);
		} else {
			return this.generatorType == LevelGeneratorType.CUSTOMIZED
				? new SurfaceChunkGenerator(this.world, this.world.getSeed(), this.world.getLevelProperties().hasStructures(), this.generatorOptions)
				: new SurfaceChunkGenerator(this.world, this.world.getSeed(), this.world.getLevelProperties().hasStructures(), this.generatorOptions);
		}
	}

	public boolean isSpawnableBlock(int x, int z) {
		return this.world.getBlockAt(new BlockPos(x, 0, z)) == Blocks.GRASS;
	}

	public float getSkyAngle(long timeOfDay, float tickDelta) {
		int i = (int)(timeOfDay % 24000L);
		float f = ((float)i + tickDelta) / 24000.0F - 0.25F;
		if (f < 0.0F) {
			f++;
		}

		if (f > 1.0F) {
			f--;
		}

		float var7 = 1.0F - (float)((Math.cos((double)f * Math.PI) + 1.0) / 2.0);
		return f + (var7 - f) / 3.0F;
	}

	public int getMoonPhase(long time) {
		return (int)(time / 24000L % 8L + 8L) % 8;
	}

	public boolean canPlayersSleep() {
		return true;
	}

	public float[] getBackgroundColor(float skyAngle, float tickDelta) {
		float f = 0.4F;
		float g = MathHelper.cos(skyAngle * (float) Math.PI * 2.0F) - 0.0F;
		float h = -0.0F;
		if (g >= h - f && g <= h + f) {
			float i = (g - h) / f * 0.5F + 0.5F;
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

	public Vec3d getFogColor(float skyAngle, float tickDelta) {
		float f = MathHelper.cos(skyAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		float g = 0.7529412F;
		float h = 0.84705883F;
		float i = 1.0F;
		g *= f * 0.94F + 0.06F;
		h *= f * 0.94F + 0.06F;
		i *= f * 0.91F + 0.09F;
		return new Vec3d((double)g, (double)h, (double)i);
	}

	public boolean containsWorldSpawn() {
		return true;
	}

	public static Dimension getById(int id) {
		if (id == -1) {
			return new TheNetherDimension();
		} else if (id == 0) {
			return new OverworldDimension();
		} else {
			return id == 1 ? new TheEndDimension() : null;
		}
	}

	public float getCloudHeight() {
		return 128.0F;
	}

	public boolean hasGround() {
		return true;
	}

	public BlockPos getForcedSpawnPoint() {
		return null;
	}

	public int getAverageYLevel() {
		return this.generatorType == LevelGeneratorType.FLAT ? 4 : this.world.getSeaLevel() + 1;
	}

	public double method_3994() {
		return this.generatorType == LevelGeneratorType.FLAT ? 1.0 : 0.03125;
	}

	public boolean isFogThick(int x, int z) {
		return false;
	}

	public abstract String getName();

	public abstract String getPersistentStateSuffix();

	public LayeredBiomeSource getBiomeSource() {
		return this.biomeSource;
	}

	public boolean doesWaterVaporize() {
		return this.waterVaporizes;
	}

	public boolean hasNoSkylight() {
		return this.hasNoSkylight;
	}

	public float[] getLightLevelToBrightness() {
		return this.lightLevelToBrightness;
	}

	public int getType() {
		return this.dimensionType;
	}

	public WorldBorder createWorldBorder() {
		return new WorldBorder();
	}
}
