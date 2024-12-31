package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import net.minecraft.state.property.Property;

public interface BlockState {
	Collection<Property> getProperties();

	<T extends Comparable<T>> T get(Property<T> property);

	<T extends Comparable<T>, V extends T> BlockState with(Property<T> property, V comparable);

	<T extends Comparable<T>> BlockState withDefaultValue(Property<T> property);

	ImmutableMap<Property, Comparable> getPropertyMap();

	Block getBlock();
}
