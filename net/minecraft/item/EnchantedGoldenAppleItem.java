package net.minecraft.item;

public class EnchantedGoldenAppleItem extends Item {
	public EnchantedGoldenAppleItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasEnchantmentGlint(ItemStack itemStack) {
		return true;
	}
}
