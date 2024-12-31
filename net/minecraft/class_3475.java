package net.minecraft;

import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class class_3475 extends Goal {
	private final PathAwareEntity field_16887;

	public class_3475(PathAwareEntity pathAwareEntity) {
		this.field_16887 = pathAwareEntity;
	}

	@Override
	public boolean canStart() {
		return this.field_16887.onGround && !this.field_16887.world.getFluidState(new BlockPos(this.field_16887)).matches(FluidTags.WATER);
	}

	@Override
	public void start() {
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : BlockPos.Mutable.mutableIterate(
			MathHelper.floor(this.field_16887.x - 2.0),
			MathHelper.floor(this.field_16887.y - 2.0),
			MathHelper.floor(this.field_16887.z - 2.0),
			MathHelper.floor(this.field_16887.x + 2.0),
			MathHelper.floor(this.field_16887.y),
			MathHelper.floor(this.field_16887.z + 2.0)
		)) {
			if (this.field_16887.world.getFluidState(blockPos2).matches(FluidTags.WATER)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos != null) {
			this.field_16887.getMotionHelper().moveTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
		}
	}
}
