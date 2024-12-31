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
import net.minecraft.class_3578;
import net.minecraft.class_3579;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ShapedRecipeType implements RecipeType {
	private final int width;
	private final int height;
	private final DefaultedList<Ingredient> field_15686;
	private final ItemStack result;
	private final Identifier field_17466;
	private final String field_15687;

	public ShapedRecipeType(Identifier identifier, String string, int i, int j, DefaultedList<Ingredient> defaultedList, ItemStack itemStack) {
		this.field_17466 = identifier;
		this.field_15687 = string;
		this.width = i;
		this.height = j;
		this.field_15686 = defaultedList;
		this.result = itemStack;
	}

	@Override
	public Identifier method_16202() {
		return this.field_17466;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17447;
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
	public DefaultedList<Ingredient> method_14252() {
		return this.field_15686;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i >= this.width && j >= this.height;
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			for (int i = 0; i <= inventory.method_11260() - this.width; i++) {
				for (int j = 0; j <= inventory.method_11259() - this.height; j++) {
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
	}

	private boolean method_3503(Inventory inventory, int i, int j, boolean bl) {
		for (int k = 0; k < inventory.method_11260(); k++) {
			for (int l = 0; l < inventory.method_11259(); l++) {
				int m = k - i;
				int n = l - j;
				Ingredient ingredient = Ingredient.field_15680;
				if (m >= 0 && n >= 0 && m < this.width && n < this.height) {
					if (bl) {
						ingredient = this.field_15686.get(this.width - m - 1 + n * this.width);
					} else {
						ingredient = this.field_15686.get(m + n * this.width);
					}
				}

				if (!ingredient.test(inventory.getInvStack(k + l * inventory.method_11260()))) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		return this.getOutput().copy();
	}

	public int method_14272() {
		return this.width;
	}

	public int method_14273() {
		return this.height;
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

			map.put(entry.getKey(), Ingredient.method_16183((JsonElement)entry.getValue()));
		}

		map.put(" ", Ingredient.field_15680);
		return map;
	}

	public static ItemStack method_16223(JsonObject jsonObject) {
		String string = JsonHelper.getString(jsonObject, "item");
		Item item = Registry.ITEM.getByIdentifier(new Identifier(string));
		if (item == null) {
			throw new JsonSyntaxException("Unknown item '" + string + "'");
		} else if (jsonObject.has("data")) {
			throw new JsonParseException("Disallowed data tag found");
		} else {
			int i = JsonHelper.getInt(jsonObject, "count", 1);
			return new ItemStack(item, i);
		}
	}

	public static class class_3581 implements class_3578<ShapedRecipeType> {
		public ShapedRecipeType method_16215(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			Map<String, Ingredient> map = ShapedRecipeType.method_14270(JsonHelper.getObject(jsonObject, "key"));
			String[] strings = ShapedRecipeType.method_14268(ShapedRecipeType.method_14263(JsonHelper.getArray(jsonObject, "pattern")));
			int i = strings[0].length();
			int j = strings.length;
			DefaultedList<Ingredient> defaultedList = ShapedRecipeType.method_14269(strings, map, i, j);
			ItemStack itemStack = ShapedRecipeType.method_16223(JsonHelper.getObject(jsonObject, "result"));
			return new ShapedRecipeType(identifier, string, i, j, defaultedList, itemStack);
		}

		@Override
		public String method_16213() {
			return "crafting_shaped";
		}

		public ShapedRecipeType method_16216(Identifier identifier, PacketByteBuf packetByteBuf) {
			int i = packetByteBuf.readVarInt();
			int j = packetByteBuf.readVarInt();
			String string = packetByteBuf.readString(32767);
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i * j, Ingredient.field_15680);

			for (int k = 0; k < defaultedList.size(); k++) {
				defaultedList.set(k, Ingredient.method_16193(packetByteBuf));
			}

			ItemStack itemStack = packetByteBuf.readItemStack();
			return new ShapedRecipeType(identifier, string, i, j, defaultedList, itemStack);
		}

		public void method_16214(PacketByteBuf packetByteBuf, ShapedRecipeType shapedRecipeType) {
			packetByteBuf.writeVarInt(shapedRecipeType.width);
			packetByteBuf.writeVarInt(shapedRecipeType.height);
			packetByteBuf.writeString(shapedRecipeType.field_15687);

			for (Ingredient ingredient : shapedRecipeType.field_15686) {
				ingredient.method_16185(packetByteBuf);
			}

			packetByteBuf.writeItemStack(shapedRecipeType.result);
		}
	}
}
