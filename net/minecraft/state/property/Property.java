package net.minecraft.state.property;

import java.util.Collection;
import java.util.Optional;

public interface Property<T extends Comparable<T>> {
	String getName();

	Collection<T> getValues();

	Class<T> getType();

	Optional<T> getValueAsString(String value);

	String name(T value);
}
