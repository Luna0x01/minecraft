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

public class CropItem extends FoodItem {
	private final BlockState field_17374;

	public CropItem(int i, float f, Block block, Item.Settings settings) {
		super(i, f, false, settings);
		this.field_17374 = block.getDefaultState();
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		IWorld iWorld = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos().up();
		if (itemUsageContext.method_16151() == Direction.UP && iWorld.method_8579(blockPos) && this.field_17374.canPlaceAt(iWorld, blockPos)) {
			iWorld.setBlockState(blockPos, this.field_17374, 11);
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			itemStack.decrement(1);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}
}
