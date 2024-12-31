package net.minecraft.state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_3755;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.MapUtil;

public class StateManager<O, S extends PropertyContainer<S>> {
	private static final Pattern VALID_PROPERTY_NAME = Pattern.compile("^[a-z0-9_]+$");
	private final O field_18692;
	private final ImmutableSortedMap<String, Property<?>> properties;
	private final ImmutableList<S> states;

	protected <A extends class_3755<O, S>> StateManager(O object, StateManager.class_3758<O, S, A> arg, Map<String, Property<?>> map) {
		this.field_18692 = object;
		this.properties = ImmutableSortedMap.copyOf(map);
		Map<Map<Property<?>, Comparable<?>>, A> map2 = Maps.newLinkedHashMap();
		List<A> list = Lists.newArrayList();
		Stream<List<Comparable<?>>> stream = Stream.of(Collections.emptyList());
		UnmodifiableIterator var7 = this.properties.values().iterator();

		while (var7.hasNext()) {
			Property<?> property = (Property<?>)var7.next();
			stream = stream.flatMap(listx -> property.getValues().stream().map(comparable -> {
					List<Comparable<?>> list2 = Lists.newArrayList(listx);
					list2.add(comparable);
					return list2;
				}));
		}

		stream.forEach(list2 -> {
			Map<Property<?>, Comparable<?>> map2x = MapUtil.createMap(this.properties.values(), list2);
			A lvx = arg.create(object, ImmutableMap.copyOf(map2x));
			map2.put(map2x, lvx);
			list.add(lvx);
		});

		for (A lv : list) {
			lv.method_16857(map2);
		}

		this.states = ImmutableList.copyOf(list);
	}

	public ImmutableList<S> getBlockStates() {
		return this.states;
	}

	public S method_16923() {
		return (S)this.states.get(0);
	}

	public O method_16924() {
		return this.field_18692;
	}

	public Collection<Property<?>> getProperties() {
		return this.properties.values();
	}

	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("block", this.field_18692)
			.add("properties", this.properties.values().stream().map(Property::getName).collect(Collectors.toList()))
			.toString();
	}

	@Nullable
	public Property<?> getProperty(String name) {
		return (Property<?>)this.properties.get(name);
	}

	public static class Builder<O, S extends PropertyContainer<S>> {
		private final O field_18693;
		private final Map<String, Property<?>> field_18694 = Maps.newHashMap();

		public Builder(O object) {
			this.field_18693 = object;
		}

		public StateManager.Builder<O, S> method_16928(Property<?>... propertys) {
			for (Property<?> property : propertys) {
				this.method_16927(property);
				this.field_18694.put(property.getName(), property);
			}

			return this;
		}

		private <T extends Comparable<T>> void method_16927(Property<T> property) {
			String string = property.getName();
			if (!StateManager.VALID_PROPERTY_NAME.matcher(string).matches()) {
				throw new IllegalArgumentException(this.field_18693 + " has invalidly named property: " + string);
			} else {
				Collection<T> collection = property.getValues();
				if (collection.size() <= 1) {
					throw new IllegalArgumentException(this.field_18693 + " attempted use property " + string + " with <= 1 possible values");
				} else {
					for (T comparable : collection) {
						String string2 = property.name(comparable);
						if (!StateManager.VALID_PROPERTY_NAME.matcher(string2).matches()) {
							throw new IllegalArgumentException(this.field_18693 + " has property: " + string + " with invalidly named value: " + string2);
						}
					}

					if (this.field_18694.containsKey(string)) {
						throw new IllegalArgumentException(this.field_18693 + " has duplicate property: " + string);
					}
				}
			}
		}

		public <A extends class_3755<O, S>> StateManager<O, S> build(StateManager.class_3758<O, S, A> arg) {
			return new StateManager<>(this.field_18693, arg, this.field_18694);
		}
	}

	public interface class_3758<O, S extends PropertyContainer<S>, A extends class_3755<O, S>> {
		A create(O object, ImmutableMap<Property<?>, Comparable<?>> immutableMap);
	}
}
