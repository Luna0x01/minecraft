package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapelessRecipeType implements RecipeType {
	private final ItemStack result;
	private final DefaultedList<Ingredient> field_15688;
	private final String field_15689;

	public ShapelessRecipeType(String string, ItemStack itemStack, DefaultedList<Ingredient> defaultedList) {
		this.field_15689 = string;
		this.result = itemStack;
		this.field_15688 = defaultedList;
	}

	@Override
	public String method_14253() {
		return this.field_15689;
	}

	@Override
	public ItemStack getOutput() {
		return this.result;
	}

	@Override
	public DefaultedList<Ingredient> method_14252() {
		return this.field_15688;
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
		List<Ingredient> list = Lists.newArrayList(this.field_15688);

		for (int i = 0; i < inventory.getHeight(); i++) {
			for (int j = 0; j < inventory.getWidth(); j++) {
				ItemStack itemStack = inventory.getStackAt(j, i);
				if (!itemStack.isEmpty()) {
					boolean bl = false;

					for (Ingredient ingredient : list) {
						if (ingredient.apply(itemStack)) {
							bl = true;
							list.remove(ingredient);
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

	public static ShapelessRecipeType load(JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "group", "");
		DefaultedList<Ingredient> defaultedList = method_14274(JsonHelper.getArray(jsonObject, "ingredients"));
		if (defaultedList.isEmpty()) {
			throw new JsonParseException("No ingredients for shapeless recipe");
		} else if (defaultedList.size() > 9) {
			throw new JsonParseException("Too many ingredients for shapeless recipe");
		} else {
			ItemStack itemStack = ShapedRecipeType.method_14266(JsonHelper.getObject(jsonObject, "result"), true);
			return new ShapelessRecipeType(string, itemStack, defaultedList);
		}
	}

	private static DefaultedList<Ingredient> method_14274(JsonArray jsonArray) {
		DefaultedList<Ingredient> defaultedList = DefaultedList.of();

		for (int i = 0; i < jsonArray.size(); i++) {
			Ingredient ingredient = ShapedRecipeType.method_14264(jsonArray.get(i));
			if (ingredient != Ingredient.field_15680) {
				defaultedList.add(ingredient);
			}
		}

		return defaultedList;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= this.field_15688.size();
	}
}
