package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class StewItem extends FoodItem {
	public StewItem(int i) {
		super(i, false);
		this.setMaxCount(1);
	}

	@Override
	public ItemStack onFinishUse(ItemStack stack, World world, PlayerEntity player) {
		super.onFinishUse(stack, world, player);
		return new ItemStack(Items.BOWL);
	}
}
