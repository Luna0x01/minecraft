package net.minecraft.screen;

import javax.annotation.Nullable;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.inventory.slot.TradeOutputSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TraderInventory;
import net.minecraft.world.World;

public class VillagerScreenHandler extends ScreenHandler {
	private final Trader trader;
	private final TraderInventory traderInventory;
	private final World world;

	public VillagerScreenHandler(PlayerInventory playerInventory, Trader trader, World world) {
		this.trader = trader;
		this.world = world;
		this.traderInventory = new TraderInventory(playerInventory.player, trader);
		this.addSlot(new Slot(this.traderInventory, 0, 36, 53));
		this.addSlot(new Slot(this.traderInventory, 1, 62, 53));
		this.addSlot(new TradeOutputSlot(playerInventory.player, trader, this.traderInventory, 2, 120, 53));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; k++) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
		}
	}

	public TraderInventory getTraderInventory() {
		return this.traderInventory;
	}

	@Override
	public void addListener(ScreenHandlerListener listener) {
		super.addListener(listener);
	}

	@Override
	public void sendContentUpdates() {
		super.sendContentUpdates();
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.traderInventory.updateRecipes();
		super.onContentChanged(inventory);
	}

	public void setRecipeIndex(int index) {
		this.traderInventory.setRecipeIndex(index);
	}

	@Override
	public void setProperty(int id, int value) {
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.trader.getCurrentCustomer() == player;
	}

	@Nullable
	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = null;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot == 2) {
				if (!this.insertItem(itemStack2, 3, 39, true)) {
					return null;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot != 0 && invSlot != 1) {
				if (invSlot >= 3 && invSlot < 30) {
					if (!this.insertItem(itemStack2, 30, 39, false)) {
						return null;
					}
				} else if (invSlot >= 30 && invSlot < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
					return null;
				}
			} else if (!this.insertItem(itemStack2, 3, 39, false)) {
				return null;
			}

			if (itemStack2.count == 0) {
				slot.setStack(null);
			} else {
				slot.markDirty();
			}

			if (itemStack2.count == itemStack.count) {
				return null;
			}

			slot.onTakeItem(player, itemStack2);
		}

		return itemStack;
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		this.trader.setCurrentCustomer(null);
		super.close(player);
		if (!this.world.isClient) {
			ItemStack itemStack = this.traderInventory.removeInvStack(0);
			if (itemStack != null) {
				player.dropItem(itemStack, false);
			}

			itemStack = this.traderInventory.removeInvStack(1);
			if (itemStack != null) {
				player.dropItem(itemStack, false);
			}
		}
	}
}
