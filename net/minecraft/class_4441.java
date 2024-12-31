package net.minecraft;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.Sound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkBlockStateStorage;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4441 implements IWorld {
	private static final Logger field_21846 = LogManager.getLogger();
	private final ChunkBlockStateStorage[] field_21847;
	private final int field_21848;
	private final int field_21849;
	private final int field_21850;
	private final int field_21851;
	private final World field_21852;
	private final long field_21853;
	private final int field_21854;
	private final LevelProperties field_21855;
	private final Random field_21856;
	private final Dimension field_21857;
	private final class_3798 field_21858;
	private final class_3604<Block> field_21859 = new class_4442<>(blockPos -> this.method_16351(blockPos).method_17011());
	private final class_3604<Fluid> field_21860 = new class_4442<>(blockPos -> this.method_16351(blockPos).method_17012());

	public class_4441(ChunkBlockStateStorage[] chunkBlockStateStorages, int i, int j, int k, int l, World world) {
		this.field_21847 = chunkBlockStateStorages;
		this.field_21848 = k;
		this.field_21849 = l;
		this.field_21850 = i;
		this.field_21851 = j;
		this.field_21852 = world;
		this.field_21853 = world.method_3581();
		this.field_21858 = world.method_3586().method_17046().method_17013();
		this.field_21854 = world.method_8483();
		this.field_21855 = world.method_3588();
		this.field_21856 = world.getRandom();
		this.field_21857 = world.method_16393();
	}

	public int method_21286() {
		return this.field_21848;
	}

	public int method_21288() {
		return this.field_21849;
	}

	public boolean method_21287(int i, int j) {
		class_3781 lv = this.field_21847[0];
		class_3781 lv2 = this.field_21847[this.field_21847.length - 1];
		return i >= lv.method_3920().x && i <= lv2.method_3920().x && j >= lv.method_3920().z && j <= lv2.method_3920().z;
	}

	@Override
	public class_3781 method_16347(int i, int j) {
		if (this.method_21287(i, j)) {
			int k = i - this.field_21847[0].method_3920().x;
			int l = j - this.field_21847[0].method_3920().z;
			return this.field_21847[k + l * this.field_21850];
		} else {
			class_3781 lv = this.field_21847[0];
			class_3781 lv2 = this.field_21847[this.field_21847.length - 1];
			field_21846.error("Requested chunk : {} {}", i, j);
			field_21846.error("Region bounds : {} {} | {} {}", lv.method_3920().x, lv.method_3920().z, lv2.method_3920().x, lv2.method_3920().z);
			throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j));
		}
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.method_16351(pos).getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.method_16351(pos).getFluidState(pos);
	}

	@Nullable
	@Override
	public PlayerEntity method_16360(double d, double e, double f, double g, Predicate<Entity> predicate) {
		return null;
	}

	@Override
	public int method_8520() {
		return 0;
	}

	@Override
	public boolean method_8579(BlockPos blockPos) {
		return this.getBlockState(blockPos).isAir();
	}

	@Override
	public Biome method_8577(BlockPos blockPos) {
		Biome biome = this.method_16351(blockPos).method_17007()[blockPos.getX() & 15 | (blockPos.getZ() & 15) << 4];
		if (biome == null) {
			throw new RuntimeException(String.format("Biome is null @ %s", blockPos));
		} else {
			return biome;
		}
	}

	@Override
	public int method_16370(LightType lightType, BlockPos blockPos) {
		class_3781 lv = this.method_16351(blockPos);
		return lv.method_9132(lightType, blockPos, this.method_16393().isOverworld());
	}

	@Override
	public int method_16379(BlockPos blockPos, int i) {
		return this.method_16351(blockPos).method_16993(blockPos, i, this.method_16393().isOverworld());
	}

	@Override
	public boolean method_8487(int i, int j, boolean bl) {
		return this.method_21287(i, j);
	}

	@Override
	public boolean method_8535(BlockPos blockPos, boolean bl) {
		BlockState blockState = this.getBlockState(blockPos);
		if (blockState.isAir()) {
			return false;
		} else {
			if (bl) {
				blockState.method_16867(this.field_21852, blockPos, 0);
			}

			return this.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	@Override
	public boolean method_8555(BlockPos blockPos) {
		return this.method_16351(blockPos).method_9148(blockPos);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		class_3781 lv = this.method_16351(pos);
		BlockEntity blockEntity = lv.getBlockEntity(pos);
		if (blockEntity != null) {
			return blockEntity;
		} else {
			NbtCompound nbtCompound = lv.method_17008(pos);
			if (nbtCompound != null) {
				if ("DUMMY".equals(nbtCompound.getString("id"))) {
					blockEntity = ((BlockEntityProvider)this.getBlockState(pos).getBlock()).createBlockEntity(this.field_21852);
				} else {
					blockEntity = BlockEntity.method_16781(nbtCompound);
				}

				if (blockEntity != null) {
					lv.method_9136(pos, blockEntity);
					return blockEntity;
				}
			}

			if (lv.getBlockState(pos).getBlock() instanceof BlockEntityProvider) {
				field_21846.warn("Tried to access a block entity before it was created. {}", pos);
			}

			return null;
		}
	}

	@Override
	public boolean setBlockState(BlockPos blockPos, BlockState blockState, int i) {
		class_3781 lv = this.method_16351(blockPos);
		BlockState blockState2 = lv.method_16994(blockPos, blockState, false);
		Block block = blockState.getBlock();
		if (block.hasBlockEntity()) {
			if (lv.method_17009().method_17054() == class_3786.class_3787.LEVELCHUNK) {
				lv.method_9136(blockPos, ((BlockEntityProvider)block).createBlockEntity(this));
			} else {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putInt("x", blockPos.getX());
				nbtCompound.putInt("y", blockPos.getY());
				nbtCompound.putInt("z", blockPos.getZ());
				nbtCompound.putString("id", "DUMMY");
				lv.method_16995(nbtCompound);
			}
		} else if (blockState2 != null && blockState2.getBlock().hasBlockEntity()) {
			lv.method_9150(blockPos);
		}

		if (blockState.method_16908(this, blockPos)) {
			this.method_21289(blockPos);
		}

		return true;
	}

	private void method_21289(BlockPos blockPos) {
		this.method_16351(blockPos).method_17005(blockPos);
	}

	@Override
	public boolean method_3686(Entity entity) {
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		this.method_16347(i, j).method_3887(entity);
		return true;
	}

	@Override
	public boolean method_8553(BlockPos blockPos) {
		return this.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
	}

	@Override
	public void method_16403(LightType lightType, BlockPos blockPos, int i) {
		this.method_16351(blockPos).method_3891(lightType, this.field_21857.isOverworld(), blockPos, i);
	}

	@Override
	public WorldBorder method_8524() {
		return this.field_21852.method_8524();
	}

	@Override
	public boolean method_16368(@Nullable Entity entity, VoxelShape voxelShape) {
		return true;
	}

	@Override
	public int method_8576(BlockPos blockPos, Direction direction) {
		return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
	}

	@Override
	public boolean method_16390() {
		return false;
	}

	@Deprecated
	@Override
	public World method_16348() {
		return this.field_21852;
	}

	@Override
	public LevelProperties method_3588() {
		return this.field_21855;
	}

	@Override
	public LocalDifficulty method_8482(BlockPos blockPos) {
		if (!this.method_21287(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
			throw new RuntimeException("We are asking a region for a chunk out of bound");
		} else {
			return new LocalDifficulty(this.field_21852.method_16346(), this.field_21852.getTimeOfDay(), 0L, this.field_21852.method_16344());
		}
	}

	@Nullable
	@Override
	public class_4070 method_16399() {
		return this.field_21852.method_16399();
	}

	@Override
	public ChunkProvider method_3586() {
		return this.field_21852.method_3586();
	}

	@Override
	public SaveHandler method_3587() {
		return this.field_21852.method_3587();
	}

	@Override
	public long method_3581() {
		return this.field_21853;
	}

	@Override
	public class_3604<Block> getBlockTickScheduler() {
		return this.field_21859;
	}

	@Override
	public class_3604<Fluid> method_16340() {
		return this.field_21860;
	}

	@Override
	public int method_8483() {
		return this.field_21854;
	}

	@Override
	public Random getRandom() {
		return this.field_21856;
	}

	@Override
	public void method_16342(BlockPos blockPos, Block block) {
	}

	@Override
	public int method_16372(class_3804.class_3805 arg, int i, int j) {
		return this.method_16347(i >> 4, j >> 4).method_16992(arg, i & 15, j & 15) + 1;
	}

	@Override
	public void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, Sound sound, SoundCategory soundCategory, float f, float g) {
	}

	@Override
	public void method_16343(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
	}

	@Override
	public BlockPos method_3585() {
		return this.field_21852.method_3585();
	}

	@Override
	public Dimension method_16393() {
		return this.field_21857;
	}
}
