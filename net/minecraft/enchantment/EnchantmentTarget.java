package net.minecraft.enchantment;

import net.minecraft.class_3564;
import net.minecraft.class_3685;
import net.minecraft.block.Block;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

public enum EnchantmentTarget {
	ALL {
		@Override
		public boolean isCompatible(Item item) {
			for (EnchantmentTarget enchantmentTarget : EnchantmentTarget.values()) {
				if (enchantmentTarget != EnchantmentTarget.ALL && enchantmentTarget.isCompatible(item)) {
					return true;
				}
			}

			return false;
		}
	},
	ALL_ARMOR {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof ArmorItem;
		}
	},
	FEET {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).method_11352() == EquipmentSlot.FEET;
		}
	},
	LEGS {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).method_11352() == EquipmentSlot.LEGS;
		}
	},
	ARMOR_CHEST {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).method_11352() == EquipmentSlot.CHEST;
		}
	},
	HEAD {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof ArmorItem && ((ArmorItem)item).method_11352() == EquipmentSlot.HEAD;
		}
	},
	WEAPON {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof SwordItem;
		}
	},
	DIGGER {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof ToolItem;
		}
	},
	FISHING_ROD {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof FishingRodItem;
		}
	},
	TRIDENT {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof class_3564;
		}
	},
	BREAKABLE {
		@Override
		public boolean isCompatible(Item item) {
			return item.isDamageable();
		}
	},
	BOW {
		@Override
		public boolean isCompatible(Item item) {
			return item instanceof BowItem;
		}
	},
	WEARABLE {
		@Override
		public boolean isCompatible(Item item) {
			Block block = Block.getBlockFromItem(item);
			return item instanceof ArmorItem || item instanceof ElytraItem || block instanceof class_3685 || block instanceof PumpkinBlock;
		}
	};

	private EnchantmentTarget() {
	}

	public abstract boolean isCompatible(Item item);
}
