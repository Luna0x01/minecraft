package net.minecraft.world.dimension;

import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.class_2711;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.NetherChunkGenerator;

public class TheNetherDimension extends Dimension {
	@Override
	public void init() {
		this.field_4787 = new class_2711(Biomes.NETHER);
		this.waterVaporizes = true;
		this.hasNoSkylight = true;
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
	public ChunkGenerator getChunkGenerator() {
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

	@Override
	public DimensionType getDimensionType() {
		return DimensionType.NETHER;
	}
}
