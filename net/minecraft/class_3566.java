package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.registry.Registry;

public class class_3566 {
	private static final List<class_3566.class_3567<Potion>> field_17408 = Lists.newArrayList();
	private static final List<class_3566.class_3567<Item>> field_17409 = Lists.newArrayList();
	private static final List<Ingredient> field_17410 = Lists.newArrayList();
	private static final Predicate<ItemStack> field_17411 = itemStack -> {
		for (Ingredient ingredient : field_17410) {
			if (ingredient.test(itemStack)) {
				return true;
			}
		}

		return false;
	};

	public static boolean method_16158(ItemStack itemStack) {
		return method_16161(itemStack) || method_16163(itemStack);
	}

	protected static boolean method_16161(ItemStack itemStack) {
		int i = 0;

		for (int j = field_17409.size(); i < j; i++) {
			if (((class_3566.class_3567)field_17409.get(i)).field_17413.test(itemStack)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean method_16163(ItemStack itemStack) {
		int i = 0;

		for (int j = field_17408.size(); i < j; i++) {
			if (((class_3566.class_3567)field_17408.get(i)).field_17413.test(itemStack)) {
				return true;
			}
		}

		return false;
	}

	public static boolean method_16159(ItemStack itemStack, ItemStack itemStack2) {
		return !field_17411.test(itemStack) ? false : method_16162(itemStack, itemStack2) || method_16164(itemStack, itemStack2);
	}

	protected static boolean method_16162(ItemStack itemStack, ItemStack itemStack2) {
		Item item = itemStack.getItem();
		int i = 0;

		for (int j = field_17409.size(); i < j; i++) {
			class_3566.class_3567<Item> lv = (class_3566.class_3567<Item>)field_17409.get(i);
			if (lv.field_17412 == item && lv.field_17413.test(itemStack2)) {
				return true;
			}
		}

		return false;
	}

	protected static boolean method_16164(ItemStack itemStack, ItemStack itemStack2) {
		Potion potion = PotionUtil.getPotion(itemStack);
		int i = 0;

		for (int j = field_17408.size(); i < j; i++) {
			class_3566.class_3567<Potion> lv = (class_3566.class_3567<Potion>)field_17408.get(i);
			if (lv.field_17412 == potion && lv.field_17413.test(itemStack2)) {
				return true;
			}
		}

		return false;
	}

	public static ItemStack method_16166(ItemStack itemStack, ItemStack itemStack2) {
		if (!itemStack2.isEmpty()) {
			Potion potion = PotionUtil.getPotion(itemStack2);
			Item item = itemStack2.getItem();
			int i = 0;

			for (int j = field_17409.size(); i < j; i++) {
				class_3566.class_3567<Item> lv = (class_3566.class_3567<Item>)field_17409.get(i);
				if (lv.field_17412 == item && lv.field_17413.test(itemStack)) {
					return PotionUtil.setPotion(new ItemStack(lv.field_17414), potion);
				}
			}

			i = 0;

			for (int l = field_17408.size(); i < l; i++) {
				class_3566.class_3567<Potion> lv2 = (class_3566.class_3567<Potion>)field_17408.get(i);
				if (lv2.field_17412 == potion && lv2.field_17413.test(itemStack)) {
					return PotionUtil.setPotion(new ItemStack(item), lv2.field_17414);
				}
			}
		}

		return itemStack2;
	}

	public static void method_16155() {
		method_16156(Items.POTION);
		method_16156(Items.SPLASH_POTION);
		method_16156(Items.LINGERING_POTION);
		method_16157(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
		method_16157(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
		method_16160(Potions.WATER, Items.GLISTERING_MELON, Potions.MUNDANE);
		method_16160(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
		method_16160(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
		method_16160(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
		method_16160(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
		method_16160(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
		method_16160(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
		method_16160(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
		method_16160(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
		method_16160(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
		method_16160(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
		method_16160(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
		method_16160(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
		method_16160(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
		method_16160(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
		method_16160(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
		method_16160(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
		method_16160(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
		method_16160(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
		method_16160(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
		method_16160(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
		method_16160(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
		method_16160(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
		method_16160(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
		method_16160(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
		method_16160(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
		method_16160(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
		method_16160(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
		method_16160(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
		method_16160(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
		method_16160(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
		method_16160(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
		method_16160(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
		method_16160(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
		method_16160(Potions.AWKWARD, Items.GLISTERING_MELON, Potions.HEALING);
		method_16160(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
		method_16160(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
		method_16160(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
		method_16160(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
		method_16160(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
		method_16160(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
		method_16160(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
		method_16160(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
		method_16160(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
		method_16160(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
		method_16160(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
		method_16160(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
		method_16160(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
		method_16160(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
		method_16160(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
		method_16160(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
		method_16160(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
		method_16160(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
		method_16160(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
		method_16160(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
	}

	private static void method_16157(Item item, Item item2, Item item3) {
		if (!(item instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(item));
		} else if (!(item3 instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(item3));
		} else {
			field_17409.add(new class_3566.class_3567<>(item, Ingredient.ofItems(item2), item3));
		}
	}

	private static void method_16156(Item item) {
		if (!(item instanceof PotionItem)) {
			throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(item));
		} else {
			field_17410.add(Ingredient.ofItems(item));
		}
	}

	private static void method_16160(Potion potion, Item item, Potion potion2) {
		field_17408.add(new class_3566.class_3567<>(potion, Ingredient.ofItems(item), potion2));
	}

	static class class_3567<T> {
		private final T field_17412;
		private final Ingredient field_17413;
		private final T field_17414;

		public class_3567(T object, Ingredient ingredient, T object2) {
			this.field_17412 = object;
			this.field_17413 = ingredient;
			this.field_17414 = object2;
		}
	}
}
