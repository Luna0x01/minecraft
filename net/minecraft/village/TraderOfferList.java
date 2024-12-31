package net.minecraft.village;

import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.PacketByteBuf;

public class TraderOfferList extends ArrayList<TradeOffer> {
	public TraderOfferList() {
	}

	public TraderOfferList(NbtCompound nbtCompound) {
		this.fromNbt(nbtCompound);
	}

	@Nullable
	public TradeOffer getValidRecipe(ItemStack firstBuyItem, ItemStack secondBuyItem, int index) {
		if (index > 0 && index < this.size()) {
			TradeOffer tradeOffer = (TradeOffer)this.get(index);
			return !this.method_8454(firstBuyItem, tradeOffer.getFirstStack())
					|| (!secondBuyItem.isEmpty() || tradeOffer.hasSecondStack())
						&& (!tradeOffer.hasSecondStack() || !this.method_8454(secondBuyItem, tradeOffer.getSecondStack()))
					|| firstBuyItem.getCount() < tradeOffer.getFirstStack().getCount()
					|| tradeOffer.hasSecondStack() && secondBuyItem.getCount() < tradeOffer.getSecondStack().getCount()
				? null
				: tradeOffer;
		} else {
			for (int i = 0; i < this.size(); i++) {
				TradeOffer tradeOffer2 = (TradeOffer)this.get(i);
				if (this.method_8454(firstBuyItem, tradeOffer2.getFirstStack())
					&& firstBuyItem.getCount() >= tradeOffer2.getFirstStack().getCount()
					&& (
						!tradeOffer2.hasSecondStack() && secondBuyItem.isEmpty()
							|| tradeOffer2.hasSecondStack()
								&& this.method_8454(secondBuyItem, tradeOffer2.getSecondStack())
								&& secondBuyItem.getCount() >= tradeOffer2.getSecondStack().getCount()
					)) {
					return tradeOffer2;
				}
			}

			return null;
		}
	}

	private boolean method_8454(ItemStack itemStack, ItemStack itemStack2) {
		ItemStack itemStack3 = itemStack.copy();
		if (itemStack3.getItem().isDamageable()) {
			itemStack3.setDamage(itemStack3.getDamage());
		}

		return ItemStack.equalsIgnoreNbt(itemStack3, itemStack2)
			&& (!itemStack2.hasNbt() || itemStack3.hasNbt() && NbtHelper.areEqual(itemStack2.getNbt(), itemStack3.getNbt(), false));
	}

	public void toPacket(PacketByteBuf buffer) {
		buffer.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			TradeOffer tradeOffer = (TradeOffer)this.get(i);
			buffer.writeItemStack(tradeOffer.getFirstStack());
			buffer.writeItemStack(tradeOffer.getResult());
			ItemStack itemStack = tradeOffer.getSecondStack();
			buffer.writeBoolean(!itemStack.isEmpty());
			if (!itemStack.isEmpty()) {
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
			ItemStack itemStack3 = ItemStack.EMPTY;
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
			nbtList.add((NbtElement)tradeOffer.toNbt());
		}

		nbtCompound.put("Recipes", nbtList);
		return nbtCompound;
	}
}
