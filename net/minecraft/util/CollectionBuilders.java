package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;

public class CollectionBuilders {
	public static <T> Iterable<T[]> method_10515(Class<T> class_, Iterable<? extends Iterable<? extends T>> iterable) {
		return new CollectionBuilders.Product(class_, method_10518(Iterable.class, iterable));
	}

	public static <T> Iterable<List<T>> method_10516(Iterable<? extends Iterable<? extends T>> iterable) {
		return method_10519(method_10515(Object.class, iterable));
	}

	private static <T> Iterable<List<T>> method_10519(Iterable<Object[]> iterable) {
		return Iterables.transform(iterable, new CollectionBuilders.Object2ListFunction());
	}

	private static <T> T[] method_10518(Class<? super T> class_, Iterable<? extends T> iterable) {
		List<T> list = Lists.newArrayList();

		for (T object : iterable) {
			list.add(object);
		}

		return (T[])list.toArray(method_10517(class_, list.size()));
	}

	private static <T> T[] method_10517(Class<? super T> class_, int i) {
		return (T[])((Object[])Array.newInstance(class_, i));
	}

	static class Object2ListFunction<T> implements Function<Object[], List<T>> {
		private Object2ListFunction() {
		}

		public List<T> apply(@Nullable Object[] array) {
			return Arrays.asList(array);
		}
	}

	static class Product<T> implements Iterable<T[]> {
		private final Class<T> clazz;
		private final Iterable<? extends T>[] iterables;

		private Product(Class<T> class_, Iterable<? extends T>[] iterables) {
			this.clazz = class_;
			this.iterables = iterables;
		}

		public Iterator<T[]> iterator() {
			return (Iterator<T[]>)(this.iterables.length <= 0
				? Collections.singletonList(CollectionBuilders.method_10517(this.clazz, 0)).iterator()
				: new CollectionBuilders.Product.ProductIterator(this.clazz, this.iterables));
		}

		static class ProductIterator<T> extends UnmodifiableIterator<T[]> {
			private int field_11449 = -2;
			private final Iterable<? extends T>[] field_11450;
			private final Iterator<? extends T>[] field_11451;
			private final T[] field_11452;

			private ProductIterator(Class<T> class_, Iterable<? extends T>[] iterables) {
				this.field_11450 = iterables;
				this.field_11451 = CollectionBuilders.method_10517(Iterator.class, this.field_11450.length);

				for (int i = 0; i < this.field_11450.length; i++) {
					this.field_11451[i] = iterables[i].iterator();
				}

				this.field_11452 = (T[])CollectionBuilders.method_10517(class_, this.field_11451.length);
			}

			private void method_10522() {
				this.field_11449 = -1;
				Arrays.fill(this.field_11451, null);
				Arrays.fill(this.field_11452, null);
			}

			public boolean hasNext() {
				if (this.field_11449 == -2) {
					this.field_11449 = 0;

					for (Iterator<? extends T> iterator : this.field_11451) {
						if (!iterator.hasNext()) {
							this.method_10522();
							break;
						}
					}

					return true;
				} else {
					if (this.field_11449 >= this.field_11451.length) {
						for (this.field_11449 = this.field_11451.length - 1; this.field_11449 >= 0; this.field_11449--) {
							Iterator<? extends T> iterator2 = this.field_11451[this.field_11449];
							if (iterator2.hasNext()) {
								break;
							}

							if (this.field_11449 == 0) {
								this.method_10522();
								break;
							}

							iterator2 = this.field_11450[this.field_11449].iterator();
							this.field_11451[this.field_11449] = iterator2;
							if (!iterator2.hasNext()) {
								this.method_10522();
								break;
							}
						}
					}

					return this.field_11449 >= 0;
				}
			}

			public T[] method_10521() {
				if (!this.hasNext()) {
					throw new NoSuchElementException();
				} else {
					while (this.field_11449 < this.field_11451.length) {
						this.field_11452[this.field_11449] = (T)this.field_11451[this.field_11449].next();
						this.field_11449++;
					}

					return (T[])((Object[])this.field_11452.clone());
				}
			}
		}
	}
}
