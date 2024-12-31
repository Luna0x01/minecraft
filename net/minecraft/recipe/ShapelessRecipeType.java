package net.minecraft.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.class_3175;
import net.minecraft.class_3578;
import net.minecraft.class_3579;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ShapelessRecipeType implements RecipeType {
	private final Identifier field_17467;
	private final String field_15689;
	private final ItemStack result;
	private final DefaultedList<Ingredient> field_15688;

	public ShapelessRecipeType(Identifier identifier, String string, ItemStack itemStack, DefaultedList<Ingredient> defaultedList) {
		this.field_17467 = identifier;
		this.field_15689 = string;
		this.result = itemStack;
		this.field_15688 = defaultedList;
	}

	@Override
	public Identifier method_16202() {
		return this.field_17467;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17448;
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
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			class_3175 lv = new class_3175();
			int i = 0;

			for (int j = 0; j < inventory.method_11259(); j++) {
				for (int k = 0; k < inventory.method_11260(); k++) {
					ItemStack itemStack = inventory.getInvStack(k + j * inventory.method_11260());
					if (!itemStack.isEmpty()) {
						i++;
						lv.method_15943(new ItemStack(itemStack.getItem()));
					}
				}
			}

			return i == this.field_15688.size() && lv.method_14172(this, null);
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		return this.result.copy();
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= this.field_15688.size();
	}

	public static class class_3582 implements class_3578<ShapelessRecipeType> {
		public ShapelessRecipeType method_16215(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			DefaultedList<Ingredient> defaultedList = method_16236(JsonHelper.getArray(jsonObject, "ingredients"));
			if (defaultedList.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else if (defaultedList.size() > 9) {
				throw new JsonParseException("Too many ingredients for shapeless recipe");
			} else {
				ItemStack itemStack = ShapedRecipeType.method_16223(JsonHelper.getObject(jsonObject, "result"));
				return new ShapelessRecipeType(identifier, string, itemStack, defaultedList);
			}
		}

		private static DefaultedList<Ingredient> method_16236(JsonArray jsonArray) {
			DefaultedList<Ingredient> defaultedList = DefaultedList.of();

			for (int i = 0; i < jsonArray.size(); i++) {
				Ingredient ingredient = Ingredient.method_16183(jsonArray.get(i));
				if (!ingredient.method_16196()) {
					defaultedList.add(ingredient);
				}
			}

			return defaultedList;
		}

		@Override
		public String method_16213() {
			return "crafting_shapeless";
		}

		public ShapelessRecipeType method_16216(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString(32767);
			int i = packetByteBuf.readVarInt();
			DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.field_15680);

			for (int j = 0; j < defaultedList.size(); j++) {
				defaultedList.set(j, Ingredient.method_16193(packetByteBuf));
			}

			ItemStack itemStack = packetByteBuf.readItemStack();
			return new ShapelessRecipeType(identifier, string, itemStack, defaultedList);
		}

		public void method_16214(PacketByteBuf packetByteBuf, ShapelessRecipeType shapelessRecipeType) {
			packetByteBuf.writeString(shapelessRecipeType.field_15689);
			packetByteBuf.writeVarInt(shapelessRecipeType.field_15688.size());

			for (Ingredient ingredient : shapelessRecipeType.field_15688) {
				ingredient.method_16185(packetByteBuf);
			}

			packetByteBuf.writeItemStack(shapelessRecipeType.result);
		}
	}
}
