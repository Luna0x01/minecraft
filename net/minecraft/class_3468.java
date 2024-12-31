package net.minecraft;

import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.PathAwareEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RenderBlockView;

public class class_3468 extends Goal {
	private final PathAwareEntity field_16845;

	public class_3468(PathAwareEntity pathAwareEntity) {
		this.field_16845 = pathAwareEntity;
		this.setCategoryBits(3);
	}

	@Override
	public boolean canStart() {
		return this.field_16845.getAir() < 140;
	}

	@Override
	public boolean shouldContinue() {
		return this.canStart();
	}

	@Override
	public boolean canStop() {
		return false;
	}

	@Override
	public void start() {
		this.method_15677();
	}

	private void method_15677() {
		Iterable<BlockPos.Mutable> iterable = BlockPos.Mutable.mutableIterate(
			MathHelper.floor(this.field_16845.x - 1.0),
			MathHelper.floor(this.field_16845.y),
			MathHelper.floor(this.field_16845.z - 1.0),
			MathHelper.floor(this.field_16845.x + 1.0),
			MathHelper.floor(this.field_16845.y + 8.0),
			MathHelper.floor(this.field_16845.z + 1.0)
		);
		BlockPos blockPos = null;

		for (BlockPos blockPos2 : iterable) {
			if (this.method_15676(this.field_16845.world, blockPos2)) {
				blockPos = blockPos2;
				break;
			}
		}

		if (blockPos == null) {
			blockPos = new BlockPos(this.field_16845.x, this.field_16845.y + 8.0, this.field_16845.z);
		}

		this.field_16845.getNavigation().startMovingTo((double)blockPos.getX(), (double)(blockPos.getY() + 1), (double)blockPos.getZ(), 1.0);
	}

	@Override
	public void tick() {
		this.method_15677();
		this.field_16845.method_2492(this.field_16845.sidewaysSpeed, this.field_16845.forwardSpeed, this.field_16845.field_16513, 0.02F);
		this.field_16845.move(MovementType.SELF, this.field_16845.velocityX, this.field_16845.velocityY, this.field_16845.velocityZ);
	}

	private boolean method_15676(RenderBlockView renderBlockView, BlockPos blockPos) {
		BlockState blockState = renderBlockView.getBlockState(blockPos);
		return (renderBlockView.getFluidState(blockPos).isEmpty() || blockState.getBlock() == Blocks.BUBBLE_COLUMN)
			&& blockState.canPlaceAtSide(renderBlockView, blockPos, BlockPlacementEnvironment.LAND);
	}
}
