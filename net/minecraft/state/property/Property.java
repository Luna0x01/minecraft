package net.minecraft.state.property;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.state.State;

public abstract class Property<T extends Comparable<T>> {
	private final Class<T> type;
	private final String name;
	private Integer hashCodeCache;
	private final Codec<T> codec = Codec.STRING
		.comapFlatMap(
			value -> (DataResult)this.parse(value)
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error("Unable to read property: " + this + " with value: " + value)),
			this::name
		);
	private final Codec<Property.Value<T>> valueCodec = this.codec.xmap(this::createValue, Property.Value::getValue);

	protected Property(String name, Class<T> type) {
		this.type = type;
		this.name = name;
	}

	public Property.Value<T> createValue(T value) {
		return new Property.Value<>(this, value);
	}

	public Property.Value<T> createValue(State<?, ?> state) {
		return new Property.Value<>(this, state.get(this));
	}

	public Stream<Property.Value<T>> stream() {
		return this.getValues().stream().map(this::createValue);
	}

	public Codec<T> getCodec() {
		return this.codec;
	}

	public Codec<Property.Value<T>> getValueCodec() {
		return this.valueCodec;
	}

	public String getName() {
		return this.name;
	}

	public Class<T> getType() {
		return this.type;
	}

	public abstract Collection<T> getValues();

	public abstract String name(T value);

	public abstract Optional<T> parse(String name);

	public String toString() {
		return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.type).add("values", this.getValues()).toString();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof Property<?> property) ? false : this.type.equals(property.type) && this.name.equals(property.name);
		}
	}

	public final int hashCode() {
		if (this.hashCodeCache == null) {
			this.hashCodeCache = this.computeHashCode();
		}

		return this.hashCodeCache;
	}

	public int computeHashCode() {
		return 31 * this.type.hashCode() + this.name.hashCode();
	}

	public <U, S extends State<?, S>> DataResult<S> method_35307(DynamicOps<U> dynamicOps, S state, U object) {
		DataResult<T> dataResult = this.codec.parse(dynamicOps, object);
		return dataResult.map(comparable -> state.with(this, comparable)).setPartial(state);
	}

	public static final class Value<T extends Comparable<T>> {
		private final Property<T> property;
		private final T value;

		Value(Property<T> property, T value) {
			if (!property.getValues().contains(value)) {
				throw new IllegalArgumentException("Value " + value + " does not belong to property " + property);
			} else {
				this.property = property;
				this.value = value;
			}
		}

		public Property<T> getProperty() {
			return this.property;
		}

		public T getValue() {
			return this.value;
		}

		public String toString() {
			return this.property.getName() + "=" + this.property.name(this.value);
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else {
				return !(o instanceof Property.Value<?> value) ? false : this.property == value.property && this.value.equals(value.value);
			}
		}

		public int hashCode() {
			int i = this.property.hashCode();
			return 31 * i + this.value.hashCode();
		}
	}
}
