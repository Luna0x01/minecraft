package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class EmptyChunk extends Chunk {
	private static final Biome[] field_18876 = Util.make(new Biome[256], biomes -> Arrays.fill(biomes, Biomes.PLAINS));

	public EmptyChunk(World world, int i, int j) {
		super(world, i, j, field_18876);
	}

	@Override
	public boolean isChunkEqual(int chunkX, int chunkZ) {
		return chunkX == this.chunkX && chunkZ == this.chunkZ;
	}

	@Override
	public void generateHeightmap() {
	}

	@Override
	public void calculateSkyLight() {
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return Blocks.VOID_AIR.getDefaultState();
	}

	@Override
	public int method_9132(LightType lightType, BlockPos blockPos, boolean bl) {
		return lightType.defaultValue;
	}

	@Override
	public void method_3891(LightType lightType, boolean bl, BlockPos blockPos, int i) {
	}

	@Override
	public int method_16993(BlockPos blockPos, int i, boolean bl) {
		return 0;
	}

	@Override
	public void method_3887(Entity entity) {
	}

	@Override
	public void removeEntity(Entity entity) {
	}

	@Override
	public void removeEntity(Entity entity, int index) {
	}

	@Override
	public boolean method_9148(BlockPos blockPos) {
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
	public void method_9136(BlockPos blockPos, BlockEntity blockEntity) {
	}

	@Override
	public void method_9150(BlockPos blockPos) {
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
	public void method_17070(@Nullable Entity entity, Box box, List<Entity> list, Predicate<? super Entity> predicate) {
	}

	@Override
	public <T extends Entity> void method_17075(Class<? extends T> class_, Box box, List<T> list, Predicate<? super T> predicate) {
	}

	@Override
	public boolean shouldSave(boolean bl) {
		return false;
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
