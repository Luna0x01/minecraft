package net.minecraft.enchantment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class SilkTouchEnchantment extends Enchantment {
	protected SilkTouchEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.DIGGER);
		this.setName("untouching");
	}

	@Override
	public int getMinimumPower(int level) {
		return 15;
	}

	@Override
	public int getMaximumPower(int level) {
		return super.getMinimumPower(level) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 1;
	}

	@Override
	public boolean differs(Enchantment other) {
		return super.differs(other) && other.id != FORTUNE.id;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() == Items.SHEARS ? true : super.isAcceptableItem(stack);
	}
}
