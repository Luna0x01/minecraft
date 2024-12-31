package net.minecraft.inventory.slot;

import net.minecraft.entity.data.Trader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderInventory;

public class TradeOutputSlot extends Slot {
	private final TraderInventory traderInventory;
	private final PlayerEntity player;
	private int amount;
	private final Trader trader;

	public TradeOutputSlot(PlayerEntity playerEntity, Trader trader, TraderInventory traderInventory, int i, int j, int k) {
		super(traderInventory, i, j, k);
		this.player = playerEntity;
		this.trader = trader;
		this.traderInventory = traderInventory;
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
	protected void onCrafted(ItemStack stack) {
		stack.onCraft(this.player.world, this.player, this.amount);
		this.amount = 0;
	}

	@Override
	public ItemStack method_3298(PlayerEntity playerEntity, ItemStack itemStack) {
		this.onCrafted(itemStack);
		TradeOffer tradeOffer = this.traderInventory.getTradeOffer();
		if (tradeOffer != null) {
			ItemStack itemStack2 = this.traderInventory.getInvStack(0);
			ItemStack itemStack3 = this.traderInventory.getInvStack(1);
			if (this.depleteBuyItems(tradeOffer, itemStack2, itemStack3) || this.depleteBuyItems(tradeOffer, itemStack3, itemStack2)) {
				this.trader.trade(tradeOffer);
				playerEntity.incrementStat(Stats.TRADED_WITH_VILLAGER);
				this.traderInventory.setInvStack(0, itemStack2);
				this.traderInventory.setInvStack(1, itemStack3);
			}
		}

		return itemStack;
	}

	private boolean depleteBuyItems(TradeOffer offer, ItemStack first, ItemStack second) {
		ItemStack itemStack = offer.getFirstStack();
		ItemStack itemStack2 = offer.getSecondStack();
		if (first.getItem() == itemStack.getItem() && first.getCount() >= itemStack.getCount()) {
			if (!itemStack2.isEmpty() && !second.isEmpty() && itemStack2.getItem() == second.getItem() && second.getCount() >= itemStack2.getCount()) {
				first.decrement(itemStack.getCount());
				second.decrement(itemStack2.getCount());
				return true;
			}

			if (itemStack2.isEmpty() && second.isEmpty()) {
				first.decrement(itemStack.getCount());
				return true;
			}
		}

		return false;
	}
}
