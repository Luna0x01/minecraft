package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockStateIdentifierMapAccess;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

public class BlockStateMapper {
	private final Map<Block, BlockStateIdentifierMapAccess> blockMap = Maps.newIdentityHashMap();
	private final Set<Block> blocks = Sets.newIdentityHashSet();

	public void putBlock(Block block, BlockStateIdentifierMapAccess mapper) {
		this.blockMap.put(block, mapper);
	}

	public void putBlocks(Block... blocks) {
		Collections.addAll(this.blocks, blocks);
	}

	public Map<BlockState, ModelIdentifier> getBlockStateMap() {
		Map<BlockState, ModelIdentifier> map = Maps.newIdentityHashMap();

		for (Block block : Block.REGISTRY) {
			map.putAll(this.method_12404(block));
		}

		return map;
	}

	public Set<Identifier> method_12403(Block block) {
		if (this.blocks.contains(block)) {
			return Collections.emptySet();
		} else {
			BlockStateIdentifierMapAccess blockStateIdentifierMapAccess = (BlockStateIdentifierMapAccess)this.blockMap.get(block);
			if (blockStateIdentifierMapAccess == null) {
				return Collections.singleton(Block.REGISTRY.getIdentifier(block));
			} else {
				Set<Identifier> set = Sets.newHashSet();

				for (ModelIdentifier modelIdentifier : blockStateIdentifierMapAccess.addBlock(block).values()) {
					set.add(new Identifier(modelIdentifier.getNamespace(), modelIdentifier.getPath()));
				}

				return set;
			}
		}
	}

	public Map<BlockState, ModelIdentifier> method_12404(Block block) {
		return this.blocks.contains(block)
			? Collections.emptyMap()
			: ((BlockStateIdentifierMapAccess)MoreObjects.firstNonNull(this.blockMap.get(block), new DefaultBlockStateMap())).addBlock(block);
	}
}
