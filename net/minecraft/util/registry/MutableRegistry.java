package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MutableRegistry<K, V> implements Registry<K, V> {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Map<K, V> map = this.createMap();
	private Object[] cache;

	protected Map<K, V> createMap() {
		return Maps.newHashMap();
	}

	@Nullable
	@Override
	public V get(@Nullable K key) {
		return (V)this.map.get(key);
	}

	@Override
	public void put(K key, V value) {
		Validate.notNull(key);
		Validate.notNull(value);
		this.cache = null;
		if (this.map.containsKey(key)) {
			LOGGER.debug("Adding duplicate key '{}' to registry", key);
		}

		this.map.put(key, value);
	}

	@Override
	public Set<K> getKeySet() {
		return Collections.unmodifiableSet(this.map.keySet());
	}

	@Nullable
	public V method_12584(Random random) {
		if (this.cache == null) {
			Collection<?> collection = this.map.values();
			if (collection.isEmpty()) {
				return null;
			}

			this.cache = collection.toArray(new Object[collection.size()]);
		}

		return (V)this.cache[random.nextInt(this.cache.length)];
	}

	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}

	public Iterator<V> iterator() {
		return this.map.values().iterator();
	}
}
