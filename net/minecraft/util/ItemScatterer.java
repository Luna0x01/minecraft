package net.minecraft.util;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemScatterer {
	private static final Random RANDOM = new Random();

	public static void spawn(World world, BlockPos pos, Inventory inventory) {
		spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), inventory);
	}

	public static void spawn(World world, Entity entity, Inventory inventory) {
		spawn(world, entity.x, entity.y, entity.z, inventory);
	}

	private static void spawn(World world, double x, double y, double z, Inventory inventory) {
		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack = inventory.getInvStack(i);
			if (!itemStack.isEmpty()) {
				spawnItemStack(world, x, y, z, itemStack);
			}
		}
	}

	public static void spawnItemStack(World world, double x, double y, double z, ItemStack itemStack) {
		float f = 0.75F;
		float g = 0.125F;
		float h = RANDOM.nextFloat() * 0.75F + 0.125F;
		float i = RANDOM.nextFloat() * 0.75F;
		float j = RANDOM.nextFloat() * 0.75F + 0.125F;

		while (!itemStack.isEmpty()) {
			ItemEntity itemEntity = new ItemEntity(world, x + (double)h, y + (double)i, z + (double)j, itemStack.split(RANDOM.nextInt(21) + 10));
			float k = 0.05F;
			itemEntity.velocityX = RANDOM.nextGaussian() * 0.05F;
			itemEntity.velocityY = RANDOM.nextGaussian() * 0.05F + 0.2F;
			itemEntity.velocityZ = RANDOM.nextGaussian() * 0.05F;
			world.method_3686(itemEntity);
		}
	}
}
