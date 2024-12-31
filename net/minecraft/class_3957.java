package net.minecraft;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.FillerBlockFeature;

public class class_3957 extends class_3945<class_3870> {
	private static final LoadingCache<Long, FillerBlockFeature.class_2756[]> field_19333 = CacheBuilder.newBuilder()
		.expireAfterWrite(5L, TimeUnit.MINUTES)
		.build(new class_3957.class_2715());

	public <C extends class_3845> boolean method_17536(
		IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3870 arg, class_3844<C> arg2, C arg3
	) {
		FillerBlockFeature.class_2756[] lvs = method_17546(iWorld);
		boolean bl = false;

		for (FillerBlockFeature.class_2756 lv : lvs) {
			if (lv.method_11827(blockPos)) {
				((FillerBlockFeature)arg2).method_11823(lv);
				bl |= ((FillerBlockFeature)arg2)
					.method_17343(iWorld, chunkGenerator, random, new BlockPos(lv.method_11826(), 45, lv.method_11828()), class_3845.field_19203);
			}
		}

		return bl;
	}

	public static FillerBlockFeature.class_2756[] method_17546(IWorld iWorld) {
		Random random = new Random(iWorld.method_3581());
		long l = random.nextLong() & 65535L;
		return (FillerBlockFeature.class_2756[])field_19333.getUnchecked(l);
	}

	static class class_2715 extends CacheLoader<Long, FillerBlockFeature.class_2756[]> {
		private class_2715() {
		}

		public FillerBlockFeature.class_2756[] method_11546(Long long_) throws Exception {
			List<Integer> list = Lists.newArrayList(ContiguousSet.create(Range.closedOpen(0, 10), DiscreteDomain.integers()));
			Collections.shuffle(list, new Random(long_));
			FillerBlockFeature.class_2756[] lvs = new FillerBlockFeature.class_2756[10];

			for (int i = 0; i < 10; i++) {
				int j = (int)(42.0 * Math.cos(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
				int k = (int)(42.0 * Math.sin(2.0 * (-Math.PI + (Math.PI / 10) * (double)i)));
				int l = (Integer)list.get(i);
				int m = 2 + l / 3;
				int n = 76 + l * 3;
				boolean bl = l == 1 || l == 2;
				lvs[i] = new FillerBlockFeature.class_2756(j, k, m, n, bl);
			}

			return lvs;
		}
	}
}
