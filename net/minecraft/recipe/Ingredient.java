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
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public final class Ingredient implements Predicate<ItemStack> {
	public static final Ingredient EMPTY = new Ingredient(Stream.empty());
	private final Ingredient.Entry[] entries;
	private ItemStack[] matchingStacks;
	private IntList ids;

	private Ingredient(Stream<? extends Ingredient.Entry> entries) {
		this.entries = (Ingredient.Entry[])entries.toArray(Ingredient.Entry[]::new);
	}

	public ItemStack[] getMatchingStacksClient() {
		this.cacheMatchingStacks();
		return this.matchingStacks;
	}

	private void cacheMatchingStacks() {
		if (this.matchingStacks == null) {
			this.matchingStacks = (ItemStack[])Arrays.stream(this.entries).flatMap(entry -> entry.getStacks().stream()).distinct().toArray(ItemStack[]::new);
		}
	}

	public boolean test(@Nullable ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		} else {
			this.cacheMatchingStacks();
			if (this.matchingStacks.length == 0) {
				return itemStack.isEmpty();
			} else {
				for (ItemStack itemStack2 : this.matchingStacks) {
					if (itemStack2.isOf(itemStack.getItem())) {
						return true;
					}
				}

				return false;
			}
		}
	}

	public IntList getMatchingItemIds() {
		if (this.ids == null) {
			this.cacheMatchingStacks();
			this.ids = new IntArrayList(this.matchingStacks.length);

			for (ItemStack itemStack : this.matchingStacks) {
				this.ids.add(RecipeMatcher.getItemId(itemStack));
			}

			this.ids.sort(IntComparators.NATURAL_COMPARATOR);
		}

		return this.ids;
	}

	public void write(PacketByteBuf buf) {
		this.cacheMatchingStacks();
		buf.writeCollection(Arrays.asList(this.matchingStacks), PacketByteBuf::writeItemStack);
	}

	public JsonElement toJson() {
		if (this.entries.length == 1) {
			return this.entries[0].toJson();
		} else {
			JsonArray jsonArray = new JsonArray();

			for (Ingredient.Entry entry : this.entries) {
				jsonArray.add(entry.toJson());
			}

			return jsonArray;
		}
	}

	public boolean isEmpty() {
		return this.entries.length == 0 && (this.matchingStacks == null || this.matchingStacks.length == 0) && (this.ids == null || this.ids.isEmpty());
	}

	private static Ingredient ofEntries(Stream<? extends Ingredient.Entry> entries) {
		Ingredient ingredient = new Ingredient(entries);
		return ingredient.entries.length == 0 ? EMPTY : ingredient;
	}

	public static Ingredient empty() {
		return EMPTY;
	}

	public static Ingredient ofItems(ItemConvertible... items) {
		return ofStacks(Arrays.stream(items).map(ItemStack::new));
	}

	public static Ingredient ofStacks(ItemStack... stacks) {
		return ofStacks(Arrays.stream(stacks));
	}

	public static Ingredient ofStacks(Stream<ItemStack> stacks) {
		return ofEntries(stacks.filter(stack -> !stack.isEmpty()).map(Ingredient.StackEntry::new));
	}

	public static Ingredient fromTag(Tag<Item> tag) {
		return ofEntries(Stream.of(new Ingredient.TagEntry(tag)));
	}

	public static Ingredient fromPacket(PacketByteBuf buf) {
		return ofEntries(buf.readList(PacketByteBuf::readItemStack).stream().map(Ingredient.StackEntry::new));
	}

	public static Ingredient fromJson(@Nullable JsonElement json) {
		if (json == null || json.isJsonNull()) {
			throw new JsonSyntaxException("Item cannot be null");
		} else if (json.isJsonObject()) {
			return ofEntries(Stream.of(entryFromJson(json.getAsJsonObject())));
		} else if (json.isJsonArray()) {
			JsonArray jsonArray = json.getAsJsonArray();
			if (jsonArray.size() == 0) {
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
			} else {
				return ofEntries(StreamSupport.stream(jsonArray.spliterator(), false).map(jsonElement -> entryFromJson(JsonHelper.asObject(jsonElement, "item"))));
			}
		} else {
			throw new JsonSyntaxException("Expected item to be object or array of objects");
		}
	}

	private static Ingredient.Entry entryFromJson(JsonObject json) {
		if (json.has("item") && json.has("tag")) {
			throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
		} else if (json.has("item")) {
			Item item = ShapedRecipe.getItem(json);
			return new Ingredient.StackEntry(new ItemStack(item));
		} else if (json.has("tag")) {
			Identifier identifier = new Identifier(JsonHelper.getString(json, "tag"));
			Tag<Item> tag = ServerTagManagerHolder.getTagManager()
				.getTag(Registry.ITEM_KEY, identifier, identifierx -> new JsonSyntaxException("Unknown item tag '" + identifierx + "'"));
			return new Ingredient.TagEntry(tag);
		} else {
			throw new JsonParseException("An ingredient entry needs either a tag or an item");
		}
	}

	interface Entry {
		Collection<ItemStack> getStacks();

		JsonObject toJson();
	}

	static class StackEntry implements Ingredient.Entry {
		private final ItemStack stack;

		StackEntry(ItemStack itemStack) {
			this.stack = itemStack;
		}

		@Override
		public Collection<ItemStack> getStacks() {
			return Collections.singleton(this.stack);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", Registry.ITEM.getId(this.stack.getItem()).toString());
			return jsonObject;
		}
	}

	static class TagEntry implements Ingredient.Entry {
		private final Tag<Item> tag;

		TagEntry(Tag<Item> tag) {
			this.tag = tag;
		}

		@Override
		public Collection<ItemStack> getStacks() {
			List<ItemStack> list = Lists.newArrayList();

			for (Item item : this.tag.values()) {
				list.add(new ItemStack(item));
			}

			return list;
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty(
				"tag", ServerTagManagerHolder.getTagManager().getTagId(Registry.ITEM_KEY, this.tag, () -> new IllegalStateException("Unknown item tag")).toString()
			);
			return jsonObject;
		}
	}
}
