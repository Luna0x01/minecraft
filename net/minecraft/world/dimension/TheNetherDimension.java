package net.minecraft.world.dimension;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.NetherChunkGenerator;

public class TheNetherDimension extends Dimension {
	@Override
	public void init() {
		this.biomeSource = new SingletonBiomeSource(Biome.HELL, 0.0F);
		this.waterVaporizes = true;
		this.hasNoSkylight = true;
		this.dimensionType = -1;
	}

	@Override
	public Vec3d getFogColor(float skyAngle, float tickDelta) {
		return new Vec3d(0.2F, 0.03F, 0.03F);
	}

	@Override
	protected void initializeLightLevelToBrightness() {
		float f = 0.1F;

		for (int i = 0; i <= 15; i++) {
			float g = 1.0F - (float)i / 15.0F;
			this.lightLevelToBrightness[i] = (1.0F - g) / (g * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}

	@Override
	public ChunkProvider createChunkGenerator() {
		return new NetherChunkGenerator(this.world, this.world.getLevelProperties().hasStructures(), this.world.getSeed());
	}

	@Override
	public boolean canPlayersSleep() {
		return false;
	}

	@Override
	public boolean isSpawnableBlock(int x, int z) {
		return false;
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta) {
		return 0.5F;
	}

	@Override
	public boolean containsWorldSpawn() {
		return false;
	}

	@Override
	public boolean isFogThick(int x, int z) {
		return true;
	}

	@Override
	public String getName() {
		return "Nether";
	}

	@Override
	public String getPersistentStateSuffix() {
		return "_nether";
	}

	@Override
	public WorldBorder createWorldBorder() {
		return new WorldBorder() {
			@Override
			public double getCenterX() {
				return super.getCenterX() / 8.0;
			}

			@Override
			public double getCenterZ() {
				return super.getCenterZ() / 8.0;
			}
		};
	}
}
