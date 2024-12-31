package net.minecraft.util.collection;

public class IntObjectStorage<V> {
	private transient IntObjectStorage.Entry<V>[] buckets;
	private transient int size;
	private int capacity;
	private final float growthFactor = 0.75F;

	public IntObjectStorage() {
		this.capacity = 12;
		this.buckets = new IntObjectStorage.Entry[16];
	}

	private static int hash(int key) {
		key ^= key >>> 20 ^ key >>> 12;
		return key ^ key >>> 7 ^ key >>> 4;
	}

	private static int getIndex(int hash, int bucketLength) {
		return hash & bucketLength - 1;
	}

	public V get(int key) {
		int i = hash(key);

		for (IntObjectStorage.Entry<V> entry = this.buckets[getIndex(i, this.buckets.length)]; entry != null; entry = entry.next) {
			if (entry.key == key) {
				return entry.value;
			}
		}

		return null;
	}

	public boolean hasEntry(int key) {
		return this.getEntry(key) != null;
	}

	final IntObjectStorage.Entry<V> getEntry(int key) {
		int i = hash(key);

		for (IntObjectStorage.Entry<V> entry = this.buckets[getIndex(i, this.buckets.length)]; entry != null; entry = entry.next) {
			if (entry.key == key) {
				return entry;
			}
		}

		return null;
	}

	public void set(int key, V value) {
		int i = hash(key);
		int j = getIndex(i, this.buckets.length);

		for (IntObjectStorage.Entry<V> entry = this.buckets[j]; entry != null; entry = entry.next) {
			if (entry.key == key) {
				entry.value = value;
				return;
			}
		}

		this.insert(i, key, value, j);
	}

	private void expandSize(int updatedSize) {
		IntObjectStorage.Entry<V>[] entrys = this.buckets;
		int i = entrys.length;
		if (i == 1073741824) {
			this.capacity = Integer.MAX_VALUE;
		} else {
			IntObjectStorage.Entry<V>[] entrys2 = new IntObjectStorage.Entry[updatedSize];
			this.moveTo(entrys2);
			this.buckets = entrys2;
			this.capacity = (int)((float)updatedSize * this.growthFactor);
		}
	}

	private void moveTo(IntObjectStorage.Entry<V>[] newEntryArray) {
		IntObjectStorage.Entry<V>[] entrys = this.buckets;
		int i = newEntryArray.length;

		for (int j = 0; j < entrys.length; j++) {
			IntObjectStorage.Entry<V> entry = entrys[j];
			if (entry != null) {
				entrys[j] = null;

				while (true) {
					IntObjectStorage.Entry<V> entry2 = entry.next;
					int k = getIndex(entry.hash, i);
					entry.next = newEntryArray[k];
					newEntryArray[k] = entry;
					entry = entry2;
					if (entry2 == null) {
						break;
					}
				}
			}
		}
	}

	public V remove(int key) {
		IntObjectStorage.Entry<V> entry = this.removeEntry(key);
		return entry == null ? null : entry.value;
	}

	final IntObjectStorage.Entry<V> removeEntry(int key) {
		int i = hash(key);
		int j = getIndex(i, this.buckets.length);
		IntObjectStorage.Entry<V> entry = this.buckets[j];
		IntObjectStorage.Entry<V> entry2 = entry;

		while (entry2 != null) {
			IntObjectStorage.Entry<V> entry3 = entry2.next;
			if (entry2.key == key) {
				this.size--;
				if (entry == entry2) {
					this.buckets[j] = entry3;
				} else {
					entry.next = entry3;
				}

				return entry2;
			}

			entry = entry2;
			entry2 = entry3;
		}

		return entry2;
	}

	public void clear() {
		IntObjectStorage.Entry<V>[] entrys = this.buckets;

		for (int i = 0; i < entrys.length; i++) {
			entrys[i] = null;
		}

		this.size = 0;
	}

	private void insert(int hash, int key, V value, int index) {
		IntObjectStorage.Entry<V> entry = this.buckets[index];
		this.buckets[index] = new IntObjectStorage.Entry<>(hash, key, value, entry);
		if (this.size++ >= this.capacity) {
			this.expandSize(2 * this.buckets.length);
		}
	}

	static class Entry<V> {
		final int key;
		V value;
		IntObjectStorage.Entry<V> next;
		final int hash;

		Entry(int i, int j, V object, IntObjectStorage.Entry<V> entry) {
			this.value = object;
			this.next = entry;
			this.key = j;
			this.hash = i;
		}

		public final int getKey() {
			return this.key;
		}

		public final V getValue() {
			return this.value;
		}

		public final boolean equals(Object object) {
			if (!(object instanceof IntObjectStorage.Entry)) {
				return false;
			} else {
				IntObjectStorage.Entry<V> entry = (IntObjectStorage.Entry<V>)object;
				Object object2 = this.getKey();
				Object object3 = entry.getKey();
				if (object2 == object3 || object2 != null && object2.equals(object3)) {
					Object object4 = this.getValue();
					Object object5 = entry.getValue();
					if (object4 == object5 || object4 != null && object4.equals(object5)) {
						return true;
					}
				}

				return false;
			}
		}

		public final int hashCode() {
			return IntObjectStorage.hash(this.key);
		}

		public final String toString() {
			return this.getKey() + "=" + this.getValue();
		}
	}
}
