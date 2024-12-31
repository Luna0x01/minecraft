package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface class_2741 {
	boolean onSyncedBlockEvent(World world, BlockPos pos, int type, int data);

	void method_11707(World world, BlockPos blockPos, Block block);
}
