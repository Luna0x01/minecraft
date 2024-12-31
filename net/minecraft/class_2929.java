package net.minecraft;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.util.collection.ObjectIdIterable;
import net.minecraft.util.math.MathHelper;

public class class_2929<K> implements ObjectIdIterable<K> {
	private static final Object EMPTY = null;
	private K[] field_14373;
	private int[] field_14374;
	private K[] field_14375;
	private int field_14376;
	private int size;

	public class_2929(int i) {
		i = (int)((float)i / 0.8F);
		this.field_14373 = (K[])(new Object[i]);
		this.field_14374 = new int[i];
		this.field_14375 = (K[])(new Object[i]);
	}

	public int getId(@Nullable K value) {
		return this.method_12863(this.method_12861(value, this.hash(value)));
	}

	@Nullable
	public K getById(int id) {
		return id >= 0 && id < this.field_14375.length ? this.field_14375[id] : null;
	}

	private int method_12863(int id) {
		return id == -1 ? -1 : this.field_14374[id];
	}

	public int method_12864(K value) {
		int i = this.method_12862();
		this.add(value, i);
		return i;
	}

	private int method_12862() {
		while (this.field_14376 < this.field_14375.length && this.field_14375[this.field_14376] != null) {
			this.field_14376++;
		}

		return this.field_14376;
	}

	private void resize(int size) {
		K[] objects = this.field_14373;
		int[] is = this.field_14374;
		this.field_14373 = (K[])(new Object[size]);
		this.field_14374 = new int[size];
		this.field_14375 = (K[])(new Object[size]);
		this.field_14376 = 0;
		this.size = 0;

		for (int i = 0; i < objects.length; i++) {
			if (objects[i] != null) {
				this.add(objects[i], is[i]);
			}
		}
	}

	public void add(K value, int id) {
		int i = Math.max(id, this.size + 1);
		if ((float)i >= (float)this.field_14373.length * 0.8F) {
			int j = this.field_14373.length << 1;

			while (j < id) {
				j <<= 1;
			}

			this.resize(j);
		}

		int k = this.method_12867(this.hash(value));
		this.field_14373[k] = value;
		this.field_14374[k] = id;
		this.field_14375[id] = value;
		this.size++;
		if (id == this.field_14376) {
			this.field_14376++;
		}
	}

	private int hash(@Nullable K value) {
		return (MathHelper.idealHash(System.identityHashCode(value)) & 2147483647) % this.field_14373.length;
	}

	private int method_12861(@Nullable K value, int i) {
		for (int j = i; j < this.field_14373.length; j++) {
			if (this.field_14373[j] == value) {
				return j;
			}

			if (this.field_14373[j] == EMPTY) {
				return -1;
			}
		}

		for (int k = 0; k < i; k++) {
			if (this.field_14373[k] == value) {
				return k;
			}

			if (this.field_14373[k] == EMPTY) {
				return -1;
			}
		}

		return -1;
	}

	private int method_12867(int i) {
		for (int j = i; j < this.field_14373.length; j++) {
			if (this.field_14373[j] == EMPTY) {
				return j;
			}
		}

		for (int k = 0; k < i; k++) {
			if (this.field_14373[k] == EMPTY) {
				return k;
			}
		}

		throw new RuntimeException("Overflowed :(");
	}

	public Iterator<K> iterator() {
		return Iterators.filter(Iterators.forArray(this.field_14375), Predicates.notNull());
	}

	public void clear() {
		Arrays.fill(this.field_14373, null);
		Arrays.fill(this.field_14375, null);
		this.field_14376 = 0;
		this.size = 0;
	}

	public int size() {
		return this.size;
	}
}
