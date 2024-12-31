package net.minecraft.util;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SortedArraySet<T> extends AbstractSet<T> {
	private final Comparator<T> comparator;
	private T[] elements;
	private int size;

	private SortedArraySet(int i, Comparator<T> comparator) {
		this.comparator = comparator;
		if (i < 0) {
			throw new IllegalArgumentException("Initial capacity (" + i + ") is negative");
		} else {
			this.elements = (T[])cast(new Object[i]);
		}
	}

	public static <T extends Comparable<T>> SortedArraySet<T> create(int i) {
		return new SortedArraySet<>(i, Comparator.naturalOrder());
	}

	private static <T> T[] cast(Object[] objects) {
		return (T[])objects;
	}

	private int binarySearch(T object) {
		return Arrays.binarySearch(this.elements, 0, this.size, object, this.comparator);
	}

	private static int insertionPoint(int i) {
		return -i - 1;
	}

	public boolean add(T object) {
		int i = this.binarySearch(object);
		if (i >= 0) {
			return false;
		} else {
			int j = insertionPoint(i);
			this.add(object, j);
			return true;
		}
	}

	private void ensureCapacity(int i) {
		if (i > this.elements.length) {
			if (this.elements != ObjectArrays.DEFAULT_EMPTY_ARRAY) {
				i = (int)Math.max(Math.min((long)this.elements.length + (long)(this.elements.length >> 1), 2147483639L), (long)i);
			} else if (i < 10) {
				i = 10;
			}

			Object[] objects = new Object[i];
			System.arraycopy(this.elements, 0, objects, 0, this.size);
			this.elements = (T[])cast(objects);
		}
	}

	private void add(T object, int i) {
		this.ensureCapacity(this.size + 1);
		if (i != this.size) {
			System.arraycopy(this.elements, i, this.elements, i + 1, this.size - i);
		}

		this.elements[i] = object;
		this.size++;
	}

	private void remove(int i) {
		this.size--;
		if (i != this.size) {
			System.arraycopy(this.elements, i + 1, this.elements, i, this.size - i);
		}

		this.elements[this.size] = null;
	}

	private T get(int i) {
		return this.elements[i];
	}

	public T addAndGet(T object) {
		int i = this.binarySearch(object);
		if (i >= 0) {
			return this.get(i);
		} else {
			this.add(object, insertionPoint(i));
			return object;
		}
	}

	public boolean remove(Object object) {
		int i = this.binarySearch((T)object);
		if (i >= 0) {
			this.remove(i);
			return true;
		} else {
			return false;
		}
	}

	public T first() {
		return this.get(0);
	}

	public boolean contains(Object object) {
		int i = this.binarySearch((T)object);
		return i >= 0;
	}

	public Iterator<T> iterator() {
		return new SortedArraySet.SetIterator();
	}

	public int size() {
		return this.size;
	}

	public Object[] toArray() {
		return (Object[])this.elements.clone();
	}

	public <U> U[] toArray(U[] objects) {
		if (objects.length < this.size) {
			return (U[])Arrays.copyOf(this.elements, this.size, objects.getClass());
		} else {
			System.arraycopy(this.elements, 0, objects, 0, this.size);
			if (objects.length > this.size) {
				objects[this.size] = null;
			}

			return objects;
		}
	}

	public void clear() {
		Arrays.fill(this.elements, 0, this.size, null);
		this.size = 0;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else {
			if (object instanceof SortedArraySet) {
				SortedArraySet<?> sortedArraySet = (SortedArraySet<?>)object;
				if (this.comparator.equals(sortedArraySet.comparator)) {
					return this.size == sortedArraySet.size && Arrays.equals(this.elements, sortedArraySet.elements);
				}
			}

			return super.equals(object);
		}
	}

	class SetIterator implements Iterator<T> {
		private int nextIndex;
		private int lastIndex = -1;

		private SetIterator() {
		}

		public boolean hasNext() {
			return this.nextIndex < SortedArraySet.this.size;
		}

		public T next() {
			if (this.nextIndex >= SortedArraySet.this.size) {
				throw new NoSuchElementException();
			} else {
				this.lastIndex = this.nextIndex++;
				return SortedArraySet.this.elements[this.lastIndex];
			}
		}

		public void remove() {
			if (this.lastIndex == -1) {
				throw new IllegalStateException();
			} else {
				SortedArraySet.this.remove(this.lastIndex);
				this.nextIndex--;
				this.lastIndex = -1;
			}
		}
	}
}
