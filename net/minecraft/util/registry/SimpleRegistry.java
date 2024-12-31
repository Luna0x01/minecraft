package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.ObjectIdIterable;

public class SimpleRegistry<K, V> extends MutableRegistry<K, V> implements ObjectIdIterable<V> {
	protected final IdList<V> ids = new IdList<>();
	protected final Map<V, K> objects = ((BiMap)this.map).inverse();

	public void add(int id, K identifier, V object) {
		this.ids.set(object, id);
		this.put(identifier, object);
	}

	@Override
	protected Map<K, V> createMap() {
		return HashBiMap.create();
	}

	@Override
	public V get(K key) {
		return super.get(key);
	}

	public K getIdentifier(V id) {
		return (K)this.objects.get(id);
	}

	@Override
	public boolean containsKey(K key) {
		return super.containsKey(key);
	}

	public int getRawId(V object) {
		return this.ids.getId(object);
	}

	public V getByRawId(int index) {
		return this.ids.fromId(index);
	}

	@Override
	public Iterator<V> iterator() {
		return this.ids.iterator();
	}
}
