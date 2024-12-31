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

public class FireChargeItem extends Item {
	public FireChargeItem() {
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public ActionResult method_3355(
		ItemStack itemStack, PlayerEntity playerEntity, World world, BlockPos blockPos, Hand hand, Direction direction, float f, float g, float h
	) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			blockPos = blockPos.offset(direction);
			if (!playerEntity.canModify(blockPos, direction, itemStack)) {
				return ActionResult.FAIL;
			} else {
				if (world.getBlockState(blockPos).getMaterial() == Material.AIR) {
					world.method_11486(null, blockPos, Sounds.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F);
					world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
				}

				if (!playerEntity.abilities.creativeMode) {
					itemStack.count--;
				}

				return ActionResult.SUCCESS;
			}
		}
	}
}
