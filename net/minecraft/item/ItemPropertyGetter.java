package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public interface ItemPropertyGetter {
	float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity);
}
