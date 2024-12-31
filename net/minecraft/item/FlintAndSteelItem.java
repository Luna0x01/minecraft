package net.minecraft.item;

import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.sound.Sounds;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FlintAndSteelItem extends Item {
	public FlintAndSteelItem() {
		this.maxCount = 1;
		this.setMaxDamage(64);
		this.setItemGroup(ItemGroup.TOOLS);
	}

	@Override
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		pos = pos.offset(direction);
		ItemStack itemStack = player.getStackInHand(hand);
		if (!player.canModify(pos, direction, itemStack)) {
			return ActionResult.FAIL;
		} else {
			if (world.getBlockState(pos).getMaterial() == Material.AIR) {
				world.method_11486(player, pos, Sounds.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
				world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
			}

			if (player instanceof ServerPlayerEntity) {
				AchievementsAndCriterions.field_16352.method_14369((ServerPlayerEntity)player, pos, itemStack);
			}

			itemStack.damage(1, player);
			return ActionResult.SUCCESS;
		}
	}
}
