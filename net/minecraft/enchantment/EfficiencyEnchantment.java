package net.minecraft.enchantment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class EfficiencyEnchantment extends Enchantment {
	protected EfficiencyEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.DIGGER);
		this.setName("digging");
	}

	@Override
	public int getMinimumPower(int level) {
		return 1 + 10 * (level - 1);
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() == Items.SHEARS ? true : super.isAcceptableItem(stack);
	}
}
