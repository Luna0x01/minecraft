package net.minecraft.item;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CarrotOnAStickItem extends Item {
	public CarrotOnAStickItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> method_13649(World world, PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (world.isClient) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			if (player.hasMount() && player.getVehicle() instanceof PigEntity) {
				PigEntity pigEntity = (PigEntity)player.getVehicle();
				if (itemStack.getMaxDamage() - itemStack.getDamage() >= 7 && pigEntity.method_13117()) {
					itemStack.damage(7, player);
					if (itemStack.isEmpty()) {
						ItemStack itemStack2 = new ItemStack(Items.FISHING_ROD);
						itemStack2.setNbt(itemStack.getNbt());
						return new TypedActionResult<>(ActionResult.SUCCESS, itemStack2);
					}

					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}

			player.method_15932(Stats.USED.method_21429(this));
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		}
	}
}
