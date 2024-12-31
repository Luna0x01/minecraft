package net.minecraft.util.registry;

import java.util.Set;
import javax.annotation.Nullable;

public interface Registry<K, V> extends Iterable<V> {
	@Nullable
	V get(K key);

	void put(K key, V value);

	Set<K> getKeySet();
}
