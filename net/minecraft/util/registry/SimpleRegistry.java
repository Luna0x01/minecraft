package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_2929;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleRegistry<V> implements Registry<V> {
	protected static final Logger field_21312 = LogManager.getLogger();
	protected final class_2929<V> field_13718 = new class_2929<>(256);
	protected final BiMap<Identifier, V> field_21313 = HashBiMap.create();
	protected Object[] field_21314;
	private int field_21315;

	@Override
	public void set(int rawId, Identifier identifier, V object) {
		this.field_13718.add(object, rawId);
		Validate.notNull(identifier);
		Validate.notNull(object);
		this.field_21314 = null;
		if (this.field_21313.containsKey(identifier)) {
			field_21312.debug("Adding duplicate key '{}' to registry", identifier);
		}

		this.field_21313.put(identifier, object);
		if (this.field_21315 <= rawId) {
			this.field_21315 = rawId + 1;
		}
	}

	@Override
	public void add(Identifier identifier, V object) {
		this.set(this.field_21315, identifier, object);
	}

	@Nullable
	@Override
	public Identifier getId(V object) {
		return (Identifier)this.field_21313.inverse().get(object);
	}

	@Override
	public V get(@Nullable Identifier identifier) {
		throw new UnsupportedOperationException("No default value");
	}

	@Override
	public Identifier getDefaultId() {
		throw new UnsupportedOperationException("No default key");
	}

	@Override
	public int getRawId(@Nullable V object) {
		return this.field_13718.getId(object);
	}

	@Nullable
	@Override
	public V getByRawId(int rawId) {
		return this.field_13718.getById(rawId);
	}

	@Override
	public Iterator<V> iterator() {
		return this.field_13718.iterator();
	}

	@Nullable
	@Override
	public V getByIdentifier(@Nullable Identifier identifier) {
		return (V)this.field_21313.get(identifier);
	}

	@Override
	public Set<Identifier> getKeySet() {
		return Collections.unmodifiableSet(this.field_21313.keySet());
	}

	@Override
	public boolean isEmpty() {
		return this.field_21313.isEmpty();
	}

	@Nullable
	@Override
	public V getRandom(Random random) {
		if (this.field_21314 == null) {
			Collection<?> collection = this.field_21313.values();
			if (collection.isEmpty()) {
				return null;
			}

			this.field_21314 = collection.toArray(new Object[collection.size()]);
		}

		return (V)this.field_21314[random.nextInt(this.field_21314.length)];
	}

	@Override
	public boolean containsId(Identifier identifier) {
		return this.field_21313.containsKey(identifier);
	}
}
