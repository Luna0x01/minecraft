package net.minecraft.state.property;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;

public class IntProperty extends AbstractProperty<Integer> {
	private final ImmutableSet<Integer> values;

	protected IntProperty(String string, int i, int j) {
		super(string, Integer.class);
		if (i < 0) {
			throw new IllegalArgumentException("Min value of " + string + " must be 0 or greater");
		} else if (j <= i) {
			throw new IllegalArgumentException("Max value of " + string + " must be greater than min (" + i + ")");
		} else {
			Set<Integer> set = Sets.newHashSet();

			for (int k = i; k <= j; k++) {
				set.add(k);
			}

			this.values = ImmutableSet.copyOf(set);
		}
	}

	@Override
	public Collection<Integer> getValues() {
		return this.values;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object == null || this.getClass() != object.getClass()) {
			return false;
		} else if (!super.equals(object)) {
			return false;
		} else {
			IntProperty intProperty = (IntProperty)object;
			return this.values.equals(intProperty.values);
		}
	}

	@Override
	public int hashCode() {
		int i = super.hashCode();
		return 31 * i + this.values.hashCode();
	}

	public static IntProperty of(String name, int min, int max) {
		return new IntProperty(name, min, max);
	}

	public String name(Integer integer) {
		return integer.toString();
	}
}
