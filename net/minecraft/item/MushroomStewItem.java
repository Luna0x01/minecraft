package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class MushroomStewItem extends Item {
	public MushroomStewItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ItemStack finishUsing(ItemStack itemStack, World world, LivingEntity livingEntity) {
		ItemStack itemStack2 = super.finishUsing(itemStack, world, livingEntity);
		return livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode ? itemStack2 : new ItemStack(Items.field_8428);
	}
}
