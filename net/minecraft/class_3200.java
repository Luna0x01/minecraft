package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.json.NbtCompoundJson;
import net.minecraft.util.registry.Registry;

public class class_3200 {
	public static final class_3200 field_15710 = new class_3200();
	@Nullable
	private final Tag<Item> field_17421;
	@Nullable
	private final Item field_15711;
	private final class_3638.class_3642 field_15713;
	private final class_3638.class_3642 field_15714;
	private final class_3180[] field_15715;
	@Nullable
	private final Potion field_15716;
	private final NbtCompoundJson field_15717;

	public class_3200() {
		this.field_17421 = null;
		this.field_15711 = null;
		this.field_15716 = null;
		this.field_15713 = class_3638.class_3642.field_17698;
		this.field_15714 = class_3638.class_3642.field_17698;
		this.field_15715 = new class_3180[0];
		this.field_15717 = NbtCompoundJson.EMPTY;
	}

	public class_3200(
		@Nullable Tag<Item> tag,
		@Nullable Item item,
		class_3638.class_3642 arg,
		class_3638.class_3642 arg2,
		class_3180[] args,
		@Nullable Potion potion,
		NbtCompoundJson nbtCompoundJson
	) {
		this.field_17421 = tag;
		this.field_15711 = item;
		this.field_15713 = arg;
		this.field_15714 = arg2;
		this.field_15715 = args;
		this.field_15716 = potion;
		this.field_15717 = nbtCompoundJson;
	}

	public boolean method_14294(ItemStack itemStack) {
		if (this.field_17421 != null && !this.field_17421.contains(itemStack.getItem())) {
			return false;
		} else if (this.field_15711 != null && itemStack.getItem() != this.field_15711) {
			return false;
		} else if (!this.field_15713.method_16531(itemStack.getCount())) {
			return false;
		} else if (!this.field_15714.method_16512() && !itemStack.isDamageable()) {
			return false;
		} else if (!this.field_15714.method_16531(itemStack.getMaxDamage() - itemStack.getDamage())) {
			return false;
		} else if (!this.field_15717.method_14343(itemStack)) {
			return false;
		} else {
			Map<Enchantment, Integer> map = EnchantmentHelper.get(itemStack);

			for (int i = 0; i < this.field_15715.length; i++) {
				if (!this.field_15715[i].method_14208(map)) {
					return false;
				}
			}

			Potion potion = PotionUtil.getPotion(itemStack);
			return this.field_15716 == null || this.field_15716 == potion;
		}
	}

	public static class_3200 method_16171(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "item");
			class_3638.class_3642 lv = class_3638.class_3642.method_16524(jsonObject.get("count"));
			class_3638.class_3642 lv2 = class_3638.class_3642.method_16524(jsonObject.get("durability"));
			if (jsonObject.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				NbtCompoundJson nbtCompoundJson = NbtCompoundJson.fromJson(jsonObject.get("nbt"));
				Item item = null;
				if (jsonObject.has("item")) {
					Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "item"));
					item = Registry.ITEM.getByIdentifier(identifier);
					if (item == null) {
						throw new JsonSyntaxException("Unknown item id '" + identifier + "'");
					}
				}

				Tag<Item> tag = null;
				if (jsonObject.has("tag")) {
					Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
					tag = ItemTags.method_21451().method_21486(identifier2);
					if (tag == null) {
						throw new JsonSyntaxException("Unknown item tag '" + identifier2 + "'");
					}
				}

				class_3180[] lvs = class_3180.method_14209(jsonObject.get("enchantments"));
				Potion potion = null;
				if (jsonObject.has("potion")) {
					Identifier identifier3 = new Identifier(JsonHelper.getString(jsonObject, "potion"));
					if (!Registry.POTION.containsId(identifier3)) {
						throw new JsonSyntaxException("Unknown potion '" + identifier3 + "'");
					}

					potion = Registry.POTION.get(identifier3);
				}

				return new class_3200(tag, item, lv, lv2, lvs, potion, nbtCompoundJson);
			}
		} else {
			return field_15710;
		}
	}

	public JsonElement method_16170() {
		if (this == field_15710) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_15711 != null) {
				jsonObject.addProperty("item", Registry.ITEM.getId(this.field_15711).toString());
			}

			if (this.field_17421 != null) {
				jsonObject.addProperty("tag", this.field_17421.getId().toString());
			}

			jsonObject.add("count", this.field_15713.method_16513());
			jsonObject.add("durability", this.field_15714.method_16513());
			jsonObject.add("nbt", this.field_15717.method_16545());
			if (this.field_15715.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (class_3180 lv : this.field_15715) {
					jsonArray.add(lv.method_15832());
				}

				jsonObject.add("enchantments", jsonArray);
			}

			if (this.field_15716 != null) {
				jsonObject.addProperty("potion", Registry.POTION.getId(this.field_15716).toString());
			}

			return jsonObject;
		}
	}

	public static class_3200[] method_14296(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(jsonElement, "items");
			class_3200[] lvs = new class_3200[jsonArray.size()];

			for (int i = 0; i < lvs.length; i++) {
				lvs[i] = method_16171(jsonArray.get(i));
			}

			return lvs;
		} else {
			return new class_3200[0];
		}
	}

	public static class class_3568 {
		private final List<class_3180> field_17422 = Lists.newArrayList();
		@Nullable
		private Item field_17423;
		@Nullable
		private Tag<Item> field_17424;
		private class_3638.class_3642 field_17425 = class_3638.class_3642.field_17698;
		private class_3638.class_3642 field_17426 = class_3638.class_3642.field_17698;
		@Nullable
		private Potion field_17427;
		private NbtCompoundJson field_17428 = NbtCompoundJson.EMPTY;

		private class_3568() {
		}

		public static class_3200.class_3568 method_16172() {
			return new class_3200.class_3568();
		}

		public class_3200.class_3568 method_16173(Itemable itemable) {
			this.field_17423 = itemable.getItem();
			return this;
		}

		public class_3200.class_3568 method_16175(Tag<Item> tag) {
			this.field_17424 = tag;
			return this;
		}

		public class_3200.class_3568 method_16174(class_3638.class_3642 arg) {
			this.field_17425 = arg;
			return this;
		}

		public class_3200 method_16176() {
			return new class_3200(
				this.field_17424,
				this.field_17423,
				this.field_17425,
				this.field_17426,
				(class_3180[])this.field_17422.toArray(new class_3180[0]),
				this.field_17427,
				this.field_17428
			);
		}
	}
}
