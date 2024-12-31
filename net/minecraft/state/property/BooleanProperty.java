package net.minecraft.state.property;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;

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
	public Optional<Boolean> method_11749(String string) {
		return !"true".equals(string) && !"false".equals(string) ? Optional.absent() : Optional.of(Boolean.valueOf(string));
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
	public int hashCode() {
		return 31 * super.hashCode() + this.values.hashCode();
	}
}
