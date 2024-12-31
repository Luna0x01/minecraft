package net.minecraft;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import net.minecraft.util.Identifier;

public class class_3304<T> implements class_3308<T> {
	protected class_3309<T> field_16166 = new class_3309<>();
	protected class_3309<T> field_16167 = new class_3309<>();
	private final Function<T, Iterable<String>> field_16168;
	private final Function<T, Iterable<Identifier>> field_16169;
	private final List<T> field_16170 = Lists.newArrayList();
	private Object2IntMap<T> field_16171 = new Object2IntOpenHashMap();

	public class_3304(Function<T, Iterable<String>> function, Function<T, Iterable<Identifier>> function2) {
		this.field_16168 = function;
		this.field_16169 = function2;
	}

	public void method_14700() {
		this.field_16166 = new class_3309<>();
		this.field_16167 = new class_3309<>();

		for (T object : this.field_16170) {
			this.method_14704(object);
		}

		this.field_16166.method_14708();
		this.field_16167.method_14708();
	}

	public void method_14701(T object) {
		this.field_16171.put(object, this.field_16170.size());
		this.field_16170.add(object);
		this.method_14704(object);
	}

	private void method_14704(T object) {
		((Iterable)this.field_16169.apply(object)).forEach(identifier -> this.field_16167.method_14710(object, identifier.toString().toLowerCase(Locale.ROOT)));
		((Iterable)this.field_16168.apply(object)).forEach(string -> this.field_16166.method_14710(object, string.toLowerCase(Locale.ROOT)));
	}

	@Override
	public List<T> method_14707(String string) {
		List<T> list = this.field_16166.method_14711(string);
		if (string.indexOf(58) < 0) {
			return list;
		} else {
			List<T> list2 = this.field_16167.method_14711(string);
			return (List<T>)(list2.isEmpty() ? list : Lists.newArrayList(new class_3304.class_3305(list.iterator(), list2.iterator(), this.field_16171)));
		}
	}

	static class class_3305<T> extends AbstractIterator<T> {
		private final Iterator<T> field_16172;
		private final Iterator<T> field_16173;
		private final Object2IntMap<T> field_16174;
		private T field_16175;
		private T field_16176;

		public class_3305(Iterator<T> iterator, Iterator<T> iterator2, Object2IntMap<T> object2IntMap) {
			this.field_16172 = iterator;
			this.field_16173 = iterator2;
			this.field_16174 = object2IntMap;
			this.field_16175 = (T)(iterator.hasNext() ? iterator.next() : null);
			this.field_16176 = (T)(iterator2.hasNext() ? iterator2.next() : null);
		}

		protected T computeNext() {
			if (this.field_16175 == null && this.field_16176 == null) {
				return (T)this.endOfData();
			} else {
				int i;
				if (this.field_16175 == this.field_16176) {
					i = 0;
				} else if (this.field_16175 == null) {
					i = 1;
				} else if (this.field_16176 == null) {
					i = -1;
				} else {
					i = Integer.compare(this.field_16174.getInt(this.field_16175), this.field_16174.getInt(this.field_16176));
				}

				T object = i <= 0 ? this.field_16175 : this.field_16176;
				if (i <= 0) {
					this.field_16175 = (T)(this.field_16172.hasNext() ? this.field_16172.next() : null);
				}

				if (i >= 0) {
					this.field_16176 = (T)(this.field_16173.hasNext() ? this.field_16173.next() : null);
				}

				return object;
			}
		}
	}
}
