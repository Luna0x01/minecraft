package net.minecraft.state.property;

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

	public String name(Boolean boolean_) {
		return boolean_.toString();
	}
}
