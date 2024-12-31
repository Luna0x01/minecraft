package net.minecraft;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public class class_2960 {
	public static ItemStack method_13926(List<ItemStack> list, int i, int j) {
		return i >= 0 && i < list.size() && !((ItemStack)list.get(i)).isEmpty() && j > 0 ? ((ItemStack)list.get(i)).split(j) : ItemStack.EMPTY;
	}

	public static ItemStack method_13925(List<ItemStack> list, int i) {
		return i >= 0 && i < list.size() ? (ItemStack)list.set(i, ItemStack.EMPTY) : ItemStack.EMPTY;
	}

	public static NbtCompound method_13923(NbtCompound nbtCompound, DefaultedList<ItemStack> defaultedList) {
		return method_13924(nbtCompound, defaultedList, true);
	}

	public static NbtCompound method_13924(NbtCompound nbtCompound, DefaultedList<ItemStack> defaultedList, boolean bl) {
		NbtList nbtList = new NbtList();

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = defaultedList.get(i);
			if (!itemStack.isEmpty()) {
				NbtCompound nbtCompound2 = new NbtCompound();
				nbtCompound2.putByte("Slot", (byte)i);
				itemStack.toNbt(nbtCompound2);
				nbtList.add((NbtElement)nbtCompound2);
			}
		}

		if (!nbtList.isEmpty() || bl) {
			nbtCompound.put("Items", nbtList);
		}

		return nbtCompound;
	}

	public static void method_13927(NbtCompound nbtCompound, DefaultedList<ItemStack> defaultedList) {
		NbtList nbtList = nbtCompound.getList("Items", 10);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound2 = nbtList.getCompound(i);
			int j = nbtCompound2.getByte("Slot") & 255;
			if (j >= 0 && j < defaultedList.size()) {
				defaultedList.set(j, ItemStack.from(nbtCompound2));
			}
		}
	}
}
