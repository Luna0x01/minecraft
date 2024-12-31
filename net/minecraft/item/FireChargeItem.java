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
	public ActionResult use(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction direction, float x, float y, float z) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			pos = pos.offset(direction);
			ItemStack itemStack = player.getStackInHand(hand);
			if (!player.canModify(pos, direction, itemStack)) {
				return ActionResult.FAIL;
			} else {
				if (world.getBlockState(pos).getMaterial() == Material.AIR) {
					world.method_11486(null, pos, Sounds.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F);
					world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				}

				if (!player.abilities.creativeMode) {
					itemStack.decrement(1);
				}

				return ActionResult.SUCCESS;
			}
		}
	}
}
