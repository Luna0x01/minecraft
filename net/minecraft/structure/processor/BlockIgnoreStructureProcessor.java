package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class BlockIgnoreStructureProcessor extends StructureProcessor {
	public static final Codec<BlockIgnoreStructureProcessor> CODEC = BlockState.CODEC
		.xmap(AbstractBlock.AbstractBlockState::getBlock, Block::getDefaultState)
		.listOf()
		.fieldOf("blocks")
		.xmap(BlockIgnoreStructureProcessor::new, blockIgnoreStructureProcessor -> blockIgnoreStructureProcessor.blocks)
		.codec();
	public static final BlockIgnoreStructureProcessor IGNORE_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
	public static final BlockIgnoreStructureProcessor IGNORE_AIR = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.AIR));
	public static final BlockIgnoreStructureProcessor IGNORE_AIR_AND_STRUCTURE_BLOCKS = new BlockIgnoreStructureProcessor(
		ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK)
	);
	private final ImmutableList<Block> blocks;

	public BlockIgnoreStructureProcessor(List<Block> blocks) {
		this.blocks = ImmutableList.copyOf(blocks);
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData data
	) {
		return this.blocks.contains(structureBlockInfo2.state.getBlock()) ? null : structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.BLOCK_IGNORE;
	}
}
