package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.BlockState;

public class class_3894 implements class_3845 {
	final BlockState field_19249;
	final List<BlockState> field_19250;
	final List<BlockState> field_19251;
	final List<BlockState> field_19252;

	public class_3894(BlockState blockState, BlockState[] blockStates, BlockState[] blockStates2, BlockState[] blockStates3) {
		this.field_19249 = blockState;
		this.field_19250 = Lists.newArrayList(blockStates);
		this.field_19251 = Lists.newArrayList(blockStates2);
		this.field_19252 = Lists.newArrayList(blockStates3);
	}
}
