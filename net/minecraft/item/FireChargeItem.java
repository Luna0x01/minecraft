package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.sound.Sounds;
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
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockPos blockPos = itemUsageContext.getBlockPos().offset(itemUsageContext.method_16151());
			if (world.getBlockState(blockPos).isAir()) {
				world.playSound(null, blockPos, Sounds.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F);
				world.setBlockState(blockPos, ((FireBlock)Blocks.FIRE).method_16678(world, blockPos));
			}

			itemUsageContext.getItemStack().decrement(1);
			return ActionResult.SUCCESS;
		}
	}
}
