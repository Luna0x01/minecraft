package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;

public class class_3756 extends class_3755<Block, BlockState> implements BlockState {
	public class_3756(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
		super(block, immutableMap);
	}

	@Override
	public Block getBlock() {
		return this.field_18688;
	}
}
