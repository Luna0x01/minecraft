package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import net.minecraft.util.Util;

public class class_3594<T> extends Long2ObjectOpenHashMap<T> {
	private final int field_17487;
	private final Long2LongMap field_17488 = new Long2LongLinkedOpenHashMap();

	public class_3594(int i, int j) {
		super(i);
		this.field_17487 = j;
	}

	private void method_16292(long l) {
		long m = Util.method_20227();
		this.field_17488.put(l, m);
		ObjectIterator<Entry> objectIterator = this.field_17488.long2LongEntrySet().iterator();

		while (objectIterator.hasNext()) {
			Entry entry = (Entry)objectIterator.next();
			T object = (T)super.get(entry.getLongKey());
			if (m - entry.getLongValue() <= (long)this.field_17487) {
				break;
			}

			if (object != null && this.method_16293(object)) {
				super.remove(entry.getLongKey());
				objectIterator.remove();
			}
		}
	}

	protected boolean method_16293(T object) {
		return true;
	}

	public T put(long l, T object) {
		this.method_16292(l);
		return (T)super.put(l, object);
	}

	public T put(Long long_, T object) {
		this.method_16292(long_);
		return (T)super.put(long_, object);
	}

	public T get(long l) {
		this.method_16292(l);
		return (T)super.get(l);
	}

	public void putAll(Map<? extends Long, ? extends T> map) {
		throw new RuntimeException("Not implemented");
	}

	public T remove(long l) {
		throw new RuntimeException("Not implemented");
	}

	public T remove(Object object) {
		throw new RuntimeException("Not implemented");
	}
}
