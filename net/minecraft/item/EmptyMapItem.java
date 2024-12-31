package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EmptyMapItem extends NetworkSyncedItem {
	public EmptyMapItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = FilledMapItem.method_16113(world, MathHelper.floor(player.x), MathHelper.floor(player.z), (byte)0, true, false);
		ItemStack itemStack2 = player.getStackInHand(hand);
		itemStack2.decrement(1);
		if (itemStack2.isEmpty()) {
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		} else {
			if (!player.inventory.insertStack(itemStack.copy())) {
				player.dropItem(itemStack, false);
			}

			player.method_15932(Stats.USED.method_21429(this));
			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack2);
		}
	}
}
