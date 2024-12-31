package net.minecraft;

import net.minecraft.item.IToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class class_3562 extends Item {
	private final IToolMaterial field_17386;

	public class_3562(IToolMaterial iToolMaterial, Item.Settings settings) {
		super(settings.setMaxDamageIfAbsent(iToolMaterial.getDurability()));
		this.field_17386 = iToolMaterial;
	}

	public IToolMaterial method_16137() {
		return this.field_17386;
	}

	@Override
	public int getEnchantability() {
		return this.field_17386.getEnchantability();
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return this.field_17386.getRepairIngredient().test(ingredient) || super.canRepair(stack, ingredient);
	}
}
