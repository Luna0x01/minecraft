package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class class_4489<T> {
	private final List<T> field_22252 = Lists.newArrayList();
	private final Iterator<T> field_22253;

	public class_4489(Stream<T> stream) {
		this.field_22253 = stream.iterator();
	}

	public Stream<T> method_21527() {
		return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
			private int field_22255 = 0;

			public boolean tryAdvance(Consumer<? super T> consumer) {
				T object;
				if (this.field_22255 >= class_4489.this.field_22252.size()) {
					if (!class_4489.this.field_22253.hasNext()) {
						return false;
					}

					object = (T)class_4489.this.field_22253.next();
					class_4489.this.field_22252.add(object);
				} else {
					object = (T)class_4489.this.field_22252.get(this.field_22255);
				}

				this.field_22255++;
				consumer.accept(object);
				return true;
			}
		}, false);
	}
}
