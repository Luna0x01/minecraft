package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.world.World;

public class ArrowItem extends Item {
	public ArrowItem() {
		this.setItemGroup(ItemGroup.COMBAT);
	}

	public AbstractArrowEntity method_11358(World world, ItemStack itemStack, LivingEntity livingEntity) {
		ArrowEntity arrowEntity = new ArrowEntity(world, livingEntity);
		arrowEntity.initFromStack(itemStack);
		return arrowEntity;
	}
}
