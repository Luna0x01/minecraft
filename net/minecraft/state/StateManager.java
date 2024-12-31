package net.minecraft.state;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.CollectionBuilders;
import net.minecraft.util.collection.MapUtil;

public class StateManager {
	private static final Joiner JOINER = Joiner.on(", ");
	private static final Function<Property, String> PROPERTY_STRING_FUNCTION = new Function<Property, String>() {
		public String apply(Property property) {
			return property == null ? "<NULL>" : property.getName();
		}
	};
	private final Block parentBlock;
	private final ImmutableList<Property> properties;
	private final ImmutableList<BlockState> states;

	public StateManager(Block block, Property... propertys) {
		this.parentBlock = block;
		Arrays.sort(propertys, new Comparator<Property>() {
			public int compare(Property property, Property property2) {
				return property.getName().compareTo(property2.getName());
			}
		});
		this.properties = ImmutableList.copyOf(propertys);
		Map<Map<Property, Comparable>, StateManager.BlockStateImpl> map = Maps.newLinkedHashMap();
		List<StateManager.BlockStateImpl> list = Lists.newArrayList();

		for (List<Comparable> list2 : CollectionBuilders.method_10516(this.method_9033())) {
			Map<Property, Comparable> map2 = MapUtil.createMap(this.properties, list2);
			StateManager.BlockStateImpl blockStateImpl = new StateManager.BlockStateImpl(block, ImmutableMap.copyOf(map2));
			map.put(map2, blockStateImpl);
			list.add(blockStateImpl);
		}

		for (StateManager.BlockStateImpl blockStateImpl2 : list) {
			blockStateImpl2.method_9036(map);
		}

		this.states = ImmutableList.copyOf(list);
	}

	public ImmutableList<BlockState> getBlockStates() {
		return this.states;
	}

	private List<Iterable<Comparable>> method_9033() {
		List<Iterable<Comparable>> list = Lists.newArrayList();

		for (int i = 0; i < this.properties.size(); i++) {
			list.add(((Property)this.properties.get(i)).getValues());
		}

		return list;
	}

	public BlockState getDefaultState() {
		return (BlockState)this.states.get(0);
	}

	public Block getBlock() {
		return this.parentBlock;
	}

	public Collection<Property> getProperties() {
		return this.properties;
	}

	public String toString() {
		return Objects.toStringHelper(this)
			.add("block", Block.REGISTRY.getIdentifier(this.parentBlock))
			.add("properties", Iterables.transform(this.properties, PROPERTY_STRING_FUNCTION))
			.toString();
	}

	static class BlockStateImpl extends AbstractBlockState {
		private final Block block;
		private final ImmutableMap<Property, Comparable> map;
		private ImmutableTable<Property, Comparable, BlockState> table;

		private BlockStateImpl(Block block, ImmutableMap<Property, Comparable> immutableMap) {
			this.block = block;
			this.map = immutableMap;
		}

		@Override
		public Collection<Property> getProperties() {
			return Collections.unmodifiableCollection(this.map.keySet());
		}

		@Override
		public <T extends Comparable<T>> T get(Property<T> property) {
			if (!this.map.containsKey(property)) {
				throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getStateManager());
			} else {
				return (T)property.getType().cast(this.map.get(property));
			}
		}

		@Override
		public <T extends Comparable<T>, V extends T> BlockState with(Property<T> property, V comparable) {
			if (!this.map.containsKey(property)) {
				throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getStateManager());
			} else if (!property.getValues().contains(comparable)) {
				throw new IllegalArgumentException(
					"Cannot set property " + property + " to " + comparable + " on block " + Block.REGISTRY.getIdentifier(this.block) + ", it is not an allowed value"
				);
			} else {
				return (BlockState)(this.map.get(property) == comparable ? this : (BlockState)this.table.get(property, comparable));
			}
		}

		@Override
		public ImmutableMap<Property, Comparable> getPropertyMap() {
			return this.map;
		}

		@Override
		public Block getBlock() {
			return this.block;
		}

		public boolean equals(Object object) {
			return this == object;
		}

		public int hashCode() {
			return this.map.hashCode();
		}

		public void method_9036(Map<Map<Property, Comparable>, StateManager.BlockStateImpl> map) {
			if (this.table != null) {
				throw new IllegalStateException();
			} else {
				Table<Property, Comparable, BlockState> table = HashBasedTable.create();

				for (Property<? extends Comparable> property : this.map.keySet()) {
					for (Comparable comparable : property.getValues()) {
						if (comparable != this.map.get(property)) {
							table.put(property, comparable, map.get(this.method_9037(property, comparable)));
						}
					}
				}

				this.table = ImmutableTable.copyOf(table);
			}
		}

		private Map<Property, Comparable> method_9037(Property property, Comparable comparable) {
			Map<Property, Comparable> map = Maps.newHashMap(this.map);
			map.put(property, comparable);
			return map;
		}
	}
}
