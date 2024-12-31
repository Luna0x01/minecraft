package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_3781;
import net.minecraft.class_3782;
import net.minecraft.class_3786;
import net.minecraft.class_3801;
import net.minecraft.class_3804;
import net.minecraft.class_3810;
import net.minecraft.class_3812;
import net.minecraft.class_3821;
import net.minecraft.class_3844;
import net.minecraft.class_3845;
import net.minecraft.class_3902;
import net.minecraft.class_3917;
import net.minecraft.class_3973;
import net.minecraft.class_4441;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityCategory;
import net.minecraft.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.SingletonBiomeSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatChunkGenerator extends class_3782<class_3917> {
	private static final Logger field_18989 = LogManager.getLogger();
	private final class_3917 field_18990;
	private final Biome field_18991;
	private final class_3810 field_18992 = new class_3810();

	public FlatChunkGenerator(IWorld iWorld, SingletonBiomeSource singletonBiomeSource, class_3917 arg) {
		super(iWorld, singletonBiomeSource);
		this.field_18990 = arg;
		this.field_18991 = this.method_17237();
	}

	private Biome method_17237() {
		Biome biome = this.field_18990.method_17497();
		FlatChunkGenerator.class_3800 lv = new FlatChunkGenerator.class_3800(
			biome.getSurfaceBuilder(),
			biome.getPrecipitation(),
			biome.getCategory(),
			biome.getDepth(),
			biome.getVariationModifier(),
			biome.getTemperature(),
			biome.getRainfall(),
			biome.getWaterColor(),
			biome.method_16447(),
			biome.getParent()
		);
		Map<String, Map<String, String>> map = this.field_18990.method_17498();

		for (String string : map.keySet()) {
			class_3821<?, ?>[] lvs = (class_3821<?, ?>[])class_3917.field_19307.get(string);
			if (lvs != null) {
				for (class_3821<?, ?> lv2 : lvs) {
					lv.method_16432((class_3801.class_3803)class_3917.field_19306.get(lv2), lv2);
					class_3844<?> lv3 = lv2.method_17312();
					if (lv3 instanceof class_3902) {
						class_3845 lv4 = biome.method_16441((class_3902<?>)lv3);
						lv.method_16436((class_3902)lv3, lv4 != null ? lv4 : (class_3845)class_3917.field_19308.get(lv2));
					}
				}
			}
		}

		boolean bl = (!this.field_18990.method_17502() || biome == Biomes.VOID) && map.containsKey("decoration");
		if (bl) {
			List<class_3801.class_3803> list = Lists.newArrayList();
			list.add(class_3801.class_3803.UNDERGROUND_STRUCTURES);
			list.add(class_3801.class_3803.SURFACE_STRUCTURES);

			for (class_3801.class_3803 lv5 : class_3801.class_3803.values()) {
				if (!list.contains(lv5)) {
					for (class_3821<?, ?> lv6 : biome.method_16430(lv5)) {
						lv.method_16432(lv5, lv6);
					}
				}
			}
		}

		return lv;
	}

	@Override
	public void method_17016(class_3781 arg) {
		ChunkPos chunkPos = arg.method_3920();
		int i = chunkPos.x;
		int j = chunkPos.z;
		Biome[] biomes = this.field_18840.method_11540(i * 16, j * 16, 16, 16);
		arg.method_16999(biomes);
		this.method_17235(i, j, arg);
		arg.method_17000(class_3804.class_3805.WORLD_SURFACE_WG, class_3804.class_3805.OCEAN_FLOOR_WG);
		arg.method_16990(class_3786.BASE);
	}

	@Override
	public void method_17019(class_4441 arg, class_3801.class_3802 arg2) {
		int i = 8;
		int j = arg.method_21286();
		int k = arg.method_21288();
		BitSet bitSet = new BitSet(65536);
		class_3812 lv = new class_3812();

		for (int l = j - 8; l <= j + 8; l++) {
			for (int m = k - 8; m <= k + 8; m++) {
				List<class_3973<?>> list = this.field_18991.method_16428(class_3801.class_3802.AIR);
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

	public class_3917 method_17013() {
		return this.field_18990;
	}

	@Override
	public double[] method_17027(int i, int j) {
		return new double[0];
	}

	@Override
	public int method_17025() {
		class_3781 lv = this.field_18838.method_16347(0, 0);
		return lv.method_16992(class_3804.class_3805.MOTION_BLOCKING, 8, 8);
	}

	@Override
	public void method_17018(class_4441 arg) {
		int i = arg.method_21286();
		int j = arg.method_21288();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockPos = new BlockPos(k, 0, l);
		class_3812 lv = new class_3812();
		long m = lv.method_17288(arg.method_3581(), k, l);

		for (class_3801.class_3803 lv2 : class_3801.class_3803.values()) {
			this.field_18991.method_16431(lv2, this, arg, m, lv, blockPos);
		}
	}

	@Override
	public void method_17023(class_4441 arg) {
	}

	public void method_17235(int i, int j, class_3781 arg) {
		BlockState[] blockStates = this.field_18990.method_17475();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = 0; k < blockStates.length; k++) {
			BlockState blockState = blockStates[k];
			if (blockState != null) {
				for (int l = 0; l < 16; l++) {
					for (int m = 0; m < 16; m++) {
						arg.method_16994(mutable.setPosition(l, k, m), blockState, false);
					}
				}
			}
		}
	}

	@Override
	public List<Biome.SpawnEntry> getSpawnEntries(EntityCategory category, BlockPos pos) {
		Biome biome = this.field_18838.method_8577(pos);
		return biome.getSpawnEntries(category);
	}

	@Override
	public int method_17014(World world, boolean bl, boolean bl2) {
		int i = 0;
		return i + this.field_18992.method_17278(world, bl, bl2);
	}

	@Override
	public boolean method_17015(Biome biome, class_3902<? extends class_3845> arg) {
		return this.field_18991.method_16435(arg);
	}

	@Nullable
	@Override
	public class_3845 method_17021(Biome biome, class_3902<? extends class_3845> arg) {
		return this.field_18991.method_16441(arg);
	}

	@Nullable
	@Override
	public BlockPos method_3866(World world, String string, BlockPos blockPos, int i, boolean bl) {
		return !this.field_18990.method_17498().keySet().contains(string) ? null : super.method_3866(world, string, blockPos, i, bl);
	}

	class class_3800 extends Biome {
		protected class_3800(
			SurfaceBuilder<?> surfaceBuilder,
			Biome.Precipitation precipitation,
			Biome.Category category,
			float f,
			float g,
			float h,
			float i,
			int j,
			int k,
			String string
		) {
			super(
				new Biome.Builder()
					.setSurfaceBuilder(surfaceBuilder)
					.setPrecipitation(precipitation)
					.setCategory(category)
					.setDepth(f)
					.setScale(g)
					.setTemperature(h)
					.setDownfall(i)
					.setWaterColor(j)
					.setWaterFogColor(k)
					.setParent(string)
			);
		}
	}
}
