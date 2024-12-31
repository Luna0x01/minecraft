package net.minecraft.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockStateIdentifierMap;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.property.Property;

public class BlockStateMap extends BlockStateIdentifierMap {
	private final Property<?> defaultProperty;
	private final String suffix;
	private final List<Property<?>> ignoredProperties;

	private BlockStateMap(@Nullable Property<?> property, @Nullable String string, List<Property<?>> list) {
		this.defaultProperty = property;
		this.suffix = string;
		this.ignoredProperties = list;
	}

	@Override
	protected ModelIdentifier getBlockStateIdentifier(BlockState state) {
		Map<Property<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getPropertyMap());
		String string;
		if (this.defaultProperty == null) {
			string = Block.REGISTRY.getIdentifier(state.getBlock()).toString();
		} else {
			string = this.method_12405(this.defaultProperty, map);
		}

		if (this.suffix != null) {
			string = string + this.suffix;
		}

		for (Property<?> property : this.ignoredProperties) {
			map.remove(property);
		}

		return new ModelIdentifier(string, this.getPropertyStateString(map));
	}

	private <T extends Comparable<T>> String method_12405(Property<T> property, Map<Property<?>, Comparable<?>> map) {
		return property.name((T)map.remove(this.defaultProperty));
	}

	public static class Builder {
		private Property<?> defaultProperty;
		private String suffix;
		private final List<Property<?>> ignoredProperties = Lists.newArrayList();

		public BlockStateMap.Builder defaultProperty(Property<?> property) {
			this.defaultProperty = property;
			return this;
		}

		public BlockStateMap.Builder suffix(String suffix) {
			this.suffix = suffix;
			return this;
		}

		public BlockStateMap.Builder ignoreProperties(Property<?>... properties) {
			Collections.addAll(this.ignoredProperties, properties);
			return this;
		}

		public BlockStateMap build() {
			return new BlockStateMap(this.defaultProperty, this.suffix, this.ignoredProperties);
		}
	}
}
