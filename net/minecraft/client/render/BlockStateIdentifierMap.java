package net.minecraft.client.render;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.property.Property;

public abstract class BlockStateIdentifierMap implements BlockStateIdentifierMapAccess {
	protected Map<BlockState, ModelIdentifier> map = Maps.newLinkedHashMap();

	public String getPropertyStateString(Map<Property<?>, Comparable<?>> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
			if (stringBuilder.length() != 0) {
				stringBuilder.append(",");
			}

			Property<?> property = (Property<?>)entry.getKey();
			stringBuilder.append(property.getName());
			stringBuilder.append("=");
			stringBuilder.append(this.method_12402(property, (Comparable<?>)entry.getValue()));
		}

		if (stringBuilder.length() == 0) {
			stringBuilder.append("normal");
		}

		return stringBuilder.toString();
	}

	private <T extends Comparable<T>> String method_12402(Property<T> property, Comparable<?> comparable) {
		return property.name((T)comparable);
	}

	@Override
	public Map<BlockState, ModelIdentifier> addBlock(Block block) {
		UnmodifiableIterator var2 = block.getStateManager().getBlockStates().iterator();

		while (var2.hasNext()) {
			BlockState blockState = (BlockState)var2.next();
			this.map.put(blockState, this.getBlockStateIdentifier(blockState));
		}

		return this.map;
	}

	protected abstract ModelIdentifier getBlockStateIdentifier(BlockState state);
}
