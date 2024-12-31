package net.minecraft;

import java.util.ArrayList;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class class_3297 extends ArrayList<ItemStack> {
	public static final int field_16135 = PlayerInventory.getHotbarSize();

	public class_3297() {
		this.ensureCapacity(field_16135);

		for (int i = 0; i < field_16135; i++) {
			this.add(ItemStack.EMPTY);
		}
	}

	public NbtList method_14677() {
		NbtList nbtList = new NbtList();

		for (int i = 0; i < field_16135; i++) {
			nbtList.add(((ItemStack)this.get(i)).toNbt(new NbtCompound()));
		}

		return nbtList;
	}

	public void method_14678(NbtList nbtList) {
		for (int i = 0; i < field_16135; i++) {
			this.set(i, new ItemStack(nbtList.getCompound(i)));
		}
	}

	public boolean isEmpty() {
		for (int i = 0; i < field_16135; i++) {
			if (!((ItemStack)this.get(i)).isEmpty()) {
				return false;
			}
		}

		return true;
	}
}
