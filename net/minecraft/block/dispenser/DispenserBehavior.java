package net.minecraft.block.dispenser;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;

public interface DispenserBehavior {
	DispenserBehavior INSTANCE = new DispenserBehavior() {
		@Override
		public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
			return stack;
		}
	};

	ItemStack dispense(BlockPointer pointer, ItemStack stack);
}
