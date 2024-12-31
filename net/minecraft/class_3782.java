package net.minecraft;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;

public abstract class class_3782<C extends class_3798> implements ChunkGenerator<C> {
	protected final IWorld field_18838;
	protected final long field_18839;
	protected final SingletonBiomeSource field_18840;
	protected final Map<class_3902<? extends class_3845>, Long2ObjectMap<class_3992>> field_18841 = Maps.newHashMap();
	protected final Map<class_3902<? extends class_3845>, Long2ObjectMap<LongSet>> field_18842 = Maps.newHashMap();

	public class_3782(IWorld iWorld, SingletonBiomeSource singletonBiomeSource) {
		this.field_18838 = iWorld;
		this.field_18839 = iWorld.method_3581();
		this.field_18840 = singletonBiomeSource;
	}

	@Override
	public void method_17019(class_4441 arg, class_3801.class_3802 arg2) {
		class_3812 lv = new class_3812(this.field_18839);
		int i = 8;
		int j = arg.method_21286();
		int k = arg.method_21288();
		BitSet bitSet = arg.method_16347(j, k).method_16991(arg2);

		for (int l = j - 8; l <= j + 8; l++) {
			for (int m = k - 8; m <= k + 8; m++) {
				List<class_3973<?>> list = arg.method_3586().method_17046().method_17020().method_16480(new BlockPos(l * 16, 0, m * 16), null).method_16428(arg2);
				ListIterator<class_3973<?>> listIterator = list.listIterator();

				while (listIterator.hasNext()) {
					int n = listIterator.nextIndex();
					class_3973<?> lv2 = (class_3973<?>)listIterator.next();
					lv.method_17291(arg.method_16348().method_3581() + (long)n, l, m);
					if (lv2.method_17679(arg, lv, l, m, class_3845.field_19203)) {
						lv2.method_17680(arg, lv, l, m, j, k, bitSet, class_3845.field_19203);
					}
				}
			}
		}
	}

	@Nullable
	@Override
	public BlockPos method_3866(World world, String string, BlockPos blockPos, int i, boolean bl) {
		class_3902<?> lv = (class_3902<?>)class_3844.field_19152.get(string.toLowerCase(Locale.ROOT));
		return lv != null ? lv.method_17425(world, this, blockPos, i, bl) : null;
	}

	protected void method_17028(class_3781 arg, Random random) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		int i = arg.method_3920().getActualX();
		int j = arg.method_3920().getActualZ();

		for (BlockPos blockPos : BlockPos.iterate(i, 0, j, i + 16, 0, j + 16)) {
			for (int k = 4; k >= 0; k--) {
				if (k <= random.nextInt(5)) {
					arg.method_16994(mutable.setPosition(blockPos.getX(), k, blockPos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
				}
			}
		}
	}

	@Override
	public void method_17018(class_4441 arg) {
		FallingBlock.instantFall = true;
		int i = arg.method_21286();
		int j = arg.method_21288();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockPos = new BlockPos(k, 0, l);
		Biome biome = arg.method_16347(i + 1, j + 1).method_17007()[0];
		class_3812 lv = new class_3812();
		long m = lv.method_17288(arg.method_3581(), k, l);

		for (class_3801.class_3803 lv2 : class_3801.class_3803.values()) {
			biome.method_16431(lv2, this, arg, m, lv, blockPos);
		}

		FallingBlock.instantFall = false;
	}

	public void method_17029(class_3781 arg, Biome[] biomes, class_3812 arg2, int i) {
		double d = 0.03125;
		ChunkPos chunkPos = arg.method_3920();
		int j = chunkPos.getActualX();
		int k = chunkPos.getActualZ();
		double[] ds = this.method_17027(chunkPos.x, chunkPos.z);

		for (int l = 0; l < 16; l++) {
			for (int m = 0; m < 16; m++) {
				int n = j + l;
				int o = k + m;
				int p = arg.method_16992(class_3804.class_3805.WORLD_SURFACE_WG, l, m) + 1;
				biomes[m * 16 + l]
					.method_16438(
						arg2, arg, n, o, p, ds[m * 16 + l], this.method_17013().method_17231(), this.method_17013().method_17232(), i, this.field_18838.method_3581()
					);
			}
		}
	}

	@Override
	public abstract C method_17013();

	public abstract double[] method_17027(int i, int j);

	@Override
	public boolean method_17015(Biome biome, class_3902<? extends class_3845> arg) {
		return biome.method_16435(arg);
	}

	@Nullable
	@Override
	public class_3845 method_17021(Biome biome, class_3902<? extends class_3845> arg) {
		return biome.method_16441(arg);
	}

	@Override
	public SingletonBiomeSource method_17020() {
		return this.field_18840;
	}

	@Override
	public long method_17024() {
		return this.field_18839;
	}

	@Override
	public Long2ObjectMap<class_3992> method_17017(class_3902<? extends class_3845> arg) {
		return (Long2ObjectMap<class_3992>)this.field_18841.computeIfAbsent(arg, argx -> Long2ObjectMaps.synchronize(new class_3594(8192, 10000)));
	}

	@Override
	public Long2ObjectMap<LongSet> method_17022(class_3902<? extends class_3845> arg) {
		return (Long2ObjectMap<LongSet>)this.field_18842.computeIfAbsent(arg, argx -> Long2ObjectMaps.synchronize(new class_3594(8192, 10000)));
	}

	@Override
	public int method_17026() {
		return 256;
	}
}
