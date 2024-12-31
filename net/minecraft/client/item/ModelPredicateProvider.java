package net.minecraft.client.item;

import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface ModelPredicateProvider {
	float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity);
}
