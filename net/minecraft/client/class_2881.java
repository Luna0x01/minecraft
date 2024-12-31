package net.minecraft.client;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public class class_2881 implements class_2880 {
	private static final Splitter field_13580 = Splitter.on('|').omitEmptyStrings();
	private final String field_13581;
	private final String field_13582;

	public class_2881(String string, String string2) {
		this.field_13581 = string;
		this.field_13582 = string2;
	}

	@Override
	public Predicate<BlockState> method_12379(StateManager stateManager) {
		final Property<?> property = stateManager.getProperty(this.field_13581);
		if (property == null) {
			throw new RuntimeException(this.toString() + ": Definition: " + stateManager + " has no property: " + this.field_13581);
		} else {
			String string = this.field_13582;
			boolean bl = !string.isEmpty() && string.charAt(0) == '!';
			if (bl) {
				string = string.substring(1);
			}

			List<String> list = field_13580.splitToList(string);
			if (list.isEmpty()) {
				throw new RuntimeException(this.toString() + ": has an empty value: " + this.field_13582);
			} else {
				Predicate<BlockState> predicate;
				if (list.size() == 1) {
					predicate = this.method_12382(property, string);
				} else {
					predicate = Predicates.or(Iterables.transform(list, new Function<String, Predicate<BlockState>>() {
						@Nullable
						public Predicate<BlockState> apply(@Nullable String string) {
							return class_2881.this.method_12382(property, string);
						}
					}));
				}

				return bl ? Predicates.not(predicate) : predicate;
			}
		}
	}

	private Predicate<BlockState> method_12382(Property<?> property, String string) {
		final Optional<?> optional = property.method_11749(string);
		if (!optional.isPresent()) {
			throw new RuntimeException(this.toString() + ": has an unknown value: " + this.field_13582);
		} else {
			return new Predicate<BlockState>() {
				public boolean apply(@Nullable BlockState blockState) {
					return blockState != null && blockState.get(property).equals(optional.get());
				}
			};
		}
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("key", this.field_13581).add("value", this.field_13582).toString();
	}
}
