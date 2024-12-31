package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class TheNetherDimension extends Dimension {
	private static final Vec3d field_21216 = new Vec3d(0.2F, 0.03F, 0.03F);

	public TheNetherDimension(World world, DimensionType dimensionType) {
		super(world, dimensionType, 0.1F);
		this.waterVaporizes = true;
		this.isNether = true;
	}

	@Override
	public Vec3d getFogColor(float f, float g) {
		return field_21216;
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		CavesChunkGeneratorConfig cavesChunkGeneratorConfig = ChunkGeneratorType.field_12765.createSettings();
		cavesChunkGeneratorConfig.setDefaultBlock(Blocks.field_10515.getDefaultState());
		cavesChunkGeneratorConfig.setDefaultFluid(Blocks.field_10164.getDefaultState());
		return ChunkGeneratorType.field_12765
			.create(
				this.world,
				BiomeSourceType.FIXED.applyConfig(BiomeSourceType.FIXED.getConfig(this.world.getLevelProperties()).setBiome(Biomes.field_9461)),
				cavesChunkGeneratorConfig
			);
	}

	@Override
	public boolean hasVisibleSky() {
		return false;
	}

	@Nullable
	@Override
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean bl) {
		return null;
	}

	@Nullable
	@Override
	public BlockPos getTopSpawningBlockPosition(int i, int j, boolean bl) {
		return null;
	}

	@Override
	public float getSkyAngle(long l, float f) {
		return 0.5F;
	}

	@Override
	public boolean canPlayersSleep() {
		return false;
	}

	@Override
	public boolean isFogThick(int i, int j) {
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
	public DimensionType getType() {
		return DimensionType.field_13076;
	}
}
