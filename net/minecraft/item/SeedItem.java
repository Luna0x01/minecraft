package net.minecraft.item;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class SeedItem extends Item {
	private final BlockState field_17375;

	public SeedItem(Block block, Item.Settings settings) {
		super(settings);
		this.field_17375 = block.getDefaultState();
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		IWorld iWorld = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos().up();
		if (itemUsageContext.method_16151() == Direction.UP && iWorld.method_8579(blockPos) && this.field_17375.canPlaceAt(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, this.field_17375, 11);
			ItemStack itemStack = itemUsageContext.getItemStack();
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			if (playerEntity instanceof ServerPlayerEntity) {
				AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			itemStack.decrement(1);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}
}
