package net.minecraft.state;

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
import net.minecraft.state.property.Property;

public abstract class AbstractPropertyContainer<O, S> implements PropertyContainer<S> {
	private static final Function<Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER = new Function<Entry<Property<?>, Comparable<?>>, String>() {
		public String method_11576(@Nullable Entry<Property<?>, Comparable<?>> entry) {
			if (entry == null) {
				return "<NULL>";
			} else {
				Property<?> property = (Property<?>)entry.getKey();
				return property.getName() + "=" + this.valueToString(property, (Comparable<?>)entry.getValue());
			}
		}

		private <T extends Comparable<T>> String valueToString(Property<T> property, Comparable<?> comparable) {
			return property.getName((T)comparable);
		}
	};
	protected final O owner;
	private final ImmutableMap<Property<?>, Comparable<?>> entries;
	private final int hashCode;
	private Table<Property<?>, Comparable<?>, S> withTable;

	protected AbstractPropertyContainer(O object, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
		this.owner = object;
		this.entries = immutableMap;
		this.hashCode = immutableMap.hashCode();
	}

	public <T extends Comparable<T>> S cycle(Property<T> property) {
		return this.with(property, getNext(property.getValues(), this.get(property)));
	}

	protected static <T> T getNext(Collection<T> collection, T object) {
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
		stringBuilder.append(this.owner);
		if (!this.getEntries().isEmpty()) {
			stringBuilder.append('[');
			stringBuilder.append((String)this.getEntries().entrySet().stream().map(PROPERTY_MAP_PRINTER).collect(Collectors.joining(",")));
			stringBuilder.append(']');
		}

		return stringBuilder.toString();
	}

	public Collection<Property<?>> getProperties() {
		return Collections.unmodifiableCollection(this.entries.keySet());
	}

	public <T extends Comparable<T>> boolean contains(Property<T> property) {
		return this.entries.containsKey(property);
	}

	@Override
	public <T extends Comparable<T>> T get(Property<T> property) {
		Comparable<?> comparable = (Comparable<?>)this.entries.get(property);
		if (comparable == null) {
			throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.owner);
		} else {
			return (T)property.getValueType().cast(comparable);
		}
	}

	@Override
	public <T extends Comparable<T>, V extends T> S with(Property<T> property, V comparable) {
		Comparable<?> comparable2 = (Comparable<?>)this.entries.get(property);
		if (comparable2 == null) {
			throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.owner);
		} else if (comparable2 == comparable) {
			return (S)this;
		} else {
			S object = (S)this.withTable.get(property, comparable);
			if (object == null) {
				throw new IllegalArgumentException("Cannot set property " + property + " to " + comparable + " on " + this.owner + ", it is not an allowed value");
			} else {
				return object;
			}
		}
	}

	public void createWithTable(Map<Map<Property<?>, Comparable<?>>, S> map) {
		if (this.withTable != null) {
			throw new IllegalStateException();
		} else {
			Table<Property<?>, Comparable<?>, S> table = HashBasedTable.create();
			UnmodifiableIterator var3 = this.entries.entrySet().iterator();

			while (var3.hasNext()) {
				Entry<Property<?>, Comparable<?>> entry = (Entry<Property<?>, Comparable<?>>)var3.next();
				Property<?> property = (Property<?>)entry.getKey();

				for (Comparable<?> comparable : property.getValues()) {
					if (comparable != entry.getValue()) {
						table.put(property, comparable, map.get(this.toMapWith(property, comparable)));
					}
				}
			}

			this.withTable = (Table<Property<?>, Comparable<?>, S>)(table.isEmpty() ? table : ArrayTable.create(table));
		}
	}

	private Map<Property<?>, Comparable<?>> toMapWith(Property<?> property, Comparable<?> comparable) {
		Map<Property<?>, Comparable<?>> map = Maps.newHashMap(this.entries);
		map.put(property, comparable);
		return map;
	}

	@Override
	public ImmutableMap<Property<?>, Comparable<?>> getEntries() {
		return this.entries;
	}

	public boolean equals(Object object) {
		return this == object;
	}

	public int hashCode() {
		return this.hashCode;
	}
}
