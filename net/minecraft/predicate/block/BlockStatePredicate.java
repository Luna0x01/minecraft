package net.minecraft.predicate.block;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public class BlockStatePredicate implements Predicate<BlockState> {
	public static final Predicate<BlockState> field_12902 = new Predicate<BlockState>() {
		public boolean apply(@Nullable BlockState blockState) {
			return true;
		}
	};
	private final StateManager stateManager;
	private final Map<Property<?>, Predicate<?>> properties = Maps.newHashMap();

	private BlockStatePredicate(StateManager stateManager) {
		this.stateManager = stateManager;
	}

	public static BlockStatePredicate create(Block block) {
		return new BlockStatePredicate(block.getStateManager());
	}

	public boolean apply(@Nullable BlockState blockState) {
		if (blockState != null && blockState.getBlock().equals(this.stateManager.getBlock())) {
			for (Entry<Property<?>, Predicate<?>> entry : this.properties.entrySet()) {
				if (!this.method_11747(blockState, (Property)entry.getKey(), (Predicate<?>)entry.getValue())) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	protected <T extends Comparable<T>> boolean method_11747(BlockState blockState, Property<T> property, Predicate<?> predicate) {
		return predicate.apply(blockState.get(property));
	}

	public <V extends Comparable<V>> BlockStatePredicate setProperty(Property<V> property, Predicate<? extends V> predicate) {
		if (!this.stateManager.getProperties().contains(property)) {
			throw new IllegalArgumentException(this.stateManager + " cannot support property " + property);
		} else {
			this.properties.put(property, predicate);
			return this;
		}
	}
}
