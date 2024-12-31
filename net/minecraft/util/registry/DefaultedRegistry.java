package net.minecraft.util.registry;

public class DefaultedRegistry<K, V> extends MutableRegistry<K, V> {
	private final V defaultValue;

	public DefaultedRegistry(V object) {
		this.defaultValue = object;
	}

	@Override
	public V get(K key) {
		V object = super.get(key);
		return object == null ? this.defaultValue : object;
	}
}
