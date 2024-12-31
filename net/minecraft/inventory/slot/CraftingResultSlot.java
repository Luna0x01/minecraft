package net.minecraft.inventory.slot;

import net.minecraft.class_3537;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class CraftingResultSlot extends Slot {
	private final CraftingInventory craftingInv;
	private final PlayerEntity player;
	private int amount;

	public CraftingResultSlot(PlayerEntity playerEntity, CraftingInventory craftingInventory, Inventory inventory, int i, int j, int k) {
		super(inventory, i, j, k);
		this.player = playerEntity;
		this.craftingInv = craftingInventory;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return false;
	}

	@Override
	public ItemStack takeStack(int amount) {
		if (this.hasStack()) {
			this.amount = this.amount + Math.min(amount, this.getStack().getCount());
		}

		return super.takeStack(amount);
	}

	@Override
	protected void onCrafted(ItemStack stack, int amount) {
		this.amount += amount;
		this.onCrafted(stack);
	}

	@Override
	protected void method_13644(int i) {
		this.amount += i;
	}

	@Override
	protected void onCrafted(ItemStack stack) {
		if (this.amount > 0) {
			stack.onCraft(this.player.world, this.player, this.amount);
		}

		((class_3537)this.inventory).method_15986(this.player);
		this.amount = 0;
	}

	@Override
	public ItemStack method_3298(PlayerEntity playerEntity, ItemStack itemStack) {
		this.onCrafted(itemStack);
		DefaultedList<ItemStack> defaultedList = playerEntity.world.method_16313().method_16211(this.craftingInv, playerEntity.world);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack2 = this.craftingInv.getInvStack(i);
			ItemStack itemStack3 = defaultedList.get(i);
			if (!itemStack2.isEmpty()) {
				this.craftingInv.takeInvStack(i, 1);
				itemStack2 = this.craftingInv.getInvStack(i);
			}

			if (!itemStack3.isEmpty()) {
				if (itemStack2.isEmpty()) {
					this.craftingInv.setInvStack(i, itemStack3);
				} else if (ItemStack.equalsIgnoreNbt(itemStack2, itemStack3) && ItemStack.equalsIgnoreDamage(itemStack2, itemStack3)) {
					itemStack3.increment(itemStack2.getCount());
					this.craftingInv.setInvStack(i, itemStack3);
				} else if (!this.player.inventory.insertStack(itemStack3)) {
					this.player.dropItem(itemStack3, false);
				}
			}
		}

		return itemStack;
	}
}
