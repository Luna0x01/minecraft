package net.minecraft;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.state.PropertyContainer;
import net.minecraft.state.property.Property;

public abstract class class_3755<O, S> implements PropertyContainer<S> {
	private static final Function<Entry<Property<?>, Comparable<?>>, String> field_18684 = new Function<Entry<Property<?>, Comparable<?>>, String>() {
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
	protected final O field_18688;
	private final ImmutableMap<Property<?>, Comparable<?>> field_18685;
	private final int field_18686;
	private Table<Property<?>, Comparable<?>, S> field_18687;

	protected class_3755(O object, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
		this.field_18688 = object;
		this.field_18685 = immutableMap;
		this.field_18686 = immutableMap.hashCode();
	}

	@Override
	public <T extends Comparable<T>> S method_16930(Property<T> property) {
		return this.withProperty(property, method_16856(property.getValues(), this.getProperty(property)));
	}

	protected static <T> T method_16856(Collection<T> collection, T object) {
		Iterator<T> iterator = collection.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equals(object)) {
				if (iterator.hasNext()) {
					return (T)iterator.next();
				}

				return (T)collection.iterator().next();
			}
		}

		return (T)iterator.next();
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.field_18688);
		if (!this.getEntries().isEmpty()) {
			stringBuilder.append('[');
			stringBuilder.append((String)this.getEntries().entrySet().stream().map(field_18684).collect(Collectors.joining(",")));
			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}

	@Override
	public Collection<Property<?>> method_16929() {
		return Collections.unmodifiableCollection(this.field_18685.keySet());
	}

	@Override
	public <T extends Comparable<T>> boolean method_16933(Property<T> property) {
		return this.field_18685.containsKey(property);
	}

	@Override
	public <T extends Comparable<T>> T getProperty(Property<T> property) {
		Comparable<?> comparable = (Comparable<?>)this.field_18685.get(property);
		if (comparable == null) {
			throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.field_18688);
		} else {
			return (T)property.getType().cast(comparable);
		}
	}

	@Override
	public <T extends Comparable<T>, V extends T> S withProperty(Property<T> property, V value) {
		Comparable<?> comparable = (Comparable<?>)this.field_18685.get(property);
		if (comparable == null) {
			throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.field_18688);
		} else if (comparable == value) {
			return (S)this;
		} else {
			S object = (S)this.field_18687.get(property, value);
			if (object == null) {
				throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on " + this.field_18688 + ", it is not an allowed value");
			} else {
				return object;
			}
		}
	}

	public void method_16857(Map<Map<Property<?>, Comparable<?>>, S> map) {
		if (this.field_18687 != null) {
			throw new IllegalStateException();
		} else {
			Table<Property<?>, Comparable<?>, S> table = HashBasedTable.create();
			UnmodifiableIterator var3 = this.field_18685.entrySet().iterator();

			while (var3.hasNext()) {
				Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var3.next();
				Property<?> property = (Property<?>)entry.getKey();

				for (Comparable<?> comparable : property.getValues()) {
					if (comparable != entry.getValue()) {
						table.put(property, comparable, map.get(this.method_16858(property, comparable)));
					}
				}
			}

			this.field_18687 = (Table<Property<?>, Comparable<?>, S>)(table.isEmpty() ? table : ArrayTable.create(table));
		}
	}

	private Map<Property<?>, Comparable<?>> method_16858(Property<?> property, Comparable<?> comparable) {
		Map<Property<?>, Comparable<?>> map = Maps.newHashMap(this.field_18685);
		map.put(property, comparable);
		return map;
	}

	@Override
	public ImmutableMap<Property<?>, Comparable<?>> getEntries() {
		return this.field_18685;
	}

	public boolean equals(Object object) {
		return this == object;
	}

	public int hashCode() {
		return this.field_18686;
	}
}
