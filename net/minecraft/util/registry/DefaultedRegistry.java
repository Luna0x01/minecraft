package net.minecraft.util.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DefaultedRegistry<K, V> extends MutableRegistry<K, V> {
	private final V defaultValue;

	public DefaultedRegistry(V object) {
		this.defaultValue = object;
	}

	@Nonnull
	@Override
	public V get(@Nullable K key) {
		V object = super.get(key);
		return object == null ? this.defaultValue : object;
	}
}
