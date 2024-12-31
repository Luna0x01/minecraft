package net.minecraft.entity.data;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;

public interface Trader {
	void setCurrentCustomer(PlayerEntity player);

	PlayerEntity getCurrentCustomer();

	@Nullable
	TraderOfferList getOffers(PlayerEntity player);

	void setTraderOfferList(@Nullable TraderOfferList list);

	void trade(TradeOffer offer);

	void method_5501(ItemStack stack);

	Text getName();

	World method_13682();

	BlockPos method_13683();
}
