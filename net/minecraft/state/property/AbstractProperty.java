package net.minecraft.state.property;

import com.google.common.base.Objects;

public abstract class AbstractProperty<T extends Comparable<T>> implements Property<T> {
	private final Class<T> type;
	private final String name;

	protected AbstractProperty(String string, Class<T> class_) {
		this.type = class_;
		this.name = string;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Class<T> getType() {
		return this.type;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("name", this.name).add("clazz", this.type).add("values", this.getValues()).toString();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof AbstractProperty)) {
			return false;
		} else {
			AbstractProperty<?> abstractProperty = (AbstractProperty<?>)obj;
			return this.type.equals(abstractProperty.type) && this.name.equals(abstractProperty.name);
		}
	}

	public int hashCode() {
		return 31 * this.type.hashCode() + this.name.hashCode();
	}
}
