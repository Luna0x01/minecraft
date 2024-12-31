package net.minecraft.util.collection;

import com.google.common.collect.Lists;
import java.util.List;

public class IntArrayCache {
	private static int size = 256;
	private static final List<int[]> tcache = Lists.newArrayList();
	private static final List<int[]> tallocated = Lists.newArrayList();
	private static final List<int[]> cache = Lists.newArrayList();
	private static final List<int[]> allocated = Lists.newArrayList();

	public static synchronized int[] get(int index) {
		if (index <= 256) {
			if (tcache.isEmpty()) {
				int[] is = new int[256];
				tallocated.add(is);
				return is;
			} else {
				int[] js = (int[])tcache.remove(tcache.size() - 1);
				tallocated.add(js);
				return js;
			}
		} else if (index > size) {
			size = index;
			cache.clear();
			allocated.clear();
			int[] ks = new int[size];
			allocated.add(ks);
			return ks;
		} else if (cache.isEmpty()) {
			int[] ls = new int[size];
			allocated.add(ls);
			return ls;
		} else {
			int[] ms = (int[])cache.remove(cache.size() - 1);
			allocated.add(ms);
			return ms;
		}
	}

	public static synchronized void clear() {
		if (!cache.isEmpty()) {
			cache.remove(cache.size() - 1);
		}

		if (!tcache.isEmpty()) {
			tcache.remove(tcache.size() - 1);
		}

		cache.addAll(allocated);
		tcache.addAll(tallocated);
		allocated.clear();
		tallocated.clear();
	}

	public static synchronized String asString() {
		return "cache: " + cache.size() + ", tcache: " + tcache.size() + ", allocated: " + allocated.size() + ", tallocated: " + tallocated.size();
	}
}
