package net.minecraft.item;

import net.minecraft.class_3542;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.nbt.NbtCompound;

public class DyeableArmorItem extends ArmorItem {
	public DyeableArmorItem(class_3542 arg, EquipmentSlot equipmentSlot, Item.Settings settings) {
		super(arg, equipmentSlot, settings);
	}

	public boolean method_16049(ItemStack itemStack) {
		NbtCompound nbtCompound = itemStack.getNbtCompound("display");
		return nbtCompound != null && nbtCompound.contains("color", 99);
	}

	public int method_16050(ItemStack itemStack) {
		NbtCompound nbtCompound = itemStack.getNbtCompound("display");
		return nbtCompound != null && nbtCompound.contains("color", 99) ? nbtCompound.getInt("color") : 10511680;
	}

	public void method_16051(ItemStack itemStack) {
		NbtCompound nbtCompound = itemStack.getNbtCompound("display");
		if (nbtCompound != null && nbtCompound.contains("color")) {
			nbtCompound.remove("color");
		}
	}

	public void method_16048(ItemStack itemStack, int i) {
		itemStack.getOrCreateNbtCompound("display").putInt("color", i);
	}
}
