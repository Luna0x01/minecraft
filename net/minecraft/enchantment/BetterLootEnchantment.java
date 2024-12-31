package net.minecraft.enchantment;

import net.minecraft.util.Identifier;

public class BetterLootEnchantment extends Enchantment {
	protected BetterLootEnchantment(int i, Identifier identifier, int j, EnchantmentTarget enchantmentTarget) {
		super(i, identifier, j, enchantmentTarget);
		if (enchantmentTarget == EnchantmentTarget.DIGGER) {
			this.setName("lootBonusDigger");
		} else if (enchantmentTarget == EnchantmentTarget.FISHING_ROD) {
			this.setName("lootBonusFishing");
		} else {
			this.setName("lootBonus");
		}
	}

	@Override
	public int getMinimumPower(int level) {
		return 15 + (level - 1) * 9;
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean differs(Enchantment other) {
		return super.differs(other) && other.id != SILK_TOUCH.id;
	}
}
