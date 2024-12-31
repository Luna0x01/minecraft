package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Optional;

public class BooleanProperty extends AbstractProperty<Boolean> {
	private final ImmutableSet<Boolean> values = ImmutableSet.of(true, false);

	protected BooleanProperty(String string) {
		super(string, Boolean.class);
	}

	@Override
	public Collection<Boolean> getValues() {
		return this.values;
	}

	public static BooleanProperty of(String name) {
		return new BooleanProperty(name);
	}

	@Override
	public Optional<Boolean> getValueAsString(String value) {
		return !"true".equals(value) && !"false".equals(value) ? Optional.empty() : Optional.of(Boolean.valueOf(value));
	}

	public String name(Boolean boolean_) {
		return boolean_.toString();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof BooleanProperty && super.equals(object)) {
			BooleanProperty booleanProperty = (BooleanProperty)object;
			return this.values.equals(booleanProperty.values);
		} else {
			return false;
		}
	}

	@Override
	public int computeHashCode() {
		return 31 * super.computeHashCode() + this.values.hashCode();
	}
}
