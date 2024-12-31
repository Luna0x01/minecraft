package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.PlayerEntity;
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
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		blockPos = blockPos.offset(direction);
		if (!playerEntity.canModify(blockPos, direction, itemStack)) {
			return ActionResult.FAIL;
		} else {
			if (world.getBlockState(blockPos).getMaterial() == Material.AIR) {
				world.method_11486(playerEntity, blockPos, Sounds.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
				world.setBlockState(blockPos, Blocks.FIRE.getDefaultState(), 11);
			}

			itemStack.damage(1, playerEntity);
			return ActionResult.SUCCESS;
		}
	}
}
