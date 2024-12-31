package net.minecraft;

import com.google.gson.JsonObject;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class class_3584 implements RecipeType {
	private final Identifier field_17468;
	private final String field_17469;
	private final Ingredient field_17470;
	private final ItemStack field_17471;
	private final float field_17472;
	private final int field_17473;

	public class_3584(Identifier identifier, String string, Ingredient ingredient, ItemStack itemStack, float f, int i) {
		this.field_17468 = identifier;
		this.field_17469 = string;
		this.field_17470 = ingredient;
		this.field_17471 = itemStack;
		this.field_17472 = f;
		this.field_17473 = i;
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		return inventory instanceof FurnaceBlockEntity && this.field_17470.test(inventory.getInvStack(0));
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		return this.field_17471.copy();
	}

	@Override
	public boolean method_14250(int i, int j) {
		return true;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17462;
	}

	@Override
	public DefaultedList<Ingredient> method_14252() {
		DefaultedList<Ingredient> defaultedList = DefaultedList.of();
		defaultedList.add(this.field_17470);
		return defaultedList;
	}

	public float method_16245() {
		return this.field_17472;
	}

	@Override
	public ItemStack getOutput() {
		return this.field_17471;
	}

	@Override
	public String method_14253() {
		return this.field_17469;
	}

	public int method_16246() {
		return this.field_17473;
	}

	@Override
	public Identifier method_16202() {
		return this.field_17468;
	}

	public static class class_3585 implements class_3578<class_3584> {
		public class_3584 method_16215(Identifier identifier, JsonObject jsonObject) {
			String string = JsonHelper.getString(jsonObject, "group", "");
			Ingredient ingredient;
			if (JsonHelper.hasArray(jsonObject, "ingredient")) {
				ingredient = Ingredient.method_16183(JsonHelper.getArray(jsonObject, "ingredient"));
			} else {
				ingredient = Ingredient.method_16183(JsonHelper.getObject(jsonObject, "ingredient"));
			}

			String string2 = JsonHelper.getString(jsonObject, "result");
			Item item = Registry.ITEM.getByIdentifier(new Identifier(string2));
			if (item != null) {
				ItemStack itemStack = new ItemStack(item);
				float f = JsonHelper.getFloat(jsonObject, "experience", 0.0F);
				int i = JsonHelper.getInt(jsonObject, "cookingtime", 200);
				return new class_3584(identifier, string, ingredient, itemStack, f, i);
			} else {
				throw new IllegalStateException(string2 + " did not exist");
			}
		}

		public class_3584 method_16216(Identifier identifier, PacketByteBuf packetByteBuf) {
			String string = packetByteBuf.readString(32767);
			Ingredient ingredient = Ingredient.method_16193(packetByteBuf);
			ItemStack itemStack = packetByteBuf.readItemStack();
			float f = packetByteBuf.readFloat();
			int i = packetByteBuf.readVarInt();
			return new class_3584(identifier, string, ingredient, itemStack, f, i);
		}

		public void method_16214(PacketByteBuf packetByteBuf, class_3584 arg) {
			packetByteBuf.writeString(arg.field_17469);
			arg.field_17470.method_16185(packetByteBuf);
			packetByteBuf.writeItemStack(arg.field_17471);
			packetByteBuf.writeFloat(arg.field_17472);
			packetByteBuf.writeVarInt(arg.field_17473);
		}

		@Override
		public String method_16213() {
			return "smelting";
		}
	}
}
