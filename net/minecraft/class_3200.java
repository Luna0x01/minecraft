package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.json.MinMaxJson;
import net.minecraft.util.json.NbtCompoundJson;

public class class_3200 {
	public static final class_3200 field_15710 = new class_3200();
	private final Item field_15711;
	private final Integer field_15712;
	private final MinMaxJson field_15713;
	private final MinMaxJson field_15714;
	private final class_3180[] field_15715;
	private final Potion field_15716;
	private final NbtCompoundJson field_15717;

	public class_3200() {
		this.field_15711 = null;
		this.field_15712 = null;
		this.field_15716 = null;
		this.field_15713 = MinMaxJson.EMPTY;
		this.field_15714 = MinMaxJson.EMPTY;
		this.field_15715 = new class_3180[0];
		this.field_15717 = NbtCompoundJson.EMPTY;
	}

	public class_3200(
		@Nullable Item item,
		@Nullable Integer integer,
		MinMaxJson minMaxJson,
		MinMaxJson minMaxJson2,
		class_3180[] args,
		@Nullable Potion potion,
		NbtCompoundJson nbtCompoundJson
	) {
		this.field_15711 = item;
		this.field_15712 = integer;
		this.field_15713 = minMaxJson;
		this.field_15714 = minMaxJson2;
		this.field_15715 = args;
		this.field_15716 = potion;
		this.field_15717 = nbtCompoundJson;
	}

	public boolean method_14294(ItemStack itemStack) {
		if (this.field_15711 != null && itemStack.getItem() != this.field_15711) {
			return false;
		} else if (this.field_15712 != null && itemStack.getData() != this.field_15712) {
			return false;
		} else if (!this.field_15713.method_14335((float)itemStack.getCount())) {
			return false;
		} else if (this.field_15714 != MinMaxJson.EMPTY && !itemStack.isDamageable()) {
			return false;
		} else if (!this.field_15714.method_14335((float)(itemStack.getMaxDamage() - itemStack.getDamage()))) {
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

	public static class_3200 method_14295(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "item");
			MinMaxJson minMaxJson = MinMaxJson.fromJson(jsonObject.get("count"));
			MinMaxJson minMaxJson2 = MinMaxJson.fromJson(jsonObject.get("durability"));
			Integer integer = jsonObject.has("data") ? JsonHelper.getInt(jsonObject, "data") : null;
			NbtCompoundJson nbtCompoundJson = NbtCompoundJson.fromJson(jsonObject.get("nbt"));
			Item item = null;
			if (jsonObject.has("item")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "item"));
				item = Item.REGISTRY.get(identifier);
				if (item == null) {
					throw new JsonSyntaxException("Unknown item id '" + identifier + "'");
				}
			}

			class_3180[] lvs = class_3180.method_14209(jsonObject.get("enchantments"));
			Potion potion = null;
			if (jsonObject.has("potion")) {
				Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "potion"));
				if (!Potion.REGISTRY.containsKey(identifier2)) {
					throw new JsonSyntaxException("Unknown potion '" + identifier2 + "'");
				}

				potion = Potion.REGISTRY.get(identifier2);
			}

			return new class_3200(item, integer, minMaxJson, minMaxJson2, lvs, potion, nbtCompoundJson);
		} else {
			return field_15710;
		}
	}

	public static class_3200[] method_14296(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(jsonElement, "items");
			class_3200[] lvs = new class_3200[jsonArray.size()];

			for (int i = 0; i < lvs.length; i++) {
				lvs[i] = method_14295(jsonArray.get(i));
			}

			return lvs;
		} else {
			return new class_3200[0];
		}
	}
}
