package net.minecraft.client;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockStateIdentifierMapAccess;
import net.minecraft.client.util.ModelIdentifier;

public class BlockStateMapper {
	private Map<Block, BlockStateIdentifierMapAccess> blockMap = Maps.newIdentityHashMap();
	private Set<Block> blocks = Sets.newIdentityHashSet();

	public void putBlock(Block block, BlockStateIdentifierMapAccess mapper) {
		this.blockMap.put(block, mapper);
	}

	public void putBlocks(Block... blocks) {
		Collections.addAll(this.blocks, blocks);
	}

	public Map<BlockState, ModelIdentifier> getBlockStateMap() {
		Map<BlockState, ModelIdentifier> map = Maps.newIdentityHashMap();

		for (Block block : Block.REGISTRY) {
			if (!this.blocks.contains(block)) {
				map.putAll(((BlockStateIdentifierMapAccess)Objects.firstNonNull(this.blockMap.get(block), new DefaultBlockStateMap())).addBlock(block));
			}
		}

		return map;
	}
}
