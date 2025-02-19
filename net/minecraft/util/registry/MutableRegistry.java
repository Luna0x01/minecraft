package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;

public abstract class MutableRegistry<T> extends Registry<T> {
	public MutableRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
		super(registryKey, lifecycle);
	}

	public abstract <V extends T> V set(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle);

	public abstract <V extends T> V add(RegistryKey<T> key, V entry, Lifecycle lifecycle);

	public abstract <V extends T> V replace(OptionalInt rawId, RegistryKey<T> key, V newEntry, Lifecycle lifecycle);

	public abstract boolean isEmpty();
}
