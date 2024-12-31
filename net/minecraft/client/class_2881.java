package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.class_4235;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public class class_2881 implements class_4235 {
	private static final Splitter field_13580 = Splitter.on('|').omitEmptyStrings();
	private final String field_13581;
	private final String field_13582;

	public class_2881(String string, String string2) {
		this.field_13581 = string;
		this.field_13582 = string2;
	}

	@Override
	public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> stateManager) {
		Property<?> property = stateManager.getProperty(this.field_13581);
		if (property == null) {
			throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.field_13581, stateManager.method_16924().toString()));
		} else {
			String string = this.field_13582;
			boolean bl = !string.isEmpty() && string.charAt(0) == '!';
			if (bl) {
				string = string.substring(1);
			}

			List<String> list = field_13580.splitToList(string);
			if (list.isEmpty()) {
				throw new RuntimeException(
					String.format("Empty value '%s' for property '%s' on '%s'", this.field_13582, this.field_13581, stateManager.method_16924().toString())
				);
			} else {
				Predicate<BlockState> predicate;
				if (list.size() == 1) {
					predicate = this.method_19265(stateManager, property, string);
				} else {
					List<Predicate<BlockState>> list2 = (List<Predicate<BlockState>>)list.stream()
						.map(stringx -> this.method_19265(stateManager, property, stringx))
						.collect(Collectors.toList());
					predicate = blockState -> list2.stream().anyMatch(predicatex -> predicatex.test(blockState));
				}

				return bl ? predicate.negate() : predicate;
			}
		}
	}

	private Predicate<BlockState> method_19265(StateManager<Block, BlockState> stateManager, Property<?> property, String string) {
		Optional<?> optional = property.getValueAsString(string);
		if (!optional.isPresent()) {
			throw new RuntimeException(
				String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", string, this.field_13581, stateManager.method_16924().toString(), this.field_13582)
			);
		} else {
			return blockState -> blockState.getProperty(property).equals(optional.get());
		}
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("key", this.field_13581).add("value", this.field_13582).toString();
	}
}
