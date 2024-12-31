package net.minecraft.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GlassBottleItem extends Item {
	public GlassBottleItem() {
		this.setItemGroup(ItemGroup.BREWING);
	}

	@Override
	public ItemStack onStartUse(ItemStack stack, World world, PlayerEntity player) {
		BlockHitResult blockHitResult = this.onHit(world, player, true);
		if (blockHitResult == null) {
			return stack;
		} else {
			if (blockHitResult.type == BlockHitResult.Type.BLOCK) {
				BlockPos blockPos = blockHitResult.getBlockPos();
				if (!world.canPlayerModifyAt(player, blockPos)) {
					return stack;
				}

				if (!player.canModify(blockPos.offset(blockHitResult.direction), blockHitResult.direction, stack)) {
					return stack;
				}

				if (world.getBlockState(blockPos).getBlock().getMaterial() == Material.WATER) {
					stack.count--;
					player.incrementStat(Stats.USED[Item.getRawId(this)]);
					if (stack.count <= 0) {
						return new ItemStack(Items.POTION);
					}

					if (!player.inventory.insertStack(new ItemStack(Items.POTION))) {
						player.dropItem(new ItemStack(Items.POTION, 1, 0), false);
					}
				}
			}

			return stack;
		}
	}
}
