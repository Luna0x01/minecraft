package net.minecraft.recipe;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import javax.annotation.Nullable;
import net.minecraft.class_3175;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Ingredient implements Predicate<ItemStack> {
	public static final Ingredient field_15680 = new Ingredient() {
		@Override
		public boolean apply(@Nullable ItemStack itemStack) {
			return itemStack.isEmpty();
		}
	};
	private final ItemStack[] field_15681;
	private IntList field_15682;

	private Ingredient(ItemStack... itemStacks) {
		this.field_15681 = itemStacks;
	}

	public ItemStack[] method_14244() {
		return this.field_15681;
	}

	public boolean apply(@Nullable ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		} else {
			for (ItemStack itemStack2 : this.field_15681) {
				if (itemStack2.getItem() == itemStack.getItem()) {
					int i = itemStack2.getData();
					if (i == 32767 || i == itemStack.getData()) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public IntList method_14249() {
		if (this.field_15682 == null) {
			this.field_15682 = new IntArrayList(this.field_15681.length);

			for (ItemStack itemStack : this.field_15681) {
				this.field_15682.add(class_3175.method_14176(itemStack));
			}

			this.field_15682.sort(IntComparators.NATURAL_COMPARATOR);
		}

		return this.field_15682;
	}

	public static Ingredient method_14245(Item item) {
		return method_14248(new ItemStack(item, 1, 32767));
	}

	public static Ingredient method_14247(Item... items) {
		ItemStack[] itemStacks = new ItemStack[items.length];

		for (int i = 0; i < items.length; i++) {
			itemStacks[i] = new ItemStack(items[i]);
		}

		return method_14248(itemStacks);
	}

	public static Ingredient method_14248(ItemStack... stacks) {
		if (stacks.length > 0) {
			for (ItemStack itemStack : stacks) {
				if (!itemStack.isEmpty()) {
					return new Ingredient(stacks);
				}
			}
		}

		return field_15680;
	}
}
