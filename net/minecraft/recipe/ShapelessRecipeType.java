package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapelessRecipeType implements RecipeType {
	private final ItemStack result;
	private final List<ItemStack> stacks;

	public ShapelessRecipeType(ItemStack itemStack, List<ItemStack> list) {
		this.result = itemStack;
		this.stacks = list;
	}

	@Override
	public ItemStack getOutput() {
		return this.result;
	}

	@Override
	public DefaultedList<ItemStack> method_13670(CraftingInventory craftingInventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.getInvSize(), ItemStack.EMPTY);

		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack itemStack = craftingInventory.getInvStack(i);
			if (itemStack.getItem().isFood()) {
				defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
			}
		}

		return defaultedList;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		List<ItemStack> list = Lists.newArrayList(this.stacks);

		for (int i = 0; i < inventory.getHeight(); i++) {
			for (int j = 0; j < inventory.getWidth(); j++) {
				ItemStack itemStack = inventory.getStackAt(j, i);
				if (!itemStack.isEmpty()) {
					boolean bl = false;

					for (ItemStack itemStack2 : list) {
						if (itemStack.getItem() == itemStack2.getItem() && (itemStack2.getData() == 32767 || itemStack.getData() == itemStack2.getData())) {
							bl = true;
							list.remove(itemStack2);
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}
			}
		}

		return list.isEmpty();
	}

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		return this.result.copy();
	}

	@Override
	public int getSize() {
		return this.stacks.size();
	}
}
