package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EmptyMapItem extends NetworkSyncedItem {
	protected EmptyMapItem() {
		this.setItemGroup(ItemGroup.MISC);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = FilledMapItem.method_13663(world, player.x, player.z, (byte)0, true, false);
		ItemStack itemStack2 = player.getStackInHand(hand);
		itemStack2.decrement(1);
		if (itemStack2.isEmpty()) {
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			if (!player.inventory.insertStack(itemStack.copy())) {
				player.dropItem(itemStack, false);
			}

			player.incrementStat(Stats.used(this));
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack2);
		}
	}
}
