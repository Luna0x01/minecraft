package net.minecraft.world;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ColorResolver;

public interface WorldView extends BlockRenderView, CollisionView, BiomeAccess.Storage {
	@Nullable
	Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);

	@Deprecated
	boolean isChunkLoaded(int chunkX, int chunkZ);

	int getTopY(Heightmap.Type heightmap, int x, int z);

	int getAmbientDarkness();

	BiomeAccess getBiomeAccess();

	default Biome getBiome(BlockPos pos) {
		return this.getBiomeAccess().getBiome(pos);
	}

	default Stream<BlockState> getStatesInBoxIfLoaded(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.floor(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.floor(box.maxZ);
		return this.isRegionLoaded(i, k, m, j, l, n) ? this.getStatesInBox(box) : Stream.empty();
	}

	@Override
	default int getColor(BlockPos pos, ColorResolver colorResolver) {
		return colorResolver.getColor(this.getBiome(pos), (double)pos.getX(), (double)pos.getZ());
	}

	@Override
	default Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		Chunk chunk = this.getChunk(BiomeCoords.toChunk(biomeX), BiomeCoords.toChunk(biomeZ), ChunkStatus.BIOMES, false);
		return chunk != null && chunk.getBiomeArray() != null
			? chunk.getBiomeArray().getBiomeForNoiseGen(biomeX, biomeY, biomeZ)
			: this.getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
	}

	Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ);

	boolean isClient();

	@Deprecated
	int getSeaLevel();

	DimensionType getDimension();

	@Override
	default int getBottomY() {
		return this.getDimension().getMinimumY();
	}

	@Override
	default int getHeight() {
		return this.getDimension().getHeight();
	}

	default BlockPos getTopPosition(Heightmap.Type heightmap, BlockPos pos) {
		return new BlockPos(pos.getX(), this.getTopY(heightmap, pos.getX(), pos.getZ()), pos.getZ());
	}

	default boolean isAir(BlockPos pos) {
		return this.getBlockState(pos).isAir();
	}

	default boolean isSkyVisibleAllowingSea(BlockPos pos) {
		if (pos.getY() >= this.getSeaLevel()) {
			return this.isSkyVisible(pos);
		} else {
			BlockPos blockPos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
			if (!this.isSkyVisible(blockPos)) {
				return false;
			} else {
				for (BlockPos var4 = blockPos.down(); var4.getY() > pos.getY(); var4 = var4.down()) {
					BlockState blockState = this.getBlockState(var4);
					if (blockState.getOpacity(this, var4) > 0 && !blockState.getMaterial().isLiquid()) {
						return false;
					}
				}

				return true;
			}
		}
	}

	@Deprecated
	default float getBrightness(BlockPos pos) {
		return this.getDimension().getBrightness(this.getLightLevel(pos));
	}

	default int getStrongRedstonePower(BlockPos pos, Direction direction) {
		return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
	}

	default Chunk getChunk(BlockPos pos) {
		return this.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()));
	}

	default Chunk getChunk(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
	}

	default Chunk getChunk(int chunkX, int chunkZ, ChunkStatus status) {
		return this.getChunk(chunkX, chunkZ, status, true);
	}

	@Nullable
	@Override
	default BlockView getChunkAsView(int chunkX, int chunkZ) {
		return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
	}

	default boolean isWater(BlockPos pos) {
		return this.getFluidState(pos).isIn(FluidTags.WATER);
	}

	default boolean containsFluid(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					BlockState blockState = this.getBlockState(mutable.set(o, p, q));
					if (!blockState.getFluidState().isEmpty()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	default int getLightLevel(BlockPos pos) {
		return this.getLightLevel(pos, this.getAmbientDarkness());
	}

	default int getLightLevel(BlockPos pos, int ambientDarkness) {
		return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000
			? this.getBaseLightLevel(pos, ambientDarkness)
			: 15;
	}

	@Deprecated
	default boolean isPosLoaded(int x, int z) {
		return this.isChunkLoaded(ChunkSectionPos.getSectionCoord(x), ChunkSectionPos.getSectionCoord(z));
	}

	@Deprecated
	default boolean isChunkLoaded(BlockPos pos) {
		return this.isPosLoaded(pos.getX(), pos.getZ());
	}

	@Deprecated
	default boolean isRegionLoaded(BlockPos min, BlockPos max) {
		return this.isRegionLoaded(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
	}

	@Deprecated
	default boolean isRegionLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		return maxY >= this.getBottomY() && minY < this.getTopY() ? this.isRegionLoaded(minX, minZ, maxX, maxZ) : false;
	}

	@Deprecated
	default boolean isRegionLoaded(int minX, int minZ, int maxX, int maxZ) {
		int i = ChunkSectionPos.getSectionCoord(minX);
		int j = ChunkSectionPos.getSectionCoord(maxX);
		int k = ChunkSectionPos.getSectionCoord(minZ);
		int l = ChunkSectionPos.getSectionCoord(maxZ);

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				if (!this.isChunkLoaded(m, n)) {
					return false;
				}
			}
		}

		return true;
	}
}
