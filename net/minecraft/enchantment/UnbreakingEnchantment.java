package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class UnbreakingEnchantment extends Enchantment {
	protected UnbreakingEnchantment(int i, Identifier identifier, int j) {
		super(i, identifier, j, EnchantmentTarget.BREAKABLE);
		this.setName("durability");
	}

	@Override
	public int getMinimumPower(int level) {
		return 5 + (level - 1) * 8;
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
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.isDamageable() ? true : super.isAcceptableItem(stack);
	}

	public static boolean shouldPreventDamage(ItemStack item, int level, Random random) {
		return item.getItem() instanceof ArmorItem && random.nextFloat() < 0.6F ? false : random.nextInt(level + 1) > 0;
	}
}
