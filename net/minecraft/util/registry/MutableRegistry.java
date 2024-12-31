package net.minecraft.util.registry;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MutableRegistry<K, V> implements Registry<K, V> {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Map<K, V> map = this.createMap();

	protected Map<K, V> createMap() {
		return Maps.newHashMap();
	}

	@Override
	public V get(K key) {
		return (V)this.map.get(key);
	}

	@Override
	public void put(K key, V value) {
		Validate.notNull(key);
		Validate.notNull(value);
		if (this.map.containsKey(key)) {
			LOGGER.debug("Adding duplicate key '" + key + "' to registry");
		}

		this.map.put(key, value);
	}

	public Set<K> keySet() {
		return Collections.unmodifiableSet(this.map.keySet());
	}

	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}

	public Iterator<V> iterator() {
		return this.map.values().iterator();
	}
}
