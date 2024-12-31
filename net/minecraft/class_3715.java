package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SkullBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class class_3715 extends class_3734 {
	protected class_3715(Block.Builder builder) {
		super(SkullBlock.class_3723.PLAYER, builder);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		Blocks.PLAYER_HEAD.onPlaced(world, pos, state, placer, itemStack);
	}
}
