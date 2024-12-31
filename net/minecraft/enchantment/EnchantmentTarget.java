package net.minecraft.enchantment;

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
	TORSO,
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
				if (armorItem.slot == 0) {
					return this == HEAD;
				} else if (armorItem.slot == 2) {
					return this == LEGS;
				} else if (armorItem.slot == 1) {
					return this == TORSO;
				} else {
					return armorItem.slot == 3 ? this == FEET : false;
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
