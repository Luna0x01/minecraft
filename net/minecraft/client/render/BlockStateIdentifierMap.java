package net.minecraft.client.render;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.property.Property;

public abstract class BlockStateIdentifierMap implements BlockStateIdentifierMapAccess {
	protected Map<BlockState, ModelIdentifier> map = Maps.newLinkedHashMap();

	public String getPropertyStateString(Map<Property, Comparable> map) {
		StringBuilder stringBuilder = new StringBuilder();

		for (Entry<Property, Comparable> entry : map.entrySet()) {
			if (stringBuilder.length() != 0) {
				stringBuilder.append(",");
			}

			Property property = (Property)entry.getKey();
			Comparable comparable = (Comparable)entry.getValue();
			stringBuilder.append(property.getName());
			stringBuilder.append("=");
			stringBuilder.append(property.name(comparable));
		}

		if (stringBuilder.length() == 0) {
			stringBuilder.append("normal");
		}

		return stringBuilder.toString();
	}

	@Override
	public Map<BlockState, ModelIdentifier> addBlock(Block block) {
		for (BlockState blockState : block.getStateManager().getBlockStates()) {
			this.map.put(blockState, this.getBlockStateIdentifier(blockState));
		}

		return this.map;
	}

	protected abstract ModelIdentifier getBlockStateIdentifier(BlockState state);
}
