package net.minecraft.entity.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;

public interface Trader {
	void setCurrentCustomer(PlayerEntity player);

	PlayerEntity getCurrentCustomer();

	TraderOfferList getOffers(PlayerEntity player);

	void setTraderOfferList(TraderOfferList list);

	void trade(TradeOffer offer);

	void method_5501(ItemStack stack);

	Text getName();
}
