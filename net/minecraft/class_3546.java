package net.minecraft;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class class_3546 extends DyeItem {
	public class_3546(DyeColor dyeColor, Item.Settings settings) {
		super(dyeColor, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		ItemPlacementContext itemPlacementContext = new ItemPlacementContext(itemUsageContext);
		if (itemPlacementContext.method_16018()) {
			IWorld iWorld = itemUsageContext.getWorld();
			BlockState blockState = Blocks.COCOA.getPlacementState(itemPlacementContext);
			BlockPos blockPos = itemPlacementContext.getBlockPos();
			if (blockState != null && iWorld.setBlockState(blockPos, blockState, 2)) {
				ItemStack itemStack = itemUsageContext.getItemStack();
				PlayerEntity playerEntity = itemPlacementContext.getPlayer();
				if (playerEntity instanceof ServerPlayerEntity) {
					AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)playerEntity, blockPos, itemStack);
				}

				itemStack.decrement(1);
				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.FAIL;
	}
}
