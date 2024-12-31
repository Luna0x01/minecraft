package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;

public class BiDefaultedRegistry<V> extends SimpleRegistry<V> {
	private final Identifier defaultId;
	private V defaultValue;

	public BiDefaultedRegistry(Identifier identifier) {
		this.defaultId = identifier;
	}

	@Override
	public void set(int rawId, Identifier identifier, V object) {
		if (this.defaultId.equals(identifier)) {
			this.defaultValue = object;
		}

		super.set(rawId, identifier, object);
	}

	@Override
	public int getRawId(@Nullable V object) {
		int i = super.getRawId(object);
		return i == -1 ? super.getRawId(this.defaultValue) : i;
	}

	@Override
	public Identifier getId(V object) {
		Identifier identifier = super.getId(object);
		return identifier == null ? this.defaultId : identifier;
	}

	@Override
	public V get(@Nullable Identifier identifier) {
		V object = this.getByIdentifier(identifier);
		return object == null ? this.defaultValue : object;
	}

	@Nonnull
	@Override
	public V getByRawId(int rawId) {
		V object = super.getByRawId(rawId);
		return object == null ? this.defaultValue : object;
	}

	@Nonnull
	@Override
	public V getRandom(Random random) {
		V object = super.getRandom(random);
		return object == null ? this.defaultValue : object;
	}

	@Override
	public Identifier getDefaultId() {
		return this.defaultId;
	}
}
