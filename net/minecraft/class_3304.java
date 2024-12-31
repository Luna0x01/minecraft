package net.minecraft;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
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
	protected class_3309<T> field_21109 = new class_3309<>();
	private final Function<T, Iterable<String>> field_16168;
	private final Function<T, Iterable<Identifier>> field_16169;
	private final List<T> field_16170 = Lists.newArrayList();
	private final Object2IntMap<T> field_16171 = new Object2IntOpenHashMap();

	public class_3304(Function<T, Iterable<String>> function, Function<T, Iterable<Identifier>> function2) {
		this.field_16168 = function;
		this.field_16169 = function2;
	}

	public void method_14700() {
		this.field_16166 = new class_3309<>();
		this.field_16167 = new class_3309<>();
		this.field_21109 = new class_3309<>();

		for (T object : this.field_16170) {
			this.method_14704(object);
		}

		this.field_16166.method_14708();
		this.field_16167.method_14708();
		this.field_21109.method_14708();
	}

	public void method_14701(T object) {
		this.field_16171.put(object, this.field_16170.size());
		this.field_16170.add(object);
		this.method_14704(object);
	}

	public void method_19611() {
		this.field_16170.clear();
		this.field_16171.clear();
	}

	private void method_14704(T object) {
		((Iterable)this.field_16169.apply(object)).forEach(identifier -> {
			this.field_16167.method_14710(object, identifier.getNamespace().toLowerCase(Locale.ROOT));
			this.field_21109.method_14710(object, identifier.getPath().toLowerCase(Locale.ROOT));
		});
		((Iterable)this.field_16168.apply(object)).forEach(string -> this.field_16166.method_14710(object, string.toLowerCase(Locale.ROOT)));
	}

	@Override
	public List<T> method_14707(String string) {
		int i = string.indexOf(58);
		if (i < 0) {
			return this.field_16166.method_14711(string);
		} else {
			List<T> list = this.field_16167.method_14711(string.substring(0, i).trim());
			String string2 = string.substring(i + 1, string.length()).trim();
			List<T> list2 = this.field_21109.method_14711(string2);
			List<T> list3 = this.field_16166.method_14711(string2);
			return Lists.newArrayList(
				new class_3304.class_4299(list.iterator(), new class_3304.class_3305<T>(list2.iterator(), list3.iterator(), this.field_16171), this.field_16171)
			);
		}
	}

	static class class_3305<T> extends AbstractIterator<T> {
		private final PeekingIterator<T> field_21113;
		private final PeekingIterator<T> field_21114;
		private final Object2IntMap<T> field_16174;

		public class_3305(Iterator<T> iterator, Iterator<T> iterator2, Object2IntMap<T> object2IntMap) {
			this.field_21113 = Iterators.peekingIterator(iterator);
			this.field_21114 = Iterators.peekingIterator(iterator2);
			this.field_16174 = object2IntMap;
		}

		protected T computeNext() {
			boolean bl = !this.field_21113.hasNext();
			boolean bl2 = !this.field_21114.hasNext();
			if (bl && bl2) {
				return (T)this.endOfData();
			} else if (bl) {
				return (T)this.field_21114.next();
			} else if (bl2) {
				return (T)this.field_21113.next();
			} else {
				int i = Integer.compare(this.field_16174.getInt(this.field_21113.peek()), this.field_16174.getInt(this.field_21114.peek()));
				if (i == 0) {
					this.field_21114.next();
				}

				return (T)(i <= 0 ? this.field_21113.next() : this.field_21114.next());
			}
		}
	}

	static class class_4299<T> extends AbstractIterator<T> {
		private final PeekingIterator<T> field_21110;
		private final PeekingIterator<T> field_21111;
		private final Object2IntMap<T> field_21112;

		public class_4299(Iterator<T> iterator, Iterator<T> iterator2, Object2IntMap<T> object2IntMap) {
			this.field_21110 = Iterators.peekingIterator(iterator);
			this.field_21111 = Iterators.peekingIterator(iterator2);
			this.field_21112 = object2IntMap;
		}

		protected T computeNext() {
			while (this.field_21110.hasNext() && this.field_21111.hasNext()) {
				int i = Integer.compare(this.field_21112.getInt(this.field_21110.peek()), this.field_21112.getInt(this.field_21111.peek()));
				if (i == 0) {
					this.field_21111.next();
					return (T)this.field_21110.next();
				}

				if (i < 0) {
					this.field_21110.next();
				} else {
					this.field_21111.next();
				}
			}

			return (T)this.endOfData();
		}
	}
}
