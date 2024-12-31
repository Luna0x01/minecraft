package net.minecraft.state.property;

import com.google.common.base.Optional;
import java.util.Collection;

public interface Property<T extends Comparable<T>> {
	String getName();

	Collection<T> getValues();

	Class<T> getType();

	Optional<T> method_11749(String string);

	String name(T value);
}
