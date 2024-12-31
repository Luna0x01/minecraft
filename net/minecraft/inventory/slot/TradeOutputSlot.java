package net.minecraft.inventory.slot;

import net.minecraft.entity.data.Trader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderInventory;

public class TradeOutputSlot extends Slot {
	private final TraderInventory traderInventory;
	private PlayerEntity player;
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
			this.amount = this.amount + Math.min(amount, this.getStack().count);
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
	public void onTakeItem(PlayerEntity player, ItemStack stack) {
		this.onCrafted(stack);
		TradeOffer tradeOffer = this.traderInventory.getTradeOffer();
		if (tradeOffer != null) {
			ItemStack itemStack = this.traderInventory.getInvStack(0);
			ItemStack itemStack2 = this.traderInventory.getInvStack(1);
			if (this.depleteBuyItems(tradeOffer, itemStack, itemStack2) || this.depleteBuyItems(tradeOffer, itemStack2, itemStack)) {
				this.trader.trade(tradeOffer);
				player.incrementStat(Stats.TRADED_WITH_VILLAGER);
				if (itemStack != null && itemStack.count <= 0) {
					itemStack = null;
				}

				if (itemStack2 != null && itemStack2.count <= 0) {
					itemStack2 = null;
				}

				this.traderInventory.setInvStack(0, itemStack);
				this.traderInventory.setInvStack(1, itemStack2);
			}
		}
	}

	private boolean depleteBuyItems(TradeOffer offer, ItemStack first, ItemStack second) {
		ItemStack itemStack = offer.getFirstStack();
		ItemStack itemStack2 = offer.getSecondStack();
		if (first != null && first.getItem() == itemStack.getItem()) {
			if (itemStack2 != null && second != null && itemStack2.getItem() == second.getItem()) {
				first.count = first.count - itemStack.count;
				second.count = second.count - itemStack2.count;
				return true;
			}

			if (itemStack2 == null && second == null) {
				first.count = first.count - itemStack.count;
				return true;
			}
		}

		return false;
	}
}
