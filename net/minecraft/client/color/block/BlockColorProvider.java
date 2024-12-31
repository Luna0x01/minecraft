package net.minecraft.client.color.block;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public interface BlockColorProvider {
	int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex);
}
