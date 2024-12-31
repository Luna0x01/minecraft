package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import net.minecraft.class_2741;
import net.minecraft.state.property.Property;

public interface BlockState extends class_2741, BaseBlockState {
	Collection<Property<?>> getProperties();

	<T extends Comparable<T>> T get(Property<T> property);

	<T extends Comparable<T>, V extends T> BlockState with(Property<T> property, V comparable);

	<T extends Comparable<T>> BlockState withDefaultValue(Property<T> property);

	ImmutableMap<Property<?>, Comparable<?>> getPropertyMap();

	Block getBlock();
}
