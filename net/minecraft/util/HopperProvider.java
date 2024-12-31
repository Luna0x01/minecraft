package net.minecraft.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;

public interface HopperProvider extends Inventory {
	World getEntityWorld();

	double getX();

	double getY();

	double getZ();
}
