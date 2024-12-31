package net.minecraft.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockStateIdentifierMap;
import net.minecraft.client.util.ModelIdentifier;

public class DefaultBlockStateMap extends BlockStateIdentifierMap {
	@Override
	protected ModelIdentifier getBlockStateIdentifier(BlockState state) {
		return new ModelIdentifier(Block.REGISTRY.getIdentifier(state.getBlock()), this.getPropertyStateString(state.getPropertyMap()));
	}
}
