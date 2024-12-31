package net.minecraft;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public interface class_3602 {
	boolean setBlockState(BlockPos blockPos, BlockState blockState, int i);

	boolean method_3686(Entity entity);

	boolean method_8553(BlockPos blockPos);

	void method_16403(LightType lightType, BlockPos blockPos, int i);

	boolean method_8535(BlockPos blockPos, boolean bl);
}
