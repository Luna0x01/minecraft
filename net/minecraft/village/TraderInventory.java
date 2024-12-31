package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.entity.data.Trader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TraderInventory implements Inventory {
	private final Trader trader;
	private final ItemStack[] inventory = new ItemStack[3];
	private final PlayerEntity player;
	private TradeOffer tradeOffer;
	private int field_4137;

	public TraderInventory(PlayerEntity playerEntity, Trader trader) {
		this.player = playerEntity;
		this.trader = trader;
	}

	@Override
	public int getInvSize() {
		return this.inventory.length;
	}

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		return this.inventory[slot];
	}

	@Nullable
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		if (slot == 2 && this.inventory[slot] != null) {
			return class_2960.method_12933(this.inventory, slot, this.inventory[slot].count);
		} else {
			ItemStack itemStack = class_2960.method_12933(this.inventory, slot, amount);
			if (itemStack != null && this.method_3282(slot)) {
				this.updateRecipes();
			}

			return itemStack;
		}
	}

	private boolean method_3282(int slot) {
		return slot == 0 || slot == 1;
	}

	@Nullable
	@Override
	public ItemStack removeInvStack(int slot) {
		return class_2960.method_12932(this.inventory, slot);
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
		this.inventory[slot] = stack;
		if (stack != null && stack.count > this.getInvMaxStackAmount()) {
			stack.count = this.getInvMaxStackAmount();
		}

		if (this.method_3282(slot)) {
			this.updateRecipes();
		}
	}

	@Override
	public String getTranslationKey() {
		return "mob.villager";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public Text getName() {
		return (Text)(this.hasCustomName() ? new LiteralText(this.getTranslationKey()) : new TranslatableText(this.getTranslationKey()));
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
		ItemStack itemStack = this.inventory[0];
		ItemStack itemStack2 = this.inventory[1];
		if (itemStack == null) {
			itemStack = itemStack2;
			itemStack2 = null;
		}

		if (itemStack == null) {
			this.setInvStack(2, null);
		} else {
			TraderOfferList traderOfferList = this.trader.getOffers(this.player);
			if (traderOfferList != null) {
				TradeOffer tradeOffer = traderOfferList.getValidRecipe(itemStack, itemStack2, this.field_4137);
				if (tradeOffer != null && !tradeOffer.isDisabled()) {
					this.tradeOffer = tradeOffer;
					this.setInvStack(2, tradeOffer.getResult().copy());
				} else if (itemStack2 != null) {
					tradeOffer = traderOfferList.getValidRecipe(itemStack2, itemStack, this.field_4137);
					if (tradeOffer != null && !tradeOffer.isDisabled()) {
						this.tradeOffer = tradeOffer;
						this.setInvStack(2, tradeOffer.getResult().copy());
					} else {
						this.setInvStack(2, null);
					}
				} else {
					this.setInvStack(2, null);
				}
			}
		}

		this.trader.method_5501(this.getInvStack(2));
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
		for (int i = 0; i < this.inventory.length; i++) {
			this.inventory[i] = null;
		}
	}
}
