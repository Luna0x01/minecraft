package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldView;

public class RuleStructureProcessor extends StructureProcessor {
	public static final Codec<RuleStructureProcessor> CODEC = StructureProcessorRule.CODEC
		.listOf()
		.fieldOf("rules")
		.xmap(RuleStructureProcessor::new, ruleStructureProcessor -> ruleStructureProcessor.rules)
		.codec();
	private final ImmutableList<StructureProcessorRule> rules;

	public RuleStructureProcessor(List<? extends StructureProcessorRule> rules) {
		this.rules = ImmutableList.copyOf(rules);
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
		Random random = new Random(MathHelper.hashCode(structureBlockInfo2.pos));
		BlockState blockState = world.getBlockState(structureBlockInfo2.pos);
		UnmodifiableIterator var9 = this.rules.iterator();

		while (var9.hasNext()) {
			StructureProcessorRule structureProcessorRule = (StructureProcessorRule)var9.next();
			if (structureProcessorRule.test(structureBlockInfo2.state, blockState, structureBlockInfo.pos, structureBlockInfo2.pos, pivot, random)) {
				return new Structure.StructureBlockInfo(structureBlockInfo2.pos, structureProcessorRule.getOutputState(), structureProcessorRule.getOutputNbt());
			}
		}

		return structureBlockInfo2;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.RULE;
	}
}
