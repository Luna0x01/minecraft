package net.minecraft.item;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class FlintAndSteelItem extends Item {
	public FlintAndSteelItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		PlayerEntity playerEntity = itemUsageContext.getPlayer();
		IWorld iWorld = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos().offset(itemUsageContext.method_16151());
		if (method_16064(iWorld, blockPos)) {
			iWorld.playSound(playerEntity, blockPos, Sounds.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
			BlockState blockState = ((FireBlock)Blocks.FIRE).method_16678(iWorld, blockPos);
			iWorld.setBlockState(blockPos, blockState, 11);
			ItemStack itemStack = itemUsageContext.getItemStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			if (playerEntity != null) {
				itemStack.damage(1, playerEntity);
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.FAIL;
		}
	}

	public static boolean method_16064(IWorld iWorld, BlockPos blockPos) {
		BlockState blockState = ((FireBlock)Blocks.FIRE).method_16678(iWorld, blockPos);
		boolean bl = false;

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			if (iWorld.getBlockState(blockPos.offset(direction)).getBlock() == Blocks.OBSIDIAN
				&& ((NetherPortalBlock)Blocks.NETHER_PORTAL).method_16705(iWorld, blockPos) != null) {
				bl = true;
			}
		}

		return iWorld.method_8579(blockPos) && (blockState.canPlaceAt(iWorld, blockPos) || bl);
	}
}
