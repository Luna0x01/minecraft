package net.minecraft.client;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface BlockColorable {
	int method_12155(BlockState blockState, @Nullable BlockView blockView, @Nullable BlockPos blockPos, int i);
}
