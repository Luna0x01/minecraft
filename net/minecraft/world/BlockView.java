package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

public interface BlockView {
	@Nullable
	BlockEntity getBlockEntity(BlockPos pos);

	BlockState getBlockState(BlockPos pos);

	FluidState getFluidState(BlockPos pos);

	default int getMaxLightLevel() {
		return 15;
	}
}
