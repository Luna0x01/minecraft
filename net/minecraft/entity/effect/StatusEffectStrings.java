package net.minecraft.entity.effect;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.item.FishItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;

public class StatusEffectStrings {
	private static final List<StatusEffectStrings.class_2697<Potion>> field_12339 = Lists.newArrayList();
	private static final List<StatusEffectStrings.class_2697<Item>> field_12340 = Lists.newArrayList();
	private static final List<Ingredient> field_12341 = Lists.newArrayList();
	private static final Predicate<ItemStack> field_12342 = new Predicate<ItemStack>() {
		public boolean apply(ItemStack itemStack) {
			for (Ingredient ingredient : StatusEffectStrings.field_12341) {
				if (ingredient.apply(itemStack)) {
					return true;
				}
			}

			return false;
		}
	};

	public static boolean method_11417(ItemStack itemStack) {
		return method_11423(itemStack) || method_11425(itemStack);
	}

	protected static boolean method_11423(ItemStack itemStack) {
		int i = 0;

		for (int j = field_12340.size(); i < j; i++) {
			if (((StatusEffectStrings.class_2697)field_12340.get(i)).field_15679.apply(itemStack)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean method_11425(ItemStack itemStack) {
		int i = 0;

		for (int j = field_12339.size(); i < j; i++) {
			if (((StatusEffectStrings.class_2697)field_12339.get(i)).field_15679.apply(itemStack)) {
				return true;
			}
		}

		return false;
	}

	public static boolean method_11418(ItemStack itemStack, ItemStack itemStack2) {
		return !field_12342.apply(itemStack) ? false : method_11424(itemStack, itemStack2) || method_11426(itemStack, itemStack2);
	}

	protected static boolean method_11424(ItemStack itemStack, ItemStack itemStack2) {
		Item item = itemStack.getItem();
		int i = 0;

		for (int j = field_12340.size(); i < j; i++) {
			StatusEffectStrings.class_2697<Item> lv = (StatusEffectStrings.class_2697<Item>)field_12340.get(i);
			if (lv.field_12345 == item && lv.field_15679.apply(itemStack2)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean method_11426(ItemStack itemStack, ItemStack itemStack2) {
		Potion potion = PotionUtil.getPotion(itemStack);
		int i = 0;

		for (int j = field_12339.size(); i < j; i++) {
			StatusEffectStrings.class_2697<Potion> lv = (StatusEffectStrings.class_2697<Potion>)field_12339.get(i);
			if (lv.field_12345 == potion && lv.field_15679.apply(itemStack2)) {
				return true;
			}
		}

		return false;
	}

	public static ItemStack method_11427(ItemStack itemStack, ItemStack itemStack2) {
		if (!itemStack2.isEmpty()) {
			Potion potion = PotionUtil.getPotion(itemStack2);
			Item item = itemStack2.getItem();
			int i = 0;

			for (int j = field_12340.size(); i < j; i++) {
				StatusEffectStrings.class_2697<Item> lv = (StatusEffectStrings.class_2697<Item>)field_12340.get(i);
				if (lv.field_12345 == item && lv.field_15679.apply(itemStack)) {
					return PotionUtil.setPotion(new ItemStack(lv.field_12347), potion);
				}
			}

			i = 0;

			for (int l = field_12339.size(); i < l; i++) {
				StatusEffectStrings.class_2697<Potion> lv2 = (StatusEffectStrings.class_2697<Potion>)field_12339.get(i);
				if (lv2.field_12345 == potion && lv2.field_15679.apply(itemStack)) {
					return PotionUtil.setPotion(new ItemStack(item), lv2.field_12347);
				}
			}
		}

		return itemStack2;
	}

	public static void method_11416() {
		method_14241(Items.POTION);
		method_14241(Items.SPLASH_POTION);
		method_14241(Items.LINGERING_POTION);
		method_11419(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
		method_11419(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
		method_14242(Potions.WATER, Items.GLISTERING_MELON, Potions.MUNDANE);
		method_14242(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
		method_14242(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
		method_14242(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
		method_14242(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
		method_14242(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
		method_14242(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
		method_14242(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
		method_14242(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
		method_14242(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
		method_14242(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
		method_14242(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
		method_14242(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
		method_14242(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
		method_14242(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
		method_14242(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
		method_14242(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
		method_14242(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
		method_14242(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
		method_14242(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
		method_14242(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
		method_14242(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
		method_14242(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
		method_14242(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
		method_14242(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
		method_14242(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
		method_14242(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
		method_14242(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
		method_14243(Potions.AWKWARD, Ingredient.method_14248(new ItemStack(Items.RAW_FISH, 1, FishItem.FishType.PUFFERFISH.getId())), Potions.WATER_BREATHING);
		method_14242(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
		method_14242(Potions.AWKWARD, Items.GLISTERING_MELON, Potions.HEALING);
		method_14242(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
		method_14242(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
		method_14242(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
		method_14242(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
		method_14242(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
		method_14242(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
		method_14242(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
		method_14242(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
		method_14242(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
		method_14242(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
		method_14242(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
		method_14242(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
		method_14242(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
		method_14242(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
		method_14242(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
		method_14242(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
		method_14242(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
		method_14242(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
	}

	private static void method_11419(PotionItem potionItem, Item item, PotionItem potionItem2) {
		field_12340.add(new StatusEffectStrings.class_2697<>(potionItem, Ingredient.method_14247(item), potionItem2));
	}

	private static void method_14241(PotionItem potionItem) {
		field_12341.add(Ingredient.method_14247(potionItem));
	}

	private static void method_14242(Potion potion, Item item, Potion potion2) {
		method_14243(potion, Ingredient.method_14247(item), potion2);
	}

	private static void method_14243(Potion potion, Ingredient ingredient, Potion potion2) {
		field_12339.add(new StatusEffectStrings.class_2697<>(potion, ingredient, potion2));
	}

	static class class_2697<T> {
		final T field_12345;
		final Ingredient field_15679;
		final T field_12347;

		public class_2697(T object, Ingredient ingredient, T object2) {
			this.field_12345 = object;
			this.field_15679 = ingredient;
			this.field_12347 = object2;
		}
	}
}
