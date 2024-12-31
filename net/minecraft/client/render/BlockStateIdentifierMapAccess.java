package net.minecraft.client.render;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ModelIdentifier;

public interface BlockStateIdentifierMapAccess {
	Map<BlockState, ModelIdentifier> addBlock(Block block);
}
