package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk {
	public EmptyChunk(World world, int i, int j) {
		super(world, i, j);
	}

	@Override
	public boolean isChunkEqual(int chunkX, int chunkZ) {
		return chunkX == this.chunkX && chunkZ == this.chunkZ;
	}

	@Override
	public int getHighestBlockY(int x, int z) {
		return 0;
	}

	@Override
	public void generateHeightmap() {
	}

	@Override
	public void calculateSkyLight() {
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return Blocks.AIR.getDefaultState();
	}

	@Override
	public int getBlockOpacityAtPos(BlockPos pos) {
		return 255;
	}

	@Override
	public int getLightAtPos(LightType lightType, BlockPos pos) {
		return lightType.defaultValue;
	}

	@Override
	public void setLightAtPos(LightType lightType, BlockPos pos, int lightLevel) {
	}

	@Override
	public int getLightLevel(BlockPos pos, int darkness) {
		return 0;
	}

	@Override
	public void addEntity(Entity entity) {
	}

	@Override
	public void removeEntity(Entity entity) {
	}

	@Override
	public void removeEntity(Entity entity, int index) {
	}

	@Override
	public boolean hasDirectSunlight(BlockPos pos) {
		return false;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos, Chunk.Status status) {
		return null;
	}

	@Override
	public void addBlockEntity(BlockEntity be) {
	}

	@Override
	public void method_9136(BlockPos pos, BlockEntity be) {
	}

	@Override
	public void method_9150(BlockPos pos) {
	}

	@Override
	public void loadToWorld() {
	}

	@Override
	public void unloadFromWorld() {
	}

	@Override
	public void setModified() {
	}

	@Override
	public void method_9141(@Nullable Entity entity, Box box, List<Entity> list, Predicate<? super Entity> pred) {
	}

	@Override
	public <T extends Entity> void method_9140(Class<? extends T> clazz, Box box, List<T> list, Predicate<? super T> pred) {
	}

	@Override
	public boolean shouldSave(boolean bl) {
		return false;
	}

	@Override
	public Random getRandom(long seed) {
		return new Random(
			this.getWorld().getSeed()
					+ (long)(this.chunkX * this.chunkX * 4987142)
					+ (long)(this.chunkX * 5947611)
					+ (long)(this.chunkZ * this.chunkZ) * 4392871L
					+ (long)(this.chunkZ * 389711)
				^ seed
		);
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public boolean areSectionsEmptyBetween(int startY, int endY) {
		return true;
	}
}
