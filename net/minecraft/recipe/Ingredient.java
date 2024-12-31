package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.class_3175;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public final class Ingredient implements Predicate<ItemStack> {
	private static final Predicate<? super Ingredient.class_3577> field_17439 = arg -> !arg.method_16198().stream().allMatch(ItemStack::isEmpty);
	public static final Ingredient field_15680 = new Ingredient(Stream.empty());
	private final Ingredient.class_3577[] field_17440;
	private ItemStack[] field_15681;
	private IntList field_15682;

	private Ingredient(Stream<? extends Ingredient.class_3577> stream) {
		this.field_17440 = (Ingredient.class_3577[])stream.filter(field_17439).toArray(Ingredient.class_3577[]::new);
	}

	public ItemStack[] method_14244() {
		this.method_16197();
		return this.field_15681;
	}

	private void method_16197() {
		if (this.field_15681 == null) {
			this.field_15681 = (ItemStack[])Arrays.stream(this.field_17440).flatMap(arg -> arg.method_16198().stream()).distinct().toArray(ItemStack[]::new);
		}
	}

	public boolean test(@Nullable ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		} else if (this.field_17440.length == 0) {
			return itemStack.isEmpty();
		} else {
			this.method_16197();

			for (ItemStack itemStack2 : this.field_15681) {
				if (itemStack2.getItem() == itemStack.getItem()) {
					return true;
				}
			}

			return false;
		}
	}

	public IntList method_14249() {
		if (this.field_15682 == null) {
			this.method_16197();
			this.field_15682 = new IntArrayList(this.field_15681.length);

			for (ItemStack itemStack : this.field_15681) {
				this.field_15682.add(class_3175.method_15944(itemStack));
			}

			this.field_15682.sort(IntComparators.NATURAL_COMPARATOR);
		}

		return this.field_15682;
	}

	public void method_16185(PacketByteBuf packetByteBuf) {
		this.method_16197();
		packetByteBuf.writeVarInt(this.field_15681.length);

		for (int i = 0; i < this.field_15681.length; i++) {
			packetByteBuf.writeItemStack(this.field_15681[i]);
		}
	}

	public JsonElement method_16194() {
		if (this.field_17440.length == 1) {
			return this.field_17440[0].method_16199();
		} else {
			JsonArray jsonArray = new JsonArray();

			for (Ingredient.class_3577 lv : this.field_17440) {
				jsonArray.add(lv.method_16199());
			}

			return jsonArray;
		}
	}

	public boolean method_16196() {
		return this.field_17440.length == 0 && (this.field_15681 == null || this.field_15681.length == 0) && (this.field_15682 == null || this.field_15682.isEmpty());
	}

	private static Ingredient method_16186(Stream<? extends Ingredient.class_3577> stream) {
		Ingredient ingredient = new Ingredient(stream);
		return ingredient.field_17440.length == 0 ? field_15680 : ingredient;
	}

	public static Ingredient ofItems(Itemable... itemables) {
		return method_16186(Arrays.stream(itemables).map(itemable -> new Ingredient.class_3575(new ItemStack(itemable))));
	}

	public static Ingredient method_14248(ItemStack... stacks) {
		return method_16186(Arrays.stream(stacks).map(itemStack -> new Ingredient.class_3575(itemStack)));
	}

	public static Ingredient fromTag(Tag<Item> tag) {
		return method_16186(Stream.of(new Ingredient.class_3576(tag)));
	}

	public static Ingredient method_16193(PacketByteBuf packetByteBuf) {
		int i = packetByteBuf.readVarInt();
		return method_16186(Stream.generate(() -> new Ingredient.class_3575(packetByteBuf.readItemStack())).limit((long)i));
	}

	public static Ingredient method_16183(@Nullable JsonElement jsonElement) {
		if (jsonElement == null || jsonElement.isJsonNull()) {
			throw new JsonSyntaxException("Item cannot be null");
		} else if (jsonElement.isJsonObject()) {
			return method_16186(Stream.of(method_16184(jsonElement.getAsJsonObject())));
		} else if (jsonElement.isJsonArray()) {
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			if (jsonArray.size() == 0) {
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
			} else {
				return method_16186(StreamSupport.stream(jsonArray.spliterator(), false).map(jsonElementx -> method_16184(JsonHelper.asObject(jsonElementx, "item"))));
			}
		} else {
			throw new JsonSyntaxException("Expected item to be object or array of objects");
		}
	}

	public static Ingredient.class_3577 method_16184(JsonObject jsonObject) {
		if (jsonObject.has("item") && jsonObject.has("tag")) {
			throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
		} else if (jsonObject.has("item")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "item"));
			Item item = Registry.ITEM.getByIdentifier(identifier);
			if (item == null) {
				throw new JsonSyntaxException("Unknown item '" + identifier + "'");
			} else {
				return new Ingredient.class_3575(new ItemStack(item));
			}
		} else if (jsonObject.has("tag")) {
			Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
			Tag<Item> tag = ItemTags.method_21451().method_21486(identifier2);
			if (tag == null) {
				throw new JsonSyntaxException("Unknown item tag '" + identifier2 + "'");
			} else {
				return new Ingredient.class_3576(tag);
			}
		} else {
			throw new JsonParseException("An ingredient entry needs either a tag or an item");
		}
	}

	static class class_3575 implements Ingredient.class_3577 {
		private final ItemStack field_17441;

		private class_3575(ItemStack itemStack) {
			this.field_17441 = itemStack;
		}

		@Override
		public Collection<ItemStack> method_16198() {
			return Collections.singleton(this.field_17441);
		}

		@Override
		public JsonObject method_16199() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getId(this.field_17441.getItem()).toString());
			return jsonObject;
		}
	}

	static class class_3576 implements Ingredient.class_3577 {
		private final Tag<Item> field_17442;

		private class_3576(Tag<Item> tag) {
			this.field_17442 = tag;
		}

		@Override
		public Collection<ItemStack> method_16198() {
			List<ItemStack> list = Lists.newArrayList();

			for (Item item : this.field_17442.values()) {
				list.add(new ItemStack(item));
			}

			return list;
		}

		@Override
		public JsonObject method_16199() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("tag", this.field_17442.getId().toString());
			return jsonObject;
		}
	}

	interface class_3577 {
		Collection<ItemStack> method_16198();

		JsonObject method_16199();
	}
}
