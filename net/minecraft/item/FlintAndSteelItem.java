package net.minecraft.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
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
	public boolean use(ItemStack itemStack, PlayerEntity player, World world, BlockPos pos, Direction direction, float facingX, float facingY, float facingZ) {
		pos = pos.offset(direction);
		if (!player.canModify(pos, direction, itemStack)) {
			return false;
		} else {
			if (world.getBlockState(pos).getBlock().getMaterial() == Material.AIR) {
				world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "fire.ignite", 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
			}

			itemStack.damage(1, player);
			return true;
		}
	}
}
