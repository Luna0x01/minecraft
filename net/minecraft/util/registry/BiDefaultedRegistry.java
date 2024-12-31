package net.minecraft.util.registry;

import org.apache.commons.lang3.Validate;

public class BiDefaultedRegistry<K, V> extends SimpleRegistry<K, V> {
	private final K defaultKey;
	private V defaultValue;

	public BiDefaultedRegistry(K object) {
		this.defaultKey = object;
	}

	@Override
	public void add(int id, K identifier, V object) {
		if (this.defaultKey.equals(identifier)) {
			this.defaultValue = object;
		}

		super.add(id, identifier, object);
	}

	public void validateDefaultKey() {
		Validate.notNull(this.defaultKey);
	}

	@Override
	public V get(K key) {
		V object = super.get(key);
		return object == null ? this.defaultValue : object;
	}

	@Override
	public V getByRawId(int index) {
		V object = super.getByRawId(index);
		return object == null ? this.defaultValue : object;
	}
}
