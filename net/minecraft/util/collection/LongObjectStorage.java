package net.minecraft.util.collection;

public class LongObjectStorage<V> {
	private transient LongObjectStorage.Entry<V>[] entries;
	private transient int usedEntriesCount;
	private int entriesMask;
	private int growthLimit;
	private final float growthFactor = 0.75F;
	private transient volatile int size;

	public LongObjectStorage() {
		this.growthLimit = 3072;
		this.entries = new LongObjectStorage.Entry[4096];
		this.entriesMask = this.entries.length - 1;
	}

	private static int hash(long l) {
		return hash((int)(l ^ l >>> 32));
	}

	private static int hash(int i) {
		i ^= i >>> 20 ^ i >>> 12;
		return i ^ i >>> 7 ^ i >>> 4;
	}

	private static int getEntryIndex(int i, int j) {
		return i & j;
	}

	public int getUsedEntriesCount() {
		return this.usedEntriesCount;
	}

	public V get(long key) {
		int i = hash(key);

		for (LongObjectStorage.Entry<V> entry = this.entries[getEntryIndex(i, this.entriesMask)]; entry != null; entry = entry.next) {
			if (entry.key == key) {
				return entry.value;
			}
		}

		return null;
	}

	public boolean contains(long key) {
		return this.getEntry(key) != null;
	}

	final LongObjectStorage.Entry<V> getEntry(long key) {
		int i = hash(key);

		for (LongObjectStorage.Entry<V> entry = this.entries[getEntryIndex(i, this.entriesMask)]; entry != null; entry = entry.next) {
			if (entry.key == key) {
				return entry;
			}
		}

		return null;
	}

	public void set(long key, V value) {
		int i = hash(key);
		int j = getEntryIndex(i, this.entriesMask);

		for (LongObjectStorage.Entry<V> entry = this.entries[j]; entry != null; entry = entry.next) {
			if (entry.key == key) {
				entry.value = value;
				return;
			}
		}

		this.size++;
		this.insert(i, key, value, j);
	}

	private void expand(int newSize) {
		LongObjectStorage.Entry<V>[] entrys = this.entries;
		int i = entrys.length;
		if (i == 1073741824) {
			this.growthLimit = Integer.MAX_VALUE;
		} else {
			LongObjectStorage.Entry<V>[] entrys2 = new LongObjectStorage.Entry[newSize];
			this.copyToNewEntries(entrys2);
			this.entries = entrys2;
			this.entriesMask = this.entries.length - 1;
			this.growthLimit = (int)((float)newSize * this.growthFactor);
		}
	}

	private void copyToNewEntries(LongObjectStorage.Entry<V>[] newEntries) {
		LongObjectStorage.Entry<V>[] entrys = this.entries;
		int i = newEntries.length;

		for (int j = 0; j < entrys.length; j++) {
			LongObjectStorage.Entry<V> entry = entrys[j];
			if (entry != null) {
				entrys[j] = null;

				while (true) {
					LongObjectStorage.Entry<V> entry2 = entry.next;
					int k = getEntryIndex(entry.hash, i - 1);
					entry.next = newEntries[k];
					newEntries[k] = entry;
					entry = entry2;
					if (entry2 == null) {
						break;
					}
				}
			}
		}
	}

	public V remove(long key) {
		LongObjectStorage.Entry<V> entry = this.removeEntry(key);
		return entry == null ? null : entry.value;
	}

	final LongObjectStorage.Entry<V> removeEntry(long key) {
		int i = hash(key);
		int j = getEntryIndex(i, this.entriesMask);
		LongObjectStorage.Entry<V> entry = this.entries[j];
		LongObjectStorage.Entry<V> entry2 = entry;

		while (entry2 != null) {
			LongObjectStorage.Entry<V> entry3 = entry2.next;
			if (entry2.key == key) {
				this.size++;
				this.usedEntriesCount--;
				if (entry == entry2) {
					this.entries[j] = entry3;
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

	private void insert(int hash, long key, V value, int index) {
		LongObjectStorage.Entry<V> entry = this.entries[index];
		this.entries[index] = new LongObjectStorage.Entry<>(hash, key, value, entry);
		if (this.usedEntriesCount++ >= this.growthLimit) {
			this.expand(2 * this.entries.length);
		}
	}

	static class Entry<V> {
		final long key;
		V value;
		LongObjectStorage.Entry<V> next;
		final int hash;

		Entry(int i, long l, V object, LongObjectStorage.Entry<V> entry) {
			this.value = object;
			this.next = entry;
			this.key = l;
			this.hash = i;
		}

		public final long getKey() {
			return this.key;
		}

		public final V getValue() {
			return this.value;
		}

		public final boolean equals(Object object) {
			if (!(object instanceof LongObjectStorage.Entry)) {
				return false;
			} else {
				LongObjectStorage.Entry<V> entry = (LongObjectStorage.Entry<V>)object;
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
			return LongObjectStorage.hash(this.key);
		}

		public final String toString() {
			return this.getKey() + "=" + this.getValue();
		}
	}
}
