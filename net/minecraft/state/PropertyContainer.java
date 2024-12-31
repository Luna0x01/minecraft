package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import net.minecraft.state.property.Property;

public interface PropertyContainer<C> {
	Collection<Property<?>> method_16929();

	<T extends Comparable<T>> boolean method_16933(Property<T> property);

	<T extends Comparable<T>> T getProperty(Property<T> property);

	<T extends Comparable<T>, V extends T> C withProperty(Property<T> property, V value);

	<T extends Comparable<T>> C method_16930(Property<T> property);

	ImmutableMap<Property<?>, Comparable<?>> getEntries();
}
