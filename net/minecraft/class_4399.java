package net.minecraft;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;

public class class_4399 extends class_4398 {
	private boolean field_21659;

	@Override
	protected void method_20434(RecipeType recipeType, boolean bl) {
		this.field_21659 = this.field_21655.method_15979(recipeType);
		int i = this.field_21653.method_14177(recipeType, null);
		if (this.field_21659) {
			ItemStack itemStack = this.field_21655.getSlot(0).getStack();
			if (itemStack.isEmpty() || i <= itemStack.getCount()) {
				return;
			}
		}

		int j = this.method_20436(bl, i, this.field_21659);
		IntList intList = new IntArrayList();
		if (this.field_21653.method_14173(recipeType, intList, j)) {
			if (!this.field_21659) {
				this.method_20432(this.field_21655.method_15981());
				this.method_20432(0);
			}

			this.method_20439(j, intList);
		}
	}

	@Override
	protected void method_20431() {
		this.method_20432(this.field_21655.method_15981());
		super.method_20431();
	}

	protected void method_20439(int i, IntList intList) {
		Iterator<Integer> iterator = intList.iterator();
		Slot slot = this.field_21655.getSlot(0);
		ItemStack itemStack = class_3175.method_14174((Integer)iterator.next());
		if (!itemStack.isEmpty()) {
			int j = Math.min(itemStack.getMaxCount(), i);
			if (this.field_21659) {
				j -= slot.getStack().getCount();
			}

			for (int k = 0; k < j; k++) {
				this.method_20433(slot, itemStack);
			}
		}
	}
}
