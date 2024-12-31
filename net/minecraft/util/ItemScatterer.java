package net.minecraft.util;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
			if (itemStack != null) {
				spawnItemStack(world, x, y, z, itemStack);
			}
		}
	}

	private static void spawnItemStack(World world, double x, double y, double z, ItemStack itemStack) {
		float f = RANDOM.nextFloat() * 0.8F + 0.1F;
		float g = RANDOM.nextFloat() * 0.8F + 0.1F;
		float h = RANDOM.nextFloat() * 0.8F + 0.1F;

		while (itemStack.count > 0) {
			int i = RANDOM.nextInt(21) + 10;
			if (i > itemStack.count) {
				i = itemStack.count;
			}

			itemStack.count -= i;
			ItemEntity itemEntity = new ItemEntity(world, x + (double)f, y + (double)g, z + (double)h, new ItemStack(itemStack.getItem(), i, itemStack.getData()));
			if (itemStack.hasNbt()) {
				itemEntity.getItemStack().setNbt((NbtCompound)itemStack.getNbt().copy());
			}

			float j = 0.05F;
			itemEntity.velocityX = RANDOM.nextGaussian() * (double)j;
			itemEntity.velocityY = RANDOM.nextGaussian() * (double)j + 0.2F;
			itemEntity.velocityZ = RANDOM.nextGaussian() * (double)j;
			world.spawnEntity(itemEntity);
		}
	}
}
