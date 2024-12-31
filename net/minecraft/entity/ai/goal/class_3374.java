package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class class_3374 extends FollowOwnerGoal {
	public class_3374(TameableEntity tameableEntity, double d, float f, float g) {
		super(tameableEntity, d, f, g);
	}

	@Override
	protected boolean method_15080(int i, int j, int k, int l, int m) {
		BlockState blockState = this.field_16856.getBlockState(new BlockPos(i + l, k - 1, j + m));
		return (blockState.method_16913() || blockState.isIn(BlockTags.LEAVES))
			&& this.field_16856.method_8579(new BlockPos(i + l, k, j + m))
			&& this.field_16856.method_8579(new BlockPos(i + l, k + 1, j + m));
	}
}
