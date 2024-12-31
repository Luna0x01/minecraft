package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_2929;
import net.minecraft.util.collection.ObjectIdIterable;

public class SimpleRegistry<K, V> extends MutableRegistry<K, V> implements ObjectIdIterable<V> {
	protected final class_2929<V> field_13718 = new class_2929(256);
	protected final Map<V, K> objects = ((BiMap)this.map).inverse();

	public void add(int id, K identifier, V object) {
		this.field_13718.add(object, id);
		this.put(identifier, object);
	}

	@Override
	protected Map<K, V> createMap() {
		return HashBiMap.create();
	}

	@Nullable
	@Override
	public V get(@Nullable K key) {
		return super.get(key);
	}

	@Nullable
	public K getIdentifier(V id) {
		return (K)this.objects.get(id);
	}

	@Override
	public boolean containsKey(K key) {
		return super.containsKey(key);
	}

	public int getRawId(@Nullable V value) {
		return this.field_13718.getId(value);
	}

	@Nullable
	public V getByRawId(int index) {
		return this.field_13718.getById(index);
	}

	@Override
	public Iterator<V> iterator() {
		return this.field_13718.iterator();
	}
}
