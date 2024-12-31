package net.minecraft.client.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuffixArray<T> {
	private static final boolean PRINT_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
	private static final boolean PRINT_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
	private static final Logger LOGGER = LogManager.getLogger();
	protected final List<T> objects = Lists.newArrayList();
	private final IntList characters = new IntArrayList();
	private final IntList suffixStarts = new IntArrayList();
	private IntList suffixIndexToObjectIndex = new IntArrayList();
	private IntList suffixSplits = new IntArrayList();
	private int maxTextLength;

	public void add(T object, String string) {
		this.maxTextLength = Math.max(this.maxTextLength, string.length());
		int i = this.objects.size();
		this.objects.add(object);
		this.suffixStarts.add(this.characters.size());

		for (int j = 0; j < string.length(); j++) {
			this.suffixIndexToObjectIndex.add(i);
			this.suffixSplits.add(j);
			this.characters.add(string.charAt(j));
		}

		this.suffixIndexToObjectIndex.add(i);
		this.suffixSplits.add(string.length());
		this.characters.add(-1);
	}

	public void sort() {
		int i = this.characters.size();
		int[] is = new int[i];
		final int[] js = new int[i];
		final int[] ks = new int[i];
		int[] ls = new int[i];
		IntComparator intComparator = new IntComparator() {
			public int compare(int i, int j) {
				return js[i] == js[j] ? Integer.compare(ks[i], ks[j]) : Integer.compare(js[i], js[j]);
			}

			public int compare(Integer integer, Integer integer2) {
				return this.compare(integer.intValue(), integer2.intValue());
			}
		};
		Swapper swapper = (ix, j) -> {
			if (ix != j) {
				int kx = js[ix];
				js[ix] = js[j];
				js[j] = kx;
				kx = ks[ix];
				ks[ix] = ks[j];
				ks[j] = kx;
				kx = ls[ix];
				ls[ix] = ls[j];
				ls[j] = kx;
			}
		};

		for (int j = 0; j < i; j++) {
			is[j] = this.characters.getInt(j);
		}

		int k = 1;

		for (int l = Math.min(i, this.maxTextLength); k * 2 < l; k *= 2) {
			for (int m = 0; m < i; ls[m] = m++) {
				js[m] = is[m];
				ks[m] = m + k < i ? is[m + k] : -2;
			}

			Arrays.quickSort(0, i, intComparator, swapper);

			for (int n = 0; n < i; n++) {
				if (n > 0 && js[n] == js[n - 1] && ks[n] == ks[n - 1]) {
					is[ls[n]] = is[ls[n - 1]];
				} else {
					is[ls[n]] = n;
				}
			}
		}

		IntList intList = this.suffixIndexToObjectIndex;
		IntList intList2 = this.suffixSplits;
		this.suffixIndexToObjectIndex = new IntArrayList(intList.size());
		this.suffixSplits = new IntArrayList(intList2.size());

		for (int o = 0; o < i; o++) {
			int p = ls[o];
			this.suffixIndexToObjectIndex.add(intList.getInt(p));
			this.suffixSplits.add(intList2.getInt(p));
		}

		if (PRINT_ARRAY) {
			this.printArray();
		}
	}

	private void printArray() {
		for (int i = 0; i < this.suffixIndexToObjectIndex.size(); i++) {
			LOGGER.debug("{} {}", i, this.getDebugString(i));
		}

		LOGGER.debug("");
	}

	private String getDebugString(int i) {
		int j = this.suffixSplits.getInt(i);
		int k = this.suffixStarts.getInt(this.suffixIndexToObjectIndex.getInt(i));
		StringBuilder stringBuilder = new StringBuilder();

		for (int l = 0; k + l < this.characters.size(); l++) {
			if (l == j) {
				stringBuilder.append('^');
			}

			int m = this.characters.get(k + l);
			if (m == -1) {
				break;
			}

			stringBuilder.append((char)m);
		}

		return stringBuilder.toString();
	}

	private int compare(String string, int i) {
		int j = this.suffixStarts.getInt(this.suffixIndexToObjectIndex.getInt(i));
		int k = this.suffixSplits.getInt(i);

		for (int l = 0; l < string.length(); l++) {
			int m = this.characters.getInt(j + k + l);
			if (m == -1) {
				return 1;
			}

			char c = string.charAt(l);
			char d = (char)m;
			if (c < d) {
				return -1;
			}

			if (c > d) {
				return 1;
			}
		}

		return 0;
	}

	public List<T> findAll(String string) {
		int i = this.suffixIndexToObjectIndex.size();
		int j = 0;
		int k = i;

		while (j < k) {
			int l = j + (k - j) / 2;
			int m = this.compare(string, l);
			if (PRINT_COMPARISONS) {
				LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", string, l, this.getDebugString(l), m);
			}

			if (m > 0) {
				j = l + 1;
			} else {
				k = l;
			}
		}

		if (j >= 0 && j < i) {
			int n = j;
			k = i;

			while (j < k) {
				int o = j + (k - j) / 2;
				int p = this.compare(string, o);
				if (PRINT_COMPARISONS) {
					LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", string, o, this.getDebugString(o), p);
				}

				if (p >= 0) {
					j = o + 1;
				} else {
					k = o;
				}
			}

			int q = j;
			IntSet intSet = new IntOpenHashSet();

			for (int r = n; r < q; r++) {
				intSet.add(this.suffixIndexToObjectIndex.getInt(r));
			}

			int[] is = intSet.toIntArray();
			java.util.Arrays.sort(is);
			Set<T> set = Sets.newLinkedHashSet();

			for (int s : is) {
				set.add(this.objects.get(s));
			}

			return Lists.newArrayList(set);
		} else {
			return Collections.emptyList();
		}
	}
}
