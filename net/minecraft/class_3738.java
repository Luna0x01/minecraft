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

public class class_3738 extends class_3734 {
	protected class_3738(Block.Builder builder) {
		super(SkullBlock.class_3723.WITHER_SKELETON, builder);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		Blocks.WITHER_SKELETON_SKULL.onPlaced(world, pos, state, placer, itemStack);
	}
}
