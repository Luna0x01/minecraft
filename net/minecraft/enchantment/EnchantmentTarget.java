package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

public enum EnchantmentTarget {
	ALL,
	ALL_ARMOR,
	FEET,
	LEGS,
	ARMOR_CHEST,
	HEAD,
	WEAPON,
	DIGGER,
	FISHING_ROD,
	BREAKABLE,
	BOW;

	public boolean isCompatible(Item item) {
		if (this == ALL) {
			return true;
		} else if (this == BREAKABLE && item.isDamageable()) {
			return true;
		} else if (item instanceof ArmorItem) {
			if (this == ALL_ARMOR) {
				return true;
			} else {
				ArmorItem armorItem = (ArmorItem)item;
				if (armorItem.field_12275 == EquipmentSlot.HEAD) {
					return this == HEAD;
				} else if (armorItem.field_12275 == EquipmentSlot.LEGS) {
					return this == LEGS;
				} else if (armorItem.field_12275 == EquipmentSlot.CHEST) {
					return this == ARMOR_CHEST;
				} else {
					return armorItem.field_12275 == EquipmentSlot.FEET ? this == FEET : false;
				}
			}
		} else if (item instanceof SwordItem) {
			return this == WEAPON;
		} else if (item instanceof ToolItem) {
			return this == DIGGER;
		} else if (item instanceof BowItem) {
			return this == BOW;
		} else {
			return item instanceof FishingRodItem ? this == FISHING_ROD : false;
		}
	}
}
