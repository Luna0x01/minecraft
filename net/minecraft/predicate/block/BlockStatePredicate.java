package net.minecraft.predicate.block;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public class BlockStatePredicate implements Predicate<BlockState> {
	public static final Predicate<BlockState> TRUE = state -> true;
	private final StateManager<Block, BlockState> stateManager;
	private final Map<Property<?>, Predicate<Object>> properties = Maps.newHashMap();

	private BlockStatePredicate(StateManager<Block, BlockState> stateManager) {
		this.stateManager = stateManager;
	}

	public static BlockStatePredicate create(Block block) {
		return new BlockStatePredicate(block.getStateManager());
	}

	public boolean test(@Nullable BlockState blockState) {
		if (blockState != null && blockState.getBlock().equals(this.stateManager.method_16924())) {
			if (this.properties.isEmpty()) {
				return true;
			} else {
				for (Entry<Property<?>, Predicate<Object>> entry : this.properties.entrySet()) {
					if (!this.testProperty(blockState, (Property)entry.getKey(), (Predicate<Object>)entry.getValue())) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	protected <T extends Comparable<T>> boolean testProperty(BlockState state, Property<T> property, Predicate<Object> predicate) {
		T comparable = state.getProperty(property);
		return predicate.test(comparable);
	}

	public <V extends Comparable<V>> BlockStatePredicate addTest(Property<V> property, Predicate<Object> predicate) {
		if (!this.stateManager.getProperties().contains(property)) {
			throw new IllegalArgumentException(this.stateManager + " cannot support property " + property);
		} else {
			this.properties.put(property, predicate);
			return this;
		}
	}
}
