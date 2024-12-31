package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.class_3807;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.BiomeSourceType;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class TheNetherDimension extends Dimension {
	@Override
	public void init() {
		this.waterVaporizes = true;
		this.hasNoSkylight = true;
		this.field_18953 = false;
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
			this.lightLevelToBrightness[i] = (1.0F - g) / (g * 3.0F + 1.0F) * 0.9F + 0.1F;
		}
	}

	@Override
	public ChunkGenerator<?> method_17193() {
		class_3807 lv = ChunkGeneratorType.CAVES.method_17040();
		lv.method_17212(Blocks.NETHERRACK.getDefaultState());
		lv.method_17213(Blocks.LAVA.getDefaultState());
		return ChunkGeneratorType.CAVES.create(this.world, BiomeSourceType.FIXED.method_16484(BiomeSourceType.FIXED.method_16486().method_16498(Biomes.NETHER)), lv);
	}

	@Override
	public boolean canPlayersSleep() {
		return false;
	}

	@Nullable
	@Override
	public BlockPos method_17191(ChunkPos chunkPos, boolean bl) {
		return null;
	}

	@Nullable
	@Override
	public BlockPos method_17190(int i, int j, boolean bl) {
		return null;
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
	public DimensionType method_11789() {
		return DimensionType.THE_NETHER;
	}
}
