package net.minecraft.util.collection;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LowercaseMap<V> implements Map<String, V> {
	private final Map<String, V> delegate = Maps.newLinkedHashMap();

	public int size() {
		return this.delegate.size();
	}

	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	public boolean containsKey(Object object) {
		return this.delegate.containsKey(object.toString().toLowerCase());
	}

	public boolean containsValue(Object object) {
		return this.delegate.containsKey(object);
	}

	public V get(Object object) {
		return (V)this.delegate.get(object.toString().toLowerCase());
	}

	public V put(String string, V object) {
		return (V)this.delegate.put(string.toLowerCase(), object);
	}

	public V remove(Object object) {
		return (V)this.delegate.remove(object.toString().toLowerCase());
	}

	public void putAll(Map<? extends String, ? extends V> map) {
		for (Entry<? extends String, ? extends V> entry : map.entrySet()) {
			this.put((String)entry.getKey(), (V)entry.getValue());
		}
	}

	public void clear() {
		this.delegate.clear();
	}

	public Set<String> keySet() {
		return this.delegate.keySet();
	}

	public Collection<V> values() {
		return this.delegate.values();
	}

	public Set<Entry<String, V>> entrySet() {
		return this.delegate.entrySet();
	}
}
