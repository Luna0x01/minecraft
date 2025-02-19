package net.minecraft.state.property;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.StringIdentifiable;

public class EnumProperty<T extends Enum<T> & StringIdentifiable> extends Property<T> {
	private final ImmutableSet<T> values;
	private final Map<String, T> byName = Maps.newHashMap();

	protected EnumProperty(String name, Class<T> type, Collection<T> values) {
		super(name, type);
		this.values = ImmutableSet.copyOf(values);

		for (T enum_ : values) {
			String string = enum_.asString();
			if (this.byName.containsKey(string)) {
				throw new IllegalArgumentException("Multiple values have the same name '" + string + "'");
			}

			this.byName.put(string, enum_);
		}
	}

	@Override
	public Collection<T> getValues() {
		return this.values;
	}

	@Override
	public Optional<T> parse(String name) {
		return Optional.ofNullable((Enum)this.byName.get(name));
	}

	public String name(T enum_) {
		return enum_.asString();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof EnumProperty && super.equals(object)) {
			EnumProperty<?> enumProperty = (EnumProperty<?>)object;
			return this.values.equals(enumProperty.values) && this.byName.equals(enumProperty.byName);
		} else {
			return false;
		}
	}

	@Override
	public int computeHashCode() {
		int i = super.computeHashCode();
		i = 31 * i + this.values.hashCode();
		return 31 * i + this.byName.hashCode();
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type) {
		return of(name, type, Predicates.alwaysTrue());
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, Predicate<T> filter) {
		return of(name, type, (Collection<T>)Arrays.stream((Enum[])type.getEnumConstants()).filter(filter).collect(Collectors.toList()));
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, T... values) {
		return of(name, type, Lists.newArrayList(values));
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, Collection<T> values) {
		return new EnumProperty<>(name, type, values);
	}
}
