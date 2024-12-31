package net.minecraft.entity.data;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TraderInventory;
import net.minecraft.village.TraderOfferList;
import net.minecraft.world.World;

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

	@Nullable
	@Override
	public TraderOfferList getOffers(PlayerEntity player) {
		return this.list;
	}

	@Override
	public void setTraderOfferList(@Nullable TraderOfferList list) {
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

	@Override
	public World method_13682() {
		return this.player.world;
	}

	@Override
	public BlockPos method_13683() {
		return new BlockPos(this.player);
	}
}
