package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class LavaSubmergedBlockStructureProcessor extends StructureProcessor {
	public static final Codec<LavaSubmergedBlockStructureProcessor> CODEC = Codec.unit(() -> LavaSubmergedBlockStructureProcessor.INSTANCE);
	public static final LavaSubmergedBlockStructureProcessor INSTANCE = new LavaSubmergedBlockStructureProcessor();

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
		BlockPos blockPos = structureBlockInfo2.pos;
		boolean bl = world.getBlockState(blockPos).isOf(Blocks.LAVA);
		return bl && !Block.isShapeFullCube(structureBlockInfo2.state.getOutlineShape(world, blockPos))
			? new Structure.StructureBlockInfo(blockPos, Blocks.LAVA.getDefaultState(), structureBlockInfo2.nbt)
			: structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
	}
}
