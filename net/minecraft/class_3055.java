package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;

public class class_3055 extends Slot {
	public class_3055(Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
	}
}
