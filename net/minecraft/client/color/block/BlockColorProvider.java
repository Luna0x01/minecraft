package net.minecraft.client.color.block;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public interface BlockColorProvider {
	int getColor(BlockState blockState, @Nullable BlockRenderView blockRenderView, @Nullable BlockPos blockPos, int i);
}
