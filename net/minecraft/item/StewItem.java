package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class StewItem extends FoodItem {
	public StewItem(int i) {
		super(i, false);
		this.setMaxCount(1);
	}

	@Nullable
	@Override
	public ItemStack method_3367(ItemStack stack, World world, LivingEntity entity) {
		super.method_3367(stack, world, entity);
		return new ItemStack(Items.BOWL);
	}
}
