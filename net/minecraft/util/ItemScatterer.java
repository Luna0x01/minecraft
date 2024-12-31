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
		float f = RANDOM.nextFloat() * 0.8F + 0.1F;
		float g = RANDOM.nextFloat() * 0.8F + 0.1F;
		float h = RANDOM.nextFloat() * 0.8F + 0.1F;

		while (!itemStack.isEmpty()) {
			ItemEntity itemEntity = new ItemEntity(world, x + (double)f, y + (double)g, z + (double)h, itemStack.split(RANDOM.nextInt(21) + 10));
			float i = 0.05F;
			itemEntity.velocityX = RANDOM.nextGaussian() * 0.05F;
			itemEntity.velocityY = RANDOM.nextGaussian() * 0.05F + 0.2F;
			itemEntity.velocityZ = RANDOM.nextGaussian() * 0.05F;
			world.spawnEntity(itemEntity);
		}
	}
}
