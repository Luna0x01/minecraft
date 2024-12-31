package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.PacketByteBuf;

public class TraderOfferList extends ArrayList<TradeOffer> {
	public TraderOfferList() {
	}

	public TraderOfferList(NbtCompound nbtCompound) {
		this.fromNbt(nbtCompound);
	}

	public TradeOffer getValidRecipe(ItemStack firstBuyItem, ItemStack secondBuyItem, int index) {
		if (index > 0 && index < this.size()) {
			TradeOffer tradeOffer = (TradeOffer)this.get(index);
			return !this.method_8454(firstBuyItem, tradeOffer.getFirstStack())
					|| (secondBuyItem != null || tradeOffer.hasSecondStack())
						&& (!tradeOffer.hasSecondStack() || !this.method_8454(secondBuyItem, tradeOffer.getSecondStack()))
					|| firstBuyItem.count < tradeOffer.getFirstStack().count
					|| tradeOffer.hasSecondStack() && secondBuyItem.count < tradeOffer.getSecondStack().count
				? null
				: tradeOffer;
		} else {
			for (int i = 0; i < this.size(); i++) {
				TradeOffer tradeOffer2 = (TradeOffer)this.get(i);
				if (this.method_8454(firstBuyItem, tradeOffer2.getFirstStack())
					&& firstBuyItem.count >= tradeOffer2.getFirstStack().count
					&& (
						!tradeOffer2.hasSecondStack() && secondBuyItem == null
							|| tradeOffer2.hasSecondStack()
								&& this.method_8454(secondBuyItem, tradeOffer2.getSecondStack())
								&& secondBuyItem.count >= tradeOffer2.getSecondStack().count
					)) {
					return tradeOffer2;
				}
			}

			return null;
		}
	}

	private boolean method_8454(ItemStack itemStack, ItemStack itemStack2) {
		return ItemStack.equalsIgnoreNbt(itemStack, itemStack2)
			&& (!itemStack2.hasNbt() || itemStack.hasNbt() && NbtHelper.matches(itemStack2.getNbt(), itemStack.getNbt(), false));
	}

	public void toPacket(PacketByteBuf buffer) {
		buffer.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			buffer.writeItemStack(tradeOffer.getFirstStack());
			buffer.writeItemStack(tradeOffer.getResult());
			ItemStack itemStack = tradeOffer.getSecondStack();
			buffer.writeBoolean(itemStack != null);
			if (itemStack != null) {
				buffer.writeItemStack(itemStack);
			}

			buffer.writeBoolean(tradeOffer.isDisabled());
			buffer.writeInt(tradeOffer.getUses());
			buffer.writeInt(tradeOffer.getMaxUses());
		}
	}

	public static TraderOfferList fromPacket(PacketByteBuf byteBuf) throws IOException {
		TraderOfferList traderOfferList = new TraderOfferList();
		int i = byteBuf.readByte() & 255;

		for (int j = 0; j < i; j++) {
			ItemStack itemStack = byteBuf.readItemStack();
			ItemStack itemStack2 = byteBuf.readItemStack();
			ItemStack itemStack3 = null;
			if (byteBuf.readBoolean()) {
				itemStack3 = byteBuf.readItemStack();
			}

			boolean bl = byteBuf.readBoolean();
			int k = byteBuf.readInt();
			int l = byteBuf.readInt();
			TradeOffer tradeOffer = new TradeOffer(itemStack, itemStack3, itemStack2, k, l);
			if (bl) {
				tradeOffer.clearUses();
			}

			traderOfferList.add(tradeOffer);
		}

		return traderOfferList;
	}

	public void fromNbt(NbtCompound nbt) {
		NbtList nbtList = nbt.getList("Recipes", 10);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			this.add(new TradeOffer(nbtCompound));
		}
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		NbtList nbtList = new NbtList();

		for (int i = 0; i < this.size(); i++) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			nbtList.add(tradeOffer.toNbt());
		}

		nbtCompound.put("Recipes", nbtList);
		return nbtCompound;
	}
}
