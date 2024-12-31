package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.class_3781;
import net.minecraft.class_3786;
import net.minecraft.class_3789;
import net.minecraft.class_3790;
import net.minecraft.class_3801;
import net.minecraft.class_3804;
import net.minecraft.class_3992;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkBlockStateStorage implements class_3781 {
	private static final Logger field_18909 = LogManager.getLogger();
	private final ChunkPos field_18910;
	private boolean field_18911;
	private final AtomicInteger field_18912 = new AtomicInteger();
	private Biome[] field_18913;
	private final Map<class_3804.class_3805, class_3804> field_18914 = Maps.newEnumMap(class_3804.class_3805.class);
	private volatile class_3786 field_18915 = class_3786.EMPTY;
	private final Map<BlockPos, BlockEntity> field_18916 = Maps.newHashMap();
	private final Map<BlockPos, NbtCompound> field_18917 = Maps.newHashMap();
	private final ChunkSection[] field_18918 = new ChunkSection[16];
	private final List<NbtCompound> field_18919 = Lists.newArrayList();
	private final List<BlockPos> field_18920 = Lists.newArrayList();
	private final ShortList[] field_18921 = new ShortList[16];
	private final Map<String, class_3992> field_18922 = Maps.newHashMap();
	private final Map<String, LongSet> field_18923 = Maps.newHashMap();
	private final class_3790 field_18924;
	private final class_3789<Block> field_18925;
	private final class_3789<Fluid> field_18926;
	private long field_18927;
	private final Map<class_3801.class_3802, BitSet> field_18928 = Maps.newHashMap();
	private boolean field_18929;

	public ChunkBlockStateStorage(int i, int j, class_3790 arg) {
		this(new ChunkPos(i, j), arg);
	}

	public ChunkBlockStateStorage(ChunkPos chunkPos, class_3790 arg) {
		this.field_18910 = chunkPos;
		this.field_18924 = arg;
		this.field_18925 = new class_3789<>(block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, chunkPos);
		this.field_18926 = new class_3789<>(fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, chunkPos);
	}

	public static ShortList method_17119(ShortList[] shortLists, int i) {
		if (shortLists[i] == null) {
			shortLists[i] = new ShortArrayList();
		}

		return shortLists[i];
	}

	@Nullable
	@Override
	public BlockState getBlockState(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		if (j >= 0 && j < 256) {
			return this.field_18918[j >> 4] == Chunk.EMPTY ? Blocks.AIR.getDefaultState() : this.field_18918[j >> 4].getBlockState(i & 15, j & 15, k & 15);
		} else {
			return Blocks.VOID_AIR.getDefaultState();
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		return j >= 0 && j < 256 && this.field_18918[j >> 4] != Chunk.EMPTY
			? this.field_18918[j >> 4].method_17093(i & 15, j & 15, k & 15)
			: Fluids.EMPTY.getDefaultState();
	}

	@Override
	public List<BlockPos> method_17010() {
		return this.field_18920;
	}

	public ShortList[] method_17139() {
		ShortList[] shortLists = new ShortList[16];

		for (BlockPos blockPos : this.field_18920) {
			method_17119(shortLists, blockPos.getY() >> 4).add(method_17135(blockPos));
		}

		return shortLists;
	}

	public void method_17115(short s, int i) {
		this.method_17134(method_17116(s, i, this.field_18910));
	}

	public void method_17134(BlockPos blockPos) {
		this.field_18920.add(blockPos);
	}

	@Nullable
	@Override
	public BlockState method_16994(BlockPos blockPos, BlockState blockState, boolean bl) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		if (j >= 0 && j < 256) {
			if (blockState.getLuminance() > 0) {
				this.field_18920.add(new BlockPos((i & 15) + this.method_3920().getActualX(), j, (k & 15) + this.method_3920().getActualZ()));
			}

			if (this.field_18918[j >> 4] == Chunk.EMPTY) {
				if (blockState.getBlock() == Blocks.AIR) {
					return blockState;
				}

				this.field_18918[j >> 4] = new ChunkSection(j >> 4 << 4, this.method_17147());
			}

			BlockState blockState2 = this.field_18918[j >> 4].getBlockState(i & 15, j & 15, k & 15);
			this.field_18918[j >> 4].setBlockState(i & 15, j & 15, k & 15, blockState);
			if (this.field_18929) {
				this.method_17128(class_3804.class_3805.MOTION_BLOCKING).method_17242(i & 15, j, k & 15, blockState);
				this.method_17128(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES).method_17242(i & 15, j, k & 15, blockState);
				this.method_17128(class_3804.class_3805.OCEAN_FLOOR).method_17242(i & 15, j, k & 15, blockState);
				this.method_17128(class_3804.class_3805.WORLD_SURFACE).method_17242(i & 15, j, k & 15, blockState);
			}

			return blockState2;
		} else {
			return Blocks.VOID_AIR.getDefaultState();
		}
	}

	@Override
	public void method_9136(BlockPos blockPos, BlockEntity blockEntity) {
		blockEntity.setPosition(blockPos);
		this.field_18916.put(blockPos, blockEntity);
	}

	public Set<BlockPos> method_17140() {
		Set<BlockPos> set = Sets.newHashSet(this.field_18917.keySet());
		set.addAll(this.field_18916.keySet());
		return set;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return (BlockEntity)this.field_18916.get(pos);
	}

	public Map<BlockPos, BlockEntity> method_17141() {
		return this.field_18916;
	}

	public void method_17124(NbtCompound nbtCompound) {
		this.field_18919.add(nbtCompound);
	}

	@Override
	public void method_3887(Entity entity) {
		NbtCompound nbtCompound = new NbtCompound();
		entity.saveToNbt(nbtCompound);
		this.method_17124(nbtCompound);
	}

	public List<NbtCompound> method_17142() {
		return this.field_18919;
	}

	@Override
	public void method_16999(Biome[] biomes) {
		this.field_18913 = biomes;
	}

	@Override
	public Biome[] method_17007() {
		return this.field_18913;
	}

	public void method_17117(boolean bl) {
		this.field_18911 = bl;
	}

	public boolean method_17133() {
		return this.field_18911;
	}

	@Override
	public class_3786 method_17009() {
		return this.field_18915;
	}

	@Override
	public void method_16990(class_3786 arg) {
		this.field_18915 = arg;
		this.method_17117(true);
	}

	public void method_17129(String string) {
		this.method_16990(class_3786.method_17050(string));
	}

	@Override
	public ChunkSection[] method_17003() {
		return this.field_18918;
	}

	@Override
	public int method_9132(LightType lightType, BlockPos blockPos, boolean bl) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getY();
		int k = blockPos.getZ() & 15;
		int l = j >> 4;
		if (l >= 0 && l <= this.field_18918.length - 1) {
			ChunkSection chunkSection = this.field_18918[l];
			if (chunkSection == Chunk.EMPTY) {
				return this.method_9148(blockPos) ? lightType.defaultValue : 0;
			} else if (lightType == LightType.SKY) {
				return !bl ? 0 : chunkSection.getSkyLight(i, j & 15, k);
			} else {
				return lightType == LightType.BLOCK ? chunkSection.getBlockLight(i, j & 15, k) : lightType.defaultValue;
			}
		} else {
			return 0;
		}
	}

	@Override
	public int method_16993(BlockPos blockPos, int i, boolean bl) {
		int j = blockPos.getX() & 15;
		int k = blockPos.getY();
		int l = blockPos.getZ() & 15;
		int m = k >> 4;
		if (m >= 0 && m <= this.field_18918.length - 1) {
			ChunkSection chunkSection = this.field_18918[m];
			if (chunkSection != Chunk.EMPTY) {
				int n = bl ? chunkSection.getSkyLight(j, k & 15, l) : 0;
				n -= i;
				int o = chunkSection.getBlockLight(j, k & 15, l);
				if (o > n) {
					n = o;
				}

				return n;
			} else {
				return this.method_17147() && i < LightType.SKY.defaultValue ? LightType.SKY.defaultValue - i : 0;
			}
		} else {
			return 0;
		}
	}

	@Override
	public boolean method_9148(BlockPos blockPos) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getY();
		int k = blockPos.getZ() & 15;
		return j >= this.method_16992(class_3804.class_3805.MOTION_BLOCKING, i, k);
	}

	public void method_17118(ChunkSection[] chunkSections) {
		if (this.field_18918.length != chunkSections.length) {
			field_18909.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.field_18918.length);
		} else {
			System.arraycopy(chunkSections, 0, this.field_18918, 0, this.field_18918.length);
		}
	}

	public Set<class_3804.class_3805> method_17143() {
		return this.field_18914.keySet();
	}

	@Nullable
	public class_3804 method_17123(class_3804.class_3805 arg) {
		return (class_3804)this.field_18914.get(arg);
	}

	public void method_17112(class_3804.class_3805 arg, long[] ls) {
		this.method_17128(arg).method_17244(ls);
	}

	@Override
	public void method_17000(class_3804.class_3805... args) {
		for (class_3804.class_3805 lv : args) {
			this.method_17128(lv);
		}
	}

	private class_3804 method_17128(class_3804.class_3805 arg) {
		return (class_3804)this.field_18914.computeIfAbsent(arg, argx -> {
			class_3804 lv = new class_3804(this, argx);
			lv.method_17238();
			return lv;
		});
	}

	@Override
	public int method_16992(class_3804.class_3805 arg, int i, int j) {
		class_3804 lv = (class_3804)this.field_18914.get(arg);
		if (lv == null) {
			this.method_17000(arg);
			lv = (class_3804)this.field_18914.get(arg);
		}

		return lv.method_17240(i & 15, j & 15) - 1;
	}

	@Override
	public ChunkPos method_3920() {
		return this.field_18910;
	}

	@Override
	public void method_9143(long l) {
	}

	@Nullable
	@Override
	public class_3992 method_16996(String string) {
		return (class_3992)this.field_18922.get(string);
	}

	@Override
	public void method_16998(String string, class_3992 arg) {
		this.field_18922.put(string, arg);
		this.field_18911 = true;
	}

	@Override
	public Map<String, class_3992> method_17004() {
		return Collections.unmodifiableMap(this.field_18922);
	}

	public void method_17114(Map<String, class_3992> map) {
		this.field_18922.clear();
		this.field_18922.putAll(map);
		this.field_18911 = true;
	}

	@Nullable
	@Override
	public LongSet method_17002(String string) {
		return (LongSet)this.field_18923.computeIfAbsent(string, stringx -> new LongOpenHashSet());
	}

	@Override
	public void method_16997(String string, long l) {
		((LongSet)this.field_18923.computeIfAbsent(string, stringx -> new LongOpenHashSet())).add(l);
		this.field_18911 = true;
	}

	@Override
	public Map<String, LongSet> method_17006() {
		return Collections.unmodifiableMap(this.field_18923);
	}

	public void method_17125(Map<String, LongSet> map) {
		this.field_18923.clear();
		this.field_18923.putAll(map);
		this.field_18911 = true;
	}

	@Override
	public void method_3891(LightType lightType, boolean bl, BlockPos blockPos, int i) {
		int j = blockPos.getX() & 15;
		int k = blockPos.getY();
		int l = blockPos.getZ() & 15;
		int m = k >> 4;
		if (m < 16 && m >= 0) {
			if (this.field_18918[m] == Chunk.EMPTY) {
				if (i == lightType.defaultValue) {
					return;
				}

				this.field_18918[m] = new ChunkSection(m << 4, this.method_17147());
			}

			if (lightType == LightType.SKY) {
				if (bl) {
					this.field_18918[m].setSkyLight(j, k & 15, l, i);
				}
			} else if (lightType == LightType.BLOCK) {
				this.field_18918[m].setBlockLight(j, k & 15, l, i);
			}
		}
	}

	public static short method_17135(BlockPos blockPos) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		int l = i & 15;
		int m = j & 15;
		int n = k & 15;
		return (short)(l | m << 4 | n << 8);
	}

	public static BlockPos method_17116(short s, int i, ChunkPos chunkPos) {
		int j = (s & 15) + (chunkPos.x << 4);
		int k = (s >>> 4 & 15) + (i << 4);
		int l = (s >>> 8 & 15) + (chunkPos.z << 4);
		return new BlockPos(j, k, l);
	}

	@Override
	public void method_17005(BlockPos blockPos) {
		if (!World.method_11475(blockPos)) {
			method_17119(this.field_18921, blockPos.getY() >> 4).add(method_17135(blockPos));
		}
	}

	public ShortList[] method_17144() {
		return this.field_18921;
	}

	public void method_17126(short s, int i) {
		method_17119(this.field_18921, i).add(s);
	}

	public class_3789<Block> method_17011() {
		return this.field_18925;
	}

	public class_3789<Fluid> method_17012() {
		return this.field_18926;
	}

	private boolean method_17147() {
		return true;
	}

	public class_3790 method_17145() {
		return this.field_18924;
	}

	public void method_17121(long l) {
		this.field_18927 = l;
	}

	public long method_17136() {
		return this.field_18927;
	}

	@Override
	public void method_16995(NbtCompound nbtCompound) {
		this.field_18917.put(new BlockPos(nbtCompound.getInt("x"), nbtCompound.getInt("y"), nbtCompound.getInt("z")), nbtCompound);
	}

	public Map<BlockPos, NbtCompound> method_17146() {
		return Collections.unmodifiableMap(this.field_18917);
	}

	@Override
	public NbtCompound method_17008(BlockPos blockPos) {
		return (NbtCompound)this.field_18917.get(blockPos);
	}

	@Override
	public void method_9150(BlockPos blockPos) {
		this.field_18916.remove(blockPos);
		this.field_18917.remove(blockPos);
	}

	@Override
	public BitSet method_16991(class_3801.class_3802 arg) {
		return (BitSet)this.field_18928.computeIfAbsent(arg, argx -> new BitSet(65536));
	}

	public void method_17111(class_3801.class_3802 arg, BitSet bitSet) {
		this.field_18928.put(arg, bitSet);
	}

	public void method_17109(int i) {
		this.field_18912.addAndGet(i);
	}

	public boolean method_17120() {
		return this.field_18912.get() > 0;
	}

	public void method_17127(boolean bl) {
		this.field_18929 = bl;
	}
}
