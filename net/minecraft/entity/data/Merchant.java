package net.minecraft.entity.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderOfferList;

public class Merchant implements Trader {
	private final TraderInventory inventory;
	private final PlayerEntity player;
	private TraderOfferList list;
	private final Text name;

	public Merchant(PlayerEntity playerEntity, Text text) {
		this.player = playerEntity;
		this.name = text;
		this.inventory = new TraderInventory(playerEntity, this);
	}

	@Override
	public PlayerEntity getCurrentCustomer() {
		return this.player;
	}

	@Override
	public void setCurrentCustomer(PlayerEntity player) {
	}

	@Override
	public TraderOfferList getOffers(PlayerEntity player) {
		return this.list;
	}

	@Override
	public void setTraderOfferList(TraderOfferList list) {
		this.list = list;
	}

	@Override
	public void trade(TradeOffer offer) {
		offer.use();
	}

	@Override
	public void method_5501(ItemStack stack) {
	}

	@Override
	public Text getName() {
		return (Text)(this.name != null ? this.name : new TranslatableText("entity.Villager.name"));
	}
}
