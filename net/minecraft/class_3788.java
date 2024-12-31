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
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.chunk.ChunkSection;

public class class_3788 extends ChunkBlockStateStorage {
	private final class_3781 field_18882;

	public class_3788(class_3781 arg) {
		super(arg.method_3920(), class_3790.field_18935);
		this.field_18882 = arg;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.field_18882.getBlockEntity(pos);
	}

	@Nullable
	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.field_18882.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.field_18882.getFluidState(pos);
	}

	@Override
	public int getMaxLightLevel() {
		return this.field_18882.getMaxLightLevel();
	}

	@Nullable
	@Override
	public BlockState method_16994(BlockPos blockPos, BlockState blockState, boolean bl) {
		return null;
	}

	@Override
	public void method_9136(BlockPos blockPos, BlockEntity blockEntity) {
	}

	@Override
	public void method_3887(Entity entity) {
	}

	@Override
	public void method_16990(class_3786 arg) {
	}

	@Override
	public ChunkSection[] method_17003() {
		return this.field_18882.method_17003();
	}

	@Override
	public int method_9132(LightType lightType, BlockPos blockPos, boolean bl) {
		return this.field_18882.method_9132(lightType, blockPos, bl);
	}

	@Override
	public int method_16993(BlockPos blockPos, int i, boolean bl) {
		return this.field_18882.method_16993(blockPos, i, bl);
	}

	@Override
	public boolean method_9148(BlockPos blockPos) {
		return this.field_18882.method_9148(blockPos);
	}

	@Override
	public void method_17112(class_3804.class_3805 arg, long[] ls) {
	}

	private class_3804.class_3805 method_17062(class_3804.class_3805 arg) {
		if (arg == class_3804.class_3805.WORLD_SURFACE_WG) {
			return class_3804.class_3805.WORLD_SURFACE;
		} else {
			return arg == class_3804.class_3805.OCEAN_FLOOR_WG ? class_3804.class_3805.OCEAN_FLOOR : arg;
		}
	}

	@Override
	public int method_16992(class_3804.class_3805 arg, int i, int j) {
		return this.field_18882.method_16992(this.method_17062(arg), i, j);
	}

	@Override
	public ChunkPos method_3920() {
		return this.field_18882.method_3920();
	}

	@Override
	public void method_9143(long l) {
	}

	@Nullable
	@Override
	public class_3992 method_16996(String string) {
		return this.field_18882.method_16996(string);
	}

	@Override
	public void method_16998(String string, class_3992 arg) {
	}

	@Override
	public Map<String, class_3992> method_17004() {
		return this.field_18882.method_17004();
	}

	@Override
	public void method_17114(Map<String, class_3992> map) {
	}

	@Nullable
	@Override
	public LongSet method_17002(String string) {
		return this.field_18882.method_17002(string);
	}

	@Override
	public void method_16997(String string, long l) {
	}

	@Override
	public Map<String, LongSet> method_17006() {
		return this.field_18882.method_17006();
	}

	@Override
	public void method_17125(Map<String, LongSet> map) {
	}

	@Override
	public Biome[] method_17007() {
		return this.field_18882.method_17007();
	}

	@Override
	public void method_17117(boolean bl) {
	}

	@Override
	public boolean method_17133() {
		return false;
	}

	@Override
	public class_3786 method_17009() {
		return this.field_18882.method_17009();
	}

	@Override
	public void method_9150(BlockPos blockPos) {
	}

	@Override
	public void method_3891(LightType lightType, boolean bl, BlockPos blockPos, int i) {
		this.field_18882.method_3891(lightType, bl, blockPos, i);
	}

	@Override
	public void method_17005(BlockPos blockPos) {
	}

	@Override
	public void method_16995(NbtCompound nbtCompound) {
	}

	@Nullable
	@Override
	public NbtCompound method_17008(BlockPos blockPos) {
		return this.field_18882.method_17008(blockPos);
	}

	@Override
	public void method_16999(Biome[] biomes) {
	}

	@Override
	public void method_17000(class_3804.class_3805... args) {
	}

	@Override
	public List<BlockPos> method_17010() {
		return this.field_18882.method_17010();
	}

	@Override
	public class_3789<Block> method_17011() {
		return new class_3789<>(block -> block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, this.method_3920());
	}

	@Override
	public class_3789<Fluid> method_17012() {
		return new class_3789<>(fluid -> fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, this.method_3920());
	}

	@Override
	public BitSet method_16991(class_3801.class_3802 arg) {
		return this.field_18882.method_16991(arg);
	}

	@Override
	public void method_17127(boolean bl) {
	}
}
