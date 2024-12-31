package net.minecraft.block.entity;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ScreenHandlerLock;
import net.minecraft.screen.NamedScreenHandlerFactory;

public interface LockableScreenHandlerFactory extends Inventory, NamedScreenHandlerFactory {
	boolean hasLock();

	void setLock(ScreenHandlerLock lock);

	ScreenHandlerLock getLock();
}
