package net.minecraft.predicate.block;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public class BlockStatePredicate implements Predicate<BlockState> {
	private final StateManager stateManager;
	private final Map<Property, Predicate> properties = Maps.newHashMap();

	private BlockStatePredicate(StateManager stateManager) {
		this.stateManager = stateManager;
	}

	public static BlockStatePredicate create(Block block) {
		return new BlockStatePredicate(block.getStateManager());
	}

	public boolean apply(BlockState blockState) {
		if (blockState != null && blockState.getBlock().equals(this.stateManager.getBlock())) {
			for (Entry<Property, Predicate> entry : this.properties.entrySet()) {
				Object object = blockState.get((Property)entry.getKey());
				if (!((Predicate)entry.getValue()).apply(object)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
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
