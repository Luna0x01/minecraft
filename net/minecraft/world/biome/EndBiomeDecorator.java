package net.minecraft.world.biome;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.FillerBlockFeature;

public class EndBiomeDecorator extends BiomeDecorator {
	private static final LoadingCache<Long, FillerBlockFeature.class_2756[]> field_12553 = CacheBuilder.newBuilder()
		.expireAfterWrite(5L, TimeUnit.MINUTES)
		.build(new EndBiomeDecorator.class_2715());
	private final FillerBlockFeature field_12554 = new FillerBlockFeature();

	@Override
	protected void method_11530(Biome biome, World world, Random random) {
		this.method_11528(world, random);
		FillerBlockFeature.class_2756[] lvs = method_11545(world);

		for (FillerBlockFeature.class_2756 lv : lvs) {
			if (lv.method_11827(this.startPos)) {
				this.field_12554.method_11823(lv);
				this.field_12554.generate(world, random, new BlockPos(lv.method_11826(), 45, lv.method_11828()));
			}
		}
	}

	public static FillerBlockFeature.class_2756[] method_11545(World world) {
		Random random = new Random(world.getSeed());
		long l = random.nextLong() & 65535L;
		return (FillerBlockFeature.class_2756[])field_12553.getUnchecked(l);
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
