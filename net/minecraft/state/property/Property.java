package net.minecraft.state.property;

import java.util.Collection;

public interface Property<T extends Comparable<T>> {
	String getName();

	Collection<T> getValues();

	Class<T> getType();

	String name(T value);
}
