package net.minecraft.item;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CarrotOnAStickItem extends Item {
	public CarrotOnAStickItem() {
		this.setItemGroup(ItemGroup.TRANSPORTATION);
		this.setMaxCount(1);
		this.setMaxDamage(25);
	}

	@Override
	public boolean isHandheld() {
		return true;
	}

	@Override
	public boolean shouldRotate() {
		return true;
	}

	@Override
	public TypedActionResult<ItemStack> method_11373(ItemStack itemStack, World world, PlayerEntity playerEntity, Hand hand) {
		if (playerEntity.hasMount() && playerEntity.getVehicle() instanceof PigEntity) {
			PigEntity pigEntity = (PigEntity)playerEntity.getVehicle();
			if (itemStack.getMaxDamage() - itemStack.getData() >= 7 && pigEntity.method_13117()) {
				itemStack.damage(7, playerEntity);
				if (itemStack.count == 0) {
					ItemStack itemStack2 = new ItemStack(Items.FISHING_ROD);
					itemStack2.setNbt(itemStack.getNbt());
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack2);
				}

				return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
			}
		}

		playerEntity.incrementStat(Stats.used(this));
		return new TypedActionResult<>(ActionResult.PASS, itemStack);
	}
}
