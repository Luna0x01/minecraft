package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FireChargeItem extends Item {
	public FireChargeItem() {
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		if (world.isClient) {
			return true;
		} else {
			pos = pos.offset(direction);
			if (!player.canModify(pos, direction, itemStack)) {
				return false;
			} else {
				if (world.getBlockState(pos).getBlock().getMaterial() == Material.AIR) {
					world.playSound(
						(double)pos.getX() + 0.5,
						(double)pos.getY() + 0.5,
						(double)pos.getZ() + 0.5,
						"item.fireCharge.use",
						1.0F,
						(RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F
					);
					world.setBlockState(pos, Blocks.FIRE.getDefaultState());
				}

				if (!player.abilities.creativeMode) {
					itemStack.count--;
				}

				return true;
			}
		}
	}
}
