package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapedRecipeType implements RecipeType {
	private final int width;
	private final int height;
	private final DefaultedList<Ingredient> field_15686;
	private final ItemStack result;
	private final String field_15687;

	public ShapedRecipeType(String string, int i, int j, DefaultedList<Ingredient> defaultedList, ItemStack itemStack) {
		this.field_15687 = string;
		this.width = i;
		this.height = j;
		this.field_15686 = defaultedList;
		this.result = itemStack;
	}

	@Override
	public String method_14253() {
		return this.field_15687;
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
	public DefaultedList<Ingredient> method_14252() {
		return this.field_15686;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i >= this.width && j >= this.height;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		for (int i = 0; i <= 3 - this.width; i++) {
			for (int j = 0; j <= 3 - this.height; j++) {
				if (this.method_3503(inventory, i, j, true)) {
					return true;
				}

				if (this.method_3503(inventory, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean method_3503(CraftingInventory inventory, int width, int height, boolean bl) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int k = i - width;
				int l = j - height;
				Ingredient ingredient = Ingredient.field_15680;
				if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
					if (bl) {
						ingredient = this.field_15686.get(this.width - k - 1 + l * this.width);
					} else {
						ingredient = this.field_15686.get(k + l * this.width);
					}
				}

				if (!ingredient.apply(inventory.getStackAt(i, j))) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		return this.getOutput().copy();
	}

	public int method_14272() {
		return this.width;
	}

	public int method_14273() {
		return this.height;
	}

	public static ShapedRecipeType load(JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "group", "");
		Map<String, Ingredient> map = method_14270(JsonHelper.getObject(jsonObject, "key"));
		String[] strings = method_14268(method_14263(JsonHelper.getArray(jsonObject, "pattern")));
		int i = strings[0].length();
		int j = strings.length;
		DefaultedList<Ingredient> defaultedList = method_14269(strings, map, i, j);
		ItemStack itemStack = method_14266(JsonHelper.getObject(jsonObject, "result"), true);
		return new ShapedRecipeType(string, i, j, defaultedList, itemStack);
	}

	private static DefaultedList<Ingredient> method_14269(String[] strings, Map<String, Ingredient> map, int i, int j) {
		DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.field_15680);
		Set<String> set = Sets.newHashSet(map.keySet());
		set.remove(" ");

		for (int k = 0; k < strings.length; k++) {
			for (int l = 0; l < strings[k].length(); l++) {
				String string = strings[k].substring(l, l + 1);
				Ingredient ingredient = (Ingredient)map.get(string);
				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
				}

				set.remove(string);
				defaultedList.set(l + i * k, ingredient);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return defaultedList;
		}
	}

	@VisibleForTesting
	static String[] method_14268(String... strings) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for (int m = 0; m < strings.length; m++) {
			String string = strings[m];
			i = Math.min(i, method_14267(string));
			int n = method_14271(string);
			j = Math.max(j, n);
			if (n < 0) {
				if (k == m) {
					k++;
				}

				l++;
			} else {
				l = 0;
			}
		}

		if (strings.length == l) {
			return new String[0];
		} else {
			String[] strings2 = new String[strings.length - l - k];

			for (int o = 0; o < strings2.length; o++) {
				strings2[o] = strings[o + k].substring(i, j + 1);
			}

			return strings2;
		}
	}

	private static int method_14267(String string) {
		int i = 0;

		while (i < string.length() && string.charAt(i) == ' ') {
			i++;
		}

		return i;
	}

	private static int method_14271(String string) {
		int i = string.length() - 1;

		while (i >= 0 && string.charAt(i) == ' ') {
			i--;
		}

		return i;
	}

	private static String[] method_14263(JsonArray jsonArray) {
		String[] strings = new String[jsonArray.size()];
		if (strings.length > 3) {
			throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
		} else if (strings.length == 0) {
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		} else {
			for (int i = 0; i < strings.length; i++) {
				String string = JsonHelper.asString(jsonArray.get(i), "pattern[" + i + "]");
				if (string.length() > 3) {
					throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
				}

				if (i > 0 && strings[0].length() != string.length()) {
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				}

				strings[i] = string;
			}

			return strings;
		}
	}

	private static Map<String, Ingredient> method_14270(JsonObject jsonObject) {
		Map<String, Ingredient> map = Maps.newHashMap();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (((String)entry.getKey()).length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}

			if (" ".equals(entry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}

			map.put(entry.getKey(), method_14264((JsonElement)entry.getValue()));
		}

		map.put(" ", Ingredient.field_15680);
		return map;
	}

	public static Ingredient method_14264(@Nullable JsonElement jsonElement) {
		if (jsonElement == null || jsonElement.isJsonNull()) {
			throw new JsonSyntaxException("Item cannot be null");
		} else if (jsonElement.isJsonObject()) {
			return Ingredient.method_14248(method_14266(jsonElement.getAsJsonObject(), false));
		} else if (!jsonElement.isJsonArray()) {
			throw new JsonSyntaxException("Expected item to be object or array of objects");
		} else {
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			if (jsonArray.size() == 0) {
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
			} else {
				ItemStack[] itemStacks = new ItemStack[jsonArray.size()];

				for (int i = 0; i < jsonArray.size(); i++) {
					itemStacks[i] = method_14266(JsonHelper.asObject(jsonArray.get(i), "item"), false);
				}

				return Ingredient.method_14248(itemStacks);
			}
		}
	}

	public static ItemStack method_14266(JsonObject jsonObject, boolean bl) {
		String string = JsonHelper.getString(jsonObject, "item");
		Item item = Item.REGISTRY.get(new Identifier(string));
		if (item == null) {
			throw new JsonSyntaxException("Unknown item '" + string + "'");
		} else if (item.isUnbreakable() && !jsonObject.has("data")) {
			throw new JsonParseException("Missing data for item '" + string + "'");
		} else {
			int i = JsonHelper.getInt(jsonObject, "data", 0);
			int j = bl ? JsonHelper.getInt(jsonObject, "count", 1) : 1;
			return new ItemStack(item, j, i);
		}
	}
}
