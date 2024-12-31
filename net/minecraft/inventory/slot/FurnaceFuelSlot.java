package net.minecraft.inventory.slot;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FurnaceFuelSlot extends Slot {
	public FurnaceFuelSlot(Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return FurnaceBlockEntity.isFuel(stack) || isBucket(stack);
	}

	@Override
	public int getMaxStackAmount(ItemStack stack) {
		return isBucket(stack) ? 1 : super.getMaxStackAmount(stack);
	}

	public static boolean isBucket(ItemStack stack) {
		return stack.getItem() == Items.BUCKET;
	}
}
