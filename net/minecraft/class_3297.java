package net.minecraft;

import com.google.common.collect.ForwardingList;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public class class_3297 extends ForwardingList<ItemStack> {
	private final DefaultedList<ItemStack> field_20640 = DefaultedList.ofSize(PlayerInventory.getHotbarSize(), ItemStack.EMPTY);

	protected List<ItemStack> delegate() {
		return this.field_20640;
	}

	public NbtList method_14677() {
		NbtList nbtList = new NbtList();

		for (ItemStack itemStack : this.delegate()) {
			nbtList.add((NbtElement)itemStack.toNbt(new NbtCompound()));
		}

		return nbtList;
	}

	public void method_14678(NbtList nbtList) {
		List<ItemStack> list = this.delegate();

		for (int i = 0; i < list.size(); i++) {
			list.set(i, ItemStack.from(nbtList.getCompound(i)));
		}
	}

	public boolean isEmpty() {
		for (ItemStack itemStack : this.delegate()) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
