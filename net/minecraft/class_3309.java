package net.minecraft;

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

public class class_3309<T> {
	private static final boolean field_16181 = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
	private static final boolean field_16182 = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
	private static final Logger field_16183 = LogManager.getLogger();
	protected final List<T> field_16180 = Lists.newArrayList();
	private final IntList field_16184 = new IntArrayList();
	private final IntList field_16185 = new IntArrayList();
	private IntList field_16186 = new IntArrayList();
	private IntList field_16187 = new IntArrayList();
	private int field_16188;

	public void method_14710(T object, String string) {
		this.field_16188 = Math.max(this.field_16188, string.length());
		int i = this.field_16180.size();
		this.field_16180.add(object);
		this.field_16185.add(this.field_16184.size());

		for (int j = 0; j < string.length(); j++) {
			this.field_16186.add(i);
			this.field_16187.add(j);
			this.field_16184.add(string.charAt(j));
		}

		this.field_16186.add(i);
		this.field_16187.add(string.length());
		this.field_16184.add(-1);
	}

	public void method_14708() {
		int i = this.field_16184.size();
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
			is[j] = this.field_16184.getInt(j);
		}

		int k = 1;

		for (int l = Math.min(i, this.field_16188); k * 2 < l; k *= 2) {
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

		IntList intList = this.field_16186;
		IntList intList2 = this.field_16187;
		this.field_16186 = new IntArrayList(intList.size());
		this.field_16187 = new IntArrayList(intList2.size());

		for (int o = 0; o < i; o++) {
			int p = ls[o];
			this.field_16186.add(intList.getInt(p));
			this.field_16187.add(intList2.getInt(p));
		}

		if (field_16182) {
			this.method_14714();
		}
	}

	private void method_14714() {
		for (int i = 0; i < this.field_16186.size(); i++) {
			field_16183.debug("{} {}", i, this.method_14709(i));
		}

		field_16183.debug("");
	}

	private String method_14709(int i) {
		int j = this.field_16187.getInt(i);
		int k = this.field_16185.getInt(this.field_16186.getInt(i));
		StringBuilder stringBuilder = new StringBuilder();

		for (int l = 0; k + l < this.field_16184.size(); l++) {
			if (l == j) {
				stringBuilder.append('^');
			}

			int m = this.field_16184.get(k + l);
			if (m == -1) {
				break;
			}

			stringBuilder.append((char)m);
		}

		return stringBuilder.toString();
	}

	private int method_14712(String string, int i) {
		int j = this.field_16185.getInt(this.field_16186.getInt(i));
		int k = this.field_16187.getInt(i);

		for (int l = 0; l < string.length(); l++) {
			int m = this.field_16184.getInt(j + k + l);
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

	public List<T> method_14711(String string) {
		int i = this.field_16186.size();
		int j = 0;
		int k = i;

		while (j < k) {
			int l = j + (k - j) / 2;
			int m = this.method_14712(string, l);
			if (field_16181) {
				field_16183.debug("comparing lower \"{}\" with {} \"{}\": {}", string, l, this.method_14709(l), m);
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
				int p = this.method_14712(string, o);
				if (field_16181) {
					field_16183.debug("comparing upper \"{}\" with {} \"{}\": {}", string, o, this.method_14709(o), p);
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
				intSet.add(this.field_16186.getInt(r));
			}

			int[] is = intSet.toIntArray();
			java.util.Arrays.sort(is);
			Set<T> set = Sets.newLinkedHashSet();

			for (int s : is) {
				set.add(this.field_16180.get(s));
			}

			return Lists.newArrayList(set);
		} else {
			return Collections.emptyList();
		}
	}
}
