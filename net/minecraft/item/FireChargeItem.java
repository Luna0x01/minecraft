package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem extends Item {
	public FireChargeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		boolean bl = false;
		if (blockState.getBlock() == Blocks.field_17350) {
			if (!(Boolean)blockState.get(CampfireBlock.LIT) && !(Boolean)blockState.get(CampfireBlock.WATERLOGGED)) {
				this.playUseSound(world, blockPos);
				world.setBlockState(blockPos, blockState.with(CampfireBlock.LIT, Boolean.valueOf(true)));
				bl = true;
			}
		} else {
			blockPos = blockPos.offset(itemUsageContext.getSide());
			if (world.getBlockState(blockPos).isAir()) {
				this.playUseSound(world, blockPos);
				world.setBlockState(blockPos, ((FireBlock)Blocks.field_10036).getStateForPosition(world, blockPos));
				bl = true;
			}
		}

		if (bl) {
			itemUsageContext.getStack().decrement(1);
			return ActionResult.field_5812;
		} else {
			return ActionResult.field_5814;
		}
	}

	private void playUseSound(World world, BlockPos blockPos) {
		world.playSound(null, blockPos, SoundEvents.field_15013, SoundCategory.field_15245, 1.0F, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F);
	}
}
