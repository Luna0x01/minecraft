package net.minecraft.client;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RenderBlockView;

public interface BlockColorable {
	int getColor(BlockState state, @Nullable RenderBlockView renderView, @Nullable BlockPos pos, int i);
}
