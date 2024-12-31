package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.world.World;

public class SpectralArrowItem extends ArrowItem {
	@Override
	public AbstractArrowEntity method_11358(World world, ItemStack itemStack, LivingEntity livingEntity) {
		return new SpectralArrowEntity(world, livingEntity);
	}
}
