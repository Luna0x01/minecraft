package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	public void validate() {
		Validate.notNull(this.defaultValue, "Missing default of DefaultedMappedRegistry: " + this.defaultKey, new Object[0]);
	}

	@Override
	public int getRawId(V value) {
		int i = super.getRawId(value);
		return i == -1 ? super.getRawId(this.defaultValue) : i;
	}

	@Nonnull
	@Override
	public K getIdentifier(V id) {
		K object = super.getIdentifier(id);
		return object == null ? this.defaultKey : object;
	}

	@Nonnull
	@Override
	public V get(@Nullable K key) {
		V object = super.get(key);
		return object == null ? this.defaultValue : object;
	}

	@Nonnull
	@Override
	public V getByRawId(int index) {
		V object = super.getByRawId(index);
		return object == null ? this.defaultValue : object;
	}

	@Nonnull
	@Override
	public V method_12584(Random random) {
		V object = super.method_12584(random);
		return object == null ? this.defaultValue : object;
	}
}
