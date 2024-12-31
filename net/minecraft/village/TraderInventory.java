package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;

public class TraderInventory implements Inventory {
	private final Trader trader;
	private final DefaultedList<ItemStack> field_15104 = DefaultedList.ofSize(3, ItemStack.EMPTY);
	private final PlayerEntity player;
	private TradeOffer tradeOffer;
	private int field_4137;

	public TraderInventory(PlayerEntity playerEntity, Trader trader) {
		this.player = playerEntity;
		this.trader = trader;
	}

	@Override
	public int getInvSize() {
		return this.field_15104.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.field_15104) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return this.field_15104.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack itemStack = this.field_15104.get(slot);
		if (slot == 2 && !itemStack.isEmpty()) {
			return class_2960.method_13926(this.field_15104, slot, itemStack.getCount());
		} else {
			ItemStack itemStack2 = class_2960.method_13926(this.field_15104, slot, amount);
			if (!itemStack2.isEmpty() && this.method_3282(slot)) {
				this.updateRecipes();
			}

			return itemStack2;
		}
	}

	private boolean method_3282(int slot) {
		return slot == 0 || slot == 1;
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_13925(this.field_15104, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		this.field_15104.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getInvMaxStackAmount()) {
			stack.setCount(this.getInvMaxStackAmount());
		}

		if (this.method_3282(slot)) {
			this.updateRecipes();
		}
	}

	@Override
	public Text method_15540() {
		return new TranslatableText("mob.villager");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Nullable
	@Override
	public Text method_15541() {
		return null;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.trader.getCurrentCustomer() == player;
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
	}

	@Override
	public void onInvClose(PlayerEntity player) {
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public void markDirty() {
		this.updateRecipes();
	}

	public void updateRecipes() {
		this.tradeOffer = null;
		ItemStack itemStack = this.field_15104.get(0);
		ItemStack itemStack2 = this.field_15104.get(1);
		if (itemStack.isEmpty()) {
			itemStack = itemStack2;
			itemStack2 = ItemStack.EMPTY;
		}

		if (itemStack.isEmpty()) {
			this.setInvStack(2, ItemStack.EMPTY);
		} else {
			TraderOfferList traderOfferList = this.trader.getOffers(this.player);
			if (traderOfferList != null) {
				TradeOffer tradeOffer = traderOfferList.getValidRecipe(itemStack, itemStack2, this.field_4137);
				if (tradeOffer != null && !tradeOffer.isDisabled()) {
					this.tradeOffer = tradeOffer;
					this.setInvStack(2, tradeOffer.getResult().copy());
				} else if (!itemStack2.isEmpty()) {
					tradeOffer = traderOfferList.getValidRecipe(itemStack2, itemStack, this.field_4137);
					if (tradeOffer != null && !tradeOffer.isDisabled()) {
						this.tradeOffer = tradeOffer;
						this.setInvStack(2, tradeOffer.getResult().copy());
					} else {
						this.setInvStack(2, ItemStack.EMPTY);
					}
				} else {
					this.setInvStack(2, ItemStack.EMPTY);
				}
			}

			this.trader.method_5501(this.getInvStack(2));
		}
	}

	public TradeOffer getTradeOffer() {
		return this.tradeOffer;
	}

	public void setRecipeIndex(int i) {
		this.field_4137 = i;
		this.updateRecipes();
	}

	@Override
	public int getProperty(int key) {
		return 0;
	}

	@Override
	public void setProperty(int id, int value) {
	}

	@Override
	public int getProperties() {
		return 0;
	}

	@Override
	public void clear() {
		this.field_15104.clear();
	}
}
