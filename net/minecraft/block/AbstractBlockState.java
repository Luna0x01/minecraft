package net.minecraft.block;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.state.property.Property;

public abstract class AbstractBlockState implements BlockState {
	private static final Joiner JOINER = Joiner.on(',');
	private static final Function<Entry<Property<?>, Comparable<?>>, String> BLOCKSTATE_PROPERTY_ENTRY_FUNCTION = new Function<Entry<Property<?>, Comparable<?>>, String>() {
		@Nullable
		public String apply(@Nullable Entry<Property<?>, Comparable<?>> entry) {
			if (entry == null) {
				return "<NULL>";
			} else {
				Property<?> property = (Property<?>)entry.getKey();
				return property.getName() + "=" + this.method_11705(property, (Comparable<?>)entry.getValue());
			}
		}

		private <T extends Comparable<T>> String method_11705(Property<T> property, Comparable<?> comparable) {
			return property.name((T)comparable);
		}
	};

	@Override
	public <T extends Comparable<T>> BlockState withDefaultValue(Property<T> property) {
		return this.with(property, getNextPropertyValue(property.getValues(), this.get(property)));
	}

	protected static <T> T getNextPropertyValue(Collection<T> possibleValues, T value) {
		Iterator<T> iterator = possibleValues.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equals(value)) {
				if (iterator.hasNext()) {
					return (T)iterator.next();
				}

				return (T)possibleValues.iterator().next();
			}
		}

		return (T)iterator.next();
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Block.REGISTRY.getIdentifier(this.getBlock()));
		if (!this.getPropertyMap().isEmpty()) {
			stringBuilder.append("[");
			JOINER.appendTo(stringBuilder, Iterables.transform(this.getPropertyMap().entrySet(), BLOCKSTATE_PROPERTY_ENTRY_FUNCTION));
			stringBuilder.append("]");
		}

		return stringBuilder.toString();
	}
}
