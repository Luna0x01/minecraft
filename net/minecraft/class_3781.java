package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import org.apache.logging.log4j.LogManager;

public interface class_3781 extends BlockView {
	@Nullable
	BlockState method_16994(BlockPos blockPos, BlockState blockState, boolean bl);

	void method_9136(BlockPos blockPos, BlockEntity blockEntity);

	void method_3887(Entity entity);

	void method_16990(class_3786 arg);

	@Nullable
	default ChunkSection method_16989() {
		ChunkSection[] chunkSections = this.method_17003();

		for (int i = chunkSections.length - 1; i >= 0; i--) {
			if (chunkSections[i] != Chunk.EMPTY) {
				return chunkSections[i];
			}
		}

		return null;
	}

	default int method_17001() {
		ChunkSection chunkSection = this.method_16989();
		return chunkSection == null ? 0 : chunkSection.getYOffset();
	}

	ChunkSection[] method_17003();

	int method_9132(LightType lightType, BlockPos blockPos, boolean bl);

	int method_16993(BlockPos blockPos, int i, boolean bl);

	boolean method_9148(BlockPos blockPos);

	int method_16992(class_3804.class_3805 arg, int i, int j);

	ChunkPos method_3920();

	void method_9143(long l);

	@Nullable
	class_3992 method_16996(String string);

	void method_16998(String string, class_3992 arg);

	Map<String, class_3992> method_17004();

	@Nullable
	LongSet method_17002(String string);

	void method_16997(String string, long l);

	Map<String, LongSet> method_17006();

	Biome[] method_17007();

	class_3786 method_17009();

	void method_9150(BlockPos blockPos);

	void method_3891(LightType lightType, boolean bl, BlockPos blockPos, int i);

	default void method_17005(BlockPos blockPos) {
		LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", blockPos);
	}

	default void method_16995(NbtCompound nbtCompound) {
		LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
	}

	@Nullable
	default NbtCompound method_17008(BlockPos blockPos) {
		throw new UnsupportedOperationException();
	}

	default void method_16999(Biome[] biomes) {
		throw new UnsupportedOperationException();
	}

	default void method_17000(class_3804.class_3805... args) {
		throw new UnsupportedOperationException();
	}

	default List<BlockPos> method_17010() {
		throw new UnsupportedOperationException();
	}

	class_3604<Block> method_17011();

	class_3604<Fluid> method_17012();

	BitSet method_16991(class_3801.class_3802 arg);
}
