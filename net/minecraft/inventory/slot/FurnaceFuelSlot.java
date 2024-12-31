package net.minecraft.inventory.slot;

import javax.annotation.Nullable;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FurnaceFuelSlot extends Slot {
	public FurnaceFuelSlot(Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
	}

	@Override
	public boolean canInsert(@Nullable ItemStack stack) {
		return FurnaceBlockEntity.isFuel(stack) || isBucket(stack);
	}

	@Override
	public int getMaxStackAmount(ItemStack stack) {
		return isBucket(stack) ? 1 : super.getMaxStackAmount(stack);
	}

	public static boolean isBucket(ItemStack stack) {
		return stack != null && stack.getItem() != null && stack.getItem() == Items.BUCKET;
	}
}
