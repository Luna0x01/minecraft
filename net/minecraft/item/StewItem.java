package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class StewItem extends FoodItem {
	public StewItem(int i, Item.Settings settings) {
		super(i, 0.6F, false, settings);
	}

	@Override
	public ItemStack method_3367(ItemStack stack, World world, LivingEntity entity) {
		super.method_3367(stack, world, entity);
		return new ItemStack(Items.BOWL);
	}
}
