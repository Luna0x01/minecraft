package net.minecraft.state.property;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.minecraft.util.StringIdentifiable;

public class EnumProperty<T extends Enum<T> & StringIdentifiable> extends AbstractProperty<T> {
	private final ImmutableSet<T> values;
	private final Map<String, T> byName = Maps.newHashMap();

	protected EnumProperty(String string, Class<T> class_, Collection<T> collection) {
		super(string, class_);
		this.values = ImmutableSet.copyOf(collection);

		for (T enum_ : collection) {
			String string2 = enum_.asString();
			if (this.byName.containsKey(string2)) {
				throw new IllegalArgumentException("Multiple values have the same name '" + string2 + "'");
			}

			this.byName.put(string2, enum_);
		}
	}

	@Override
	public Collection<T> getValues() {
		return this.values;
	}

	public String name(T enum_) {
		return enum_.asString();
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type) {
		return of(name, type, Predicates.alwaysTrue());
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, Predicate<T> pred) {
		return of(name, type, Collections2.filter(Lists.newArrayList(type.getEnumConstants()), pred));
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, T... values) {
		return of(name, type, Lists.newArrayList(values));
	}

	public static <T extends Enum<T> & StringIdentifiable> EnumProperty<T> of(String name, Class<T> type, Collection<T> values) {
		return new EnumProperty<>(name, type, values);
	}
}
